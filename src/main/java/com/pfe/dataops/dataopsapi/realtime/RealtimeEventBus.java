package com.pfe.dataops.dataopsapi.realtime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RealtimeEventBus {

    private final SimpMessagingTemplate simp;

    public RealtimeEventBus(SimpMessagingTemplate simp) {
        this.simp = simp;
    }

    public void publish(String type, String source, Map<String,Object> payload) {
        RealtimeEvent event = new RealtimeEvent(type, source, payload);
        // Tous les clients abonnés à /topic/events reçoivent ce message
        simp.convertAndSend("/topic/events", event);
    }

    // Helpers pratiques

    public void runStarted(Long runId, String flowType, String datasetUrn) {
        publish("RUN_STARTED", "runs-service", Map.of(
                "runId", runId,
                "flowType", flowType,
                "datasetUrn", datasetUrn
        ));
    }

    public void runFinished(Long runId, String status, String datasetUrn) {
        publish("RUN_FINISHED", "runs-service", Map.of(
                "runId", runId,
                "status", status,
                "datasetUrn", datasetUrn
        ));
    }

    public void alertCreated(Long alertId, String severity, String datasetUrn) {
        publish("ALERT_CREATED", "alerts-service", Map.of(
                "alertId", alertId,
                "severity", severity,
                "datasetUrn", datasetUrn
        ));
    }

    public void datasetUpdated(String urn, String status) {
        publish("DATASET_UPDATED", "catalog-service", Map.of(
                "urn", urn,
                "status", status
        ));
    }

    public void accessChanged(String datasetUrn) {
        publish("ACCESS_CHANGED", "governance-service", Map.of(
                "datasetUrn", datasetUrn
        ));
    }
}
