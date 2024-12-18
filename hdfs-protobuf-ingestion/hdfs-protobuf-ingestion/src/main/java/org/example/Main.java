package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {

        BlockingQueue<PlaylistOuterClass.Playlist> queue = new LinkedBlockingQueue<>();
        System.setProperty("hadoop.home.dir", "C:\\coding");
        try (KafkaAdapter kafkaAdapter = KafkaAdapter.newKafkaAdapter(queue, "PLAYLIST", "localhost:9092");
             HDFSSink hdfsSink = HDFSSink.newHDFSSink(queue, "hdfs://localhost:9000", "/playlist")) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down...");
                try {
                    kafkaAdapter.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    hdfsSink.close();
                }
            }));

            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}