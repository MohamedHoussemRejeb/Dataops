package com.pfe.dataops.dataopsapi.quality.dto;

import java.util.List;

public class HeatmapDto {

    public static class HeatCellDto {
        private String dataset;
        private String check;
        private double value;

        public HeatCellDto() {}
        public HeatCellDto(String dataset, String check, double value) {
            this.dataset = dataset;
            this.check = check;
            this.value = value;
        }
        public String getDataset() { return dataset; }
        public void setDataset(String dataset) { this.dataset = dataset; }
        public String getCheck() { return check; }
        public void setCheck(String check) { this.check = check; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }

    private List<String> datasets;
    private List<String> checks;
    private List<HeatCellDto> data;

    public HeatmapDto() {}

    public HeatmapDto(List<String> datasets, List<String> checks, List<HeatCellDto> data) {
        this.datasets = datasets;
        this.checks = checks;
        this.data = data;
    }

    public List<String> getDatasets() { return datasets; }
    public void setDatasets(List<String> datasets) { this.datasets = datasets; }

    public List<String> getChecks() { return checks; }
    public void setChecks(List<String> checks) { this.checks = checks; }

    public List<HeatCellDto> getData() { return data; }
    public void setData(List<HeatCellDto> data) { this.data = data; }
}
