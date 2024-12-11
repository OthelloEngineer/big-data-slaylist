package big.data.ingestion.data;

import java.util.List;

public class Playlist {

    private String name;
    private int pid;
    private long modifiedAt;
    private int numTracks;
    private int numAlbums;
    private int numFollowers;
    private List<Track> tracks;
    private long durationMs;
    private int numArtists;

    public Playlist(String name, int pid, long modifiedAt, int numTracks, int numAlbums, int numFollowers, List<Track> tracks, long durationMs, int numArtists) {
        this.name = name;
        this.pid = pid;
        this.modifiedAt = modifiedAt;
        this.numTracks = numTracks;
        this.numAlbums = numAlbums;
        this.numFollowers = numFollowers;
        this.tracks = tracks;
        this.durationMs = durationMs;
        this.numArtists = numArtists;
    }

    public int getPid() {
        return pid;
    }
}