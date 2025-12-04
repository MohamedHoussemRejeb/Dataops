// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/column/ColumnEdgeCrudDto.java
package com.pfe.dataops.dataopsapi.catalog.lineage.column;

public record ColumnEdgeCrudDto(
        Long id,
        Long fromColumnId,
        Long toColumnId,
        String kind
) {}
