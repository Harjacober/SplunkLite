import com.kingjoe.splunklite.LogBuffer;
import com.kingjoe.splunklite.LogController;
import com.kingjoe.splunklite.LogEntry;
import com.kingjoe.splunklite.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogLevelTest {
    private LogController logController;

    @BeforeEach
    void setUp() {
        logController = new LogController(new LogBuffer());
    }

    @Test
    void ingestLogs() {
        //Given
        LogEntry entry = new LogEntry("message", "test-service", LogLevel.INFO, System.currentTimeMillis());

        //When
        logController.ingestLogs(entry);

        //Then
        List<LogEntry> results = logController.searchLogs(LogLevel.INFO, "test-service", 1).getBody();
        assertEquals(1, results.size());
        assertEquals("message", results.get(0).message());
    }

    @Test
    void testGcPressure() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long beforeUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long beforeGcCount = gcCount();

        logController.simulateLoad(1_000_000);

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
