// src/main/java/com/pfe/dataops/dataopsapi/governance/access/AccessMatrixService.java
package com.pfe.dataops.dataopsapi.governance.access;

import com.pfe.dataops.dataopsapi.governance.LegalTag;
import com.pfe.dataops.dataopsapi.governance.Sensitivity;
import com.pfe.dataops.dataopsapi.governance.StewardRole;
import com.pfe.dataops.dataopsapi.governance.access.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccessMatrixService {

    private final AccessMatrixRepository repo;

    public AccessMatrixService(AccessMatrixRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public AccessMatrixResponse list(
            String q,
            String roleStr,
            String sensitivityStr,
            String legalStr,
            int page,
            int size
    ) {
        // Convert filters
        StewardRole role = roleStr != null && !roleStr.isBlank()
                ? StewardRole.fromString(roleStr)
                : null;

        Sensitivity sensitivity = sensitivityStr != null && !sensitivityStr.isBlank()
                ? Sensitivity.fromString(sensitivityStr)
                : null;

        LegalTag legal = legalStr != null && !legalStr.isBlank()
                ? LegalTag.fromString(legalStr)
                : null;

        String qNorm = q != null ? q.toLowerCase(Locale.ROOT).trim() : null;

        List<AccessMatrixEntryEntity> all = repo.findAll();

        Stream<AccessMatrixEntryEntity> stream = all.stream();

        if (qNorm != null && !qNorm.isBlank()) {
            stream = stream.filter(e -> {
                String hay = (e.getPersonName() + " " +
                        e.getPersonEmail() + " " +
                        e.getDatasetName() + " " +
                        e.getDatasetUrn())
                        .toLowerCase(Locale.ROOT);
                return hay.contains(qNorm);
            });
        }

        if (role != null) {
            stream = stream.filter(e -> e.getPersonRole() == role);
        }

        if (sensitivity != null) {
            stream = stream.filter(e -> e.getSensitivity() == sensitivity);
        }

        if (legal != null) {
            stream = stream.filter(e -> e.getLegalTags() != null && e.getLegalTags().contains(legal));
        }

        List<AccessMatrixEntryEntity> filtered = stream.toList();
        long total = filtered.size();

        // pagination
        int pageIndex = Math.max(page - 1, 0);
        int from = pageIndex * size;
        int to = Math.min(from + size, filtered.size());

        List<AccessEntryDto> items = filtered.stream()
                .sorted((a, b) -> {
                    int cmp = a.getPersonName().compareToIgnoreCase(b.getPersonName());
                    if (cmp != 0) return cmp;
                    return a.getDatasetName().compareToIgnoreCase(b.getDatasetName());
                })
                .skip(from)
                .limit(size)
                .map(this::toDto)
                .collect(Collectors.toList());

        return new AccessMatrixResponse(items, total);
    }

    private AccessEntryDto toDto(AccessMatrixEntryEntity e) {
        PersonDto person = new PersonDto(
                e.getPersonName(),
                e.getPersonEmail(),
                e.getPersonRole()
        );

        DatasetDto dataset = new DatasetDto(
                e.getDatasetUrn(),
                e.getDatasetName(),
                e.getSensitivity(),
                e.getLegalTags()
        );

        return new AccessEntryDto(
                e.getId(),
                person,
                dataset,
                e.getAccessLevel(),
                e.isInherited()
        );
    }
}
