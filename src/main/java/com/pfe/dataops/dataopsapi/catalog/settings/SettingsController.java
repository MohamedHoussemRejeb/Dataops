// src/main/java/com/pfe/dataops/dataopsapi/catalog/settings/SettingsController.java
package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:4200")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // ---------- CUSTOM FIELDS ----------
    @GetMapping("/custom-fields")
    public List<CustomFieldDto> getCustomFields() {
        return settingsService.getCustomFields();
    }

    @PostMapping("/custom-fields")
    public CustomFieldDto createCustomField(@RequestBody CustomFieldDto dto) {
        return settingsService.createField(dto);
    }

    @PutMapping("/custom-fields/{key}")
    public CustomFieldDto updateCustomField(@PathVariable String key,
                                            @RequestBody CustomFieldDto dto) {
        return settingsService.updateField(key, dto);
    }

    @DeleteMapping("/custom-fields/{key}")
    public void deleteCustomField(@PathVariable String key) {
        settingsService.deleteField(key);
    }

    // ---------- TEMPLATES ----------
    @GetMapping("/templates")
    public List<SchemaTemplateDto> getTemplates() {
        return settingsService.getTemplates();
    }

    @PostMapping("/templates")
    public SchemaTemplateDto saveTemplate(@RequestBody SchemaTemplateDto dto) {
        return settingsService.saveTemplate(dto);
    }

    @DeleteMapping("/templates/{id}")
    public void deleteTemplate(@PathVariable String id) {
        settingsService.deleteTemplate(id);
    }
}
