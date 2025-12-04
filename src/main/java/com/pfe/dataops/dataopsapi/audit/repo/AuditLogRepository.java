// src/main/java/com/pfe/dataops/dataopsapi/audit/repo/AuditLogRepository.java
package com.pfe.dataops.dataopsapi.audit.repo;

import com.pfe.dataops.dataopsapi.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // plus de @Query ici, on laisse juste le CRUD de base
}
