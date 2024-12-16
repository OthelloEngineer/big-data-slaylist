package big.data.ingestion.components;

import big.data.ingestion.data.PlaylistOuterClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlaylistManager {
    private final Map<String, PlaylistOuterClass.Playlist.Builder> inMemoryPlaylists = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, PlaylistOuterClass.Playlist> kafkaTemplate;

    public PlaylistManager(KafkaTemplate<String, PlaylistOuterClass.Playlist> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void storePlaylist(String playlistId, PlaylistOuterClass.Playlist playlist) {
        inMemoryPlaylists.put(playlistId, playlist.toBuilder());
    }

    public void updateSongWithArtist(String artistUri, PlaylistOuterClass.Artist artist) {
        inMemoryPlaylists.values().forEach(playlist -> {
            playlist.getSongsBuilderList().forEach(song -> {
                if (song.hasArtist() && song.getArtist().getArtistUri().equals(artistUri)) {
                    song.setArtist(artist); // Update song with enriched artist data
                }
            });
        });
    }

    public boolean isPlaylistComplete(PlaylistOuterClass.Playlist.Builder playlist) {
        return playlist.getSongsList().stream()
                .allMatch(PlaylistOuterClass.Song::hasArtist);
    }

    public PlaylistOuterClass.Playlist finalizePlaylist(String playlistId) {
        PlaylistOuterClass.Playlist.Builder playlist = inMemoryPlaylists.remove(playlistId);
        return playlist != null ? playlist.build() : null;
    }

    public Map<String, PlaylistOuterClass.Playlist.Builder> getAllPlaylists() {
        return inMemoryPlaylists;
    }
}