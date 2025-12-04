// src/main/java/com/pfe/dataops/dataopsapi/catalog/docs/DocumentUploadRequest.java
package com.pfe.dataops.dataopsapi.catalog.docs;

import java.util.List;

public record DocumentUploadRequest(
        String title,
        String type,          // "policy" | "guideline" | "procedure" | "other"
        String summary,
        List<String> tags
) {}
