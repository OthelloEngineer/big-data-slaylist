package big.data.ingestion.data;

import java.util.List;

public class Artist {
    private List<String> genres;
    private String name;
    private int popularity;
    private String uri;

    public Artist(List<String> genres, String name, int popularity, String uri) {
        this.genres = genres;
        this.name = name;
        this.popularity = popularity;
        this.uri = uri;
    }


}
