package com.pfe.dataops.dataopsapi.catalog.dto;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.Sensitivity;
import com.pfe.dataops.dataopsapi.catalog.enums.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DatasetDto {
    private String id;              // String pour Angular
    private String urn;
    private String name;
    private String description;
    private String domain;

    private OwnerDto owner;

    private List<String> tags;
    private List<String> dependencies;

    private Sensitivity sensitivity;
    private List<LegalTag> legal;

    private Integer trust;
    private Risk risk;

    private LoadStatus lastStatus;
    private Instant lastEndedAt;
    private Integer lastDurationSec;

    private SlaFrequency slaFrequency;
    private String slaExpectedBy;
    private Integer slaMaxDelayMin;
}
