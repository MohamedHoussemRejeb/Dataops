// src/main/java/com/pfe/dataops/dataopsapi/audit/entity/AuditLog.java
package com.pfe.dataops.dataopsapi.audit.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 200)
    private String actor;         // username / service

    @Column(nullable = false, length = 100)
    private String action;        // CONFIG_UPDATE, DATASET_VIEW, RUN_TRIGGER, ...

    @Column(name = "resource_type", length = 100)
    private String resourceType;  // DATASET, WORKFLOW, SETTINGS, USER...

    @Column(name = "resource_id", length = 500)
    private String resourceId;    // urn, job name, id...

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payloadBefore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payloadAfter;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payloadDiff;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;
}
