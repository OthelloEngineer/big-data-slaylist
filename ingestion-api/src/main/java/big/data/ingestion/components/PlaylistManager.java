package big.data.ingestion.components;

import big.data.ingestion.data.PlaylistOuterClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlaylistManager {

    private final Map<String, PlaylistOuterClass.Playlist.Builder> inMemoryPlaylists = new ConcurrentHashMap<>();

    // Generate a deterministic ID for a playlist (hash of key fields)
    public String generatePlaylistId(PlaylistOuterClass.Playlist playlist) {
        return Integer.toHexString((playlist.getPlaylistName() + playlist.getFollowers()).hashCode());
    }

    public void storePlaylist(String playlistId, PlaylistOuterClass.Playlist playlist) {
        inMemoryPlaylists.put(playlistId, playlist.toBuilder());
    }

    public PlaylistOuterClass.Playlist.Builder getPlaylist(String playlistId) {
        return inMemoryPlaylists.get(playlistId);
    }

    public Map<String, PlaylistOuterClass.Playlist.Builder> getAllPlaylists() {
        return inMemoryPlaylists;
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
}