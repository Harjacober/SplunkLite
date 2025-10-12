package com.kingjoe.splunklite;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogBuffer logBuffer;
    private Executor executor = Executors.newFixedThreadPool(16);

    public LogController(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @PostMapping
    public ResponseEntity<String> ingestLogs(@RequestBody LogEntry logEntry) {
        logBuffer.add(logEntry);
        return ResponseEntity.ok("log added");
    }

    @GetMapping("/search")
    public ResponseEntity<List<LogEntry>> searchLogs(
            @RequestParam(required = false) LogLevel level,
            @RequestParam(required = false) String service,
            @RequestParam(required = false, defaultValue = "1") int limit,
            @RequestParam(required = false, defaultValue = "0") long from
    ) {
        return ResponseEntity.ok(logBuffer.search(new Filter(level, service, limit)));
    }

    @PostMapping("/simulate/load")
    public ResponseEntity<String> simulateLoad(@RequestParam long size) {
        //        executor.execute(() -> {
        //            for (long i = 0; i < size; i++) {
        //                logBuffer.add(new LogEntry("message-" + i, "service-" + i, logLevel(i), Instant.now()));
        //            }
        //        });
        for (long i = 0; i < size; i++) {
            long start = System.nanoTime();
            logBuffer.add(new LogEntry("message-" + i, "service-" + i, logLevel(i), System.currentTimeMillis()));
            System.out.printf("Item %s took %s ns%n", i, System.nanoTime() - start);
        }
        return ResponseEntity.ok("Success");
    }

    private LogLevel logLevel(long pos) {
        return LogLevel.values()[Math.toIntExact(pos % LogLevel.values().length)];
    }
}
