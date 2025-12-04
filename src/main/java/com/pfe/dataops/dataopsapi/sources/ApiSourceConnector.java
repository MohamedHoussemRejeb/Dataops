// src/main/java/com/pfe/dataops/dataopsapi/sources/ApiSourceConnector.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

@Component
public class ApiSourceConnector implements SourceConnector {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean supports(SoftwareSource source) {
        return "api".equalsIgnoreCase(source.getType());
    }

    @Override
    public HealthCheckResult test(SoftwareSource source) throws Exception {
        String url = source.getConnectionUrl(); // ⚠️ remplis ce champ dans ta base
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("No connectionUrl configured for API source " + source.getName());
        }

        Instant start = Instant.now();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            int latency = (int) Duration.between(start, Instant.now()).toMillis();

            if (response.getStatusCode().is2xxSuccessful()) {
                return new HealthCheckResult(
                        SourceHealthStatus.ONLINE,
                        latency,
                        "HTTP " + response.getStatusCode().value()
                );
            } else {
                return new HealthCheckResult(
                        SourceHealthStatus.OFFLINE,
                        latency,
                        "HTTP " + response.getStatusCode().value()
                );
            }
        } catch (Exception e) {
            int latency = (int) Duration.between(start, Instant.now()).toMillis();
            return new HealthCheckResult(
                    SourceHealthStatus.OFFLINE,
                    latency,
                    "Error: " + e.getMessage()
            );
        }
    }
}
