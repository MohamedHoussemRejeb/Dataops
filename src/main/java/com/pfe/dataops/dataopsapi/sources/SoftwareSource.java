// src/main/java/com/pfe/dataops/dataopsapi/sources/SoftwareSource.java
package com.pfe.dataops.dataopsapi.sources;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "software_sources")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SoftwareSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160, unique = true)
    private String name;

    @Column(length = 80)
    private String vendor;

    @Column(length = 40)
    private String type;    // "db", "file", "api", "saas", ...

    @Column(length = 120)
    private String licence;

    @Column(length = 120)
    private String owner;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "software_source_tags",
            joinColumns = @JoinColumn(name = "source_id")
    )
    @Column(name = "tag", length = 80)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(length = 500)
    private String connectionUrl;   // ex: jdbc:postgresql://... ou ftp://...

    // 👇 Infos techniques pour les tests de connexion
    @Column(length = 200)
    private String host;

    private Integer port;

    @Column(length = 120)
    private String dbName;      // pour Postgres

    @Column(length = 120)
    private String username;

    @Column(length = 200)
    private String password;    // pour le PFE ok en clair, en vrai → chiffré ;)
}
