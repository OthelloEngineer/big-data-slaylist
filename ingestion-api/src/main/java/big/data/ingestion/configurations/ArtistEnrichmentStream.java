package big.data.ingestion.configurations;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ArtistEnrichmentStream {

    private final PlaylistManager playlistManager;

    public ArtistEnrichmentStream(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    @Bean
    public KafkaStreams artistEnrichmentStreams() {
        StreamsBuilder builder = new StreamsBuilder();

        // Consume artist data from ARTIST topic
        KStream<String, PlaylistOuterClass.Artist> artistStream = builder.stream("ARTIST");

        // Process each artist and update in-memory playlists
        artistStream.foreach((key, artist) -> {
            System.out.println("Enriching playlists with artist: " + artist.getArtistUri());
            playlistManager.updateSongWithArtist(artist.getArtistUri(), artist);

            // Check for completed playlists
            playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
                if (playlistManager.isPlaylistComplete(playlistBuilder)) {
                    PlaylistOuterClass.Playlist finalizedPlaylist = playlistManager.finalizePlaylist(playlistId);
                    System.out.println("Playlist finalized: " + playlistId);
                    // Publish to PLAYLISTS topic
                    // Use KafkaTemplate or KafkaProducer here
                    System.out.println("Enriched Playlist: " + finalizedPlaylist);
                }
            });
        });

        // Start Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), getStreamsConfig());
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        return streams;
    }

    private Properties getStreamsConfig() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ingestion-api");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}
