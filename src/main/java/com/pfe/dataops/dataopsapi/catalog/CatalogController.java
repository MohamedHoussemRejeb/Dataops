// src/main/java/com/pfe/dataops/dataopsapi/catalog/CatalogController.java
package com.pfe.dataops.dataopsapi.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.dataops.dataopsapi.catalog.dto.DatasetDto;
import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.lineage.LineageService;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import com.pfe.dataops.dataopsapi.models.LineageGraph;
import com.pfe.dataops.dataopsapi.quality.SlaConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;
    private final LineageService lineageService;
    private final DatasetRepository datasetRepository;
    private final ObjectMapper objectMapper;

    public CatalogController(CatalogService catalogService,
                             LineageService lineageService,
                             DatasetRepository datasetRepository,
                             ObjectMapper objectMapper) {
        this.catalogService = catalogService;
        this.lineageService = lineageService;
        this.datasetRepository = datasetRepository;
        this.objectMapper = objectMapper;
    }

    // ========== DATASETS POUR ANGULAR ==========

    // GET http://localhost:8083/api/catalog/datasets
    @GetMapping("/datasets")
    public List<DatasetDto> listDatasets() {
        return catalogService.list();  // <-- méthode list()
    }

    // GET http://localhost:8083/api/catalog/datasets/{id}
    @GetMapping("/datasets/{id}")
    public DatasetDto getDataset(@PathVariable Long id) {
        return catalogService.get(id);  // <-- méthode get()
    }

    // POST http://localhost:8083/api/catalog/datasets
    // ⭐ création d’un dataset
    @PostMapping("/datasets")
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetDto createDataset(@RequestBody DatasetDto dto) {
        return catalogService.create(dto);
    }

    // PUT http://localhost:8083/api/catalog/datasets/{id}
    // ⭐ mise à jour d’un dataset
    @PutMapping("/datasets/{id}")
    public DatasetDto updateDataset(@PathVariable Long id,
                                    @RequestBody DatasetDto dto) {
        return catalogService.update(id, dto);
    }

    // DELETE http://localhost:8083/api/catalog/datasets/{id}
    // ⭐ suppression d’un dataset (corrige ton 405)
    @DeleteMapping("/datasets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDataset(@PathVariable Long id) {
        catalogService.delete(id);
    }

    // ========== SLA CONFIG ==========

    // GET http://localhost:8083/api/catalog/datasets/{id}/sla-config
    @GetMapping("/datasets/{id}/sla-config")
    public SlaConfig getSlaConfig(@PathVariable Long id) throws JsonProcessingException {
        DatasetEntity ds = datasetRepository.findById(id).orElseThrow();

        if (ds.getSlaConfig() == null) {
            return new SlaConfig(); // objet vide par défaut
        }

        return objectMapper.readValue(ds.getSlaConfig(), SlaConfig.class);
    }

    // PUT http://localhost:8083/api/catalog/datasets/{id}/sla-config
    @PutMapping("/datasets/{id}/sla-config")
    public SlaConfig updateSlaConfig(
            @PathVariable Long id,
            @RequestBody SlaConfig config
    ) throws JsonProcessingException {

        DatasetEntity ds = datasetRepository.findById(id).orElseThrow();

        String json = objectMapper.writeValueAsString(config);
        ds.setSlaConfig(json);

        datasetRepository.save(ds);

        return config;
    }

    // ========== LINEAGE ==========

    // GET http://localhost:8083/api/catalog/datasets/{id}/lineage
    @GetMapping("/datasets/{id}/lineage")
    public LineageGraph getLineage(@PathVariable Long id) {
        return lineageService.getLineageForDataset(id);
    }
}
