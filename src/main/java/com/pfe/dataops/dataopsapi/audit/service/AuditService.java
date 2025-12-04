// src/main/java/com/pfe/dataops/dataopsapi/audit/service/AuditService.java
package com.pfe.dataops.dataopsapi.audit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pfe.dataops.dataopsapi.audit.dto.AuditEventDto;
import com.pfe.dataops.dataopsapi.audit.entity.AuditLog;
import com.pfe.dataops.dataopsapi.audit.repo.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repo;
    private final ObjectMapper objectMapper;

    public AuditLog log(AuditEventDto dto) {
        AuditLog log = AuditLog.builder()
                .actor(dto.getActor())
                .action(dto.getAction())
                .resourceType(dto.getResourceType())
                .resourceId(dto.getResourceId())
                .payloadBefore(dto.getPayloadBefore())
                .payloadAfter(dto.getPayloadAfter())
                .payloadDiff(dto.getPayloadDiff())
                .ipAddress(dto.getIpAddress())
                .userAgent(dto.getUserAgent())
                .build();
        return repo.save(log);
    }

    public AuditLog logWithRequest(AuditEventDto dto, HttpServletRequest request) {
        if (dto.getIpAddress() == null) {
            dto.setIpAddress(request.getRemoteAddr());
        }
        if (dto.getUserAgent() == null) {
            dto.setUserAgent(request.getHeader("User-Agent"));
        }
        return log(dto);
    }

    public ObjectNode computeDiff(JsonNode before, JsonNode after) {
        ObjectNode diff = objectMapper.createObjectNode();
        if (before == null || after == null) {
            return diff;
        }
        Iterator<String> fieldNames = before.fieldNames();
        while (fieldNames.hasNext()) {
            String f = fieldNames.next();
            JsonNode oldVal = before.get(f);
            JsonNode newVal = after.get(f);
            if (!Objects.equals(oldVal, newVal)) {
                ObjectNode change = objectMapper.createObjectNode();
                change.set("old", oldVal);
                change.set("new", newVal);
                diff.set(f, change);
            }
        }
        return diff;
    }

    /**
     * Recherche pour l’UI (tableau Angular) avec pagination en mémoire.
     */
    public Page<AuditLog> search(
            String actor,
            String action,
            String resourceType,
            String resourceId,
            Instant fromTs,
            Instant toTs,
            int page,
            int size
    ) {
        // 1) Charger tous les logs triés du plus récent au plus ancien
        var sort = Sort.by(Sort.Direction.DESC, "timestamp");
        var all = repo.findAll(sort);  // List<AuditLog>

        // 2) Filtres en mémoire
        var stream = all.stream();

        if (actor != null && !actor.isBlank()) {
            stream = stream.filter(log -> actor.equals(log.getActor()));
        }
        if (action != null && !action.isBlank()) {
            stream = stream.filter(log -> action.equals(log.getAction()));
        }
        if (resourceType != null && !resourceType.isBlank()) {
            stream = stream.filter(log -> resourceType.equals(log.getResourceType()));
        }
        if (resourceId != null && !resourceId.isBlank()) {
            stream = stream.filter(log -> resourceId.equals(log.getResourceId()));
        }
        if (fromTs != null) {
            stream = stream.filter(log -> {
                var ts = log.getTimestamp();
                return ts != null && !ts.isBefore(fromTs);
            });
        }
        if (toTs != null) {
            stream = stream.filter(log -> {
                var ts = log.getTimestamp();
                return ts != null && !ts.isAfter(toTs);
            });
        }

        var filtered = stream.toList();
        int total = filtered.size();

        // 3) Pagination en mémoire
        int fromIndex = page * size;
        if (fromIndex >= total) {
            return new PageImpl<>(
                    java.util.List.of(),
                    PageRequest.of(page, size, sort),
                    total
            );
        }
        int toIndex = Math.min(fromIndex + size, total);
        var slice = filtered.subList(fromIndex, toIndex);

        return new PageImpl<>(
                slice,
                PageRequest.of(page, size, sort),
                total
        );
    }
}
