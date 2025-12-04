package com.pfe.dataops.dataopsapi.catalog.settings;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_settings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AppSettings {

    @Id
    private Long id = 1L; // une seule ligne

    private boolean darkMode;

    @Column(columnDefinition = "jsonb")
    private String thresholds; // JSON string

    @Column(columnDefinition = "jsonb")
    private String colors; // JSON string
}

