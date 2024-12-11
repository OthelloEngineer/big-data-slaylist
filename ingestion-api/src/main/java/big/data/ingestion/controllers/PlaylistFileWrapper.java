package big.data.ingestion.controllers;

import big.data.ingestion.data.Playlist;

import java.util.List;

import java.util.List;

public class PlaylistFileWrapper {

    private Info info; // Represents the "info" object at the top level
    private List<Playlist> playlists; // Represents the array of playlists

    // Getter for "info"
    public Info getInfo() {
        return info;
    }

    // Getter for "playlists"
    public List<Playlist> getPlaylists() {
        return playlists;
    }

    // Nested class for the "info" object
    public static class Info {
        private String generatedOn;
        private String slice;
        private String version;

        // Getters only for necessary fields
        public String getGeneratedOn() {
            return generatedOn;
        }

        public String getSlice() {
            return slice;
        }

        public String getVersion() {
            return version;
        }
    }
}
