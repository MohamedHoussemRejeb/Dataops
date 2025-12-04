// src/main/java/com/pfe/dataops/dataopsapi/governance/access/AccessMatrixRepository.java
package com.pfe.dataops.dataopsapi.governance.access;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessMatrixRepository extends JpaRepository<AccessMatrixEntryEntity, Long> {
    // For now we use in-memory filtering in service (simpler)
}
