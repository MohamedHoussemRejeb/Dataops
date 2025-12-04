// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceHealthDto.java
package com.pfe.dataops.dataopsapi.sources;

import java.time.Instant;
import java.util.List;

public record SourceHealthDto(
        Long id,
        String name,
        String vendor,
        String type,
        String licence,
        String owner,
        List<String> tags,
        String connectionUrl,

        String status,
        Instant lastCheckedAt,
        Instant lastSuccessAt,
        Long lastLatencyMs,
        String lastMessage
) {
    public static SourceHealthDto of(SoftwareSource s, SourceHealth h) {
        return new SourceHealthDto(
                s.getId(),
                s.getName(),
                s.getVendor(),
                s.getType(),
                s.getLicence(),
                s.getOwner(),
                s.getTags(),
                s.getConnectionUrl(),
                h != null && h.getStatus() != null ? h.getStatus().name() : "UNKNOWN",
                h != null ? h.getLastCheckedAt() : null,
                h != null ? h.getLastSuccessAt() : null,
                h != null ? h.getLastLatencyMs() : null,
                h != null ? h.getLastMessage() : null
        );
    }
}
