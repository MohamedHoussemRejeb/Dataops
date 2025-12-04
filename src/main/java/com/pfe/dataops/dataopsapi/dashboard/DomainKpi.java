package com.pfe.dataops.dataopsapi.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DomainKpi {
    private String domain;
    private double failedRate;     // 0..1
    private double slaRespectRate; // 0..1
    private double riskScore;      // 0..1
}
