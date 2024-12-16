package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PlaylistManager playlistManager;
    private final ObjectMapper objectMapper;

    public PlaylistIngestionController(KafkaTemplate<String, String> kafkaTemplate, PlaylistManager playlistManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.playlistManager = playlistManager;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/dataset")
    public ResponseEntity<String> ingestDataset(@RequestBody String jsonPayload) throws IOException {
        JsonNode root = objectMapper.readTree(jsonPayload);
        JsonNode playlistsNode = root.get("playlists");

        if (playlistsNode == null || !playlistsNode.isArray()) {
            return ResponseEntity.badRequest().body("Invalid input format: 'playlists' array not found.");
        }

        // Process each playlist
        for (JsonNode playlistNode : playlistsNode) {
            int followers = playlistNode.path("num_followers").asInt();

            // Skip playlists with fewer than 10 followers
            if (followers < 10) {
                System.out.println("Skipped playlist due to low followers: " + playlistNode.path("name").asText());
                continue;
            }

            // Build Playlist proto message
            PlaylistOuterClass.Playlist playlist = buildPlaylistProto(playlistNode);

            // Generate unique ID and store playlist
            String playlistId = Integer.toHexString((playlist.getPlaylistName() + playlist.getFollowers()).hashCode());
            playlistManager.storePlaylist(playlistId, playlist);

            // Extract unique artist URIs and send to ARTISTID topic
            Set<String> artistUris = new HashSet<>();
            playlist.getSongsList().forEach(song -> {
                if (song.hasArtist() && !song.getArtist().getArtistUri().isEmpty()) {
                    artistUris.add(song.getArtist().getArtistUri());
                }
            });

            artistUris.forEach(artistUri -> {
                kafkaTemplate.send("ARTISTID", artistUri);
                System.out.println("Published artist URI: " + artistUri);
            });

            System.out.println("Stored playlist: " + playlist.getPlaylistName());
        }

        return ResponseEntity.ok("Dataset ingestion completed.");
    }

    private PlaylistOuterClass.Playlist buildPlaylistProto(JsonNode playlistNode) {
        PlaylistOuterClass.Playlist.Builder playlistBuilder = PlaylistOuterClass.Playlist.newBuilder();

        // Set Playlist metadata
        playlistBuilder.setPlaylistName(playlistNode.path("name").asText());
        playlistBuilder.setFollowers(playlistNode.path("num_followers").asInt());
        playlistBuilder.setNumArtists(playlistNode.path("num_artists").asInt());
        playlistBuilder.setNumSongs(playlistNode.path("num_tracks").asInt());
        playlistBuilder.setNumAlbums(playlistNode.path("num_albums").asInt());
        playlistBuilder.setPid(playlistNode.path("pid").asInt());
        playlistBuilder.setOrigin(PlaylistOuterClass.Playlist.Origin.DATASET); // Explicitly set origin to DATASET

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