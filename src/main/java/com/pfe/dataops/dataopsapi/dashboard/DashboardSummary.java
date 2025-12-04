package com.pfe.dataops.dataopsapi.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class DashboardSummary {

    private long runsToday;
    private double failedRate;      // 0..1
    private double slaRespectRate;  // 0..1

    private List<DomainKpi> domains;
    private List<RiskyDataset> topDatasets;
    private List<TopError> topErrors;
}
