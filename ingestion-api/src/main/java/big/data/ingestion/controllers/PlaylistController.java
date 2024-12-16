package big.data.ingestion.controllers;

import big.data.ingestion.components.PlaylistManager;
import big.data.ingestion.data.PlaylistOuterClass;
import big.data.ingestion.services.ArtistProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingest")
public class PlaylistController {

    private final ArtistProducer artistProducer;
    private final PlaylistManager playlistManager;

    public PlaylistController(ArtistProducer artistProducer, PlaylistManager playlistManager) {
        this.artistProducer = artistProducer;
        this.playlistManager = playlistManager;
    }

    @PostMapping("/playlist")
    public ResponseEntity<String> ingestPlaylist(@RequestBody PlaylistOuterClass.Playlist playlist) {
        String playlistId = "playlist-" + playlist.hashCode();

        // Extract artist URIs and send to ARTISTID
        artistProducer.extractAndSendUniqueArtistUris(playlist.getSongsList(), "ARTISTID");

        // Store the playlist in memory
        playlistManager.storePlaylist(playlistId, playlist);

        return ResponseEntity.ok("Playlist uploaded and artist URIs sent for processing.");
    }
}