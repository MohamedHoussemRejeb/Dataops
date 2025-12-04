// src/main/java/com/pfe/dataops/dataopsapi/alerts/AlertWebsocketNotifier.java
package com.pfe.dataops.dataopsapi.alerts;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertWebsocketNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envoie une alerte fraîchement créée vers les clients WebSocket.
     */
    public void notifyAlertCreated(Alert alert) {
        // ⚠️ ADAPTE la destination au topic que ton front écoute :
        //  - si ton Angular est abonné à /topic/alerts => garde tel quel
        //  - si c’est /topic/notifications => remplace ci-dessous
        messagingTemplate.convertAndSend("/topic/alerts", new AlertCreatedPayload(alert));
    }

    /**
     * Payload simplifié envoyé au front (évite d’envoyer toute l’entity JPA)
     */
    public record AlertCreatedPayload(
            String id,
            String createdAt,
            String severity,
            String source,
            String message,
            String runId,
            String flowType,
            String datasetUrn
    ) {
        public AlertCreatedPayload(Alert a) {
            this(
                    a.getId(),
                    a.getCreatedAt() != null ? a.getCreatedAt().toString() : null,
                    a.getSeverity() != null ? a.getSeverity().name() : null,
                    a.getSource() != null ? a.getSource().name() : null,
                    a.getMessage(),
                    a.getRunId(),
                    a.getFlowType(),
                    a.getDatasetUrn()
            );
        }
    }
}
