// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRunApiService.java
package com.pfe.dataops.dataopsapi.backend.etl;

import com.pfe.dataops.dataopsapi.backend.etl.dto.EtlRunDto;
import com.pfe.dataops.dataopsapi.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EtlRunApiService {

    private final EtlRunRepository repo;
    private final NotificationService notificationService;

    public EtlRunApiService(EtlRunRepository repo,
                            NotificationService notificationService) {
        this.repo = repo;
        this.notificationService = notificationService;
    }

    // ---------- READ ----------

    public List<EtlRunDto> listAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(EtlRun::getStartedAt).reversed())
                .map(EtlRunApiService::toDto)
                .toList();
    }

    public EtlRunDto getOne(Long id) {
        var run = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found " + id));
        return toDto(run);
    }

    public Optional<EtlRun> findById(Long id) {
        return repo.findById(id);
    }

    // ---------- CREATE RUN (optionnel) ----------

    @Transactional
    public EtlRun createRun(String jobName,
                            String ownerUsername,
                            EtlRun.Trigger trigger,
                            boolean dryRun) {
        EtlRun run = new EtlRun();
        run.setJobName(jobName);
        run.setStatus(EtlRun.Status.PENDING);
        run.setTrigger(trigger);
        run.setDryRun(dryRun);
        run.setOwnerUsername(ownerUsername);
        run.setCreatedAt(Instant.now());

        EtlRun saved = repo.save(run);

        // notif "run créé" si tu veux
        notificationService.notifyEtlRunCreated(saved);

        return saved;
    }

    // ---------- UPDATE STATUS + NOTIFS AUTOMATIQUES ----------

    @Transactional
    public EtlRun updateStatus(Long runId,
                               EtlRun.Status newStatus,
                               String message,
                               Long rowsIn,
                               Long rowsOut,
                               Long rowsError,
                               Long durationMs) {

        EtlRun run = repo.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run not found " + runId));

        EtlRun.Status oldStatus = run.getStatus();

        // le setter de l'entité tronque déjà à 1000 chars
        run.setStatus(newStatus);
        run.setMessage(message);
        run.setRowsIn(rowsIn);
        run.setRowsOut(rowsOut);
        run.setRowsError(rowsError);
        run.setDurationMs(durationMs);

        if (run.getStartedAt() == null &&
                (newStatus == EtlRun.Status.RUNNING
                        || newStatus == EtlRun.Status.SUCCESS
                        || newStatus == EtlRun.Status.FAILED)) {
            run.setStartedAt(Instant.now());
        }
        if (newStatus == EtlRun.Status.SUCCESS
                || newStatus == EtlRun.Status.FAILED
                || newStatus == EtlRun.Status.CANCELLED) {
            run.setFinishedAt(Instant.now());
        }

        EtlRun saved = repo.save(run);

        // 🔔 Notifications si changement de statut
        if (newStatus != oldStatus) {
            switch (newStatus) {
                case RUNNING -> notificationService.notifyEtlRunStarted(saved);
                case SUCCESS -> notificationService.notifyEtlRunSucceeded(saved);
                case FAILED -> notificationService.notifyEtlRunFailed(saved);
                case CANCELLED -> notificationService.notifyEtlRunCancelled(saved);
                default -> { }
            }
        }

        return saved;
    }

    @Transactional
    public EtlRun markFailed(Long runId, String errorMessage) {
        return updateStatus(
                runId,
                EtlRun.Status.FAILED,
                errorMessage,
                null,
                null,
                null,
                null
        );
    }

    // ---------- Mapping entity -> DTO ----------

    private static EtlRunDto toDto(EtlRun e) {
        EtlRunDto.RetryParamsDto retry = null;
        if (e.getRetryRequestedAt() != null ||
                (e.getRetryReason() != null && !e.getRetryReason().isBlank())) {
            retry = new EtlRunDto.RetryParamsDto(
                    e.getRetryRequestedAt(),
                    e.getRetryReason()
            );
        }

        return new EtlRunDto(
                String.valueOf(e.getId()),
                e.getJobName(),
                e.getStatus(),
                e.getStartedAt(),
                e.getFinishedAt(),
                e.getRowsIn(),
                e.getRowsOut(),
                e.getRowsError(),
                e.getDurationMs(),
                e.getSourceFile(),
                e.getMessage(),
                e.getTrigger(),
                e.getProgress(),
                e.getDryRun(),
                retry
        );
    }
}
