package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineage/column-edges")
public class ColumnLineageEdgeCrudController {

    private final ColumnLineageEdgeRepository edgeRepo;
    private final DatasetColumnRepository columnRepo;

    public ColumnLineageEdgeCrudController(ColumnLineageEdgeRepository edgeRepo,
                                           DatasetColumnRepository columnRepo) {
        this.edgeRepo = edgeRepo;
        this.columnRepo = columnRepo;
    }

    private ColumnEdgeCrudDto toDto(ColumnLineageEdgeEntity e) {
        return new ColumnEdgeCrudDto(
                e.getId(),
                e.getFromColumn().getId(),
                e.getToColumn().getId(),
                e.getKind()
        );
    }

    // GET /api/lineage/column-edges?datasetId=14
    @GetMapping
    public List<ColumnEdgeCrudDto> list(@RequestParam Long datasetId) {
        return edgeRepo
                .findByFromColumn_Dataset_IdOrToColumn_Dataset_Id(datasetId, datasetId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ColumnEdgeCrudDto get(@PathVariable Long id) {
        return edgeRepo.findById(id).map(this::toDto).orElseThrow();
    }

    @PostMapping
    public ColumnEdgeCrudDto create(@RequestBody ColumnEdgeCrudDto req) {
        var from = columnRepo.findById(req.fromColumnId()).orElseThrow();
        var to   = columnRepo.findById(req.toColumnId()).orElseThrow();

        var e = new ColumnLineageEdgeEntity();
        e.setFromColumn(from);
        e.setToColumn(to);
        e.setKind(req.kind());
        e = edgeRepo.save(e);

        return toDto(e);
    }

    @PutMapping("/{id}")
    public ColumnEdgeCrudDto update(@PathVariable Long id,
                                    @RequestBody ColumnEdgeCrudDto req) {
        var e = edgeRepo.findById(id).orElseThrow();
        var from = columnRepo.findById(req.fromColumnId()).orElseThrow();
        var to   = columnRepo.findById(req.toColumnId()).orElseThrow();

        e.setFromColumn(from);
        e.setToColumn(to);
        e.setKind(req.kind());
        e = edgeRepo.save(e);

        return toDto(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        edgeRepo.deleteById(id);
    }
}
