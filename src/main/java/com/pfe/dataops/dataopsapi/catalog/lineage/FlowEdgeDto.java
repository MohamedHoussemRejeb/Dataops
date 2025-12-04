package com.pfe.dataops.dataopsapi.catalog.lineage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowEdgeDto {
    private String from;
    private String to;
    private String kind;
}
