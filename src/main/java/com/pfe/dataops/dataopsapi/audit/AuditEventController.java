// src/main/java/com/pfe/dataops/dataopsapi/audit/AuditEventController.java
package com.pfe.dataops.dataopsapi.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit/events")
@RequiredArgsConstructor
@CrossOrigin
public class AuditEventController {

    private final AuditEventService service;

    /** Used by the timeline */
    @GetMapping
    public List<AuditEventDto> list() {
        return service.listRecent();
    }

    /** Used by other services or front to log activity */
    @PostMapping
    public AuditEventDto create(@RequestBody AuditEventDto dto) {
        return service.create(dto);
    }
}
