// src/main/java/com/pfe/dataops/dataopsapi/governance/access/dto/PersonDto.java
package com.pfe.dataops.dataopsapi.governance.access.dto;

import com.pfe.dataops.dataopsapi.governance.StewardRole;

public record PersonDto(
        String name,
        String email,
        StewardRole role
) { }
