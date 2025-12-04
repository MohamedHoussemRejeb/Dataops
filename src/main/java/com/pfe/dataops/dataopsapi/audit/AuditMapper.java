package com.pfe.dataops.dataopsapi.audit;
import com.pfe.dataops.dataopsapi.audit.AuditEventDto;
import com.pfe.dataops.dataopsapi.audit.AuditUserDto;
import com.pfe.dataops.dataopsapi.audit.AuditEventEntity;

public class AuditMapper {

    public static AuditEventDto toDto(AuditEventEntity event) {
        return AuditEventDto.builder()
                .id(event.getId())
                .kind(event.getKind())
                .at(event.getAt())              // <-- was getTimestamp()
                .user(AuditUserDto.builder()
                        .name(event.getUserName())
                        .email(event.getUserEmail())
                        .build())
                .meta(event.getMeta())
                .build();
    }
}