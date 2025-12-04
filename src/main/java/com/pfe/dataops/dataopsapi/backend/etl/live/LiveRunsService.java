
package com.pfe.dataops.dataopsapi.backend.etl.live;


import com.pfe.dataops.dataopsapi.backend.etl.live.dto.LiveLogDto;
import com.pfe.dataops.dataopsapi.backend.etl.live.dto.LiveRunDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LiveRunsService {

    private final Map<String, LiveRunDto> runs = new ConcurrentHashMap<>();
    private final Map<String, List<LiveLogDto>> logs = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public LiveRunsService() {
        // seed: on démarre avec 1 run RUNNING pour que l'écran ne soit pas vide
        createSimulatedRun(false); // dryRun = false par défaut
    }

    /** renvoie uniquement les runs RUNNING */
    public List<LiveRunDto> getRunning() {
        return runs.values().stream()
                .filter(r -> "RUNNING".equals(r.getStatus()))
                .sorted(Comparator.comparing(LiveRunDto::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    /** logs d'un run */
    public List<LiveLogDto> getLogs(String runId) {
        return logs.getOrDefault(runId, Collections.emptyList());
    }

    /** utilisé par le scheduler pour faire avancer */
    public void tick() {
        for (LiveRunDto run : runs.values()) {
            if (!"RUNNING".equals(run.getStatus())) continue;

            int inc = 5 + random.nextInt(8); // +5..+12
            int next = Math.min(100, (run.getProgress() == null ? 0 : run.getProgress()) + inc);
            run.setProgress(next);

            // push step log
            pushInfo(run.getId(),
                    next >= 100
                            ? (Boolean.TRUE.equals(run.getDryRun()) ? "Dry-run terminé" : "Traitement terminé")
                            : randomStep());

            // fin ?
            if (next >= 100) {
                run.setStatus("SUCCESS");
                pushWarn(run.getId(),
                        Boolean.TRUE.equals(run.getDryRun())
                                ? "Dry-run terminé, aucune écriture persistée"
                                : "Run terminé OK");
            }
        }
    }

    /** fabrique un run RUNNING pour tester depuis Postman par ex. */
    public LiveRunDto createSimulatedRun(boolean dryRun) {
        String id = UUID.randomUUID().toString();
        String[] flows = {"ARTICLES","COMMANDES","EXPEDITIONS","ANNULATIONS","MOUVEMENTS"};
        String flowType = flows[random.nextInt(flows.length)];

        LiveRunDto r = new LiveRunDto(
                id,
                flowType,
                "RUNNING",
                Instant.now(),
                0,
                dryRun,
                dryRun ? "Dry-run en cours" : "Simulation en cours"
        );

        runs.put(id, r);
        logs.put(id, new ArrayList<>());
        pushInfo(id, "Démarrage du run " + flowType + (dryRun ? " (dry-run)" : ""));

        return r;
    }

    // --- utils logs
    private void pushInfo(String runId, String msg) {
        push(runId, "INFO", msg);
    }
    private void pushWarn(String runId, String msg) {
        push(runId, "WARN", msg);
    }
    private void push(String runId, String level, String msg) {
        logs.computeIfAbsent(runId, k -> new ArrayList<>())
                .add(new LiveLogDto(Instant.now(), level, msg));
    }

    private String randomStep() {
        String[] steps = {
                "Lecture source",
                "Transformation",
                "Écriture cible",
                "Validation"
        };
        return steps[random.nextInt(steps.length)];
    }
}
