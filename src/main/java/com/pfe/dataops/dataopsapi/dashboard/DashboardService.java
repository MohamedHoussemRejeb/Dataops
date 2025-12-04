// src/main/java/com/pfe/dataops/dataopsapi/dashboard/DashboardService.java
package com.pfe.dataops.dataopsapi.dashboard;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import com.pfe.dataops.dataopsapi.backend.etl.EtlRunRepository;
import com.pfe.dataops.dataopsapi.alerts.AlertRepository;
import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.enums.LoadStatus;
import com.pfe.dataops.dataopsapi.catalog.enums.Risk;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EtlRunRepository etlRunRepository;
    private final AlertRepository alertRepository;
    private final DatasetRepository datasetRepository;

    // 1) TIMESERIES
    public TimeseriesResponse getTimeseries(int days) {
        Instant since = Instant.now().minus(Duration.ofDays(days));

        var runPoints =
                etlRunRepository.findRunStatsSince(since)
                        .stream()
                        .map(p -> TimeseriesResponse.RunDayPoint.builder()
                                .day(p.getDay().toString())
                                .total(p.getTotal())
                                .failed(p.getFailed())
                                .build())
                        .toList();

        var slaPoints =
                alertRepository.findSlaStatsSince(since)
                        .stream()
                        .map(p -> TimeseriesResponse.SlaDayPoint.builder()
                                .day(p.getDay().toString())
                                .ok(p.getOk())
                                .late(p.getLate())
                                .failed(p.getFailed())
                                .build())
                        .toList();

        return TimeseriesResponse.builder()
                .runs(runPoints)
                .sla(slaPoints)
                .build();
    }

    // 2) SUMMARY KPI
    public DashboardSummary getSummary() {

        Instant startOfDay = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        long runsToday =
                etlRunRepository.countByStartedAtGreaterThanEqual(startOfDay);

        long failedToday =
                etlRunRepository.countByStartedAtGreaterThanEqualAndStatus(
                        startOfDay,
                        EtlRun.Status.FAILED
                );

        double failedRate = runsToday == 0
                ? 0.0
                : (double) failedToday / runsToday;

        // SLA via datasets
        List<DatasetEntity> allDatasets = datasetRepository.findAll();
        long totalDatasets = allDatasets.size();

        long slaOk = allDatasets.stream()
                .filter(d -> d.getLastStatus() != null
                        && d.getLastStatus() != LoadStatus.FAILED)
                .count();

        double slaRespectRate = totalDatasets == 0
                ? 1.0
                : (double) slaOk / totalDatasets;

        // domain KPIs + risky datasets
        List<DomainKpi> domainKpis = computeDomainKpis(allDatasets);
        List<RiskyDataset> riskyDatasets = computeRiskyDatasets(allDatasets);

        // 🔹 TEMP: pas encore de topErrors => liste vide
        List<TopError> topErrors = List.of();

        return DashboardSummary.builder()
                .runsToday(runsToday)
                .failedRate(failedRate)
                .slaRespectRate(slaRespectRate)
                .domains(domainKpis)
                .topDatasets(riskyDatasets)
                .topErrors(topErrors)
                .build();
    }

    // 3) DOMAIN KPIS
    private List<DomainKpi> computeDomainKpis(List<DatasetEntity> all) {

        Map<String, List<DatasetEntity>> byDomain = all.stream()
                .collect(Collectors.groupingBy(d ->
                        d.getDomain() != null && !d.getDomain().isBlank()
                                ? d.getDomain()
                                : "Unknown"
                ));

        return byDomain.entrySet().stream()
                .map(entry -> {
                    String domain = entry.getKey();
                    List<DatasetEntity> ds = entry.getValue();
                    long total = ds.size();

                    long okCount = ds.stream()
                            .filter(d -> d.getLastStatus() != null
                                    && d.getLastStatus() != LoadStatus.FAILED)
                            .count();

                    long failedCount = ds.stream()
                            .filter(d -> d.getLastStatus() == LoadStatus.FAILED)
                            .count();

                    double slaRate = total == 0 ? 1.0 : (double) okCount / total;
                    double failedRate = total == 0 ? 0.0 : (double) failedCount / total;

                    double avgRisk = ds.stream()
                            .mapToDouble(d -> mapRiskEnum(d.getRisk()))
                            .average()
                            .orElse(0.3);

                    double riskScore = Math.min(1.0,
                            0.6 * avgRisk + 0.4 * failedRate);

                    return DomainKpi.builder()
                            .domain(domain)
                            .failedRate(failedRate)
                            .slaRespectRate(slaRate)
                            .riskScore(riskScore)
                            .build();
                })
                .toList();
    }

    // 4) RISKY DATASETS
    private List<RiskyDataset> computeRiskyDatasets(List<DatasetEntity> all) {

        return all.stream()
                .map(d -> {
                    LoadStatus status = d.getLastStatus();
                    double baseRisk = mapRiskEnum(d.getRisk());

                    double statusBoost;
                    if (status == LoadStatus.FAILED) {
                        statusBoost = 0.3;
                    } else if (status == null) {
                        statusBoost = 0.15;
                    } else {
                        statusBoost = 0.0;
                    }

                    double riskScore = Math.min(1.0, baseRisk + statusBoost);

                    return RiskyDataset.builder()
                            .id(d.getId())
                            .urn(d.getUrn())
                            .name(d.getName())
                            .domain(d.getDomain())
                            .lastStatus(status != null ? status.name() : "UNKNOWN")
                            .riskScore(riskScore)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getRiskScore(), a.getRiskScore()))
                .limit(5)
                .toList();
    }

    // 5) MAP RISK ENUM
    private double mapRiskEnum(Risk risk) {
        if (risk == null) return 0.3;

        return switch (risk.name().toUpperCase()) {
            case "CRITICAL" -> 1.0;
            case "HIGH"     -> 0.8;
            case "MEDIUM"   -> 0.5;
            case "LOW"      -> 0.2;
            default         -> 0.3;
        };
    }
}
