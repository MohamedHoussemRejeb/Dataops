package com.pfe.dataops.dataopsapi.catalog.settings;
public record UiSettingsDto(
        boolean darkMode,

        Integer maxDurationMin,
        Integer errorRateWarn,
        Integer errorRateCrit,
        Integer freshnessOkMin,
        Integer freshnessWarnMin,
        Integer nullWarnPct,
        Integer nullCritPct,

        String colorOk,
        String colorRunning,
        String colorLate,
        String colorFailed,
        String colorUnknown
) {}




