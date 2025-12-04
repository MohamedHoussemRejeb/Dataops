// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/column/ColumnLineageService.java
package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ColumnLineageService {

    private final DatasetColumnRepository columnRepo;
    private final ColumnLineageEdgeRepository edgeRepo;

    public ColumnLineageService(DatasetColumnRepository columnRepo,
                                ColumnLineageEdgeRepository edgeRepo) {
        this.columnRepo = columnRepo;
        this.edgeRepo = edgeRepo;
    }

    // ----------- SEARCH GLOBAL -----------
    public ColumnSearchResponse search(String q) {

        String query = (q == null ? "" : q.trim());

        List<DatasetColumnEntity> cols =
                query.isEmpty()
                        ? columnRepo.findAll()
                        : columnRepo.findByNameContainingIgnoreCase(query);

        var items = cols.stream()
                .map(c -> new ColumnSearchItemDto(
                        c.getDataset().getUrn(),
                        c.getDataset().getName(),
                        c.getName(),
                        c.getType(),
                        c.getSensitivity()
                ))
                .toList();

        return new ColumnSearchResponse(items);
    }

    // ----------- GRAPH POUR UNE COLONNE -----------
    public ColumnGraphDto columnGraph(String datasetUrn, String columnName) {

        // on récupère la colonne principale
        List<DatasetColumnEntity> cols =
                columnRepo.findByDataset_UrnAndNameIgnoreCase(datasetUrn, columnName);
        if (cols.isEmpty()) {
            return new ColumnGraphDto(List.of(), List.of());
        }
        DatasetColumnEntity col = cols.stream().findFirst().orElseThrow();

        // liens où cette colonne est source ou cible
        List<ColumnLineageEdgeEntity> edges =
                edgeRepo.findByFromColumnOrToColumn(col, col);

        // pour l’instant: 1 node central + les extrémités de chaque edge
        var centerNode = new ColumnGraphNodeDto(
                "center",
                col.getDataset().getName() + "." + col.getName(),
                "column"
        );

        // tu peux enrichir ici mais ce n’est pas obligatoire pour compiler
        return new ColumnGraphDto(
                List.of(centerNode),
                List.of() // à remplir quand tu voudras dessiner le graphe complet
        );
    }
}
