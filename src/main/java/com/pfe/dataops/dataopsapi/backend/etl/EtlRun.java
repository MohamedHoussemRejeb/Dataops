// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRun.java
package com.pfe.dataops.dataopsapi.backend.etl;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "etl_runs")
public class EtlRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(name = "rows_in")
    private Long rowsIn;

    @Column(name = "rows_out")
    private Long rowsOut;

    @Column(name = "rows_error")
    private Long rowsError;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_mode", length = 16)
    private Trigger trigger;

    @Column(name = "progress_pct")
    private Integer progress;

    @Column(name = "dry_run")
    private Boolean dryRun;

    @Column(name = "source_file")
    private String sourceFile;

    // ✅ on aligne avec la DB : varchar(1000)
    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "retry_requested_at")
    private Instant retryRequestedAt;

    @Column(name = "retry_reason", length = 500)
    private String retryReason;

    // 🔥 NEW: propriétaire du run (username Keycloak)
    @Column(name = "owner_username", length = 128)
    private String ownerUsername;

    public enum Status {
        PENDING, RUNNING, SUCCESS, FAILED, CANCELLED
    }

    public enum Trigger {
        SCHEDULED, MANUAL, RETRY
    }

    // ---- Getters / Setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getRowsIn() { return rowsIn; }
    public void setRowsIn(Long rowsIn) { this.rowsIn = rowsIn; }

    public Long getRowsOut() { return rowsOut; }
    public void setRowsOut(Long rowsOut) { this.rowsOut = rowsOut; }

    public Long getRowsError() { return rowsError; }
    public void setRowsError(Long rowsError) { this.rowsError = rowsError; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Trigger getTrigger() { return trigger; }
    public void setTrigger(Trigger trigger) { this.trigger = trigger; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Boolean getDryRun() { return dryRun; }
    public void setDryRun(Boolean dryRun) { this.dryRun = dryRun; }

    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }

    public String getMessage() { return message; }

    // ✅ tronquage automatique à 1000 caractères pour éviter l'erreur SQL
    public void setMessage(String message) {
        if (message != null && message.length() > 1000) {
            this.message = message.substring(0, 1000);
        } else {
            this.message = message;
        }
    }

    public Instant getRetryRequestedAt() { return retryRequestedAt; }
    public void setRetryRequestedAt(Instant retryRequestedAt) { this.retryRequestedAt = retryRequestedAt; }

    public String getRetryReason() { return retryReason; }
    public void setRetryReason(String retryReason) { this.retryReason = retryReason; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
}
