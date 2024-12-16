package big.data.ingestion.services;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class PlaylistEnrichmentConsumer {

    private final PlaylistManager playlistManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, String> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void enrichArtistData(ConsumerRecord<String, String> record) {
        try {
            // Deserialize the ArtistResponse message
            PlaylistOuterClass.ArtistResponse artistResponse =
                    PlaylistOuterClass.ArtistResponse.parseFrom(record.value().getBytes());

            String artistUri = artistResponse.getArtistUri();
            PlaylistOuterClass.Artist artist = artistResponse.getArtist();

            // Iterate through all stored playlists and update matching tracks
            playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
                playlistManager.updateSongWithArtist(playlistId, artistUri, artist);

                // Send enriched playlist to PLAYLISTS topic (optional: finalize immediately)
                PlaylistOuterClass.Playlist enrichedPlaylist = playlistManager.finalizePlaylist(playlistId);
                try {
                    String playlistJson = objectMapper.writeValueAsString(enrichedPlaylist);
                    kafkaTemplate.send("PLAYLISTS", playlistJson);
                    System.out.println("Enriched playlist sent to PLAYLISTS: " + playlistId);
                } catch (Exception e) {
                    System.err.println("Failed to serialize playlist: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("Failed to process artist data: " + e.getMessage());
        }
    }
}