package big.data.ingestion.configurations;

import big.data.ingestion.data.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Properties;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaStreamsConfig(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        // Stream from the INGESTION topic
        KStream<String, String> stream = streamsBuilder.stream("INGESTION");

        // Process the stream
        KStream<String, String> processedStream = stream
                .filter((key, value) -> {
                    try {
                        // Deserialize JSON to Playlist object
                        Playlist playlist = objectMapper.readValue(value, Playlist.class);

                        // Filter playlists with fewer than 10 followers
                        return playlist.getNumFollowers() >= 10;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize playlist", e);
                    }
                })
                .peek((key, value) -> {
                    try {
                        // Deserialize Playlist
                        Playlist playlist = objectMapper.readValue(value, Playlist.class);

                        // Publish artist IDs to ARTISTID topic
                        playlist.getTracks().forEach(track -> {
                            String artistUri = track.getArtistUri(); // Assuming artistUri is the ID
                            kafkaTemplate.send("ARTISTID", artistUri); // Publish to ARTISTID topic
                            System.out.println("Published artist uri: " + artistUri);
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to process playlist", e);
                    }
                });

        // Publish processed playlists to PLAYLISTS topic
        processedStream.to("PLAYLISTS");

        return stream;
    }

    @Bean
    public Properties kafkaStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ingestion-api");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "Kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}