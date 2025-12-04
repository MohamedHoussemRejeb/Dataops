// src/main/java/com/pfe/dataops/dataopsapi/catalog/lineage/LineageEdgeEntity.java
package com.pfe.dataops.dataopsapi.catalog.lineage;

import jakarta.persistence.*;

@Entity
@Table(name = "catalog_lineage_edge")
public class LineageEdgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_id", nullable = false)
    private Long datasetId;

    @Column(name = "from_id", nullable = false, length = 100)
    private String fromId;

    @Column(name = "to_id", nullable = false, length = 100)
    private String toId;

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

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    // getters / setters
}
