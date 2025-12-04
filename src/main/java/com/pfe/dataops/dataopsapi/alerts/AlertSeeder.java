// src/main/java/com/pfe/dataops/dataopsapi/alerts/AlertSeeder.java
package com.pfe.dataops.dataopsapi.alerts;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class AlertSeeder implements CommandLineRunner {

    private final AlertRepository repo;

    public AlertSeeder(AlertRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return; // ne seed pas si déjà des données

        Alert a1 = new Alert();
        a1.setId(UUID.randomUUID().toString());
        a1.setCreatedAt(Instant.now().minusSeconds(3600));
        a1.setSeverity(AlertSeverity.ERROR);
        a1.setSource(AlertSource.RUN);
        a1.setRunId("r1");
        a1.setFlowType("EXPEDITIONS");
        a1.setMessage("FAILED: Timeout");
        a1.setAcknowledged(false);
        a1.setDatasetUrn("dataset://expeditions");

        Alert a2 = new Alert();
        a2.setId(UUID.randomUUID().toString());
        a2.setCreatedAt(Instant.now().minusSeconds(2600));
        a2.setSeverity(AlertSeverity.WARN);
        a2.setSource(AlertSource.SLA);
        a2.setFlowType("COMMANDES");
        a2.setMessage("SLA dépassé de 12 min");
        a2.setAcknowledged(false);
        a2.setDatasetUrn("dataset://commandes");

        Alert a3 = new Alert();
        a3.setId(UUID.randomUUID().toString());
        a3.setCreatedAt(Instant.now().minusSeconds(1600));
        a3.setSeverity(AlertSeverity.CRITICAL);
        a3.setSource(AlertSource.SYSTEM);
        a3.setMessage("Espace disque < 5%");
        a3.setAcknowledged(false);
        a3.setDatasetUrn("dataset://infra/disk");

        repo.saveAll(List.of(a1,a2,a3));
    }
}
