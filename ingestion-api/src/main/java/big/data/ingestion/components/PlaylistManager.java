package big.data.ingestion.components;

import big.data.ingestion.data.Playlist;
import big.data.ingestion.data.Artist;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlaylistManager {

    private final Map<Integer, Playlist.Builder> inMemoryPlaylists = new ConcurrentHashMap<>();

    public void storePlaylist(int playlistId, Playlist.Builder playlistBuilder) {
        inMemoryPlaylists.put(playlistId, playlistBuilder);
    }

    public void updateSongWithArtist(String artistUri, Artist artist) {
        inMemoryPlaylists.values().forEach(playlistBuilder -> {
            playlistBuilder.getSongs().forEach(songBuilder -> {
                if (songBuilder.getArtist() != null && artistUri.equals(songBuilder.getArtist().getArtistUri())) {
                    songBuilder.setArtist(artist); // Update the artist field
                }
            });
        });
    }

    public boolean isPlaylistComplete(Playlist.Builder playlistBuilder) {
        return playlistBuilder.getSongs().stream().allMatch(song -> song.getArtist() != null);
    }

    public Playlist finalizePlaylist(int playlistId) {
        Playlist.Builder playlistBuilder = inMemoryPlaylists.remove(playlistId);
        if (playlistBuilder != null) {
            return playlistBuilder.build();
        }
        return null;
    }

    public Map<Integer, Playlist.Builder> getAllPlaylists() {
        return inMemoryPlaylists;
    }
}
