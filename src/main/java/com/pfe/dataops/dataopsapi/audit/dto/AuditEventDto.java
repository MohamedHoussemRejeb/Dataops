// src/main/java/com/pfe/dataops/dataopsapi/audit/dto/AuditEventDto.java
package com.pfe.dataops.dataopsapi.audit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventDto {

    private String actor;
    private String action;
    private String resourceType;
    private String resourceId;

    private JsonNode payloadBefore;
    private JsonNode payloadAfter;
    private JsonNode payloadDiff;

    private String ipAddress;
    private String userAgent;
}
