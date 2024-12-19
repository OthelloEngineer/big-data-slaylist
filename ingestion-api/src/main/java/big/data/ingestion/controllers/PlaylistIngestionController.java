package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.ArtistRequest;
import big.data.ingestion.data.Playlist;
import big.data.ingestion.data.Song;
import big.data.ingestion.data.Origin;
import big.data.ingestion.services.PlaylistEnrichmentConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/ingest")
public class PlaylistIngestionController {

    private final KafkaTemplate<String, ArtistRequest> kafkaTemplate;
    private final PlaylistManager playlistManager;
    private final PlaylistEnrichmentConsumer enrichmentConsumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Global Set for tracking all artist URIs
    private final Set<String> globalArtistUris = new HashSet<>();

    public PlaylistIngestionController(KafkaTemplate<String, ArtistRequest> kafkaTemplate,
                                       PlaylistManager playlistManager,
                                       PlaylistEnrichmentConsumer enrichmentConsumer) {
        this.kafkaTemplate = kafkaTemplate;
        this.playlistManager = playlistManager;
        this.enrichmentConsumer = enrichmentConsumer;
    }

    @PostMapping("/dataset")
    public ResponseEntity<String> ingestDataset(@RequestBody String jsonPayload) throws IOException {
        JsonNode root = objectMapper.readTree(jsonPayload);
        JsonNode playlistsNode = root.get("playlists");

        if (playlistsNode == null || !playlistsNode.isArray()) {
            return ResponseEntity.badRequest().body("Invalid input format: 'playlists' array not found.");
        }

        playlistsNode.forEach(playlistNode -> {
            try {
                // Map JSON to Playlist.Builder
                Playlist.Builder playlistBuilder = mapJsonToPlaylist(playlistNode);

                // Skip playlists with fewer than 10 followers
                if (playlistBuilder.getFollowers() < 10) {
                    System.out.println("Skipped playlist with fewer than 10 followers: " + playlistBuilder.getPlaylistName());
                    return;
                }

                playlistManager.storePlaylist(playlistBuilder.getPid(), playlistBuilder);

                // Collect and send artist URIs as ArtistRequest
                playlistBuilder.getSongs().forEach(song -> {
                    if (song.getArtist() != null) {
                        String artistUri = song.getArtist().getArtistUri();
                        synchronized (globalArtistUris) {
                            if (globalArtistUris.add(artistUri)) {
                                ArtistRequest artistRequest = ArtistRequest.newBuilder()
                                        .setArtistUri(artistUri)
                                        .build();
                                kafkaTemplate.send("ARTISTID", artistUri, artistRequest);
                                System.out.println("Sent ArtistRequest: " + artistUri);
                                enrichmentConsumer.addPendingArtistUri(artistUri);
                            }
                        }
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException("Failed to process playlist", e);
            }
        });

        // Wait for artist enrichment
        System.out.println("Waiting for artist data to complete playlist enrichment...");
        while (!enrichmentConsumer.isAllArtistsProcessed(globalArtistUris)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(500).body("Failed to process playlists due to interruption.");
            }
        }

        // Finalize and publish complete playlists
        enrichmentConsumer.finalizePendingPlaylists();
        return ResponseEntity.ok("Dataset ingestion completed.");
    }

    private Playlist.Builder mapJsonToPlaylist(JsonNode playlistNode) {
        Playlist.Builder playlistBuilder = Playlist.newBuilder();

        playlistBuilder.setPlaylistName(playlistNode.get("name").asText());
        playlistBuilder.setFollowers(playlistNode.get("num_followers").asInt());
        playlistBuilder.setNumArtists(playlistNode.get("num_artists").asInt());
        playlistBuilder.setNumSongs(playlistNode.get("num_tracks").asInt());
        playlistBuilder.setNumAlbums(playlistNode.get("num_albums").asInt());
        playlistBuilder.setPid(playlistNode.get("pid").asInt());
        playlistBuilder.setOrigin(Origin.DATASET);
        playlistBuilder.setGenres(new HashMap<>()); // Set genres as an empty map initially

        playlistNode.get("tracks").forEach(trackNode -> {
            Song.Builder songBuilder = Song.newBuilder();
            songBuilder.setTrackUri(trackNode.get("track_uri").asText());
            songBuilder.setTrackName(trackNode.get("track_name").asText());
            songBuilder.setDurationMs(trackNode.get("duration_ms").asText());

            songBuilder.setArtist(null); // Artist will be set during enrichment
            playlistBuilder.getSongs().add(songBuilder.build());
        });

        return playlistBuilder;
    }
}
