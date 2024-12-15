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

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
        KStream<String, String> filteredStream = stream
                .filter((key, value) -> {
                    try {
                        // Deserialize JSON to Playlist object
                        Playlist playlist = objectMapper.readValue(value, Playlist.class);

                        // Filter playlists with fewer than 10 followers
                        return playlist.getNumFollowers() >= 10;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to deserialize playlist", e);
                    }
                });

        // Extract unique artist URIs and publish to ARTISTID
        filteredStream.foreach((key, value) -> {
            try {
                Playlist playlist = objectMapper.readValue(value, Playlist.class);

                // Use a Set to deduplicate artist URIs
                Set<String> uniqueArtistUris = new HashSet<>();
                playlist.getTracks().forEach(track -> uniqueArtistUris.add(track.getArtistUri()));

                // Publish each unique artist URI to ARTISTID
                uniqueArtistUris.forEach(artistUri -> {
                    kafkaTemplate.send("ARTISTID", artistUri);
                    System.out.println("Published artist URI: " + artistUri);
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to process playlist for artist URIs", e);
            }
        });

        // Keep the filtered playlists in the INGESTION topic
        filteredStream.to("INGESTION");

        return stream;
    }

    @Bean
    public Properties kafkaStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ingestion-api");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}
