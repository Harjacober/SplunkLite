import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogBuffer {

    List<LogEntry> buffer = new ArrayList<>();

    public void add(LogEntry entry) {
        buffer.add(entry);
    }

    public List<LogEntry> search(Filter filter) {
        return buffer.stream()
                .filter(entry -> filter.level() == null || entry.level().equals(filter.level()))
                .filter(entry -> filter.service() == null || entry.service().equalsIgnoreCase(filter.service()))
                .limit(filter.limit())
                .toList();
    }
}
