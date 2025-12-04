// src/main/java/com/pfe/dataops/dataopsapi/sources/FileSourceConnector.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

@Component
public class FileSourceConnector implements SourceConnector {

    @Override
    public boolean supports(SoftwareSource source) {
        return "file".equalsIgnoreCase(source.getType());
    }

    @Override
    public HealthCheckResult test(SoftwareSource source) {
        String path = source.getConnectionUrl();
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("No connectionUrl (file path) for source " + source.getName());
        }

        Instant start = Instant.now();
        File f = new File(path);
        boolean ok = f.exists() && f.canRead();
        int latency = (int) Duration.between(start, Instant.now()).toMillis();

        if (ok) {
            return new HealthCheckResult(
                    SourceHealthStatus.ONLINE,
                    latency,
                    "Path exists & readable"
            );
        } else {
            return new HealthCheckResult(
                    SourceHealthStatus.OFFLINE,
                    latency,
                    "Path missing or not readable"
            );
        }
    }
}
