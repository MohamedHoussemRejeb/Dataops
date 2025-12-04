package com.pfe.dataops.dataopsapi.catalog.lineage.column;

import jakarta.persistence.*;

@Entity
@Table(name = "column_lineage_edge")
public class ColumnLineageEdgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_column_id")
    private DatasetColumnEntity fromColumn;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_column_id")
    private DatasetColumnEntity toColumn;

    @Column(length = 50)
    private String kind; // ex: "copy", "concat", "mask"...

    // --- getters / setters ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public DatasetColumnEntity getFromColumn() { return fromColumn; }

    public void setFromColumn(DatasetColumnEntity fromColumn) { this.fromColumn = fromColumn; }

    public DatasetColumnEntity getToColumn() { return toColumn; }

    public void setToColumn(DatasetColumnEntity toColumn) { this.toColumn = toColumn; }

    public String getKind() { return kind; }

    public void setKind(String kind) { this.kind = kind; }
}
