// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceHealthService.java
package com.pfe.dataops.dataopsapi.sources;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SourceHealthService {

    private final SoftwareSourceRepository sourceRepo;
    private final SourceHealthRepository healthRepo;

    public SourceHealthService(SoftwareSourceRepository sourceRepo,
                               SourceHealthRepository healthRepo) {
        this.sourceRepo = sourceRepo;
        this.healthRepo = healthRepo;
    }

    // Liste complète : on joint config + health
    @Transactional(readOnly = true)
    public List<SourceHealthDto> listAll() {
        List<SoftwareSource> sources = sourceRepo.findAll();
        return sources.stream()
                .map(src -> {
                    SourceHealth h = healthRepo.findBySource(src)
                            .orElse(null);
                    return SourceHealthDto.of(src, h);
                })
                .toList();
    }

    // Test de toutes les sources
    @Transactional
    public List<SourceHealthDto> testAll() {
        List<SoftwareSource> sources = sourceRepo.findAll();
        for (SoftwareSource src : sources) {
            testOneInternal(src);
        }
        return listAll();
    }

    // Test d’une seule source
    @Transactional
    public SourceHealthDto testOne(Long sourceId) {
        SoftwareSource src = sourceRepo.findById(sourceId)
                .orElseThrow(() -> new IllegalArgumentException("Source introuvable " + sourceId));

        SourceHealth h = testOneInternal(src);
        return SourceHealthDto.of(src, h);
    }

    private SourceHealth testOneInternal(SoftwareSource src) {
        SourceHealth health = healthRepo.findBySource(src)
                .orElseGet(() -> {
                    SourceHealth h = new SourceHealth();
                    h.setSource(src);
                    h.setStatus(SourceHealthStatus.UNKNOWN);
                    return h;
                });

        Instant start = Instant.now();
        health.setLastCheckedAt(start);

        System.out.println("[HEALTH] Test de la source : " + src.getName()
                + " (type=" + src.getType() + ")");

        try {
            if ("db".equalsIgnoreCase(src.getType())) {
                testPostgres(src);
            } else if ("file".equalsIgnoreCase(src.getType())) {
                testFtp(src);
            } else {
                // autre type → on ne sait pas tester
                health.setStatus(SourceHealthStatus.UNKNOWN);
                health.setLastMessage("Type non supporté pour le test : " + src.getType());
                return healthRepo.save(health);
            }

            long latency = Duration.between(start, Instant.now()).toMillis();
            health.setStatus(SourceHealthStatus.ONLINE);
            health.setLastLatencyMs(latency);
            health.setLastSuccessAt(Instant.now());
            health.setLastMessage("Connexion OK");

        } catch (Exception e) {
            long latency = Duration.between(start, Instant.now()).toMillis();
            health.setStatus(SourceHealthStatus.OFFLINE);
            health.setLastLatencyMs(latency);
            health.setLastMessage("Erreur : " + e.getMessage());
            System.out.println("[HEALTH] Échec pour " + src.getName() + " → " + e.getMessage());
        }

        return healthRepo.save(health);
    }

    // ---------- Tests concrets ----------

    /**
     * Test d'une source de type "db" en PostgreSQL.
     * Utilise connectionUrl si présent, sinon reconstruit à partir de host/port/dbName.
     */
    private void testPostgres(SoftwareSource src) throws Exception {
        String url  = src.getConnectionUrl();
        String user = src.getUsername();
        String pass = src.getPassword();

        // Si l'URL est absente, on la reconstruit depuis host/port/dbName
        if (url == null || url.isBlank()) {
            String host   = (src.getHost()   != null && !src.getHost().isBlank())   ? src.getHost()   : "localhost";
            Integer port  = (src.getPort()   != null && src.getPort() > 0)          ? src.getPort()   : 5432;
            String dbName = (src.getDbName() != null && !src.getDbName().isBlank()) ? src.getDbName() : null;

            StringBuilder sb = new StringBuilder("jdbc:postgresql://")
                    .append(host)
                    .append(":")
                    .append(port);
            if (dbName != null) {
                sb.append("/").append(dbName);
            }
            url = sb.toString();
        }

        if (user == null || user.isBlank()) {
            throw new IllegalStateException("Paramètres DB incomplets pour " + src.getName() + " (user manquant)");
        }

        System.out.println("[HEALTH] Test Postgres → " + url + " (user=" + user + ")");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            // juste ouvrir/fermer suffit pour le test
        }
    }

    private void testFtp(SoftwareSource src) throws Exception {
        String host = src.getHost();
        Integer port = src.getPort();
        String user = src.getUsername();
        String pass = src.getPassword();

        if (host == null || host.isBlank() || user == null || user.isBlank()) {
            throw new IllegalStateException("Paramètres FTP incomplets pour " + src.getName());
        }

        int p = (port != null && port > 0) ? port : 21;

        System.out.println("[HEALTH] Test FTP → " + host + ":" + p + " (user=" + user + ")");

        FTPClient ftp = new FTPClient();
        try {
            ftp.setConnectTimeout(5000);
            ftp.connect(host, p);
            boolean ok = ftp.login(user, pass != null ? pass : "");
            if (!ok) {
                throw new IllegalStateException("Login FTP refusé");
            }
            ftp.noop(); // ping
        } finally {
            try {
                ftp.logout();
            } catch (Exception ignored) {}
            try {
                ftp.disconnect();
            } catch (Exception ignored) {}
        }
    }
}
