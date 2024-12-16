package big.data.ingestion.services;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlaylistEnrichmentConsumer {

    private final PlaylistManager playlistManager;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, String> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void enrichArtistData(ConsumerRecord<String, String> record) {
        try {
            PlaylistOuterClass.ArtistResponse artistResponse =
                    PlaylistOuterClass.ArtistResponse.parseFrom(record.value().getBytes());

            String artistUri = artistResponse.getArtistUri();
            PlaylistOuterClass.Artist artist = artistResponse.getArtist();

            playlistManager.getInMemoryPlaylists().forEach((playlistId, playlist) -> {
                playlistManager.updateSongWithArtist(playlistId, artistUri, artist);

                // If all artists are enriched, send to PLAYLISTS
                PlaylistOuterClass.Playlist finalPlaylist = playlistManager.finalizePlaylist(playlistId);
                kafkaTemplate.send("PLAYLISTS", finalPlaylist.toByteArray());
                System.out.println("Enriched playlist sent to PLAYLISTS: " + playlistId);
            });
        } catch (Exception e) {
            System.err.println("Failed to process artist data: " + e.getMessage());
        }
    }
}