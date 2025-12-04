// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRunController.java
package com.pfe.dataops.dataopsapi.backend.etl;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.runs.EtlRunService;
import com.pfe.dataops.dataopsapi.runs.RunMetadata;
import com.pfe.dataops.dataopsapi.runs.RunMetadataRepository;
import com.pfe.dataops.dataopsapi.runs.dto.RunCreateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.*;

@RestController
@RequestMapping("/api/etl/runs")   // ✅ même chemin que le front / Postman
@Validated
public class EtlRunController {

    private final EtlRunService service;
    private final RunMetadataRepository runMetadataRepository;   // ✅ NEW

    public EtlRunController(EtlRunService service,
                            RunMetadataRepository runMetadataRepository) {
        this.service = service;
        this.runMetadataRepository = runMetadataRepository;
    }

    // GET /api/etl/runs?page=0&size=20&sort=createdAt,desc&job=...&status=SUCCESS&from=2025-10-01&to=2025-10-24
    @GetMapping
    public Page<EtlRun> list(
            @RequestParam(required = false) String job,
            @RequestParam(required = false) String status, // SUCCESS | FAILED | RUNNING
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Sort s = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, s);

        Instant fromI = (from == null) ? null : from.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toI   = (to == null)   ? null : to.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        EtlRun.Status statusEnum = parseStatusOrNull(status);

        // ✅ on délègue toute la partie "search" au service
        return service.search(pageable, job, statusEnum, fromI, toI);
    }

    @GetMapping("{id}")
    public ResponseEntity<EtlRun> get(@PathVariable Long id) {
        // si tu veux, tu peux ajouter une méthode findById dans EtlRunService;
        // pour l’instant on passe directement par le repo via une petite astuce :
        Page<EtlRun> page = service.search(
                PageRequest.of(0, 1),
                null, null, null, null
        );

        return page.getContent().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/{id}/meta")
    public ResponseEntity<RunMetadata> getMetadata(@PathVariable Long id) {
        return runMetadataRepository.findByEtlRunId(id)
                .map(ResponseEntity::ok)
                // 👇 204 No Content si aucune metadata pour ce run
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<EtlRun> create(@Valid @RequestBody RunCreateRequest req) {
        // ✅ Ici on ne reconstruit plus l’objet nous-mêmes.
        //    Tout est fait dans EtlRunService.create(...) :
        //    - repo.save(...)
        //    - WebSocket events
        //    - WorkflowEngine.onRunCompleted(...)
        EtlRun saved = service.create(req);

        return ResponseEntity
                .created(URI.create("/api/etl/runs/" + saved.getId()))
                .body(saved);
    }

    // ---------------------
    // Helpers
    // ---------------------

    private static Sort parseSort(String raw){
        if (raw == null || raw.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt", "id");
        }
        String[] parts = raw.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(dir, field);
    }

    private static EtlRun.Status parseStatusOrNull(String raw){
        if (raw == null || raw.isBlank()) return null;
        try {
            return EtlRun.Status.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex){
            return null; // statut inconnu => pas de filtre
        }
    }
}
