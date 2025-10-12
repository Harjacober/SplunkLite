package com.kingjoe.splunklite;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Component
public class FileStorage {

    public static String WARM_STORAGE_DIR = "src/resources/persisted_logs";
    public String persist(String logs) throws IOException {
        String name = LocalDateTime.now().toString();
        Path file = Path.of(WARM_STORAGE_DIR, name);
        Files.createFile(file);
        Files.writeString(file, logs);

        return file.toString();
    }
}
