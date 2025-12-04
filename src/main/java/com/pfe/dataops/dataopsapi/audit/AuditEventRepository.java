// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditEventRepository.java
package com.pfe.dataops.dataopsapi.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {

    // Timeline: derniers 500
    List<AuditEventEntity> findTop500ByOrderByAtDesc();

    // Filtre temporel (optionnel)
    List<AuditEventEntity> findAllByAtBetweenOrderByAtDesc(Instant from, Instant to);
}
