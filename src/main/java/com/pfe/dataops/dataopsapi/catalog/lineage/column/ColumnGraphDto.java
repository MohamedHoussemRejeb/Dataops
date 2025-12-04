package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import java.util.List;

public record ColumnGraphDto(
        List<ColumnGraphNodeDto> nodes,
        List<ColumnGraphEdgeDto> edges
) {}
