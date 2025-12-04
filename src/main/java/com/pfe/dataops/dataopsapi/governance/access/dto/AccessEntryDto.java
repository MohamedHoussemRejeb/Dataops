// src/main/java/com/pfe/dataops/dataopsapi/governance/access/dto/AccessEntryDto.java
package com.pfe.dataops.dataopsapi.governance.access.dto;

import com.pfe.dataops.dataopsapi.governance.StewardRole;

public record AccessEntryDto(
        Long id,
        PersonDto person,
        DatasetDto dataset,
        StewardRole access,
        boolean inherited
) { }
