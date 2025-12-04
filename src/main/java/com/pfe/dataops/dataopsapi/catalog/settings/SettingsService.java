// src/main/java/com/pfe/dataops/dataopsapi/catalog/settings/SettingsService.java
package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SettingsService {

    private final CustomFieldRepository customRepo;
    private final SchemaTemplateRepository templateRepo;
    private final UiSettingsRepository uiRepo;     // 👈 nouveau

    public SettingsService(CustomFieldRepository customRepo,
                           SchemaTemplateRepository templateRepo,
                           UiSettingsRepository uiRepo) {
        this.customRepo = customRepo;
        this.templateRepo = templateRepo;
        this.uiRepo = uiRepo;
    }

    // --------- UI SETTINGS ----------

    @Transactional(readOnly = true)
    public UiSettingsDto getUiSettings() {
        UiSettingsEntity e = uiRepo.findById(1L).orElseGet(this::createDefaultUi);
        return toUiDto(e);
    }

    @Transactional
    public UiSettingsDto saveUiSettings(UiSettingsDto dto) {
        UiSettingsEntity e = uiRepo.findById(1L).orElseGet(UiSettingsEntity::new);
        e.setId(1L);

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

        return toUiDto(uiRepo.save(e));
    }

    private UiSettingsEntity createDefaultUi() {
        UiSettingsEntity e = new UiSettingsEntity();
        e.setId(1L);
        e.setDarkMode(false);
        e.setMaxDurationMin(60);
        e.setErrorRateWarn(3);
        e.setErrorRateCrit(10);
        e.setFreshnessOkMin(60);
        e.setFreshnessWarnMin(180);
        e.setNullWarnPct(5);
        e.setNullCritPct(20);
        e.setColorOk("#2fb344");
        e.setColorRunning("#1c7ed6");
        e.setColorLate("#f59f00");
        e.setColorFailed("#e03131");
        e.setColorUnknown("#adb5bd");
        return uiRepo.save(e);
    }

    private UiSettingsDto toUiDto(UiSettingsEntity e) {
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

    // --------- Custom Fields ----------

    public List<CustomFieldDto> getCustomFields() {
        return customRepo.findAll().stream().map(this::toDto).toList();
    }

    public CustomFieldDto createField(CustomFieldDto dto) {
        if (customRepo.existsByKey(dto.key())) {
            throw new IllegalArgumentException("Key already exists");
        }
        CustomField entity = fromDto(dto);
        return toDto(customRepo.save(entity));
    }

    public CustomFieldDto updateField(String key, CustomFieldDto dto) {
        CustomField entity = customRepo.findByKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));

        entity.setKey(dto.key());
        entity.setLabel(dto.label());
        entity.setType(FieldType.valueOf(dto.type().toUpperCase()));
        entity.setOptions(joinOptions(dto.options()));
        entity.setRequired(dto.required());
        entity.setHelp(dto.help());

        return toDto(customRepo.save(entity));
    }

    public void deleteField(String key) {
        customRepo.deleteByKey(key);
    }

    // --------- Templates ----------

    @Transactional(readOnly = true)               // 👈 IMPORTANT pour éviter le LazyInitializationException
    public List<SchemaTemplateDto> getTemplates() {
        return templateRepo.findAll().stream().map(this::toTemplateDto).toList();
    }

    @Transactional
    public SchemaTemplateDto saveTemplate(SchemaTemplateDto dto) {
        SchemaTemplate tpl = new SchemaTemplate();
        tpl.setId(dto.id());
        tpl.setName(dto.name());
        tpl.getFields().clear();

        dto.fields().forEach(f -> {
            TemplateField tf = new TemplateField();
            tf.setTemplate(tpl);
            tf.setKey(f.key());
            tf.setLabel(f.label());
            tf.setType(FieldType.valueOf(f.type().toUpperCase()));
            tf.setOptions(joinOptions(f.options()));
            tf.setRequired(f.required());
            tf.setHelp(f.help());
            tpl.getFields().add(tf);
        });

        return toTemplateDto(templateRepo.save(tpl));
    }

    public void deleteTemplate(String id) {
        templateRepo.deleteById(id);
    }

    // --------- Mapping helpers (inchangés) ----------

    private CustomFieldDto toDto(CustomField e) {
        List<String> opts = splitOptions(e.getOptions());
        return new CustomFieldDto(
                e.getKey(),
                e.getLabel(),
                e.getType().name().toLowerCase(),
                opts,
                e.getRequired(),
                e.getHelp()
        );
    }

    private CustomField fromDto(CustomFieldDto dto) {
        CustomField e = new CustomField();
        e.setKey(dto.key());
        e.setLabel(dto.label());
        e.setType(FieldType.valueOf(dto.type().toUpperCase()));
        e.setOptions(joinOptions(dto.options()));
        e.setRequired(dto.required());
        e.setHelp(dto.help());
        return e;
    }

    private SchemaTemplateDto toTemplateDto(SchemaTemplate tpl) {
        List<CustomFieldDto> fields = tpl.getFields().stream()
                .map(f -> new CustomFieldDto(
                        f.getKey(),
                        f.getLabel(),
                        f.getType().name().toLowerCase(),
                        splitOptions(f.getOptions()),
                        f.getRequired(),
                        f.getHelp()
                ))
                .toList();
        return new SchemaTemplateDto(tpl.getId(), tpl.getName(), fields);
    }

    private List<String> splitOptions(String options) {
        if (options == null || options.isBlank()) return Collections.emptyList();
        return Arrays.asList(options.split("\\|"));
    }

    private String joinOptions(List<String> options) {
        if (options == null || options.isEmpty()) return null;
        return String.join("|", options);
    }
}
