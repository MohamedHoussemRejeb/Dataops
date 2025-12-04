// src/main/java/com/pfe/dataops/dataopsapi/live/TalendJobService.java
package com.pfe.dataops.dataopsapi.live;

import com.pfe.dataops.dataopsapi.alerts.Alert;
import com.pfe.dataops.dataopsapi.alerts.AlertRepository;
import com.pfe.dataops.dataopsapi.alerts.AlertSeverity;
import com.pfe.dataops.dataopsapi.alerts.AlertSource;
import com.pfe.dataops.dataopsapi.alerts.AlertWebsocketNotifier;
import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.backend.etl.EtlRunRepository;
import com.pfe.dataops.dataopsapi.backend.etl.live.AlertEvent;
import com.pfe.dataops.dataopsapi.quality.QualityService;          // ⭐️ NEW
import com.pfe.dataops.dataopsapi.runs.EtlRunCompletedEvent;
import com.pfe.dataops.dataopsapi.runs.RunLauncher;
import com.pfe.dataops.dataopsapi.runs.RunMetadata;
import com.pfe.dataops.dataopsapi.runs.RunMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Primary
@RequiredArgsConstructor
public class TalendJobService implements RunLauncher {

    private final LiveRunRepository liveRunRepository;
    private final LiveLogRepository liveLogRepository;
    private final EtlRunRepository etlRunRepository;
    private final ApplicationEventPublisher events;
    private final RunMetadataRepository runMetadataRepository;

    // Kafka pour publier les événements d’alerte / run
    private final KafkaTemplate<String, AlertEvent> alertKafkaTemplate;

    // Repository pour écrire dans la table alerts
    private final AlertRepository alertRepository;

    // Notifier WebSocket pour les nouvelles alertes
    private final AlertWebsocketNotifier alertWebsocketNotifier;

    // ⭐️ NEW : service qualité pour alimenter le dashboard qualité
    private final QualityService qualityService;

    // 🔍 Patterns génériques Talend
    private static final Pattern PATTERN_ROWS_READ =
            Pattern.compile("(\\d+)\\s+(rows|records)\\s+read");

    private static final Pattern PATTERN_ROWS_INSERTED =
            Pattern.compile("(\\d+)\\s+(rows|records)\\s+(inserted|loaded|written)");

    private static final Pattern PATTERN_ROWS_REJECTED =
            Pattern.compile("(\\d+)\\s+(rows|records)\\s+reject(ed)?");

    // === CHEMINS TALEND ===
    private static final String PATH_ARTICLE =
            "C:\\Users\\Mohamed Houssem\\Downloads\\TOS_DI-20211109_1610-V8.0.1\\TOS_DI-20211109_1610-V8.0.1\\COCACOLA_Viapost_IntegrationArticle\\COCACOLA_Viapost_IntegrationArticle_run.bat";

    private static final String PATH_COMMANDE =
            "C:\\Users\\Mohamed Houssem\\Downloads\\TOS_DI-20211109_1610-V8.0.1\\TOS_DI-20211109_1610-V8.0.1\\COCACOLA_Viapost_IntegrationCommande\\COCACOLA_Viapost_IntegrationCommande_run.bat";

    private static final String PATH_ANNULATION =
            "C:\\Users\\Mohamed Houssem\\Downloads\\TOS_DI-20211109_1610-V8.0.1\\TOS_DI-20211109_1610-V8.0.1\\Viapost_COCACOLA_IntegrationFluxAnnulation\\Viapost_COCACOLA_IntegrationFluxAnnulation_run.bat";

    private static final String PATH_MVT_STOCK =
            "C:\\Users\\Mohamed Houssem\\Downloads\\TOS_DI-20211109_1610-V8.0.1\\TOS_DI-20211109_1610-V8.0.1\\Viapost_COCACOLA_IntegrationFluxMouvementStock\\Viapost_COCACOLA_IntegrationFluxMouvementStock_run.bat";

    // ======================================================================================
    //                                   API CONTROLLER
    // ======================================================================================

    public LiveRun startArticleJob() {
        return startJob("TALEND_ARTICLE", "COCACOLA_Viapost_IntegrationArticle", PATH_ARTICLE);
    }

    public LiveRun startCommandeJob() {
        return startJob("TALEND_COMMANDE", "COCACOLA_Viapost_IntegrationCommande", PATH_COMMANDE);
    }

    public LiveRun startAnnulationJob() {
        return startJob("TALEND_ANNULATION", "Viapost_COCACOLA_IntegrationFluxAnnulation", PATH_ANNULATION);
    }

    public LiveRun startMouvementStockJob() {
        return startJob("TALEND_MVT_STOCK", "Viapost_COCACOLA_IntegrationFluxMouvementStock", PATH_MVT_STOCK);
    }

    // ======================================================================================
    //                                ORCHESTRATION ENGINE SUPPORT
    // ======================================================================================

