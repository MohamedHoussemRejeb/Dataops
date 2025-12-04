package com.pfe.dataops.dataopsapi.catalog.settings;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatusColorsDto {
    private String OK;
    private String RUNNING;
    private String LATE;
    private String FAILED;
    private String UNKNOWN;
}
