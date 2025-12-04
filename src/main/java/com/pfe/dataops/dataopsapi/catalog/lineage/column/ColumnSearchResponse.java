package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import java.util.List;

public record ColumnSearchResponse(
        List<ColumnSearchItemDto> items
) {}
