package com.pfe.dataops.dataopsapi.models;


import com.pfe.dataops.dataopsapi.catalog.enums.Layer;

public class NodeMeta {

    private String id;
    private String label;
    private Layer layer;
    private String owner;

    public NodeMeta() {}

    public NodeMeta(String id, String label, Layer layer, String owner) {
        this.id = id;
        this.label = label;
        this.layer = layer;
        this.owner = owner;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public Layer getLayer() { return layer; }
    public String getOwner() { return owner; }

    public void setId(String id) { this.id = id; }
    public void setLabel(String label) { this.label = label; }
    public void setLayer(Layer layer) { this.layer = layer; }
    public void setOwner(String owner) { this.owner = owner; }
}

