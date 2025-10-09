import java.time.Instant;

public record LogEntry (String message, String service, LogLevel level, Instant timestamp){}


