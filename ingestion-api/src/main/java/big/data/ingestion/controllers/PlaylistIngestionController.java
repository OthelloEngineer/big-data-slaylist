package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import big.data.ingestion.services.PlaylistEnrichmentConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/ingest")
public class PlaylistIngestionController {

    private final KafkaTemplate<String, String> kafkaTemplate; // For sending artist URIs
    private final PlaylistManager playlistManager;
    private final PlaylistEnrichmentConsumer enrichmentConsumer;
    private final ObjectMapper objectMapper;

    public PlaylistIngestionController(KafkaTemplate<String, String> kafkaTemplate,
                                       PlaylistManager playlistManager,
                                       PlaylistEnrichmentConsumer enrichmentConsumer) {
        this.kafkaTemplate = kafkaTemplate;
        this.playlistManager = playlistManager;
        this.enrichmentConsumer = enrichmentConsumer;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/dataset")
    public ResponseEntity<String> ingestDataset(@RequestBody String jsonPayload) throws IOException {
        JsonNode root = objectMapper.readTree(jsonPayload);
        JsonNode playlistsNode = root.get("playlists");

        if (playlistsNode == null || !playlistsNode.isArray()) {
            return ResponseEntity.badRequest().body("Invalid input format: 'playlists' array not found.");
        }

        // Track all required artist URIs for enrichment
        Set<String> artistIds = new HashSet<>();
        playlistsNode.forEach(playlistNode -> {
            int followers = playlistNode.path("num_followers").asInt();
            if (followers < 10) {
                System.out.println("Skipped playlist due to low followers: " + playlistNode.path("name").asText());
                return;
            }

            PlaylistOuterClass.Playlist playlist = buildPlaylistProto(playlistNode);
            playlistManager.storePlaylist(playlist.getPid(), playlist);

            playlist.getSongsList().forEach(song -> {
                if (song.hasArtist() && !song.getArtist().getArtistUri().isEmpty()) {
                    String artistUri = song.getArtist().getArtistUri();
                    if (enrichmentConsumer.addPendingArtistUri(artistUri)) { // Prevent duplicates
                        kafkaTemplate.send("ARTISTID", artistUri);
                        System.out.println("Published artist URI to ARTISTID topic: " + artistUri);
                    }
                    artistIds.add(artistUri);
                }
            });
        });

        System.out.println("Waiting for artist data to complete playlist enrichment...");

        // Wait until all required artists are processed
        while (!enrichmentConsumer.isAllArtistsProcessed(artistIds)) {
            try {
                Thread.sleep(1000); // Avoid tight looping
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(500).body("Failed to process playlists due to interruption.");
            }
        }

        // Finalize and publish playlists after enrichment
        enrichmentConsumer.finalizePlaylistsIfComplete();
        return ResponseEntity.ok("Dataset ingestion completed.");
    }

    private PlaylistOuterClass.Playlist buildPlaylistProto(JsonNode playlistNode) {
        PlaylistOuterClass.Playlist.Builder playlistBuilder = PlaylistOuterClass.Playlist.newBuilder();
        playlistBuilder.setPlaylistName(playlistNode.path("name").asText());
        playlistBuilder.setFollowers(playlistNode.path("num_followers").asInt());
        playlistBuilder.setPid(playlistNode.path("pid").asInt());

        playlistNode.path("tracks").forEach(trackNode -> {
            PlaylistOuterClass.Song.Builder songBuilder = PlaylistOuterClass.Song.newBuilder();
            songBuilder.setTrackUri(trackNode.path("track_uri").asText());
            songBuilder.setTrackName(trackNode.path("track_name").asText());

            PlaylistOuterClass.Artist.Builder artistBuilder = PlaylistOuterClass.Artist.newBuilder();
            artistBuilder.setArtistUri(trackNode.path("artist_uri").asText());
            songBuilder.setArtist(artistBuilder.build());

            playlistBuilder.addSongs(songBuilder);
        });

        return playlistBuilder.build();
    }
}
