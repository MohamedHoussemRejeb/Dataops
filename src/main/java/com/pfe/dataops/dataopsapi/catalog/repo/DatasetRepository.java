// src/main/java/com/pfe/dataops/dataopsapi/catalog/repo/DatasetRepository.java
package com.pfe.dataops.dataopsapi.catalog.repo;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.enums.LoadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<DatasetEntity, Long> {

    /**
     * Pour le KPI % SLA respectés :
     * on approxime "SLA respecté" = dernier run SUCCESS
     */
    long countByLastStatus(LoadStatus lastStatus);

    /**
     * Optionnel, utile si tu veux afficher "datasets à risque"
     * (tout ce qui n'est pas SUCCESS).
     */
    long countByLastStatusNot(LoadStatus lastStatus);
    boolean existsByUrn(String urn);
    Optional<DatasetEntity> findByUrn(String urn);
    boolean existsByNameIgnoreCase(String name);
}
