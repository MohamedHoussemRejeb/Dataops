package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "dataset_columns")
public class DatasetColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dataset_id")
    private DatasetEntity dataset;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 80)
    private String type;

    @Column(length = 80)
    private String sensitivity;

    // --- getters / setters ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public DatasetEntity getDataset() { return dataset; }

    public void setDataset(DatasetEntity dataset) { this.dataset = dataset; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getSensitivity() { return sensitivity; }

    public void setSensitivity(String sensitivity) { this.sensitivity = sensitivity; }
}
