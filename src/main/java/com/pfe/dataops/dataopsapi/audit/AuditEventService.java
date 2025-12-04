// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditEventService.java
package com.pfe.dataops.dataopsapi.audit;

import com.pfe.dataops.dataopsapi.audit.AuditEventDto;
import com.pfe.dataops.dataopsapi.audit.AuditUserDto;
import com.pfe.dataops.dataopsapi.audit.AuditEventEntity;
import com.pfe.dataops.dataopsapi.audit.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditEventService {

    private final AuditEventRepository repo;

    /** 🔹 Utilisé par le timeline : derniers events (ex: 500 derniers) */
    public List<AuditEventDto> listRecent() {
        List<AuditEventEntity> events = repo.findTop500ByOrderByAtDesc();
        return events.stream()
                .map(AuditMapper::toDto)
                .toList();
    }

    /** 🔹 Utilisé par d’autres services ou par le front pour logguer un event */
    public AuditEventDto create(AuditEventDto dto) {

        AuditUserDto user = dto.getUser();

        AuditEventEntity entity = AuditEventEntity.builder()
                .kind(dto.getKind())
                .at(dto.getAt() != null ? dto.getAt() : Instant.now())
                .userName(user != null ? user.getName() : "unknown")
                .userEmail(user != null ? user.getEmail() : null)
                .meta(dto.getMeta())
                .build();

        AuditEventEntity saved = repo.save(entity);
        return AuditMapper.toDto(saved);
    }

    /** (optionnel) si tu veux un filtre temporel */
    public List<AuditEventDto> findEvents(Instant from, Instant to) {
        List<AuditEventEntity> events =
                repo.findAllByAtBetweenOrderByAtDesc(from, to);  // <--- ici aussi
        return events.stream()
                .map(AuditMapper::toDto)
                .toList();
    }
}
