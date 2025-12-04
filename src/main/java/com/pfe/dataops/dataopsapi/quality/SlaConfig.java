// src/main/java/com/pfe/dataops/dataopsapi/quality/SlaConfig.java
package com.pfe.dataops.dataopsapi.quality;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SlaConfig {

    // simple global thresholds
    private Integer maxDurationMin;      // e.g. 60
    private Integer warningErrorRate;    // e.g. 3
    private Integer criticalErrorRate;   // e.g. 10

    private FreshnessConfig freshness;

    // list of conditional rules (IF/ELSE)
    private List<SlaConditionRule> rules;   // optional, advanced

    @Data
    public static class FreshnessConfig {
        private Integer ok;    // e.g. 60
        private Integer warn;  // 120
        private Integer late;  // 300
    }

    @Data
    public static class SlaConditionRule {
        private String ifDatasetNameEquals;   // "Annulation"
        private Map<String, Object> overrides;
        // example: { "maxDurationMin": 20, "criticalErrorRate": 5 }
    }
}
