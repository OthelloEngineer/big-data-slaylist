package big.data.ingestion.components;

import big.data.ingestion.data.PlaylistOuterClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlaylistManager {

    private final Map<String, PlaylistOuterClass.Playlist.Builder> inMemoryPlaylists = new ConcurrentHashMap<>();

    public void storePlaylist(String playlistId, PlaylistOuterClass.Playlist playlist) {
        inMemoryPlaylists.put(playlistId, playlist.toBuilder());
    }

    public PlaylistOuterClass.Playlist.Builder getPlaylist(String playlistId) {
        return inMemoryPlaylists.get(playlistId);
    }

    public void updateSongWithArtist(String playlistId, String artistUri, PlaylistOuterClass.Artist artist) {
        PlaylistOuterClass.Playlist.Builder playlist = inMemoryPlaylists.get(playlistId);
        if (playlist != null) {
            playlist.getSongsBuilderList().forEach(song -> {
                if (song.hasArtist() && song.getArtist().getArtistUri().equals(artistUri)) {
                    song.setArtist(artist);
                }
            });
        }
    }

    public PlaylistOuterClass.Playlist finalizePlaylist(String playlistId) {
        return inMemoryPlaylists.remove(playlistId).build();
    }

    public Map<String, PlaylistOuterClass.Playlist.Builder> getInMemoryPlaylists() {
        return inMemoryPlaylists;
    }
}