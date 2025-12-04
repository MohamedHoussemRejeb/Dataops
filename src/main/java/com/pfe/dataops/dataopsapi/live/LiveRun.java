package com.pfe.dataops.dataopsapi.live;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_runs")
public class LiveRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String flowType;      // e.g. "TALEND_ARTICLE"

    @Column(nullable = false)
    private String status;        // RUNNING, SUCCESS, FAILED, PENDING

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer progress;     // 0..100
    private Boolean dryRun;       // optional flag

    // =====================
    //   Constructors
    // =====================

    public LiveRun() {
    }

    public LiveRun(String flowType, LocalDateTime startTime, String status) {
        this.flowType = flowType;
        this.startTime = startTime;
        this.status = status;
        this.progress = 0;
        this.dryRun = false;
    }

    // =====================
    //   Getters / Setters
    // =====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }
}
