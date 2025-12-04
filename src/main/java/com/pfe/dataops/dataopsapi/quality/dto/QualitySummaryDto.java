package com.pfe.dataops.dataopsapi.quality.dto;

public class QualitySummaryDto {
    private double error_rate;
    private double freshness_min;
    private double null_rate;
    private String lastUpdated;

    public QualitySummaryDto() {}

    public QualitySummaryDto(double error_rate, double freshness_min, double null_rate, String lastUpdated) {
        this.error_rate = error_rate;
        this.freshness_min = freshness_min;
        this.null_rate = null_rate;
        this.lastUpdated = lastUpdated;
    }

    public double getError_rate() { return error_rate; }
    public void setError_rate(double error_rate) { this.error_rate = error_rate; }

    public double getFreshness_min() { return freshness_min; }
    public void setFreshness_min(double freshness_min) { this.freshness_min = freshness_min; }

    public double getNull_rate() { return null_rate; }
    public void setNull_rate(double null_rate) { this.null_rate = null_rate; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
