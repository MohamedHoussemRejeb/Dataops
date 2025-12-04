// src/main/java/com/pfe/dataops/dataopsapi/sources/HealthCheckResult.java
package com.pfe.dataops.dataopsapi.sources;

public record HealthCheckResult(
        SourceHealthStatus status,
        int latencyMs,
        String message
) {}
