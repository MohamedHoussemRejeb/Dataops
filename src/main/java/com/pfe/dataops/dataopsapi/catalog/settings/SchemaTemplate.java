package com.pfe.dataops.dataopsapi.catalog.settings;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schema_templates")
public class SchemaTemplate {

    @Id
    @Column(name = "id", nullable = false)
    private String id;    // "crm", "erp", "hr"

    @Column(nullable = false)
    private String name;  // "CRM"

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateField> fields = new ArrayList<>();

    // ====== Getters & Setters ======

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TemplateField> getFields() {
        return fields;
    }

    public void setFields(List<TemplateField> fields) {
        this.fields = fields;
    }
}
