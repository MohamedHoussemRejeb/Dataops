// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditEventDto.java
package com.pfe.dataops.dataopsapi.audit;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventDto {
    private Long id;
    private String kind;          // 'upload' | 'retry' | ...
    private Instant at;          // mapped to 'at' in TS
    private AuditUserDto user;
    private JsonNode meta;       // any JSON
}