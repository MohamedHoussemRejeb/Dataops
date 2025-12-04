package com.pfe.dataops.dataopsapi.models;


import java.util.List;

public class LineageGraph {

    private List<NodeMeta> nodes;
    private List<EdgeMeta> edges;

    public LineageGraph() {}

    public LineageGraph(List<NodeMeta> nodes, List<EdgeMeta> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<NodeMeta> getNodes() { return nodes; }
    public List<EdgeMeta> getEdges() { return edges; }

    public void setNodes(List<NodeMeta> nodes) { this.nodes = nodes; }
    public void setEdges(List<EdgeMeta> edges) { this.edges = edges; }
}

