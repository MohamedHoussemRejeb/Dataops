package com.pfe.dataops.dataopsapi.alerts;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AlertSeverity severity; // INFO/WARN/ERROR/CRITICAL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AlertSource source;     // SLA/RUN/SYSTEM

    @Column
    private String runId;

    @Column
    private String flowType;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean acknowledged = false;

    @Column(name = "dataset_urn")
    private String datasetUrn;

    public Alert() {
        // vide volontairement
    }

    // Avant insert : on remplit id + createdAt si pas fournis
    @PrePersist
    void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // ---------------------------------
    // Getters / Setters (très important)
    // ---------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public AlertSource getSource() {
        return source;
    }

    public void setSource(AlertSource source) {
        this.source = source;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getDatasetUrn() {
        return datasetUrn;
    }

    public void setDatasetUrn(String datasetUrn) {
        this.datasetUrn = datasetUrn;
    }
}
