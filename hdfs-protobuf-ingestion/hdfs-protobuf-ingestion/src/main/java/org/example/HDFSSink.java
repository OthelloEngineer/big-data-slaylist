package org.example;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class HDFSSink implements AutoCloseable {
    private final BlockingQueue<PlaylistOuterClass.Playlist> queue;
    private final String hdfsUri;
    private final String hdfsPath;
    private final Thread workerThread;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private FileSystem fileSystem;
    private FSDataOutputStream outputStream;

    private HDFSSink(BlockingQueue<PlaylistOuterClass.Playlist> queue, String hdfsUri, String hdfsPath) {
        this.queue = queue;
        this.hdfsUri = hdfsUri;
        this.hdfsPath = hdfsPath;
        this.workerThread = new Thread(this::processQueue, "HDFSSink-Worker");
        initializeHDFS();
        this.workerThread.start();
    }

    public static HDFSSink newHDFSSink(BlockingQueue<PlaylistOuterClass.Playlist> queue, String hdfsUri, String hdfsPath) {
        return new HDFSSink(queue, hdfsUri, hdfsPath);
    }

    private void initializeHDFS() {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", hdfsUri);

            fileSystem = FileSystem.get(conf);

            Path path = new Path(hdfsPath);
            if (fileSystem.exists(path)) {
                outputStream = fileSystem.append(path);
            } else {
                outputStream = fileSystem.create(path, true);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize HDFS output stream.", e);
        }
    }
    private void processQueue() {
        try {
            while (running.get()) {
                PlaylistOuterClass.Playlist record = queue.take();
                writeRecord(record);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            flushAndClose();
        }
    }

    private void writeRecord(PlaylistOuterClass.Playlist record) {
        try {
            record.writeDelimitedTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flushAndClose() {
        try {
            if (outputStream != null) {
                outputStream.hsync();
                outputStream.close();
            }
            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        running.set(false);
        workerThread.interrupt();
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
