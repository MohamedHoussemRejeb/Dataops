package com.pfe.dataops.dataopsapi.backend.etl.live;


import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class AlertEvent {

    private String id;           // ex: "talend-<etlRunId>"
    private Instant timestamp;

    private String source;       // ex: "TALEND_JOB"
    private String flowType;     // ex: "TALEND_ARTICLE"
    private String severity;     // "INFO", "WARN", "ERROR", "CRITICAL"
    private String code;         // ex: "JOB_FAILED"
    private String message;      // message lisible

    private String runId;        // ex: "RUN-42"

    private DatasetInfo dataset; // optionnel, pour faire joli plus tard

    @Data
    public static class DatasetInfo {
        private String urn;
        private String name;
        private String domain;
        private String sensitivity;
        private List<String> legal;
    }
}

