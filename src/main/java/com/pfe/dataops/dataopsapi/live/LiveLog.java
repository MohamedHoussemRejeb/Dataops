package com.pfe.dataops.dataopsapi.live;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_logs")
public class LiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the associated LiveRun (LiveRun.id).
     * Simple string link is enough for now.
     */
    @Column(nullable = false)
    private String runId;

    /**
     * Timestamp of the log line.
     */
    private LocalDateTime ts;

    /**
     * Log level, e.g. INFO / WARN / ERROR.
     */
    private String level;

    /**
     * Log message content.
     */
    @Column(length = 4000)
    private String msg;

    // =====================
    //   Constructors
    // =====================

    public LiveLog() {
    }

    public LiveLog(String runId, LocalDateTime ts, String level, String msg) {
        this.runId = runId;
        this.ts = ts;
        this.level = level;
        this.msg = msg;
    }

    // =====================
    //   Getters / Setters
    // =====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
