// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/dto/EtlRunDto.java
package com.pfe.dataops.dataopsapi.backend.etl.dto;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import java.time.Instant;

public record EtlRunDto(
        String id,
        String flowType,
        EtlRun.Status status,
        Instant startTime,
        Instant endTime,
        Long rowsIn,
        Long rowsOut,
        Long rowsError,
        Long durationMs,
        String sourceFile,
        String message,
        EtlRun.Trigger trigger,
        Integer progress,
        Boolean dryRun,
        RetryParamsDto retryParams
) {
    public record RetryParamsDto(
            Instant requestedAt,
            String reason
    ) {}
}
