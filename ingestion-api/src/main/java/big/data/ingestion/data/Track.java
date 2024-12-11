package big.data.ingestion.data;

public class Track {
    private String artistName;
    private String trackUri;
    private String artistUri;
    private String trackName;
    private String albumUri;
    private long durationMs;
    private String albumName;

    public Track(int pos, String artistName, String trackUri, String artistUri, String trackName, String albumUri, long durationMs, String albumName) {
        this.artistName = artistName;
        this.trackUri = trackUri;
        this.artistUri = artistUri;
        this.trackName = trackName;
        this.albumUri = albumUri;
        this.durationMs = durationMs;
        this.albumName = albumName;
    }

    public String getArtistUri() {
        return artistUri;
    }
}