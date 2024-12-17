package big.data.ingestion.services;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import com.google.protobuf.util.JsonFormat;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlaylistEnrichmentConsumer {

    private final PlaylistManager playlistManager;
    private final KafkaTemplate<String, String> kafkaTemplate; // Use String for value

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, String> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void consumeArtistData(PlaylistOuterClass.Artist artist) {
        processArtistData(artist);
    }

    public void consumePendingPlaylists() {
        System.out.println("Starting playlist enrichment...");
        playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
            if (playlistManager.isPlaylistComplete(playlistBuilder)) {
                PlaylistOuterClass.Playlist finalizedPlaylist = playlistManager.finalizePlaylist(playlistId);

                try {
                    // Convert Protobuf to JSON String
                    String jsonPlaylist = JsonFormat.printer().print(finalizedPlaylist);

                    // Send as String to Kafka
                    kafkaTemplate.send("PLAYLISTS", String.valueOf(playlistId), jsonPlaylist);
                    System.out.println("Finalized and published playlist: " + playlistId);
                } catch (Exception e) {
                    System.err.println("Failed to serialize playlist to JSON: " + e.getMessage());
                }
            }
        });
        System.out.println("Playlist enrichment completed.");
    }

    private void processArtistData(PlaylistOuterClass.Artist artist) {
        String artistUri = artist.getArtistUri();
        playlistManager.updateSongWithArtist(artistUri, artist);
        consumePendingPlaylists();
    }
}
