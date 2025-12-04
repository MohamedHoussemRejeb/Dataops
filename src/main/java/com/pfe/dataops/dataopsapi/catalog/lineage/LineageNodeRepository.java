// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/LineageNodeRepository.java
package com.pfe.dataops.dataopsapi.catalog.lineage;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LineageNodeRepository extends JpaRepository<LineageNodeEntity, Long> {
    List<LineageNodeEntity> findByDatasetId(Long datasetId);
}
