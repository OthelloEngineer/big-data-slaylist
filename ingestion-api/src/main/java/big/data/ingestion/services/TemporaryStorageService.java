package big.data.ingestion.services;

import big.data.ingestion.data.Playlist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemporaryStorageService {

    private final Map<Integer, Playlist> tempStorage = new ConcurrentHashMap<>();

    public void saveFilteredPlaylists(List<Playlist> playlists) {
        playlists.forEach(playlist -> tempStorage.put(playlist.getPid(), playlist));
    }

    public void savePlaylist(Playlist playlist) {
        tempStorage.put(playlist.getPid(), playlist);
    }

    public Playlist getPlaylistByPid(int pid) {
        return tempStorage.get(pid);
    }

}
