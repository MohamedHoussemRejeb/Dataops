// src/main/java/com/pfe/dataops/dataopsapi/catalog/CatalogService.java
package com.pfe.dataops.dataopsapi.catalog;

import com.pfe.dataops.dataopsapi.catalog.dto.DatasetDto;
import com.pfe.dataops.dataopsapi.catalog.dto.OwnerDto;
import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.enums.LegalTag;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final DatasetRepository repo;

    /* ==================== READ ==================== */

    @Transactional(readOnly = true)
    public List<DatasetDto> list() {
        return repo.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DatasetDto get(Long id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset not found: " + id));
    }

    /* ==================== CREATE ==================== */

    @Transactional
    public DatasetDto create(DatasetDto dto) {
        if (dto.getUrn() != null && repo.existsByUrn(dto.getUrn())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un dataset avec cet URN existe déjà : " + dto.getUrn()
            );
        }
        DatasetEntity e = new DatasetEntity();
        updateEntityFromDto(dto, e);
        DatasetEntity saved = repo.save(e);
        return toDto(saved);
    }

    /* ==================== UPDATE ==================== */

    @Transactional
    public DatasetDto update(Long id, DatasetDto dto) {
        DatasetEntity e = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset not found: " + id));

        updateEntityFromDto(dto, e);
        DatasetEntity saved = repo.save(e);
        return toDto(saved);
    }

    /* ==================== DELETE ==================== */

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset not found: " + id);
        }
        repo.deleteById(id);
    }

    /* ==================== MAPPERS ==================== */

    private DatasetDto toDto(DatasetEntity e) {
        // --- owner à partir des colonnes simples ---
        OwnerDto ownerDto = null;
        if (e.getOwnerName() != null || e.getOwnerEmail() != null) {
            ownerDto = OwnerDto.builder()
                    .name(e.getOwnerName())
                    .email(e.getOwnerEmail())
                    .build();
        }

        // --- listes : on évite les null ---
        List<String> tags = e.getTags() != null ? e.getTags() : List.of();
        List<String> deps = e.getDependencies() != null ? e.getDependencies() : List.of();
        List<LegalTag> legal = e.getLegal() != null ? e.getLegal() : List.of();

        return DatasetDto.builder()
                .id(String.valueOf(e.getId()))       // pour Angular
                .urn(e.getUrn())
                .name(e.getName())
                .description(e.getDescription())
                .domain(e.getDomain())
                .owner(ownerDto)
                .tags(tags)
                .dependencies(deps)
                .sensitivity(e.getSensitivity())
                .legal(legal)
                .trust(e.getTrust())
                .risk(e.getRisk())
                .lastStatus(e.getLastStatus())
                .lastEndedAt(e.getLastEndedAt())
                .lastDurationSec(e.getLastDurationSec())
                .slaFrequency(e.getSlaFrequency())
                .slaExpectedBy(e.getSlaExpectedBy())
                .slaMaxDelayMin(e.getSlaMaxDelayMin())
                .build();
    }

    /**
     * Met à jour une entity à partir du DTO (pour create + update).
     * On ne touche pas à l'ID ici (il est géré par JPA).
     */
    private void updateEntityFromDto(DatasetDto dto, DatasetEntity e) {
        e.setUrn(dto.getUrn());
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setDomain(dto.getDomain());

        // --- TAGS ---
        if (e.getTags() == null) {
            e.setTags(new ArrayList<>());
        } else {
            e.getTags().clear();
        }
        if (dto.getTags() != null) {
            e.getTags().addAll(dto.getTags());
        }

        // --- DEPENDENCIES ---
        if (e.getDependencies() == null) {
            e.setDependencies(new ArrayList<>());
        } else {
            e.getDependencies().clear();
        }
        if (dto.getDependencies() != null) {
            e.getDependencies().addAll(dto.getDependencies());
        }

        // --- LEGAL ---
        if (e.getLegal() == null) {
            e.setLegal(new ArrayList<>());
        } else {
            e.getLegal().clear();
        }
        if (dto.getLegal() != null) {
            e.getLegal().addAll(dto.getLegal());
        }

        e.setSensitivity(dto.getSensitivity());
        e.setTrust(dto.getTrust());
        e.setRisk(dto.getRisk());

        e.setLastStatus(dto.getLastStatus());
        e.setLastEndedAt(dto.getLastEndedAt());
        e.setLastDurationSec(dto.getLastDurationSec());

        e.setSlaFrequency(dto.getSlaFrequency());
        e.setSlaExpectedBy(dto.getSlaExpectedBy());
        e.setSlaMaxDelayMin(dto.getSlaMaxDelayMin());

        // 🔥 Owner : on remplit les colonnes simples à partir du DTO
        if (dto.getOwner() != null) {
            e.setOwnerName(dto.getOwner().getName());
            e.setOwnerEmail(dto.getOwner().getEmail());
            // si tu as ajouté ownerKeycloakId dans l’entity,
            // tu peux aussi le setter ici plus tard quand tu l’auras dans le DTO
        }
    }
}
