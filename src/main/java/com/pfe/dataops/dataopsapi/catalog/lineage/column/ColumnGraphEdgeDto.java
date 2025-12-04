package com.pfe.dataops.dataopsapi.catalog.lineage.column;

public record ColumnGraphEdgeDto(
        String from,
        String to,
        String kind
) {}
