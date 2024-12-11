package big.data.ingestion.controllers;

import big.data.ingestion.data.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ingest")
public class IngestionController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public IngestionController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/initial-dataset")
    public ResponseEntity<String> processInitialDataset(@RequestBody PlaylistFileWrapper fileWrapper) {
        // Log the "info" field for debugging or monitoring
        PlaylistFileWrapper.Info info = fileWrapper.getInfo();
        System.out.println("Processing dataset:");
        System.out.println("Generated on: " + info.getGeneratedOn());
        System.out.println("Slice: " + info.getSlice());
        System.out.println("Version: " + info.getVersion());

        // Extract playlists from the wrapper
        List<Playlist> playlists = fileWrapper.getPlaylists();

        // Process each playlist
        playlists.forEach(playlist -> {
            try {
                // Filter playlists with fewer than 10 followers
                if (playlist.getNumFollowers() >= 10) {
                    // Serialize and send playlist to INGESTION topic
                    String playlistJson = objectMapper.writeValueAsString(playlist);
                    kafkaTemplate.send("INGESTION", playlistJson);

                    // Extract unique artist URIs and send to ARTISTID topic
                    Set<String> uniqueArtistUris = new HashSet<>();
                    playlist.getTracks().forEach(track -> uniqueArtistUris.add(track.getArtistUri()));
                    uniqueArtistUris.forEach(artistUri -> {
                        kafkaTemplate.send("ARTISTID", artistUri);
                        System.out.println("Published artist URI: " + artistUri);
                    });
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize playlist", e);
            }
        });

        return ResponseEntity.ok("Playlists and artist URIs processed and published to Kafka topics.");
    }

    @PostMapping("/user-playlist")
    public ResponseEntity<String> processUserPlaylist(@RequestBody Playlist playlist) {
        try {
            // Filter playlists with fewer than 10 followers
            if (playlist.getNumFollowers() >= 10) {
                // Serialize and send playlist to INGESTION topic
                String playlistJson = objectMapper.writeValueAsString(playlist);
                kafkaTemplate.send("INGESTION", playlistJson);

                // Extract unique artist URIs and send to ARTISTID topic
                Set<String> uniqueArtistUris = new HashSet<>();
                playlist.getTracks().forEach(track -> uniqueArtistUris.add(track.getArtistUri()));
                uniqueArtistUris.forEach(artistUri -> {
                    kafkaTemplate.send("ARTISTID", artistUri);
                    System.out.println("Published artist URI: " + artistUri);
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize playlist", e);
        }

        return ResponseEntity.ok("User playlist processed and published to Kafka topics.");
    }
}
