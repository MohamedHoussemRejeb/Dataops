package com.pfe.dataops.dataopsapi.quality.dto;

import java.util.List;

public class QualitySeriesDto {

    public static class PointDto {
        private String t;
        private double v;

        public PointDto() {}
        public PointDto(String t, double v) {
            this.t = t;
            this.v = v;
        }
        public String getT() { return t; }
        public void setT(String t) { this.t = t; }
        public double getV() { return v; }
        public void setV(double v) { this.v = v; }
    }

    private String metric;
    private String range; // "7d" / "30d"
    private List<PointDto> points;

    public QualitySeriesDto() {}

    public QualitySeriesDto(String metric, String range, List<PointDto> points) {
        this.metric = metric;
        this.range = range;
        this.points = points;
    }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public String getRange() { return range; }
    public void setRange(String range) { this.range = range; }

    public List<PointDto> getPoints() { return points; }
    public void setPoints(List<PointDto> points) { this.points = points; }
}
