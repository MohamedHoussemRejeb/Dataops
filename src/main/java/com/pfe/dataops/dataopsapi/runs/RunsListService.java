package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.runs.dto.EtlRunDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RunsListService {

    // stockage en mémoire pour démo
    private final Map<String, EtlRunDto> memory = new LinkedHashMap<>();

    public RunsListService() {
        // On préremplit quelques runs au démarrage
        seed();
    }

    public List<EtlRunDto> listAll(String flowType, String status, String q) {
        List<EtlRunDto> all = new ArrayList<>(memory.values());

        // filtres basiques
        if (flowType != null && !flowType.isBlank() && !"Tous".equalsIgnoreCase(flowType)) {
            all = all.stream()
                    .filter(r -> flowType.equalsIgnoreCase(r.flowType))
                    .toList();
        }
        if (status != null && !status.isBlank() && !"Tous".equalsIgnoreCase(status)) {
            all = all.stream()
                    .filter(r -> status.equalsIgnoreCase(r.status))
                    .toList();
        }
        if (q != null && !q.isBlank()) {
            String low = q.toLowerCase();
            all = all.stream()
                    .filter(r ->
                            (r.message != null && r.message.toLowerCase().contains(low)) ||
                                    (r.sourceFile != null && r.sourceFile.toLowerCase().contains(low))
                    )
                    .toList();
        }

        // trie du plus récent (startTime) au plus ancien
        all.sort((a,b) -> {
            Instant ia = a.startTime != null ? Instant.parse(a.startTime) : Instant.EPOCH;
            Instant ib = b.startTime != null ? Instant.parse(b.startTime) : Instant.EPOCH;
            return ib.compareTo(ia);
        });

        return all;
    }

    /** retry simple : on simule juste qu'on crée un nouveau run PENDING */
    public EtlRunDto retrySimple(String id) {
        EtlRunDto original = memory.get(id);
        if (original == null) {
            // dans un vrai backend → NotFound
            return null;
        }
        EtlRunDto clone = makePendingFrom(original.flowType, "Retry simple déclenché");
        memory.put(clone.id, clone);
        return clone;
    }

    /** retry avancé avec paramètres utilisateur */
    public EtlRunDto retryAdvanced(String id, Map<String,Object> params) {
        EtlRunDto original = memory.get(id);
        if (original == null) {
            return null;
        }
        String msg = "Retry avancé : " + params;
        EtlRunDto clone = makePendingFrom(original.flowType, msg);
        memory.put(clone.id, clone);
        return clone;
    }

    /** "Générer un run" depuis l'UI */
    public EtlRunDto simulateRandomRun() {
        // on choisit un flowType aléatoire
        String[] flows = {"ARTICLES","COMMANDES","EXPEDITIONS","ANNULATIONS","MOUVEMENTS"};
        String flow = flows[new Random().nextInt(flows.length)];

        EtlRunDto r = makePendingFrom(flow, "Simulation utilisateur");
        memory.put(r.id, r);
        return r;
    }

    // ----- helpers internes -----

    private EtlRunDto makePendingFrom(String flowType, String msg) {
        EtlRunDto r = new EtlRunDto();
        r.id = UUID.randomUUID().toString();
        r.flowType = flowType;
        r.status = "PENDING";
        r.startTime = Instant.now().toString();
        r.endTime = null;
        r.rowsIn = 0;
        r.rowsOut = 0;
        r.rowsError = 0;
        r.durationMs = null;
        r.sourceFile = "manual://retry";
        r.message = msg;
        r.trigger = "MANUAL";
        r.progress = 0;
        r.dryRun = false;

        // gouvernance : on rattache à un dataset
        r.datasetId = flowType; // ex: "COMMANDES"
        r.datasetUrn = "urn:talend:" + flowType;

        return r;
    }

    private void seed() {
        // SUCCESS
        {
            EtlRunDto r = new EtlRunDto();
            r.id = UUID.randomUUID().toString();
            r.flowType = "ARTICLES";
            r.status = "SUCCESS";
            r.startTime = Instant.now().minusSeconds(3600).toString();
            r.endTime   = Instant.now().minusSeconds(3300).toString();
            r.rowsIn = 1000;
            r.rowsOut = 998;
            r.rowsError = 2;
            r.durationMs = 300000L;
            r.sourceFile = "s3://landing/articles_20251026.csv";
            r.message = "OK";
            r.trigger = "SCHEDULED";
            r.progress = 100;
            r.dryRun = false;
            r.datasetId = "ARTICLES";
            r.datasetUrn = "urn:talend:ARTICLES";
            memory.put(r.id, r);
        }

        // FAILED
        {
            EtlRunDto r = new EtlRunDto();
            r.id = UUID.randomUUID().toString();
            r.flowType = "COMMANDES";
            r.status = "FAILED";
            r.startTime = Instant.now().minusSeconds(7200).toString();
            r.endTime   = Instant.now().minusSeconds(7100).toString();
            r.rowsIn = 1234;
            r.rowsOut = 1170;
            r.rowsError = 64;
            r.durationMs = 100000L;
            r.sourceFile = "s3://landing/commandes_20251026.csv";
            r.message = "Erreur parsing colonne date";
            r.trigger = "MANUAL";
            r.progress = 100;
            r.dryRun = false;
            r.datasetId = "COMMANDES";
            r.datasetUrn = "urn:talend:COMMANDES";
            memory.put(r.id, r);
        }

        // RUNNING
        {
            EtlRunDto r = new EtlRunDto();
            r.id = UUID.randomUUID().toString();
            r.flowType = "EXPEDITIONS";
            r.status = "RUNNING";
            r.startTime = Instant.now().minusSeconds(120).toString();
            r.endTime   = null;
            r.rowsIn = 500;
            r.rowsOut = null;
            r.rowsError = 0;
            r.durationMs = null;
            r.sourceFile = "s3://landing/expeditions_live.csv";
            r.message = "En cours";
            r.trigger = "MANUAL";
            r.progress = 45;
            r.dryRun = false;
            r.datasetId = "EXPEDITIONS";
            r.datasetUrn = "urn:talend:EXPEDITIONS";
            memory.put(r.id, r);
        }

        // PENDING dry-run
        {
            EtlRunDto r = new EtlRunDto();
            r.id = UUID.randomUUID().toString();
            r.flowType = "ANNULATIONS";
            r.status = "PENDING";
            r.startTime = Instant.now().minusSeconds(600).toString();
            r.endTime   = null;
            r.rowsIn = null;
            r.rowsOut = null;
            r.rowsError = 0;
            r.durationMs = null;
            r.sourceFile = "upload_local/annulations_preview.csv";
            r.message = "Dry-run terminé (aucune écriture)";
            r.trigger = "MANUAL";
            r.progress = 0;
            r.dryRun = true;
            r.datasetId = "ANNULATIONS";
            r.datasetUrn = "urn:talend:ANNULATIONS";
            memory.put(r.id, r);
        }
    }
}