// src/main/java/com/pfe/dataops/dataopsapi/audit/web/AuditController.java
package com.pfe.dataops.dataopsapi.audit.web;

import com.pfe.dataops.dataopsapi.audit.entity.AuditLog;
import com.pfe.dataops.dataopsapi.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public Page<AuditLog> search(
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) Instant fromTs,
            @RequestParam(required = false) Instant toTs,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return auditService.search(actor, action, resourceType, resourceId, fromTs, toTs, page, size);
    }
}
