// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/LineageNodeEntity.java
package com.pfe.dataops.dataopsapi.catalog.lineage;

import com.pfe.dataops.dataopsapi.catalog.enums.Layer;
import jakarta.persistence.*;

@Entity
@Table(name = "catalog_lineage_node")
public class LineageNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_id", nullable = false)
    private Long datasetId;

    @Column(name = "technical_id", nullable = false, length = 100)
    private String technicalId;

    private String label;

    @Enumerated(EnumType.STRING)
    private Layer layer;

    private String owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public String getTechnicalId() {
        return technicalId;
    }

    public void setTechnicalId(String technicalId) {
        this.technicalId = technicalId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    // getters / setters
}
