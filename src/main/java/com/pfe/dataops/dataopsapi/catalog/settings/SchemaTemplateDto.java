package com.pfe.dataops.dataopsapi.catalog.settings;

import java.util.List;

public record SchemaTemplateDto(
        String id,
        String name,
        List<CustomFieldDto> fields
) {}
