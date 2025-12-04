// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/LineageEdgeRepository.java
package com.pfe.dataops.dataopsapi.catalog.lineage;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LineageEdgeRepository extends JpaRepository<LineageEdgeEntity, Long> {
    List<LineageEdgeEntity> findByDatasetId(Long datasetId);
}
