import java.time.Instant;
import java.util.List;

public class LogService {

    private final LogBuffer logBuffer;

    public LogService(LogBuffer logBuffer) {
        this.logBuffer = logBuffer;
    }

    public void ingestLogs(LogEntry logEntry) {
        logBuffer.add(logEntry);
    }

    public List<LogEntry> searchLogs(LogLevel level, String service, int limit) {
        return logBuffer.search(new Filter(level, service, limit));
    }

    public void simulateLoad(int size) {
        for (int i = 0; i < size; i++) {
            logBuffer.add(new LogEntry("message-" + i, "service-" + i, logLevel(i), Instant.now()));
        }
    }

    private LogLevel logLevel(int pos) {
        return LogLevel.values()[pos % LogLevel.values().length];
    }
}
