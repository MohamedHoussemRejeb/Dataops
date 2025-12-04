// src/main/java/com/pfe/dataops/dataopsapi/catalog/settings/UiSettingsEntity.java
package com.pfe.dataops.dataopsapi.catalog.settings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ui_settings")
@Getter @Setter
public class UiSettingsEntity {

    @Id
    private Long id = 1L; // toujours une seule ligne

    private boolean darkMode;

    // thresholds
    private Integer maxDurationMin;
    private Integer errorRateWarn;
    private Integer errorRateCrit;
    private Integer freshnessOkMin;
    private Integer freshnessWarnMin;
    private Integer nullWarnPct;
    private Integer nullCritPct;

    // colors
    private String colorOk;
    private String colorRunning;
    private String colorLate;
    private String colorFailed;
    private String colorUnknown;
}

