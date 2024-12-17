package big.data.ingestion.services;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import com.google.protobuf.util.JsonFormat;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaylistEnrichmentConsumer {

    private final PlaylistManager playlistManager;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Set<String> processedArtistUris = ConcurrentHashMap.newKeySet(); // Track processed artist URIs
    private final Set<String> pendingArtistUris = ConcurrentHashMap.newKeySet();   // Track pending artist URIs

    public PlaylistEnrichmentConsumer(PlaylistManager playlistManager, KafkaTemplate<String, String> kafkaTemplate) {
        this.playlistManager = playlistManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "ARTIST", groupId = "playlist-enrichment")
    public void consumeArtistData(PlaylistOuterClass.Artist artist) {
        System.out.println("Processing artist: " + artist.getArtistUri());
        processedArtistUris.add(artist.getArtistUri());
        pendingArtistUris.remove(artist.getArtistUri()); // Mark this artist as processed
        playlistManager.updateSongWithArtist(artist.getArtistUri(), artist);
    }

    public boolean isAllArtistsProcessed(Set<String> artistIds) {
        return processedArtistUris.containsAll(artistIds);
    }

    public boolean addPendingArtistUri(String artistUri) {
        return pendingArtistUris.add(artistUri); // Prevent duplicates
    }

    public void finalizePlaylistsIfComplete() {
        playlistManager.getAllPlaylists().forEach((playlistId, playlistBuilder) -> {
            if (playlistManager.isPlaylistComplete(playlistBuilder)) {
                try {
                    PlaylistOuterClass.Playlist finalizedPlaylist = playlistManager.finalizePlaylist(playlistId);
                    String jsonPlaylist = JsonFormat.printer().print(finalizedPlaylist);
                    kafkaTemplate.send("PLAYLISTS", String.valueOf(playlistId), jsonPlaylist);
                    System.out.println("Finalized and published playlist: " + playlistId);
                } catch (Exception e) {
                    System.err.println("Error finalizing playlist: " + e.getMessage());
                }
            }
        });
    }
}
