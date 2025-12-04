package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lineage/columns")
public class ColumnLineageController {

    private final ColumnLineageService service;

    public ColumnLineageController(ColumnLineageService service) {
        this.service = service;
    }

    // /api/lineage/columns/search?q=email
    @GetMapping("/search")
    public ColumnSearchResponse search(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    // /api/lineage/columns/{urn}/{column}
    @GetMapping("/{urn}/{column}")
    public ColumnGraphDto columnGraph(@PathVariable String urn,
                                      @PathVariable String column) {
        return service.columnGraph(urn, column);   // ✅ on appelle bien columnGraph(...)
    }
}
