package com.pfe.dataops.dataopsapi.backend.etl.live.dto;

import java.time.Instant;

public class LiveLogDto {
    private Instant ts;
    private String level; // "INFO" | "WARN" | "ERROR"
    private String msg;

    public LiveLogDto() {}

    public LiveLogDto(Instant ts, String level, String msg) {
        this.ts = ts;
        this.level = level;
        this.msg = msg;
    }

    public Instant getTs() { return ts; }
    public void setTs(Instant ts) { this.ts = ts; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
}
