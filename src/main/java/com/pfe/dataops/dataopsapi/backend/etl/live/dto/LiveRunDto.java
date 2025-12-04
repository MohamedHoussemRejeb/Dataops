package com.pfe.dataops.dataopsapi.backend.etl.live.dto;

import java.time.Instant;

public class LiveRunDto {
    private String id;
    private String flowType;     // ARTICLES, COMMANDES...
    private String status;       // RUNNING | SUCCESS | FAILED | PENDING
    private Instant startTime;
    private Integer progress;    // 0..100
    private Boolean dryRun;      // facultatif
    private String message;

    public LiveRunDto() {}

    public LiveRunDto(String id,
                      String flowType,
                      String status,
                      Instant startTime,
                      Integer progress,
                      Boolean dryRun,
                      String message) {
        this.id = id;
        this.flowType = flowType;
        this.status = status;
        this.startTime = startTime;
        this.progress = progress;
        this.dryRun = dryRun;
        this.message = message;
    }

    public String getId() { return id; }
    public String getFlowType() { return flowType; }
    public String getStatus() { return status; }
    public Instant getStartTime() { return startTime; }
    public Integer getProgress() { return progress; }
    public Boolean getDryRun() { return dryRun; }
    public String getMessage() { return message; }

    public void setId(String id) { this.id = id; }
    public void setFlowType(String flowType) { this.flowType = flowType; }
    public void setStatus(String status) { this.status = status; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public void setProgress(Integer progress) { this.progress = progress; }
    public void setDryRun(Boolean dryRun) { this.dryRun = dryRun; }
    public void setMessage(String message) { this.message = message; }
}