    @Override
    public void launchJob(String jobName) {
        switch (jobName) {
            case "COCACOLA_Viapost_IntegrationArticle" ->
                    startArticleJob();
            case "COCACOLA_Viapost_IntegrationCommande" ->
                    startCommandeJob();
            case "Viapost_COCACOLA_IntegrationFluxAnnulation" ->
                    startAnnulationJob();
            case "Viapost_COCACOLA_IntegrationFluxMouvementStock" ->
                    startMouvementStockJob();
            default ->
                    throw new IllegalArgumentException("❌ Unknown job: " + jobName);
        }
    }

    // ======================================================================================
    //                            CORE EXECUTION ENGINE
    // ======================================================================================

    private LiveRun startJob(String flowType, String jobName, String scriptPath) {

        LiveRun live = new LiveRun(flowType, LocalDateTime.now(), "RUNNING");
        live.setProgress(0);
        live.setDryRun(false);
        live = liveRunRepository.save(live);

        EtlRun etl = new EtlRun();
        etl.setJobName(jobName);
        etl.setStatus(EtlRun.Status.RUNNING);
        etl.setTrigger(EtlRun.Trigger.MANUAL);
        etl.setStartedAt(Instant.now());
        etl.setProgress(0);
        etl = etlRunRepository.save(etl);

        final String liveId = live.getId();
        final Long etlId = etl.getId();

        CompletableFuture.runAsync(() -> executeJob(liveId, etlId, scriptPath));

        return live;
    }

