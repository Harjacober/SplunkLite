import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogBuffer logBuffer;

    public LogController(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    @PostMapping
    public void ingestLogs(@RequestBody LogEntry logEntry) {
        logBuffer.add(logEntry);
    }

    @PostMapping("/search")
    public List<LogEntry> searchLogs(@RequestParam LogLevel level, @RequestParam String service, @RequestParam int limit) {
        return logBuffer.search(new Filter(level, service, limit));
    }

    @PostMapping("/simulate/load")
    public void simulateLoad(int size) {
        for (int i = 0; i < size; i++) {
            logBuffer.add(new LogEntry("message-" + i, "service-" + i, logLevel(i), Instant.now()));
        }
    }

    private LogLevel logLevel(int pos) {
        return LogLevel.values()[pos % LogLevel.values().length];
    }
}
