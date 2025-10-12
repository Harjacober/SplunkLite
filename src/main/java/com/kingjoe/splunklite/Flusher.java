package com.kingjoe.splunklite;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class Flusher {

    private final ObjectMapper objectMapper;
    private final FileStorage fileStorage;

    public Flusher(ObjectMapper objectMapper,
                   FileStorage fileStorage
    ) {
        this.objectMapper = objectMapper;
        this.fileStorage = fileStorage;
    }

    public void flush(Deque<LogEntry> buffer) {

    }

    public void flush(BlockingDeque<LogEntry> buffer) {
        List<LogEntry> logs = new ArrayList<>();
        int toFlush = buffer.size() / 5; //20%
        LogEntry first = null;
        LogEntry last = null;
        for (int i = 0; i < toFlush; i++) {
            if (i == 0) {
                last = buffer.peek();
            }
            if (i == toFlush - 1) {
                first = buffer.peek();
            }
            logs.add(buffer.poll());
        }


        // write logs to file
        String filePath = null;
        try {
            filePath = fileStorage.persist(objectMapper.writeValueAsString(logs));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LogSegment segment = new LogSegment(filePath, first.timestamp(), last.timestamp());

        // update index file to record this entry
        Index index = readIndexFile();
        index.segments().add(segment);
    }
}
