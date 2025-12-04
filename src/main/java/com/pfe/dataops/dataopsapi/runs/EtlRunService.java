// src/main/java/com/pfe/dataops/dataopsapi/runs/EtlRunService.java
package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.backend.etl.EtlRunRepository;
import com.pfe.dataops.dataopsapi.runs.dto.RunCreateRequest;
import com.pfe.dataops.dataopsapi.realtime.RealtimeEventBus;
import com.pfe.dataops.dataopsapi.notifications.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;

import static com.pfe.dataops.dataopsapi.backend.etl.EtlRunSpecifications.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtlRunService {

    private final EtlRunRepository repo;
    private final RealtimeEventBus eventBus;
    private final WorkflowEngine workflowEngine;
    private final NotificationService notificationService; // 🔥 NEW

    public Page<EtlRun> search(Pageable pageable,
                               String job,
                               EtlRun.Status status,
                               Instant from,
                               Instant to) {

        Specification<EtlRun> spec = Specification.allOf(
                jobContains(job),
                statusIs(status),
                createdFrom(from),
                createdTo(to)
        );

        return repo.findAll(spec, pageable);
    }

    public EtlRun create(RunCreateRequest req) {

        // 🔥 Récupérer l'utilisateur connecté (Keycloak) pour ownerUsername
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String owner = (auth != null ? auth.getName() : "system");

        EtlRun e = new EtlRun();
        e.setJobName(req.jobName());
        e.setStatus(req.status());
        e.setRowsIn(req.rowsIn() == null ? 0L : req.rowsIn());
        e.setRowsOut(req.rowsOut() == null ? 0L : req.rowsOut());
        e.setDurationMs(req.durationMs() == null ? 0L : req.durationMs());
        e.setStartedAt(req.startedAt());
        e.setFinishedAt(req.finishedAt());

        // 🔥 NEW : propriétaire du run
        e.setOwnerUsername(owner);

        EtlRun saved = repo.save(e);

        log.info("[RUN] Saved run id={}, job='{}', status={}, startedAt={}, finishedAt={}, owner={}",
                saved.getId(), saved.getJobName(), saved.getStatus(),
                saved.getStartedAt(), saved.getFinishedAt(), saved.getOwnerUsername());

        // 1) WebSocket temps réel
        try {
            if (saved.getStatus() == EtlRun.Status.RUNNING) {
                eventBus.runStarted(saved.getId(), saved.getJobName(), null);
            } else if (saved.getStatus() != null) {
                eventBus.runFinished(saved.getId(), saved.getStatus().name(), null);
            }
        } catch (Exception ex) {
            log.warn("⚠️ Failed to publish WebSocket event: {}", ex.getMessage());
        }

        // 2) Orchestrateur + notifications sur statut terminal
        if (isTerminal(saved)) {
            log.info("[RUN] Run is terminal, calling workflowEngine.onRunCompleted");
            try {
                workflowEngine.onRunCompleted(saved);
            } catch (Exception ex) {
                log.error("⚠️ Failed to run workflow engine", ex);
            }

            // 🔥 NEW : notification temps réel quand FAILED
            if (saved.getStatus() == EtlRun.Status.FAILED) {
                try {
                    notificationService.notifyEtlRunFailed(saved);
                } catch (Exception ex) {
                    log.warn("⚠️ Failed to send FAILED notification for run {}", saved.getId(), ex);
                }
            }
        } else {
            log.info("[RUN] Run is NOT terminal, no workflow (status={})", saved.getStatus());
        }

        return saved;
    }

    private boolean isTerminal(EtlRun run) {
        if (run.getStatus() == null) return false;
        if (run.getFinishedAt() == null) return false;

        return switch (run.getStatus()) {
            case SUCCESS, FAILED, CANCELLED -> true;
            default -> false;
        };
    }

    // ✅ Toujours l'event listener pour les "lancements" simulés
    @EventListener
    public void onLaunchEvent(RunLaunchEvent ev) {
        Instant now = Instant.now();

        RunCreateRequest req = new RunCreateRequest(
                ev.jobName(),
                EtlRun.Status.SUCCESS,  // 💡 mets FAILED ici pour tester les notifs
                0L,
                0L,
                0L,
                now,
                now
        );

        create(req);
    }
}
