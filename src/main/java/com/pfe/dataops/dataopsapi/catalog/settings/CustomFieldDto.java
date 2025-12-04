package com.pfe.dataops.dataopsapi.catalog.settings;

import java.util.List;

public record CustomFieldDto(
        String key,
        String label,
        String type,         // "text" | "number" | "select"
        List<String> options,
        Boolean required,
        String help
) {}
