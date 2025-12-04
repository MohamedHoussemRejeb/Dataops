package com.pfe.dataops.dataopsapi.catalog.lineage.column;

public record ColumnGraphNodeDto(
        String id,
        String label,
        String type // "column" | "job" | "dataset"
) {}
