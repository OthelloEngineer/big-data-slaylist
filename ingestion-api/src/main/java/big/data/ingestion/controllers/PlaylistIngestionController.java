package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/ingest")
public class PlaylistIngestionController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PlaylistManager playlistManager;
    private final ObjectMapper objectMapper;
    private final Set<String> publishedArtistUris; // Prevent duplicated artist URIs

    public PlaylistIngestionController(KafkaTemplate<String, String> kafkaTemplate, PlaylistManager playlistManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.playlistManager = playlistManager;
        this.objectMapper = new ObjectMapper();
        this.publishedArtistUris = new HashSet<>();
    }

    @PostMapping("/dataset")
    public ResponseEntity<String> ingestDataset(@RequestBody String jsonPayload) throws IOException {
        JsonNode root = objectMapper.readTree(jsonPayload);
        JsonNode playlistsNode = root.get("playlists");

        if (playlistsNode == null || !playlistsNode.isArray()) {
            return ResponseEntity.badRequest().body("Invalid input format: 'playlists' array not found.");
        }

        // Process each playlist asynchronously
        CompletableFuture<Void> processingTask = CompletableFuture.runAsync(() -> {
            for (JsonNode playlistNode : playlistsNode) {
                int followers = playlistNode.path("num_followers").asInt();

                // Skip playlists with fewer than 10 followers
                if (followers < 10) {
                    System.out.println("Skipped playlist due to low followers: " + playlistNode.path("name").asText());
                    continue;
                }

                processPlaylist(playlistNode, PlaylistOuterClass.Playlist.Origin.DATASET);
            }
        });

        processingTask.join(); // Wait for processing to complete
        return ResponseEntity.ok("Dataset ingestion completed.");
    }

    @PostMapping("/user-playlist")
    public ResponseEntity<String> ingestUserPlaylist(@RequestBody String jsonPayload) throws IOException {
        JsonNode playlistNode = objectMapper.readTree(jsonPayload);

        if (playlistNode.isMissingNode()) {
            return ResponseEntity.badRequest().body("Invalid input format: playlist data not found.");
        }

        processPlaylist(playlistNode, PlaylistOuterClass.Playlist.Origin.USER);
        return ResponseEntity.ok("User playlist ingestion completed.");
    }

    private void processPlaylist(JsonNode playlistNode, PlaylistOuterClass.Playlist.Origin origin) {
        try {
            // Build Playlist proto message
            PlaylistOuterClass.Playlist playlist = buildPlaylistProto(playlistNode, origin);

            // Serialize Protobuf to JSON String
            String playlistJson = JsonFormat.printer().print(playlist);

            // Store playlist
            playlistManager.storePlaylist(playlist.getPid(), playlist);

            // Extract and deduplicate artist URIs
            Set<String> artistUris = new HashSet<>();
            playlist.getSongsList().forEach(song -> {
                if (song.hasArtist() && !song.getArtist().getArtistUri().isEmpty()) {
                    String artistUri = song.getArtist().getArtistUri();
                    if (!publishedArtistUris.contains(artistUri)) {
                        artistUris.add(artistUri);
                        publishedArtistUris.add(artistUri);
                    }
                }
            });

            // Publish unique artist URIs and Playlist JSON
            artistUris.forEach(artistUri -> {
                kafkaTemplate.send("ARTISTID", artistUri);
                System.out.println("Published artist URI to ARTISTID topic: " + artistUri);
            });

            kafkaTemplate.send("PLAYLISTS", String.valueOf(playlist.getPid()), playlistJson);
            System.out.println("Published playlist to PLAYLISTS topic: " + playlist.getPlaylistName());

        } catch (Exception e) {
            System.err.println("Error processing playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PlaylistOuterClass.Playlist buildPlaylistProto(JsonNode playlistNode, PlaylistOuterClass.Playlist.Origin origin) {
        PlaylistOuterClass.Playlist.Builder playlistBuilder = PlaylistOuterClass.Playlist.newBuilder();

        // Set Playlist metadata
        playlistBuilder.setPlaylistName(playlistNode.path("name").asText());
        playlistBuilder.setFollowers(playlistNode.path("num_followers").asInt());
        playlistBuilder.setNumArtists(playlistNode.path("num_artists").asInt());
        playlistBuilder.setNumSongs(playlistNode.path("num_tracks").asInt());
        playlistBuilder.setNumAlbums(playlistNode.path("num_albums").asInt());
        playlistBuilder.setPid(playlistNode.path("pid").asInt());
        playlistBuilder.setOrigin(origin);

        // Process tracks
        JsonNode tracksNode = playlistNode.path("tracks");
        if (tracksNode.isArray()) {
            for (JsonNode trackNode : tracksNode) {
                PlaylistOuterClass.Song.Builder songBuilder = PlaylistOuterClass.Song.newBuilder();
                songBuilder.setTrackUri(trackNode.path("track_uri").asText());
                songBuilder.setTrackName(trackNode.path("track_name").asText());
                songBuilder.setDurationMs(String.valueOf(trackNode.path("duration_ms").asInt()));

                // Set Artist information
                PlaylistOuterClass.Artist.Builder artistBuilder = PlaylistOuterClass.Artist.newBuilder();
                artistBuilder.setArtistUri(trackNode.path("artist_uri").asText());
                artistBuilder.setArtistName(trackNode.path("artist_name").asText());
                songBuilder.setArtist(artistBuilder);

                playlistBuilder.addSongs(songBuilder);
            }
        }

        return playlistBuilder.build();
    }
}
