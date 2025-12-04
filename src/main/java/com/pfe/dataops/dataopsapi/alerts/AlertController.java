// src/main/java/com/pfe/dataops/dataopsapi/alerts/AlertsController.java
package com.pfe.dataops.dataopsapi.alerts;

import com.pfe.dataops.dataopsapi.alerts.dto.AckRequest;
import com.pfe.dataops.dataopsapi.alerts.dto.AlertCreateRequest;
import com.pfe.dataops.dataopsapi.alerts.dto.AlertDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*") // utile pour dev Angular en local
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<AlertDto>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<AlertDto> create(@Valid @RequestBody AlertCreateRequest req) {
        var dto = service.create(req);
        return ResponseEntity.created(URI.create("/api/alerts/" + dto.id())).body(dto);
    }

    @PostMapping(value = "/ack", consumes = "application/json")
    public ResponseEntity<Void> ack(@Valid @RequestBody AckRequest req) {
        service.acknowledgeMany(req.ids());
        return ResponseEntity.ok().build();
    }
}
