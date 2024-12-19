package big.data.ingestion.services;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.Artist;
import big.data.ingestion.data.Playlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaylistEnrichmentConsumer {

    private final PlaylistManager playlistManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Set<String> processedArtistUris = ConcurrentHashMap.newKeySet();
    private final Set<String> pendingArtistUris = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, String> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void consumeArtistData(Artist artist) {
        String artistUri = artist.getArtistUri();
        System.out.println("Processing artist: " + artistUri);

        if (pendingArtistUris.remove(artistUri)) {
            processedArtistUris.add(artistUri);
            playlistManager.updateSongWithArtist(artistUri, artist);
            finalizePendingPlaylists();
        }
    }

    public void addPendingArtistUri(String artistUri) {
        pendingArtistUris.add(artistUri);
    }

    public boolean isAllArtistsProcessed(Set<String> artistUris) {
        return processedArtistUris.containsAll(artistUris);
    }

    public void finalizePendingPlaylists() {
        playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
            if (playlistManager.isPlaylistComplete(playlistBuilder)) {
                try {
                    Playlist finalizedPlaylist = playlistBuilder.build();
                    String jsonPlaylist = objectMapper.writeValueAsString(finalizedPlaylist);
                    kafkaTemplate.send("PLAYLISTS", String.valueOf(playlistId), jsonPlaylist);
                    System.out.println("Finalized and published playlist: " + playlistId);
                    playlistManager.finalizePlaylist(playlistId);
                } catch (Exception e) {
                    System.err.println("Error finalizing playlist: " + e.getMessage());
                }
            }
        });
    }
}
