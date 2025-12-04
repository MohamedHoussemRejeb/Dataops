// src/main/java/com/pfe/dataops/dataopsapi/alerts/dto/AlertDto.java
package com.pfe.dataops.dataopsapi.alerts.dto;

import com.pfe.dataops.dataopsapi.alerts.AlertSeverity;
import com.pfe.dataops.dataopsapi.alerts.AlertSource;

import java.time.Instant;

// On renvoie dataset_urn (snake_case) comme l’attend l’UI Angular
public record AlertDto(
        String id,
        Instant createdAt,
        AlertSeverity severity,
        AlertSource source,
        String runId,
        String flowType,
        String message,
        boolean acknowledged,
        String dataset_urn
) {}
