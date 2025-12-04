// src/main/java/com/pfe/dataops/dataopsapi/catalog/settings/UiSettingsService.java
package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UiSettingsService {

    private final UiSettingsRepository repo;

    public UiSettingsService(UiSettingsRepository repo) {
        this.repo = repo;
    }

    public UiSettingsDto get() {
        UiSettingsEntity e = repo.findById(1L).orElseGet(() -> repo.save(new UiSettingsEntity()));
        return toDto(e);
    }

    @Transactional
    public UiSettingsDto save(UiSettingsDto dto) {
        UiSettingsEntity e = repo.findById(1L).orElseGet(UiSettingsEntity::new);

        e.setDarkMode(dto.darkMode());
        e.setMaxDurationMin(dto.maxDurationMin());
        e.setErrorRateWarn(dto.errorRateWarn());
        e.setErrorRateCrit(dto.errorRateCrit());
        e.setFreshnessOkMin(dto.freshnessOkMin());
        e.setFreshnessWarnMin(dto.freshnessWarnMin());
        e.setNullWarnPct(dto.nullWarnPct());
        e.setNullCritPct(dto.nullCritPct());

        e.setColorOk(dto.colorOk());
        e.setColorRunning(dto.colorRunning());
        e.setColorLate(dto.colorLate());
        e.setColorFailed(dto.colorFailed());
        e.setColorUnknown(dto.colorUnknown());

        repo.save(e);
        return toDto(e);
    }

    private UiSettingsDto toDto(UiSettingsEntity e) {
        return new UiSettingsDto(
                e.isDarkMode(),

                e.getMaxDurationMin(),
                e.getErrorRateWarn(),
                e.getErrorRateCrit(),
                e.getFreshnessOkMin(),
                e.getFreshnessWarnMin(),
                e.getNullWarnPct(),
                e.getNullCritPct(),

                e.getColorOk(),
                e.getColorRunning(),
                e.getColorLate(),
                e.getColorFailed(),
                e.getColorUnknown()
        );
    }
}

