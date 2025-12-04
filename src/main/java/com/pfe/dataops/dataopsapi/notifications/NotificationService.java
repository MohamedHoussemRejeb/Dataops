// src/main/java/com/pfe/dataops/dataopsapi/notifications/NotificationService.java
package com.pfe.dataops.dataopsapi.notifications;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // ---------- Envoi de base ----------

    public void sendToAll(NotificationDto dto) {
        // 🔍 log console pour vérifier l'envoi
        System.out.println(">>> [NOTIF] sendToAll /topic/notifications => " + dto);
        messagingTemplate.convertAndSend("/topic/notifications", dto);
    }

    public void sendToUser(String username, NotificationDto dto) {
        // 🔍 log console pour vérifier l'envoi ciblé
        System.out.println(">>> [NOTIF] sendToUser '" + username + "' /queue/notifications => " + dto);
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", dto);
    }

    // ---------- Helpers ETL RUN ---------- //

    private NotificationDto baseRunNotification(EtlRun run,
                                                String level,
                                                String title,
                                                String message) {

        // petit tronquage côté notif pour éviter d'envoyer 50 pages de logs au front
        String safeMsg = message;
        if (safeMsg != null && safeMsg.length() > 1000) {
            safeMsg = safeMsg.substring(0, 1000) + "…";
        }

        return NotificationDto.builder()
                .type("RUN")
                .level(level)
                .title(title)
                .message(safeMsg)
                .targetType("RUN")
                .targetId(run.getId() != null ? String.valueOf(run.getId()) : null)
                .username(run.getOwnerUsername()) // peut être null
                .createdAt(Instant.now())
                .build();
    }

    // Run créé (PENDING / en file d’attente)
    public void notifyEtlRunCreated(EtlRun run) {
        String msg = "Job " + run.getJobName() + " créé (status " + run.getStatus() + ").";
        NotificationDto dto = baseRunNotification(
                run,
                "INFO",
                "Run créé",
                msg
        );

        System.out.println(">>> [NOTIF] notifyEtlRunCreated runId=" + run.getId());
        sendToAll(dto);
        if (run.getOwnerUsername() != null && !run.getOwnerUsername().isBlank()) {
            sendToUser(run.getOwnerUsername(), dto);
        }
    }

    // Run démarré
    public void notifyEtlRunStarted(EtlRun run) {
        String msg = "Job " + run.getJobName() + " a démarré.";
        NotificationDto dto = baseRunNotification(
                run,
                "INFO",
                "Run démarré",
                msg
        );

        System.out.println(">>> [NOTIF] notifyEtlRunStarted runId=" + run.getId());
        sendToAll(dto);
        if (run.getOwnerUsername() != null && !run.getOwnerUsername().isBlank()) {
            sendToUser(run.getOwnerUsername(), dto);
        }
    }

    // Run terminé OK
    public void notifyEtlRunSucceeded(EtlRun run) {
        String msg = "Job " + run.getJobName() + " a terminé avec succès.";
        NotificationDto dto = baseRunNotification(
                run,
                "SUCCESS",
                "Run terminé",
                msg
        );

        System.out.println(">>> [NOTIF] notifyEtlRunSucceeded runId=" + run.getId());
        sendToAll(dto);
        if (run.getOwnerUsername() != null && !run.getOwnerUsername().isBlank()) {
            sendToUser(run.getOwnerUsername(), dto);
        }
    }

    // Run annulé
    public void notifyEtlRunCancelled(EtlRun run) {
        String msg = "Job " + run.getJobName() + " a été annulé.";
        NotificationDto dto = baseRunNotification(
                run,
                "WARNING",
                "Run annulé",
                msg
        );

        System.out.println(">>> [NOTIF] notifyEtlRunCancelled runId=" + run.getId());
        sendToAll(dto);
        if (run.getOwnerUsername() != null && !run.getOwnerUsername().isBlank()) {
            sendToUser(run.getOwnerUsername(), dto);
        }
    }

    // ---------- Run échoué ---------- //

    public void notifyEtlRunFailed(EtlRun run) {
        String error = (run.getMessage() != null && !run.getMessage().isBlank())
                ? run.getMessage()
                : "Erreur inconnue";

        String msg = "Le job " + run.getJobName() + " a échoué : " + error;

        NotificationDto dto = baseRunNotification(
                run,
                "ERROR",
                "Run en échec",
                msg
        );

        System.out.println(">>> [NOTIF] notifyEtlRunFailed runId=" + run.getId());
        // 1) Broadcast global
        sendToAll(dto);

        // 2) Notif ciblée (facultative)
        if (run.getOwnerUsername() != null && !run.getOwnerUsername().isBlank()) {
            sendToUser(run.getOwnerUsername(), dto);
        }
    }
}
