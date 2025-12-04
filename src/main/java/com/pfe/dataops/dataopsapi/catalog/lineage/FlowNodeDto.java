package com.pfe.dataops.dataopsapi.catalog.lineage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowNodeDto {
    private String id;
    private String label;
    private String type; // "dataset", "job", "report"
    private int level;
}
