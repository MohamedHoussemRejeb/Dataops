package com.pfe.dataops.dataopsapi.catalog.lineage.column;

public record ColumnSearchItemDto(
        String urn,
        String dataset,
        String column,
        String type,
        String sensitivity
) {}
