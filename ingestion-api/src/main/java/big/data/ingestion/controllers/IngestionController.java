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

import java.util.List;

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
    public ResponseEntity<String> processInitialDataset(@RequestBody List<Playlist> playlists) {
        playlists.forEach(playlist -> {
            try {
                // Serialize Playlist to JSON and send to Kafka
                String playlistJson = objectMapper.writeValueAsString(playlist);
                kafkaTemplate.send("raw-playlists-topic", playlistJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize playlist", e);
            }
        });

        return ResponseEntity.ok("Playlists sent to Kafka topic 'raw-playlists-topic'");
    }

    @PostMapping("/user-playlist")
    public ResponseEntity<String> processUserPlaylist(@RequestBody Playlist playlist) {
        try {
            // Serialize Playlist to JSON and send to Kafka
            String playlistJson = objectMapper.writeValueAsString(playlist);
            kafkaTemplate.send("raw-playlists-topic", playlistJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize playlist", e);
        }

        return ResponseEntity.ok("User playlist sent to Kafka topic 'raw-playlists-topic'");
    }



}
