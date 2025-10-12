package com.kingjoe.splunklite;

public record LogEntry(String message, String service, LogLevel level, long timestamp) {
}


