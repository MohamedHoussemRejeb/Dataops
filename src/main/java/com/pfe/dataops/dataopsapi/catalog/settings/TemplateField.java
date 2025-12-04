package com.pfe.dataops.dataopsapi.catalog.settings;

import jakarta.persistence.*;

@Entity
@Table(name = "template_fields")
public class TemplateField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private SchemaTemplate template;

    @Column(name = "field_key", nullable = false)  // ✅ évite KEY réservé
    private String key;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType type;

    @Column
    private String options; // "NONE|PARTIAL|FULL"

    @Column
    private Boolean required = false;

    @Column
    private String help;

    // ====== Getters & Setters ======

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SchemaTemplate getTemplate() {
        return template;
    }

    public void setTemplate(SchemaTemplate template) {
        this.template = template;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}
