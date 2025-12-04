// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/column/DatasetColumnDto.java
package com.pfe.dataops.dataopsapi.catalog.lineage.column;

public record DatasetColumnDto(
        Long id,
        String name,
        String type,
        String sensitivity
) {}
