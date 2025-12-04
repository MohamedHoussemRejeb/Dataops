// src/main/java/com/pfe/dataops/dataopsapi/sources/SoftwareSourceService.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SoftwareSourceService {

    private final SoftwareSourceRepository repo;

    public SoftwareSourceService(SoftwareSourceRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<SoftwareSourceDto> list() {
        return repo.findAll()
                .stream()
                .map(SoftwareSourceDto::fromEntity)
                .toList();
    }

    // ---------- IMPORT FICHIER CONTEXTE ----------

    @Transactional
    public void importContextFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            System.out.println("[IMPORT] Fichier vide ou nul");
            return;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            filename = "context-file";
        }

        String lower = filename.toLowerCase(Locale.ROOT);
        System.out.println("[IMPORT] Fichier reçu : " + filename);

        if (lower.endsWith(".properties")) {
            System.out.println("[IMPORT] .properties détecté → importFromProperties");
            importFromProperties(file);
            return;
        }

        if (lower.endsWith(".csv") || lower.endsWith(".txt")) {
            System.out.println("[IMPORT] .csv / .txt détecté → importFromDelimited");
            importFromDelimited(file);
            return;
        }

        // Fallback très simple : créer une source basique
        System.out.println("[IMPORT] Type inconnu → création simple");
        SoftwareSource source = new SoftwareSource();
        source.setName(filename);
        source.setType("config-file");
        source.setVendor("Upload");
        source.setOwner("DataOps");
        source.setTags(List.of("uploaded"));
        repo.save(source);

        System.out.println("[IMPORT] SoftwareSource créée (fallback) id=" + source.getId());
    }

    // ---------- .properties (Talend + Coca) ----------

    private void importFromProperties(MultipartFile file) throws IOException {
        Properties props = new Properties();
        try (InputStream is = file.getInputStream()) {
            props.load(is);
        }

        // Est-ce qu’on a des clés de type context.xxx_yyy ?
        boolean hasContextPattern = props.stringPropertyNames().stream()
                .anyMatch(k -> k.startsWith("context.") && k.contains("_"));

        if (!hasContextPattern) {
            // Cas comme ton fichier COCA : serverName, databaseDWH, password, port, FTP_USER...
            System.out.println("[IMPORT] Contexte global sans prefix context. → importSingleGlobalContext");
            importSingleGlobalContext(props);
            return;
        }

        // Sinon : cas Talend context.<source>_host, context.<source>_port, ...
        Map<String, Map<String, String>> bySource = new HashMap<>();

        for (String rawKey : props.stringPropertyNames()) {
            String value = props.getProperty(rawKey);
            if (value == null) continue;

            String key = rawKey.trim();

            // On enlève "context." si présent
            if (key.startsWith("context.")) {
                key = key.substring("context.".length()).trim();
            }

            int idx = key.lastIndexOf('_');
            if (idx <= 0 || idx >= key.length() - 1) {
                System.out.println("[IMPORT] Clé ignorée (format inattendu) : " + rawKey);
                continue;
            }

            String sourceName = key.substring(0, idx);   // ex: ftp_orders
            String fieldName  = key.substring(idx + 1);  // ex: host / port / db / user / password

            bySource.computeIfAbsent(sourceName, s -> new HashMap<>())
                    .put(fieldName.toLowerCase(Locale.ROOT), value.trim());
        }

        System.out.println("[IMPORT] Talend : " + bySource.size() + " source(s) détectée(s)");

        for (var entry : bySource.entrySet()) {
            String key = entry.getKey();
            Map<String, String> fields = entry.getValue();

            String host     = fields.get("host");
            String portStr  = fields.get("port");
            String dbName   = fields.get("db");
            String user     = fields.get("user");
            String password = fields.get("password");

            Integer port = null;
            if (portStr != null && !portStr.isBlank()) {
                try {
                    port = Integer.parseInt(portStr);
                } catch (NumberFormatException e) {
                    System.out.println("[IMPORT] Port invalide pour " + key + " : " + portStr);
                }
            }

            String type    = guessType(fields, port);
            String connUrl = buildConnectionUrl(type, host, port, dbName);
            String logicalName = key.toUpperCase(Locale.ROOT);

            SoftwareSource source = repo.findByName(logicalName)
                    .orElseGet(SoftwareSource::new);

            source.setName(logicalName);
            source.setType(type);
            source.setVendor(type.equals("db") ? "PostgreSQL" : type.equals("file") ? "FTP" : "Unknown");
            source.setOwner("Talend");
            source.setTags(List.of("context", "talend"));
            source.setConnectionUrl(connUrl);
            source.setHost(host);
            source.setPort(port);
            source.setDbName(dbName);
            source.setUsername(user);
            source.setPassword(password);

            repo.save(source);
            System.out.println("[IMPORT] Source Talend importée/mise à jour : " + logicalName);
        }
    }

    /**
     * Fichier "global" comme ton example Coca :
     *  inputs=...
     *  serverName=localhost
     *  databaseDWH=COCA_VIAPOST
     *  password=houssem
     *  port=5434
     *  FTP_USER=coca_user
     */
    private void importSingleGlobalContext(Properties props) {

        String serverName = props.getProperty("serverName");
        String portStr    = props.getProperty("port");
        String dbName     = props.getProperty("databaseDWH");
        String user       = props.getProperty("FTP_USER");   // on réutilise pour la DB
        String password   = props.getProperty("password");

        Integer port = null;
        if (portStr != null && !portStr.isBlank()) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.out.println("[IMPORT] Port invalide (global context) : " + portStr);
            }
        }

        String logicalName = (dbName != null && !dbName.isBlank())
                ? dbName
                : "GLOBAL_DWH";

        SoftwareSource source = repo.findByName(logicalName)
                .orElseGet(SoftwareSource::new);

        source.setName(logicalName);
        source.setType("db");
        source.setVendor("PostgreSQL");
        source.setOwner("Coca-Cola / Viaposte");
        source.setTags(List.of("context", "global", "coca"));

        source.setHost(serverName);
        source.setPort(port);
        source.setDbName(dbName);
        source.setUsername(user);
        source.setPassword(password);

        String connUrl = buildConnectionUrl("db", serverName, port, dbName);
        source.setConnectionUrl(connUrl);

        repo.save(source);

        System.out.println("[IMPORT] Global context importé → SoftwareSource : " + logicalName);
    }

    // ---------- CSV / TXT délimité ----------

    private void importFromDelimited(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.contains(";") ? line.split(";") : line.split(",");
                if (parts.length < 3) {
                    System.out.println("[IMPORT] Ligne " + lineNo + " invalide : " + line);
                    continue;
                }

                String name   = parts[0].trim();
                String type   = parts[1].trim();
                String vendor = parts[2].trim();

                String connectionUrl = parts.length > 3 ? parts[3].trim() : null;
                String owner         = parts.length > 4 ? parts[4].trim() : null;
                String tagsStr       = parts.length > 5 ? parts[5].trim() : "";

                List<String> tags = tagsStr.isBlank()
                        ? List.of()
                        : Arrays.stream(tagsStr.split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                SoftwareSource source = repo.findByName(name)
                        .orElseGet(SoftwareSource::new);

                source.setName(name);
                source.setType(type);
                source.setVendor(vendor);
                source.setOwner(owner);
                source.setConnectionUrl(connectionUrl);
                source.setTags(tags);

                repo.save(source);
                System.out.println("[IMPORT] Ligne CSV/TXT importée : " + name);
            }
        }
    }

    // ---------- Helpers ----------

    private String guessType(Map<String, String> fields, Integer port) {
        if (fields.containsKey("db")) return "db";
        if (port != null && (port == 5432 || port == 3306 || port == 1521)) return "db";
        if (port != null && (port == 21 || port == 22)) return "file";
        return "other";
    }

    private String buildConnectionUrl(String type, String host, Integer port, String dbName) {
        if (host == null || host.isBlank()) return null;
        int p = port != null ? port : -1;

        if ("db".equalsIgnoreCase(type)) {
            StringBuilder sb = new StringBuilder("jdbc:postgresql://");
            sb.append(host);
            if (p > 0) sb.append(":").append(p);
            if (dbName != null && !dbName.isBlank()) sb.append("/").append(dbName);
            return sb.toString();
        }
        if ("file".equalsIgnoreCase(type)) {
            StringBuilder sb = new StringBuilder("ftp://");
            sb.append(host);
            if (p > 0) sb.append(":").append(p);
            return sb.toString();
        }
        if (p > 0) return host + ":" + p;
        return host;
    }
}
