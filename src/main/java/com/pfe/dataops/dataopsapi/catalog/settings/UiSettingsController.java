package com.pfe.dataops.dataopsapi.catalog.settings;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/ui")
@CrossOrigin(origins = "*")
public class UiSettingsController {

    private final UiSettingsService service;

    public UiSettingsController(UiSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public UiSettingsDto get() {
        return service.get();
    }

    @PutMapping
    public UiSettingsDto save(@RequestBody UiSettingsDto dto) {
        return service.save(dto);
    }
}


