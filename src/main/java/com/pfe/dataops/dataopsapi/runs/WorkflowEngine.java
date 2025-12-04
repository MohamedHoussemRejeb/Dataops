// src/main/java/com/pfe/dataops/dataopsapi/runs/WorkflowEngine.java
package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.notifications.NotificationService;   // ⭐ IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngine {

    private final WorkflowEdgeRepository edges;
    private final RunLauncher launcher;
    private final NotificationService notificationService;   // ⭐ NOUVEAU

    // ⭐ Écoute l'événement envoyé par le service qui exécute Talend
    @EventListener
    public void handleRunCompletedEvent(EtlRunCompletedEvent event) {
        EtlRun run = event.getRun();
        log.info("[WORKFLOW] handleRunCompletedEvent received runId={}, job='{}', status={}",
                run != null ? run.getId() : null,
                run != null ? run.getJobName() : null,
                run != null ? run.getStatus() : null);

        if (run != null) {
            // 🔔 ICI : on envoie la notification temps réel selon le statut
            switch (run.getStatus()) {
                case SUCCESS -> notificationService.notifyEtlRunSucceeded(run);
                case FAILED  -> notificationService.notifyEtlRunFailed(run);
                case CANCELLED -> notificationService.notifyEtlRunCancelled(run);
                case RUNNING -> notificationService.notifyEtlRunStarted(run);
                case PENDING -> notificationService.notifyEtlRunCreated(run);
                default -> { /* rien */ }
            }
        }

        onRunCompleted(run);
    }

    // Logique de chaînage des jobs (inchangée)
    public void onRunCompleted(EtlRun run) {
        if (run == null || run.getJobName() == null) {
            log.warn("[WORKFLOW] onRunCompleted called with null run or jobName");
            return;
        }

        log.info("[WORKFLOW] Run completed: job='{}', status={}",
                run.getJobName(), run.getStatus());

        List<WorkflowEdge> outgoing =
                edges.findByFromJobAndEnabledIsTrue(run.getJobName());

        log.info("[WORKFLOW] Found {} outgoing edges for job='{}'",
                outgoing.size(), run.getJobName());

        if (outgoing.isEmpty()) {
            return;
        }

        for (WorkflowEdge edge : outgoing) {

            if (edge.isOnSuccessOnly() && run.getStatus() != EtlRun.Status.SUCCESS) {
                log.info("[WORKFLOW] Skip edge {} (requires SUCCESS, got {})",
                        edge.getId(), run.getStatus());
                continue;
            }

            String nextJob = edge.getToJob();
            log.info("[WORKFLOW] Triggering next job '{}' after '{}'",
                    nextJob, run.getJobName());

            try {
                launcher.launchJob(nextJob);
            } catch (Exception ex) {
                log.error("[WORKFLOW] Failed to launch job '{}' from edge {}",
                        nextJob, edge.getId(), ex);
            }
        }
    }
}
