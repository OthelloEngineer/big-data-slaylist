//package big.data.ingestion.configurations;
//
//import big.data.ingestion.components.PlaylistManager;
//import big.data.ingestion.data.Artist;
//import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.KafkaStreams;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.StreamsConfig;
//import org.apache.kafka.streams.kstream.KStream;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Collections;
//import java.util.Map;
//import java.util.Properties;
//
//@Configuration
//public class ArtistEnrichmentStream {
//
//    private final PlaylistManager playlistManager;
//
//    public ArtistEnrichmentStream(PlaylistManager playlistManager) {
//        this.playlistManager = playlistManager;
//    }
//
//    @Bean
//    public KafkaStreams artistEnrichmentStreams() {
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // Configure Avro Serde
//        final Map<String, String> serdeConfig = Collections.singletonMap(
//                "schema.registry.url", "http://schema-registry:8081"
//        );
//
//        final Serde<Artist> artistSerde = new SpecificAvroSerde<>();
//        artistSerde.configure(serdeConfig, false); // false for value serde
//
//        // Consume artist data from the ARTIST topic
//        KStream<String, Artist> artistStream = builder.stream(
//                "ARTIST",
//                org.apache.kafka.streams.kstream.Consumed.with(Serdes.String(), artistSerde)
//        );
//
//        // Process each artist and update in-memory playlists
//        artistStream.foreach((key, artist) -> {
//            System.out.println("Enriching playlists with artist: " + artist.getArtistUri());
//            playlistManager.updateSongWithArtist(artist.getArtistUri(), artist);
//        });
//
//        // Build and start the Kafka Streams instance
//        KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfig());
//        streams.start();
//        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//        return streams;
//    }
//
//    private Properties getStreamsConfig() {
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "artist-enrichment");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put("schema.registry.url", "http://schema-registry:8081"); // Ensure this URL matches your Schema Registry
//        return props;
//    }
//}
