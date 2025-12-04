// src/main/java/com/pfe/dataops/dataopsapi/catalog/dto/DatasetDto.java
package com.pfe.dataops.dataopsapi.backend.etl.catalog.dto;


import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.Sensitivity;
import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.LegalTag;

import java.util.List;

public class DatasetDto {
    public String id;
    public String urn;
    public String name;
    public String description;
    public String domain;

    public Owner owner;                 // optionnel
    public List<String> tags;           // optionnel
    public List<String> dependencies;   // optionnel

    public Sla sla;                     // optionnel
    public LastLoad lastLoad;           // optionnel

    // Gouvernance
    public Sensitivity sensitivity;
    public List<LegalTag> legal;

    // Qualité/risque (simple)
    public Integer trust;               // 0..100
    public String risk;                 // "LOW"|"MEDIUM"|"HIGH"|...

    // --- classes internes simples (pas de constructeur args)
    public static class Owner { public String name; public String email; }
    public static class Sla { public String frequency; public String expectedBy; public Integer maxDelayMin; }
    public static class LastLoad { public String status; public String endedAt; public Integer durationSec; }
}
