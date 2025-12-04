package com.pfe.dataops.dataopsapi.runs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-edges")   // 👈 URL exacte attendue par Angular
@RequiredArgsConstructor
public class WorkflowEdgeController {

    private final WorkflowEdgeRepository repo;

    @GetMapping
    public List<WorkflowEdge> findAll() {
        return repo.findAll();
    }

    @PostMapping
    public WorkflowEdge create(@RequestBody WorkflowEdge e) {
        e.setId(null); // force creation
        return repo.save(e);
    }

    @PutMapping("/{id}")
    public WorkflowEdge update(@PathVariable Long id, @RequestBody WorkflowEdge e) {
        e.setId(id);
        return repo.save(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
