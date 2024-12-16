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
    private final KafkaTemplate<String, PlaylistOuterClass.Playlist> kafkaTemplate;

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, PlaylistOuterClass.Playlist> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void consumeArtistData(ConsumerRecord<String, PlaylistOuterClass.Artist> record) {
        // Deserialize artist data
        PlaylistOuterClass.Artist artist = record.value();
        String artistUri = artist.getArtistUri();

        // Update in-memory playlists with the received artist data
        playlistManager.updateSongWithArtist(artistUri, artist);

        // Check all playlists to see if they are complete and finalize them
        playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
            if (playlistManager.isPlaylistComplete(playlistBuilder)) {
                // Finalize and remove the playlist
                PlaylistOuterClass.Playlist finalizedPlaylist = playlistManager.finalizePlaylist(playlistId);

                // Publish the finalized playlist to the PLAYLISTS topic
                kafkaTemplate.send("PLAYLISTS", playlistId, finalizedPlaylist);
                System.out.println("Finalized and published playlist: " + playlistId);
            }
        });
    }
}