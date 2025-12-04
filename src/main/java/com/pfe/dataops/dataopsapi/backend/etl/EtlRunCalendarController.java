// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRunCalendarController.java
package com.pfe.dataops.dataopsapi.backend.etl;

import com.pfe.dataops.dataopsapi.backend.etl.dto.EtlRunDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/runs/calendar") // <-- CHANGÉ ICI
@CrossOrigin(origins = "http://localhost:4200")
public class EtlRunCalendarController {

    private final EtlRunApiService service;

    public EtlRunCalendarController(EtlRunApiService service) {
        this.service = service;
    }

    // utilisé par RunsCalendarComponent -> runs.list()
    @GetMapping
    public List<EtlRunDto> listAll() {
        return service.listAll(); // renvoie List<EtlRunDto>
    }

    // détail d'une pastille du calendrier
    @GetMapping("{id}")
    public EtlRunDto getOne(@PathVariable Long id) {
        return service.getOne(id);
    }
}
