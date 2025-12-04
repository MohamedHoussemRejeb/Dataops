package com.pfe.dataops.dataopsapi.backend.etl.catalog;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.dto.DatasetDto;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Profile("mock")
@RestController("mockCatalogController")
@RequestMapping("/api/catalog")
@CrossOrigin(origins = "http://localhost:4200")
public class CatalogController {

    private final DatasetService service;

    public CatalogController(DatasetService service) { this.service = service; }

    @GetMapping("/datasets")
    public List<DatasetDto> list() { return service.listAll(); }

    @GetMapping("/datasets/{id}")
    public ResponseEntity<DatasetDto> one(@PathVariable String id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

