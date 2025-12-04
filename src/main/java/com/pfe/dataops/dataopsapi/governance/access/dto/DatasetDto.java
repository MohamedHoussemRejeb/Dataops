// src/main/java/com/pfe/dataops/dataopsapi/governance/access/dto/DatasetDto.java
package com.pfe.dataops.dataopsapi.governance.access.dto;

import com.pfe.dataops.dataopsapi.governance.LegalTag;
import com.pfe.dataops.dataopsapi.governance.Sensitivity;

import java.util.Set;

public record DatasetDto(
        String urn,
        String name,
        Sensitivity sensitivity,
        Set<LegalTag> legal
) { }