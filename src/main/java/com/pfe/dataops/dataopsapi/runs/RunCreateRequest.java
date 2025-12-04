package com.pfe.dataops.dataopsapi.runs;

import java.time.Instant;

public class RunCreateRequest {
    private String jobName;
    private String status;
    private Long rowsIn;
    private Long rowsOut;
    private Long durationMs;
    private Instant startedAt;
    private Instant finishedAt;

    // getters/setters
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getRowsIn() { return rowsIn; }
    public void setRowsIn(Long rowsIn) { this.rowsIn = rowsIn; }
    public Long getRowsOut() { return rowsOut; }
    public void setRowsOut(Long rowsOut) { this.rowsOut = rowsOut; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
}
