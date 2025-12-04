// src/main/java/com/pfe/dataops/dataopsapi/governance/access/AccessMatrixEntryEntity.java
package com.pfe.dataops.dataopsapi.governance.access;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.governance.LegalTag;
import com.pfe.dataops.dataopsapi.governance.Sensitivity;
import com.pfe.dataops.dataopsapi.governance.StewardRole;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "access_matrix_entries")
public class AccessMatrixEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------- PERSON --------
    @Column(name = "person_name", nullable = false, length = 200)
    private String personName;

    @Column(name = "person_email", nullable = false, length = 200)
    private String personEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_role", nullable = false, length = 20)
    private StewardRole personRole;

    // -------- DATASET (dénormalisé) --------
    @Column(name = "dataset_urn", nullable = false, length = 255)
    private String datasetUrn;

    @Column(name = "dataset_name", nullable = false, length = 255)
    private String datasetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensitivity", length = 30)
    private Sensitivity sensitivity;

    // -------- LEGAL TAGS --------
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "access_matrix_legal_tags",
            joinColumns = @JoinColumn(name = "entry_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tag", length = 30)
    private Set<LegalTag> legalTags = new HashSet<>();

    // -------- ACCESS LEVEL --------
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private StewardRole accessLevel; // OWNER / STEWARD / VIEWER

    @Column(name = "inherited", nullable = false)
    private boolean inherited = false;

    // -------- FK logique vers DatasetEntity (sur urn) --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "dataset_urn",          // la colonne qui existe déjà
            referencedColumnName = "urn",  // colonne de DatasetEntity
            insertable = false,
            updatable = false
    )
    private DatasetEntity dataset;

    // ===== getters / setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public String getPersonEmail() { return personEmail; }
    public void setPersonEmail(String personEmail) { this.personEmail = personEmail; }

    public StewardRole getPersonRole() { return personRole; }
    public void setPersonRole(StewardRole personRole) { this.personRole = personRole; }

    public String getDatasetUrn() { return datasetUrn; }
    public void setDatasetUrn(String datasetUrn) { this.datasetUrn = datasetUrn; }

    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }

    public Sensitivity getSensitivity() { return sensitivity; }
    public void setSensitivity(Sensitivity sensitivity) { this.sensitivity = sensitivity; }

    public Set<LegalTag> getLegalTags() { return legalTags; }
    public void setLegalTags(Set<LegalTag> legalTags) { this.legalTags = legalTags; }

    public StewardRole getAccessLevel() { return accessLevel; }
    public void setAccessLevel(StewardRole accessLevel) { this.accessLevel = accessLevel; }

    public boolean isInherited() { return inherited; }
    public void setInherited(boolean inherited) { this.inherited = inherited; }

    public DatasetEntity getDataset() { return dataset; }
    public void setDataset(DatasetEntity dataset) { this.dataset = dataset; }
}
