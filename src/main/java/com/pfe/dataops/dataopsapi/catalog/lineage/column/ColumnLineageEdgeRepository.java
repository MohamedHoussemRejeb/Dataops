// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/column/ColumnLineageEdgeRepository.java
package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnLineageEdgeRepository extends JpaRepository<ColumnLineageEdgeEntity, Long> {

    List<ColumnLineageEdgeEntity> findByFromColumn_Dataset_IdOrToColumn_Dataset_Id(
            Long fromDatasetId,
            Long toDatasetId
    );
    List<ColumnLineageEdgeEntity> findByFromColumnOrToColumn(
            DatasetColumnEntity from,
            DatasetColumnEntity to
    );
}
