package com.pfe.dataops.dataopsapi.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RiskyDataset {

    private Long id;
    private String urn;
    private String name;
    private String domain;
    private String lastStatus;  // OK / LATE / FAILED
    private double riskScore;   // 0..1
}
