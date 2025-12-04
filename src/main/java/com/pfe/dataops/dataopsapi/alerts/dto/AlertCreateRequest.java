// src/main/java/com/pfe/dataops/dataopsapi/alerts/dto/CreateAlertRequest.java
package com.pfe.dataops.dataopsapi.alerts.dto;

import com.pfe.dataops.dataopsapi.alerts.AlertSeverity;
import com.pfe.dataops.dataopsapi.alerts.AlertSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertCreateRequest(
        @NotNull AlertSeverity severity,
        @NotNull AlertSource source,
        String runId,
        String flowType,
        @NotBlank @Size(max = 1000) String message,
        String dataset_urn
) {}
