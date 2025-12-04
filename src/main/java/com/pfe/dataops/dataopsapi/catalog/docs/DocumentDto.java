package com.pfe.dataops.dataopsapi.catalog.docs;

import java.time.Instant;
import java.util.List;

public record DocumentDto(
        Long id,
        String title,
        String type,          // "policy", "guideline"
        List<String> tags,    // ["rgpd","law25"]
        String summary,
        String contentUrl,
        Instant updatedAt
) {}
