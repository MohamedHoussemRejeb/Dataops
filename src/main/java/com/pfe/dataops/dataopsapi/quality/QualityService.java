package com.pfe.dataops.dataopsapi.quality;

import com.pfe.dataops.dataopsapi.quality.dto.HeatmapDto;
import com.pfe.dataops.dataopsapi.quality.dto.HeatmapDto.HeatCellDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualitySeriesDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualitySeriesDto.PointDto;
import com.pfe.dataops.dataopsapi.quality.dto.QualitySummaryDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service Qualité dynamique :
 * - reçoit les infos des jobs Talend (succès / échec / lenteur / lignes en erreur)
 * - en déduit : error_rate global, fraîcheur, % null, plus une heatmap dataset × checks.
 *
 * 👉 Les méthodes utilisées par le front restent les mêmes :
 *    - getSummary()
 *    - getSeries(metric, range)
 *    - getHeatmap(range)
 *
 * 👉 TalendJobService appelle onRunCompleted(...) à chaque fin de job.
 */
@Service
public class QualityService {

    private final Random rnd = new Random();

    /**
     * Datasets dont le dernier run est considéré "en incident".
     * (échec ou qualité très dégradée)
     */
    private final Set<String> incidentDatasets = ConcurrentHashMap.newKeySet();

    /**
     * Pénalité de fraîcheur en minutes par dataset (ajouté à la moyenne).
     * Ex : +45 min si run échoué ou très lent.
     */
    private final Map<String, Integer> freshnessPenalty = new ConcurrentHashMap<>();

    /**
     * Pénalité de % null par dataset (fraction 0..1 à ajouter).
     * Ex : +0.05 → +5 points de % null.
     */
    private final Map<String, Double> nullPenalty = new ConcurrentHashMap<>();

    // ============================================================
    // === API appelée par TalendJobService à chaque fin de run ===
    // ============================================================

    /**
     * @param datasetKey  nom logique du dataset (par ex. "sales_orders")
     * @param failed      true si le job a échoué (exit code != 0)
     * @param rowsIn      nombre de lignes lues
     * @param rowsError   nombre de lignes en erreur/rejet
     * @param durationMs  durée du job en millisecondes
     */
    public void onRunCompleted(String datasetKey,
                               boolean failed,
                               long rowsIn,
                               long rowsError,
                               long durationMs) {
        if (datasetKey == null || datasetKey.isBlank()) {
            return;
        }

        // On réinitialise d'abord l'état précédent pour ce dataset
        incidentDatasets.remove(datasetKey);
        freshnessPenalty.remove(datasetKey);
        nullPenalty.remove(datasetKey);

        // 1) Cas échec : incident fort
        if (failed) {
            incidentDatasets.add(datasetKey);

            // Fraîcheur : gros retard, ex +45 min
            freshnessPenalty.put(datasetKey, 45);

            // % null / qualité : on part de rowsError / rowsIn si dispo
            if (rowsIn > 0L && rowsError > 0L) {
                double extraNull = Math.min(0.30, (double) rowsError / (double) rowsIn); // max +30%
                nullPenalty.put(datasetKey, extraNull);
            } else if (rowsError > 0L) {
                // fallback : si on ne connaît pas rowsIn
                nullPenalty.put(datasetKey, 0.10); // +10%
            }
            return;
        }

        // 2) Cas succès : pas d'échec, mais on peut quand même dégrader un peu
        //    - si job très lent → fraîcheur pénalisée
        //    - si quelques erreurs → % null / invalid un peu plus haut

        // => durée > 20 minutes : on considère que la fraîcheur n'est pas top
        if (durationMs > 20 * 60 * 1000L) {
            freshnessPenalty.put(datasetKey, 25); // +25 min
        }

        // => si on a des lignes en erreur, même si SUCCESS, on pénalise un peu le % null
        if (rowsIn > 0L && rowsError > 0L) {
            double extraNull = Math.min(0.20, (double) rowsError / (double) rowsIn); // max +20%
            nullPenalty.put(datasetKey, extraNull);
        }
    }

    // =====================================
    // === Vue globale : Summary (KPIs)  ===
    // =====================================

