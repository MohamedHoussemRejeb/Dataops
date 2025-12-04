// src/main/java/.../quality/QualityTestService.java
package com.pfe.dataops.dataopsapi.quality;

import com.pfe.dataops.dataopsapi.quality.dto.QualityTestDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualityTestHistoryPointDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QualityTestService {

    // TODO: inject here your real repositories
    // private final DatasetMetricsRepository datasetMetricsRepo;
    // private final ColumnMetricsRepository columnMetricsRepo;
    // private final DatasetRepository datasetRepo; // for SLA

    public QualityTestService(
            // DatasetMetricsRepository datasetMetricsRepo,
            // ColumnMetricsRepository columnMetricsRepo,
            // DatasetRepository datasetRepo
    ) {
        // this.datasetMetricsRepo = datasetMetricsRepo;
        // this.columnMetricsRepo = columnMetricsRepo;
        // this.datasetRepo = datasetRepo;
    }

    public List<QualityTestDto> buildTestsForDataset(String urn) {
        List<QualityTestDto> out = new ArrayList<>();

        // 1) Freshness <= SLA
        out.add(buildFreshnessRule(urn));

        // 2) NULL rate < 5% sur une colonne clé
        out.add(buildNullRule(urn, "customer_email", 5.0));

        // 3) Unicité respectée sur l’ID (ex: 0% de duplicates)
        out.add(buildUniquenessRule(urn, "customer_id", 0.0));

        // 4) Valeurs autorisées sur un code (ex: pays)
        out.add(buildValidityRule(urn, "country_code", 0.5));

        // Tu peux facilement en rajouter d'autres ici
        return out;
    }

    private QualityTestDto buildFreshnessRule(String urn) {
        // === EXEMPLE à adapter à tes repos ===
        // double freshnessMin = datasetMetricsRepo.findLatestFreshnessMinutes(urn);
        // double slaMaxDelay = datasetRepo.findSlaMaxDelayMinutes(urn);

        // Pour te donner un exemple concret, je mets du fake ici :
        double freshnessMin = 17.0;
        double slaMaxDelay = 120.0;

        String status = freshnessMin <= slaMaxDelay ? "OK" : "FAILED";

        List<QualityTestHistoryPointDto> history = List.of(
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(2), 40.0),
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(1), 60.0),
                new QualityTestHistoryPointDto(LocalDate.now(), freshnessMin)
        );

        return new QualityTestDto(
                urn + "-freshness",
                "Freshness <= SLA",
                "Le dataset est rafraîchi dans la fenêtre SLA",
                "freshness",
                urn,
                null, // dataset-level
                status,
                freshnessMin,
                slaMaxDelay,
                history
        );
    }

    private QualityTestDto buildNullRule(String urn, String column, double thresholdPercent) {
        // double nullRate = columnMetricsRepo.findLatestNullRate(urn, column); // 0-100

        double nullRate = 1.8; // exemple
        String status = nullRate <= thresholdPercent ? "OK" : "FAILED";

        List<QualityTestHistoryPointDto> history = List.of(
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(2), 3.2),
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(1), 2.4),
                new QualityTestHistoryPointDto(LocalDate.now(), nullRate)
        );

        return new QualityTestDto(
                urn + "-nulls-" + column,
                "NULL rate < " + thresholdPercent + "%",
                "Taux de valeurs NULL inférieur à " + thresholdPercent + "% sur " + column,
                "nulls",
                urn,
                column,
                status,
                nullRate,
                thresholdPercent,
                history
        );
    }

    private QualityTestDto buildUniquenessRule(String urn, String column, double maxDuplicatePercent) {
        // double duplicateRate = columnMetricsRepo.findLatestDuplicateRate(urn, column);

        double duplicateRate = 0.7; // exemple
        String status = duplicateRate <= maxDuplicatePercent ? "OK" : "FAILED";

        List<QualityTestHistoryPointDto> history = List.of(
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(2), 0.5),
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(1), 0.6),
                new QualityTestHistoryPointDto(LocalDate.now(), duplicateRate)
        );

        return new QualityTestDto(
                urn + "-uniq-" + column,
                "Unicité respectée",
                "Le pourcentage de doublons doit être <= " + maxDuplicatePercent + "%",
                "uniqueness",
                urn,
                column,
                status,
                duplicateRate,
                maxDuplicatePercent,
                history
        );
    }

    private QualityTestDto buildValidityRule(String urn, String column, double maxInvalidPercent) {
        // double invalidRate = columnMetricsRepo.findLatestInvalidRate(urn, column);

        double invalidRate = 0.2; // exemple
        String status = invalidRate <= maxInvalidPercent ? "OK" : "FAILED";

        List<QualityTestHistoryPointDto> history = List.of(
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(2), 0.4),
                new QualityTestHistoryPointDto(LocalDate.now().minusDays(1), 0.3),
                new QualityTestHistoryPointDto(LocalDate.now(), invalidRate)
        );

        return new QualityTestDto(
                urn + "-valid-" + column,
                "Valeurs autorisées",
                "Le % de valeurs hors liste autorisée doit être <= " + maxInvalidPercent + "%",
                "validity",
                urn,
                column,
                status,
                invalidRate,
                maxInvalidPercent,
                history
        );
    }
}
