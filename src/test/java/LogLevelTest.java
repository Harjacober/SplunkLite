import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogLevelTest {
    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService(new LogBuffer());
    }

    @Test
    void ingestLogs() {
        //Given
        LogEntry entry = new LogEntry("message", "test-service", LogLevel.INFO, Instant.now());

        //When
        logService.ingestLogs(entry);

        //Then
        List<LogEntry> results = logService.searchLogs(LogLevel.INFO, "test-service", 1);
        assertEquals(1, results.size());
        assertEquals("message", results.get(0).message());
    }

    @Test
    void testGcPressure() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long beforeUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long beforeGcCount = gcCount();

        logService.simulateLoad(1_000_000);

        long afterUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long afterGcCount = gcCount();

        System.out.println("Heap before: " + beforeUsed / 1024 / 1024 + " MB");
        System.out.println("Heap after: " + afterUsed / 1024 / 1024 + " MB");
        System.out.println("GC Count before: " + beforeGcCount);
        System.out.println("GC Count after: " + afterGcCount);
    }

    private long gcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionCount)
                .sum();
    }
}
