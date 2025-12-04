package com.pfe.dataops.dataopsapi.catalog.settings;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ThresholdsDto {
    private Integer maxDurationMin;
    private Integer errorRateWarn;
    private Integer errorRateCrit;
    private Integer freshnessOkMin;
    private Integer freshnessWarnMin;
    private Integer nullWarnPct;
    private Integer nullCritPct;
}

