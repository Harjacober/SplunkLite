package com.kingjoe.splunklite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LogBuffer {

    private static final Logger log = LoggerFactory.getLogger(LogBuffer.class);
    static int BUFFER_SIZE = 1_000_00;
    Deque<LogEntry> buffer = new ConcurrentLinkedDeque<>();
    AtomicInteger bufferCount = new AtomicInteger(0);

    private final Flusher flusher;

    public LogBuffer(Flusher flusher) {
        this.flusher = flusher;
    }

    public void add(LogEntry entry) {
        if (bufferCount.incrementAndGet() >= BUFFER_SIZE) {
            buffer.removeLast();
            bufferCount.decrementAndGet();
        }
        buffer.addFirst(entry);
        if (bufferCount.get() >= 0.8 * BUFFER_SIZE) {
            flusher.flush(buffer);
        }
    }

//    BlockingDeque<LogEntry> buffer = new LinkedBlockingDeque<>(BUFFER_SIZE);
//    public void add(LogEntry entry) {
//        if (!buffer.offer(entry)) {
//            buffer.poll();
//            buffer.offer(entry);
//        }
//    }

    public List<LogEntry> search(Filter filter) {
        return buffer.stream().filter(entry -> filter.level() == null || entry.level().equals(filter.level()))
                .filter(entry -> filter.service() == null || entry.service().equalsIgnoreCase(filter.service()))
                .limit(filter.limit()).toList();
    }
}
