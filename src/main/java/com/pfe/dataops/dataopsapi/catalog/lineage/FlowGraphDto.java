package com.pfe.dataops.dataopsapi.catalog.lineage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowGraphDto {

    private List<FlowNodeDto> nodes = new ArrayList<>();
    private List<FlowEdgeDto> edges = new ArrayList<>();
}
