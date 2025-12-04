// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/LineageService.java
package com.pfe.dataops.dataopsapi.catalog.lineage;

import com.pfe.dataops.dataopsapi.models.EdgeMeta;
import com.pfe.dataops.dataopsapi.models.LineageGraph;
import com.pfe.dataops.dataopsapi.models.NodeMeta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineageService {

    private final LineageNodeRepository nodeRepo;
    private final LineageEdgeRepository edgeRepo;

    public LineageService(LineageNodeRepository nodeRepo,
                          LineageEdgeRepository edgeRepo) {
        this.nodeRepo = nodeRepo;
        this.edgeRepo = edgeRepo;
    }

    public LineageGraph getLineageForDataset(Long datasetId) {
        List<NodeMeta> nodes = nodeRepo.findByDatasetId(datasetId).stream()
                .map(e -> new NodeMeta(
                        e.getTechnicalId(),
                        e.getLabel(),
                        e.getLayer(),
                        e.getOwner()
                ))
                .toList();

        List<EdgeMeta> edges = edgeRepo.findByDatasetId(datasetId).stream()
                .map(e -> new EdgeMeta(
                        e.getFromId(),
                        e.getToId()
                ))
                .toList();

        return new LineageGraph(nodes, edges);
    }
}
