package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.runs.dto.EtlRunDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/runs")
@CrossOrigin(origins = "http://localhost:4200")
public class RunsListController {

    private final RunsListService service;

    public RunsListController(RunsListService service) {
        this.service = service;
    }

    // GET /api/runs?flowType=...&status=...&q=...
    @GetMapping
    public List<EtlRunDto> list(
            @RequestParam(required = false) String flowType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q
    ) {
        return service.listAll(flowType, status, q);
    }

    // POST /api/runs/{id}/retry
    @PostMapping("/{id}/retry")
    public ResponseEntity<?> retry(@PathVariable String id) {
        EtlRunDto created = service.retrySimple(id);
        if (created == null) return ResponseEntity.notFound().build();
        return ResponseEntity.accepted().body(created);
    }

    // POST /api/runs/{id}/retry-advanced
    @PostMapping("/{id}/retry-advanced")
    public ResponseEntity<?> retryAdvanced(
            @PathVariable String id,
            @RequestBody Map<String,Object> params // RetryParams côté front
    ) {
        EtlRunDto created = service.retryAdvanced(id, params);
        if (created == null) return ResponseEntity.notFound().build();
        return ResponseEntity.accepted().body(created);
    }

    // POST /api/runs/simulate
    @PostMapping("/simulate")
    public ResponseEntity<EtlRunDto> simulate() {
        EtlRunDto created = service.simulateRandomRun();
        return ResponseEntity.accepted().body(created);
    }
}