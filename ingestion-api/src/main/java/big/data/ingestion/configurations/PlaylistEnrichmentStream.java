package big.data.ingestion.configurations;

import big.data.ingestion.data.Artist;
import big.data.ingestion.data.Playlist;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class PlaylistEnrichmentStream {

    private final ObjectMapper objectMapper;

    public PlaylistEnrichmentStream(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public KStream<String, String> enrichPlaylistsStream(StreamsBuilder streamsBuilder) {
        // Stream from the INGESTION topic
        KStream<String, String> ingestionStream = streamsBuilder.stream("INGESTION");

        // Create a KTable from the ARTIST topic
        KTable<String, String> artistTable = streamsBuilder.table("ARTIST");

        // Enrich playlists by joining with the artistTable
        KStream<String, String> enrichedPlaylistsStream = ingestionStream.mapValues(playlistJson -> {
            try {
                // Deserialize the Playlist
                Playlist playlist = objectMapper.readValue(playlistJson, Playlist.class);

                // Enrich each track with the corresponding Artist object
                playlist.getTracks().forEach(track -> {
                    String artistUri = track.getArtistUri();
                    // Join with the artistTable to get the artist JSON
                    artistTable.toStream().foreach((key, value) -> {
                        if (key.equals(artistUri)) {
                            Artist artist = null;
                            try {
                                artist = objectMapper.readValue(value, Artist.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            track.setArtist(artist);

                        }
                    });

                });

                // Serialize the enriched Playlist back to JSON
                return objectMapper.writeValueAsString(playlist);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to enrich playlist", e);
            }
        });

        // Publish enriched playlists to the PLAYLISTS topic
        enrichedPlaylistsStream.to("PLAYLISTS");

        return enrichedPlaylistsStream;
    }
}