    public QualitySummaryDto getSummary() {
        // Valeurs de base "nominales"
        double baseErrorRate = 0.034;   // 3.4%
        double baseFreshMin = 17;       // 17 min
        double baseNullRate = 0.018;    // 1.8%

        // On agrège l'état actuel
        boolean anyIncident = !incidentDatasets.isEmpty();
        int maxFreshPenalty = freshnessPenalty.values()
                .stream().mapToInt(Integer::intValue).max().orElse(0);
        double extraNull = nullPenalty.values()
                .stream().mapToDouble(Double::doubleValue).sum();

        // Error rate global : si au moins un dataset en incident, on double
        double errorRate = baseErrorRate;
        if (anyIncident) {
            errorRate *= 2.0;
        }

        // Fraîcheur : on ajoute la plus grosse pénalité
        double freshnessMin = baseFreshMin + maxFreshPenalty;

        // % null : on ajoute la somme des pénalités
        double nullRate = baseNullRate + extraNull;

        return new QualitySummaryDto(
                errorRate,
                freshnessMin,
                nullRate,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    // ==========================================
    // === Séries temporelles pour les courbes ==
    // ==========================================

    public QualitySeriesDto getSeries(String metric, String range) {
        int days = "30d".equals(range) ? 30 : 7;

        List<PointDto> points = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        boolean anyIncident = !incidentDatasets.isEmpty();
        int maxFreshPenalty = freshnessPenalty.values()
                .stream().mapToInt(Integer::intValue).max().orElse(0);
        double extraNull = nullPenalty.values()
                .stream().mapToDouble(Double::doubleValue).sum();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime t = now.minusDays(i);

            double base;
            switch (metric) {
                case "error_rate" -> base = 0.02;
                case "null_rate" -> base = 0.01;
                case "freshness_min" -> base = 10.0;
                default -> base = 0.0;
            }

            // petite fluctuation aléatoire pour que la courbe ait un peu de vie
            double val = base + (
                    metric.contains("freshness")
                            ? rnd.nextDouble() * 10
                            : rnd.nextDouble() * 0.02
            );

            // application des pénalités globales
            if ("error_rate".equals(metric) && anyIncident) {
                val *= 2.0;
            }
            if ("null_rate".equals(metric)) {
                val += extraNull;
            }
            if ("freshness_min".equals(metric)) {
                val += maxFreshPenalty;
            }

            points.add(new PointDto(t.toLocalDate().toString(), val));
        }

        return new QualitySeriesDto(metric, range, points);
    }

    // ==========================================
    // === Heatmap datasets × checks          ===
    // ==========================================

    public HeatmapDto getHeatmap(String range) {
        // ⚠ IMPORTANT :
        // les noms de datasets ici doivent matcher ce que tu utilises côté Angular (heatmap)
        // et ce qu'on mappe depuis TalendJobService (voir plus bas).
        List<String> datasets = Arrays.asList(
                "sales_orders",   // ex: commandes Coca
                "customer_dim",   // ex: référentiel client
                "products_dim",   // ex: référentiel article
                "payments_fact"   // ex: factures / paiements
        );

        List<String> checks = Arrays.asList(
                "freshness",
                "null_rate",
                "duplicates",
                "referential"
        );

        List<HeatCellDto> data = new ArrayList<>();

        for (String ds : datasets) {
            boolean dsIncident = incidentDatasets.contains(ds);
            int freshPen = freshnessPenalty.getOrDefault(ds, 0);
            double nullPen = nullPenalty.getOrDefault(ds, 0.0);

            for (String ck : checks) {
                // base 0..1
                double value = switch (ck) {
                    case "freshness" -> rnd.nextDouble();
                    case "null_rate" -> rnd.nextDouble();
                    case "duplicates" -> rnd.nextDouble();
                    case "referential" -> rnd.nextDouble();
                    default -> 0.0;
                };

                // 🔥 si dataset en incident → on pousse plusieurs cases vers le rouge
                if (dsIncident) {
                    if ("null_rate".equals(ck) || "duplicates".equals(ck)) {
                        value = Math.min(1.0, value + 0.6);
                    }
                }

                // pénalité de fraîcheur spécifique
                if (freshPen > 0 && "freshness".equals(ck)) {
                    value = Math.min(1.0, value + 0.5);
                }

                // pénalité de nulls spécifique
                if (nullPen > 0 && "null_rate".equals(ck)) {
                    value = Math.min(1.0, value + nullPen * 2); // on amplifie un peu
                }

                data.add(new HeatCellDto(ds, ck, value));
            }
        }

        return new HeatmapDto(datasets, checks, data);
    }
}
