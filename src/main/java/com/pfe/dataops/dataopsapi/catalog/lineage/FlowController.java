package com.pfe.dataops.dataopsapi.catalog.lineage;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lineage")
@RequiredArgsConstructor
public class FlowController {

    private final DatasetRepository datasetRepository;
    private final LineageNodeRepository nodeRepository;
    private final LineageEdgeRepository edgeRepository;

    @GetMapping("/flow")
    public FlowGraphDto flow(
            @RequestParam("from") String fromUrn,
            @RequestParam(name = "depth", defaultValue = "1") int depth,
            @RequestParam(name = "type",  defaultValue = "downstream") String type
    ) {
        // 1) retrouver le dataset par URN
        DatasetEntity dataset = datasetRepository.findByUrn(fromUrn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Dataset non trouvé pour l’URN " + fromUrn
                ));

        Long datasetId = dataset.getId();

        // 2) récupérer tous les nœuds & arêtes pour ce dataset
        List<LineageNodeEntity> nodes = nodeRepository.findByDatasetId(datasetId);
        List<LineageEdgeEntity> edges = edgeRepository.findByDatasetId(datasetId);

        if (nodes.isEmpty()) {
            // aucun graph -> retourner vide, pas d’erreur 500
            return new FlowGraphDto(Collections.emptyList(), Collections.emptyList());
        }

        // 3) construire une map id -> node
        Map<String, LineageNodeEntity> byId = nodes.stream()
                .collect(Collectors.toMap(LineageNodeEntity::getTechnicalId, n -> n));

        // 4) choisir un nœud racine : celui dont le label ou le technicalId matche l’URN,
        // sinon on prend n’importe lequel pour ne pas planter
        LineageNodeEntity root = nodes.stream()
                .filter(n -> fromUrn.equalsIgnoreCase(n.getLabel())
                        || fromUrn.equalsIgnoreCase(n.getTechnicalId()))
                .findFirst()
                .orElse(nodes.get(0));

        // 5) BFS pour calculer le "level" (profondeur)
        Map<String, Integer> levelById = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();

        levelById.put(root.getTechnicalId(), 0);
        queue.add(root.getTechnicalId());

        boolean downstream = !"upstream".equalsIgnoreCase(type);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentLevel = levelById.getOrDefault(current, 0);
            if (currentLevel >= depth) continue;

            for (LineageEdgeEntity e : edges) {
                String next;
                if (downstream && current.equals(e.getFromId())) {
                    next = e.getToId();
                } else if (!downstream && current.equals(e.getToId())) {
                    next = e.getFromId();
                } else {
                    continue;
                }

                if (!levelById.containsKey(next)) {
                    levelById.put(next, currentLevel + 1);
                    queue.add(next);
                }
            }
        }

        // 6) transformer en DTOs
        List<FlowNodeDto> nodeDtos = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : levelById.entrySet()) {
            LineageNodeEntity n = byId.get(entry.getKey());
            if (n == null) continue;

            String typeStr = mapLayerToType(n.getLayer() != null ? n.getLayer().name() : null);
            int level = entry.getValue();

            nodeDtos.add(new FlowNodeDto(
                    n.getTechnicalId(),
                    n.getLabel(),
                    typeStr,
                    level
            ));
        }

        List<FlowEdgeDto> edgeDtos = edges.stream()
                .filter(e -> levelById.containsKey(e.getFromId()) && levelById.containsKey(e.getToId()))
                .map(e -> new FlowEdgeDto(e.getFromId(), e.getToId(), "runs"))
                .toList();

        return new FlowGraphDto(nodeDtos, edgeDtos);
    }

    /** mappe la colonne layer vers dataset / job / report pour le front */
    private String mapLayerToType(String layer) {
        if (layer == null) return "dataset";
        String l = layer.toUpperCase(Locale.ROOT);

        if (l.contains("JOB") || l.contains("ETL") || l.contains("TRANSFORM")) {
            return "job";
        }
        if (l.contains("REPORT") || l.contains("DASHBOARD")) {
            return "report";
        }
        // RAW, SOURCE, DATA, STAGE, etc.
        return "dataset";
    }
}
