// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceHealthController.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/source-health")
@CrossOrigin(origins = "http://localhost:4200")
public class SourceHealthController {

    private final SourceHealthService service;

    public SourceHealthController(SourceHealthService service) {
        this.service = service;
    }

    @GetMapping
    public List<SourceHealthDto> listAll() {
        return service.listAll();
    }

    @PostMapping("/test-all")
    public List<SourceHealthDto> testAll() {
        return service.testAll();
    }

    @PostMapping("/{id}/test-connection")
    public SourceHealthDto testOne(@PathVariable("id") Long id) {
        return service.testOne(id);
    }
}
