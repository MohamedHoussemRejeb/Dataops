// src/main/java/.../quality/dto/QualityTestDto.java
package com.pfe.dataops.dataopsapi.quality.dto;

import java.util.List;

public record QualityTestDto(
        String id,
        String name,
        String description,
        String dimension,     // "freshness" | "nulls" | "uniqueness" | "validity" | "custom"
        String datasetUrn,
        String column,        // null si règle dataset-level
        String status,        // "OK" | "FAILED" | "WARN"
        Double currentValue,
        Double threshold,
        List<QualityTestHistoryPointDto> history
) {}
