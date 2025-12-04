// src/main/java/com/pfe/dataops/dataopsapi/governance/access/dto/AccessMatrixResponse.java
package com.pfe.dataops.dataopsapi.governance.access.dto;

import java.util.List;

public record AccessMatrixResponse(
        List<AccessEntryDto> items,
        long total
) { }
