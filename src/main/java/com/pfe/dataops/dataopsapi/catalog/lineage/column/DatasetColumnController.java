package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/datasets")
public class DatasetColumnController {

    private final DatasetColumnRepository columnRepo;

    public DatasetColumnController(DatasetColumnRepository columnRepo) {
        this.columnRepo = columnRepo;
    }

    private DatasetColumnDto toDto(DatasetColumnEntity e) {
        return new DatasetColumnDto(
                e.getId(),
                e.getName(),
                e.getType(),
                e.getSensitivity()
        );
    }

    // GET /api/catalog/datasets/{datasetId}/columns
    @GetMapping("/{datasetId}/columns")
    public List<DatasetColumnDto> list(@PathVariable Long datasetId) {
        return columnRepo.findByDataset_Id(datasetId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // GET /api/catalog/datasets/{datasetId}/columns/{id}
    @GetMapping("/{datasetId}/columns/{id}")
    public DatasetColumnDto get(@PathVariable Long datasetId, @PathVariable Long id) {
        var e = columnRepo.findById(id).orElseThrow();
        if (!e.getDataset().getId().equals(datasetId)) {
            throw new IllegalArgumentException("Column not in dataset");
        }
        return toDto(e);
    }

    // POST /api/catalog/datasets/{datasetId}/columns
    @PostMapping("/{datasetId}/columns")
    public DatasetColumnDto create(@PathVariable Long datasetId,
                                   @RequestBody DatasetColumnDto req) {
        var dataset = new DatasetEntity();
        dataset.setId(datasetId);

        var e = new DatasetColumnEntity();
        e.setDataset(dataset);
        e.setName(req.name());
        e.setType(req.type());
        e.setSensitivity(req.sensitivity());

        e = columnRepo.save(e);
        return toDto(e);
    }

    // PUT /api/catalog/datasets/{datasetId}/columns/{id}
    @PutMapping("/{datasetId}/columns/{id}")
    public DatasetColumnDto update(@PathVariable Long datasetId,
                                   @PathVariable Long id,
                                   @RequestBody DatasetColumnDto req) {
        var e = columnRepo.findById(id).orElseThrow();
        if (!e.getDataset().getId().equals(datasetId)) {
            throw new IllegalArgumentException("Column not in dataset");
        }
        e.setName(req.name());
        e.setType(req.type());
        e.setSensitivity(req.sensitivity());
        e = columnRepo.save(e);
        return toDto(e);
    }

    // DELETE /api/catalog/datasets/{datasetId}/columns/{id}
    @DeleteMapping("/{datasetId}/columns/{id}")
    public void delete(@PathVariable Long datasetId, @PathVariable Long id) {
        var e = columnRepo.findById(id).orElseThrow();
        if (!e.getDataset().getId().equals(datasetId)) {
            throw new IllegalArgumentException("Column not in dataset");
        }
        columnRepo.delete(e);
    }
}
