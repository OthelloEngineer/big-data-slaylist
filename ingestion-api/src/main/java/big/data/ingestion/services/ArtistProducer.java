package big.data.ingestion.services;

import big.data.ingestion.data.PlaylistOuterClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ArtistProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ArtistProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Set<String> extractAndSendUniqueArtistUris(List<PlaylistOuterClass.Song> songs, String topic) {
        Set<String> uniqueArtistUris = new HashSet<>();
        for (PlaylistOuterClass.Song song : songs) {
            if (song.hasArtist()) {
                String artistUri = song.getArtist().getArtistUri();
                if (uniqueArtistUris.add(artistUri)) {
                    kafkaTemplate.send(topic, artistUri);
                    System.out.println("Sent artist URI: " + artistUri);
                }
            }
        }
        return uniqueArtistUris;
    }
}