// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/column/DatasetColumnRepository.java
package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetColumnRepository extends JpaRepository<DatasetColumnEntity, Long> {

    List<DatasetColumnEntity> findByDataset_Id(Long datasetId);

    List<DatasetColumnEntity> findByDataset_UrnAndNameIgnoreCase(String urn, String name);
    List<DatasetColumnEntity> findByNameContainingIgnoreCase(String namePart);
}
