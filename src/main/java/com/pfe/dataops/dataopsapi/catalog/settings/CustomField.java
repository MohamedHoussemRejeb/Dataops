package com.pfe.dataops.dataopsapi.catalog.settings;

import jakarta.persistence.*;

@Entity
@Table(name = "custom_fields")
public class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field_key", nullable = false, unique = true)  // ✅ nouveau nom SQL
    private String key;       // "sla"

    @Column(nullable = false)
    private String label;     // "SLA (ex: daily 06:00)"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType type;   // TEXT / NUMBER / SELECT

    @Column
    private String options;   // "LOW|MEDIUM|HIGH"

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
