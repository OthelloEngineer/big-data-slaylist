package org.example;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;

public class KafkaAdapter implements AutoCloseable {
    private final BlockingQueue<PlaylistOuterClass.Playlist> queue;
    private final KafkaStreams stream;

    private KafkaAdapter(BlockingQueue<PlaylistOuterClass.Playlist> queue, KafkaStreams stream) {
        this.queue = queue;
        this.stream = stream;
    }

    public static KafkaAdapter newKafkaAdapter(BlockingQueue<PlaylistOuterClass.Playlist> queue, String topic, String bootstrapServers) {
        StreamsBuilder builder = new StreamsBuilder();

        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hdfs-protobuf-ingestor");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());

        // Create and configure the Protobuf serde
        KafkaProtobufSerde<PlaylistOuterClass.Playlist> playlistSerde = new KafkaProtobufSerde<>();
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, playlistSerde.getClass().getName());

        // Create the KStream using the provided topic
        KStream<Integer, PlaylistOuterClass.Playlist> stream = builder.stream(
                topic,
                Consumed.with(Serdes.Integer(), playlistSerde)
        );

        stream.foreach((key, value) -> {
            if (value != null) {
                try {
                    queue.put(value);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Topology topology = builder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.start();

        // Returning so the instantiator can hold onto the stream and close it later
        return new KafkaAdapter(queue, kafkaStreams);
    }

    @Override
    public void close() throws Exception {
        stream.close();
    }
}
