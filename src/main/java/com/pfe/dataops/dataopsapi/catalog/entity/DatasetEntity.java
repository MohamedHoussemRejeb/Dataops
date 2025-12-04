package com.pfe.dataops.dataopsapi.catalog.entity;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.Sensitivity;
import com.pfe.dataops.dataopsapi.catalog.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "datasets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DatasetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 500)
    private String urn;

    @Column(nullable=false, length = 160)
    private String name;

    @Column(length = 4000)
    private String description;

    @Column(length = 120)
    private String domain;

    /* 👇 plus de @ManyToOne vers OwnerEntity */

    @Column(name = "owner_name", length = 200)
    private String ownerName;

    @Column(name = "owner_email", length = 200)
    private String ownerEmail;

    // (optionnel mais très utile pour Keycloak)
    @Column(name = "owner_keycloak_id", length = 64)
    private String ownerKeycloakId;

    /** tags “libres” */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "dataset_tags",
            joinColumns=@JoinColumn(name="dataset_id")
    )
    @Column(name="tag", length = 80)
    private List<String> tags = new ArrayList<>();

    /** dépendances (ids/urn de datasets en entrée) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="dataset_dependencies",
            joinColumns=@JoinColumn(name="dataset_id")
    )
    @Column(name="dependency", length = 200)
    private List<String> dependencies = new ArrayList<>();

    /** Gouvernance */
    @Enumerated(EnumType.STRING)
    private Sensitivity sensitivity;

    @ElementCollection(
            targetClass = LegalTag.class,
            fetch = FetchType.EAGER
    )
    @CollectionTable(name="dataset_legal", joinColumns=@JoinColumn(name="dataset_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="tag", length = 40)
    private List<LegalTag> legal = new ArrayList<>();

    private Integer trust;
    @Enumerated(EnumType.STRING)
    private Risk risk;

    @Enumerated(EnumType.STRING)
    private LoadStatus lastStatus;
    private Instant    lastEndedAt;
    private Integer    lastDurationSec;

    @Enumerated(EnumType.STRING)
    private SlaFrequency slaFrequency;
    @Column(length = 10)
    private String       slaExpectedBy;
    private Integer      slaMaxDelayMin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sla_config", columnDefinition = "jsonb")
    private String slaConfig;
}