    private void executeJob(String liveId, Long etlId, String scriptPath) {

        StringBuilder logs = new StringBuilder();
        int exitCode = -1;
        boolean error = false;

        Instant start = Instant.now();

        long rowsIn = 0L;
        long rowsOut = 0L;
        long rowsError = 0L;
        long logLines = 0L;
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", scriptPath);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) {

                    logLines++;
                    logs.append(line).append("\n");

                    liveLogRepository.save(new LiveLog(
                            liveId,
                            LocalDateTime.now(),
                            detectLevel(line),
                            line
                    ));

                    String lower = line.toLowerCase();

                    Matcher mRead = PATTERN_ROWS_READ.matcher(lower);
                    if (mRead.find()) rowsIn += Long.parseLong(mRead.group(1));

                    Matcher mIns = PATTERN_ROWS_INSERTED.matcher(lower);
                    if (mIns.find()) rowsOut += Long.parseLong(mIns.group(1));

                    Matcher mRej = PATTERN_ROWS_REJECTED.matcher(lower);
                    if (mRej.find()) rowsError += Long.parseLong(mRej.group(1));
                }
            }

            exitCode = p.waitFor();

        } catch (Exception e) {
            error = true;
            logs.append("\n").append(e.getMessage());

            liveLogRepository.save(
                    new LiveLog(liveId, LocalDateTime.now(), "ERROR", e.getMessage())
            );
        }

        Instant finished = Instant.now();
        long durationMs = Duration.between(start, finished).toMillis();
        boolean failed = error || exitCode != 0;

        if (rowsIn == 0L && rowsOut == 0L && rowsError == 0L)
            rowsOut = logLines;

        LiveRun live = liveRunRepository.findById(liveId).orElseThrow();
        live.setEndTime(LocalDateTime.now());
        live.setProgress(100);
        live.setStatus(failed ? "FAILED" : "SUCCESS");
        liveRunRepository.saveAndFlush(live);

        EtlRun etl = etlRunRepository.findById(etlId).orElseThrow();
        etl.setFinishedAt(finished);
        etl.setDurationMs(durationMs);
        etl.setProgress(100);
        etl.setMessage(logs.toString());
        etl.setStatus(failed ? EtlRun.Status.FAILED : EtlRun.Status.SUCCESS);

        etl.setRowsIn(rowsIn);
        etl.setRowsOut(rowsOut);
        etl.setRowsError(rowsError);

        etlRunRepository.saveAndFlush(etl);

        // ========= persistance dans run_metadata =========
        try {
            String host = "unknown";
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Exception ignore) {
            }

            long latencySec = Math.max(1L, durationMs / 1000L);

            int cpuPct = 20 + (int) (Math.random() * 60); // 20–80 %
            int ramPct = 30 + (int) (Math.random() * 50); // 30–80 %

            RunMetadata meta = RunMetadata.builder()
                    .etlRun(etl)
                    .rowCountIn(rowsIn)
                    .rowCountOut(rowsOut)
                    .errorCount(rowsError)
                    .nullPct(0.0)
                    .invalidPct(0.0)
                    .fileName(etl.getJobName())
                    .fileSizeBytes(null)
                    .executionHost(host)
                    .latencySec(latencySec)
                    .cpuPct((double) cpuPct)
                    .ramPct((double) ramPct)
                    .extraJson(null)
                    .build();

            runMetadataRepository.save(meta);
        } catch (Exception metaEx) {
            liveLogRepository.save(
                    new LiveLog(liveId, LocalDateTime.now(), "WARN",
                            "Failed to persist run_metadata: " + metaEx.getMessage())
            );
        }

        // ⭐️ NEW : mettre à jour les métriques qualité pour le dashboard
        try {
            String datasetKey = mapFlowTypeToDatasetKey(live.getFlowType());
            qualityService.onRunCompleted(
                    datasetKey,
                    failed,
                    rowsIn,
                    rowsError,
                    durationMs
            );
        } catch (Exception qEx) {
            liveLogRepository.save(
                    new LiveLog(liveId, LocalDateTime.now(), "WARN",
                            "Failed to update quality metrics: " + qEx.getMessage())
            );
        }

        // ========= Créer une alerte en base si le job a échoué =========
        if (failed) {
            Alert alert = new Alert();
            alert.setSeverity(AlertSeverity.CRITICAL);
            alert.setSource(AlertSource.RUN);
            alert.setRunId(String.valueOf(etl.getId()));
            alert.setFlowType(live.getFlowType());

            String msg = "Échec du job " + etl.getJobName()
                    + " - " + rowsError + " lignes en erreur"
                    + " (in=" + rowsIn + ", out=" + rowsOut + ")";
            alert.setMessage(msg);

            String datasetUrn = switch (live.getFlowType()) {
                case "TALEND_ARTICLE" -> "urn:dataset:article";
                case "TALEND_COMMANDE" -> "urn:dataset:commande";
                case "TALEND_ANNULATION" -> "urn:dataset:annulation";
                case "TALEND_MVT_STOCK" -> "urn:dataset:mouvement_stock";
                default -> null;
            };
            alert.setDatasetUrn(datasetUrn);

            alert = alertRepository.save(alert);

            // 🔔 Notif WebSocket live
            try {
                alertWebsocketNotifier.notifyAlertCreated(alert);
            } catch (Exception wsEx) {
                liveLogRepository.save(
                        new LiveLog(liveId, LocalDateTime.now(), "WARN",
                                "Failed to push WebSocket alert: " + wsEx.getMessage())
                );
            }
        }

        // ========= Kafka : publier l’événement SANS bloquer le reste =========
        try {
            AlertEvent event = new AlertEvent();
            event.setId("talend-" + etl.getId());
            event.setTimestamp(Instant.now());
            event.setSource("TALEND_JOB");
            event.setFlowType(live.getFlowType());  // ex: TALEND_ARTICLE
            event.setSeverity(failed ? "CRITICAL" : "INFO");
            event.setCode(failed ? "JOB_FAILED" : "JOB_SUCCESS");
            event.setMessage(
                    failed
                            ? "Échec du job " + etl.getJobName() + " - " + rowsError + " lignes en erreur"
                            : "Job " + etl.getJobName() + " exécuté avec succès (" + rowsOut + " lignes traitées)"
            );
            event.setRunId("RUN-" + etl.getId());

            AlertEvent.DatasetInfo ds = new AlertEvent.DatasetInfo();
            ds.setName(etl.getJobName());
            ds.setDomain("COCACOLA_ETL");
            event.setDataset(ds);

            AlertEvent finalEvent = event;

            // Envoi asynchrone pour ne pas bloquer quand Kafka est down
            CompletableFuture.runAsync(() -> {
                try {
                    alertKafkaTemplate.send("dataops.alerts", finalEvent.getId(), finalEvent);
                } catch (Exception kafkaEx) {
                    liveLogRepository.save(
                            new LiveLog(liveId, LocalDateTime.now(), "WARN",
                                    "Failed to publish Kafka alert: " + kafkaEx.getMessage())
                    );
                }
            });

        } catch (Exception buildEx) {
            liveLogRepository.save(
                    new LiveLog(liveId, LocalDateTime.now(), "WARN",
                            "Failed to build Kafka AlertEvent: " + buildEx.getMessage())
            );
        }

        // 🔔 notify workflow engine (indirectly) – no circular dependency
        events.publishEvent(new EtlRunCompletedEvent(this, etl));
    }

    private String detectLevel(String line) {
        if (line.contains("ERROR")) return "ERROR";
        if (line.contains("WARN")) return "WARN";
        return "INFO";
    }

    // ⭐️ NEW : mapping flowType → dataset logique pour le dashboard qualité
    private String mapFlowTypeToDatasetKey(String flowType) {
        if (flowType == null) return null;
        return switch (flowType) {
            case "TALEND_ARTICLE"   -> "products_dim";   // référentiel article
            case "TALEND_COMMANDE"  -> "sales_orders";   // commandes
            case "TALEND_ANNULATION"-> "payments_fact";  // annulations / paiements
            case "TALEND_MVT_STOCK" -> "customer_dim";   // mouvement stock → rattaché à client (au choix)
            default -> null;
        };
    }
}
