package com.pfe.dataops.dataopsapi.backend.etl.catalog;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.dto.DatasetDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.pfe.dataops.dataopsapi.models.LineageGraph;
import com.pfe.dataops.dataopsapi.models.NodeMeta;
import com.pfe.dataops.dataopsapi.models.EdgeMeta;
import com.pfe.dataops.dataopsapi.catalog.enums.Layer;

@RestController
@RequestMapping("/api/datasets")
@CrossOrigin(origins = "http://localhost:4200")
public class DatasetController {

    private final DatasetService service;

    public DatasetController(DatasetService service) {
        this.service = service;
    }

    @GetMapping
    public List<DatasetDto> list() {
        return service.listAll();
    }
    @GetMapping("/datasets/{id}/lineage")
    public LineageGraph getLineage(@PathVariable Long id) {

        List<NodeMeta> nodes = List.of(
                new NodeMeta("s.crm", "CRM API", Layer.source, "Ana"),
                new NodeMeta("s.erp", "ERP PostgreSQL", Layer.source, "Marc"),
                new NodeMeta("t.crm_raw", "stg_crm_raw", Layer.staging, null),
                new NodeMeta("t.erp_raw", "stg_erp_raw", Layer.staging, null),
                new NodeMeta("w.catalog_articles", "Catalogue Articles", Layer.dw, "Alice Martin")
        );

        List<EdgeMeta> edges = List.of(
                new EdgeMeta("s.crm", "t.crm_raw"),
                new EdgeMeta("s.erp", "t.erp_raw"),
                new EdgeMeta("t.crm_raw", "w.catalog_articles"),
                new EdgeMeta("t.erp_raw", "w.catalog_articles")
        );

        return new LineageGraph(nodes, edges);
    }
}

