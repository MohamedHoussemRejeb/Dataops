package com.pfe.dataops.dataopsapi.realtime;

import java.time.Instant;
import java.util.Map;

public class RealtimeEvent {

    private String type;          // ex: RUN_STARTED, RUN_FAILED, ALERT_CREATED...
    private String source;        // ex: "runs-service", "alerts-service"
    private Instant timestamp;    // serveur
    private Map<String,Object> payload;  // contenu libre

    public RealtimeEvent() {}

    public RealtimeEvent(String type, String source, Map<String,Object> payload) {
        this.type = type;
        this.source = source;
        this.payload = payload;
        this.timestamp = Instant.now();
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}
