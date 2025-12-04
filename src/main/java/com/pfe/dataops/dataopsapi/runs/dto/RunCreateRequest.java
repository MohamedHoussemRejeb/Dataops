// src/main/java/com/pfe/dataops/dataopsapi/runs/dto/RunCreateRequest.java
package com.pfe.dataops.dataopsapi.runs.dto;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import jakarta.validation.constraints.*;
import java.time.Instant;

public record RunCreateRequest(
        @NotBlank String jobName,
        @NotNull EtlRun.Status status,   // <-- même enum que l’entité
        @Min(0) Long rowsIn,
        @Min(0) Long rowsOut,
        @Min(0) Long durationMs,
        Instant startedAt,
        Instant finishedAt
) {}
