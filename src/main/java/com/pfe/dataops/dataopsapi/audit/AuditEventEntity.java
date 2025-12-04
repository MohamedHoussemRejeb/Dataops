package com.pfe.dataops.dataopsapi.audit;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditEventEntity.java
@Entity
@Table(name = "audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // use "at" as creation timestamp
    @CreationTimestamp
    @Column(name = "at", nullable = false, updatable = false)
    private Instant at;

    @Column(nullable = false, length = 50)
    private String kind;

    @Column(name = "user_name", nullable = false, length = 200)
    private String userName;

    @Column(name = "user_email", length = 200)
    private String userEmail;

    // your data is currently in "meta_json", not "meta"
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta_json", columnDefinition = "jsonb")
    private JsonNode meta;
}

