// src/main/java/com/pfe/dataops/dataopsapi/catalog/docs/DocumentService.java
package com.pfe.dataops.dataopsapi.catalog.docs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final GovernanceDocumentRepository repo;

    public DocumentService(GovernanceDocumentRepository repo) {
        this.repo = repo;
    }

    // dossier où on stocke les fichiers uploadés
    @Value("${governance.docs.storage-dir:./data/governance-docs}")
    private String storageDir;

    private Path root() throws IOException {
        Path p = Paths.get(storageDir);
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
        return p;
    }

    // --- LIST + filtres simples (type, tag, q recherche texte) ---
    public List<DocumentDto> list(String type, String tag, String q) {
        List<GovernanceDocument> docs = repo.findAll();

        if (type != null && !type.isBlank()) {
            DocumentType dt = DocumentType.fromString(type);
            docs = docs.stream()
                    .filter(d -> d.getDocType() == dt)
                    .collect(Collectors.toList());
        }

        if (tag != null && !tag.isBlank()) {
            String t = tag.toLowerCase();
            docs = docs.stream()
                    .filter(d -> d.getTags() != null &&
                            d.getTags().toLowerCase().contains(t))
                    .collect(Collectors.toList());
        }

        if (q != null && !q.isBlank()) {
            String qq = q.toLowerCase();
            docs = docs.stream()
                    .filter(d ->
                            (d.getTitle() != null && d.getTitle().toLowerCase().contains(qq)) ||
                                    (d.getSummary() != null && d.getSummary().toLowerCase().contains(qq)))
                    .collect(Collectors.toList());
        }

        return docs.stream().map(this::toDto).toList();
    }

    public DocumentDto get(Long id) {
        GovernanceDocument doc = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
        return toDto(doc);
    }

    /**
     * Création avec upload de fichier (ADMIN)
     */
    public DocumentDto create(DocumentUploadRequest meta, MultipartFile file) throws IOException {
        // 1) on sauvegarde le fichier sur disque
        String storageKey = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dest = root().resolve(storageKey);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // 2) on crée l'entité
        GovernanceDocument entity = new GovernanceDocument();
        entity.setTitle(meta.title());
        entity.setDocType(DocumentType.fromString(meta.type()));
        entity.setTags(joinTags(meta.tags()));
        entity.setSummary(meta.summary());
        entity.setContentUrl(storageKey); // clé interne (nom du fichier sur disque)
        entity.setUpdatedAt(Instant.now());

        return toDto(repo.save(entity));
    }

    /**
     * Update des métadonnées uniquement (ADMIN).
     * Si tu veux aussi changer le fichier, on peut ajouter une méthode dédiée.
     */
    public DocumentDto update(Long id, DocumentDto dto) {
        GovernanceDocument entity = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
        entity.setTitle(dto.title());
        entity.setDocType(DocumentType.fromString(dto.type()));
        entity.setTags(joinTags(dto.tags()));
        entity.setSummary(dto.summary());
        // on NE touche pas au fichier ici
        entity.setUpdatedAt(Instant.now());
        return toDto(repo.save(entity));
    }

    public void delete(Long id) {
        GovernanceDocument entity = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
        // optionnel : supprimer aussi le fichier sur disque
        if (entity.getContentUrl() != null) {
            try {
                Path p = root().resolve(entity.getContentUrl());
                Files.deleteIfExists(p);
            } catch (IOException ignored) {}
        }
        repo.deleteById(id);
    }

    // --- Download du fichier ---
    public Resource getFile(Long id) throws IOException {
        GovernanceDocument doc = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Document not found"));
        Path p = root().resolve(doc.getContentUrl());
        return new FileSystemResource(p);
    }

    // --- mapping helpers ---

    private DocumentDto toDto(GovernanceDocument d) {
        List<String> tags = splitTags(d.getTags());
        // côté API, on expose directement l'URL de téléchargement
        String publicUrl = "/api/docs/" + d.getId() + "/file";
        return new DocumentDto(
                d.getId(),
                d.getTitle(),
                d.getDocType() != null ? d.getDocType().name().toLowerCase() : null,
                tags,
                d.getSummary(),
                publicUrl,               // ⭐ ici : URL publique, pas la storageKey brute
                d.getUpdatedAt()
        );
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) return Collections.emptyList();
        return Arrays.stream(tags.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return tags.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|"));
    }
}
