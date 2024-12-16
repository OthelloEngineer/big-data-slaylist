package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/ingest")
public class PlaylistController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PlaylistManager playlistManager;

    public PlaylistController(KafkaTemplate<String, String> kafkaTemplate, PlaylistManager playlistManager) {
        this.kafkaTemplate = kafkaTemplate;
        this.playlistManager = playlistManager;
    }

    @PostMapping("/playlist")
    public ResponseEntity<String> ingestPlaylist(@RequestBody PlaylistOuterClass.Playlist playlist) {
        String playlistId = Integer.toHexString((playlist.getPlaylistName() + playlist.getFollowers()).hashCode());

        // Store the playlist in memory
        playlistManager.storePlaylist(playlistId, playlist);

        // Extract and send unique artist URIs to ARTISTID topic
        Set<String> artistUris = new HashSet<>();
        playlist.getSongsList().forEach(song -> {
            if (song.hasArtist()) {
                artistUris.add(song.getArtist().getArtistUri());
            }
        });

        artistUris.forEach(artistUri -> {
            kafkaTemplate.send("ARTISTID", artistUri);
            System.out.println("Published artist URI: " + artistUri);
        });

        return ResponseEntity.ok("Playlist received, artist URIs sent, and playlist stored in memory.");
    }
}