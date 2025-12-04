// src/main/java/com/pfe/dataops/dataopsapi/governance/access/KeycloakUserDto.java
package com.pfe.dataops.dataopsapi.governance.access;

import java.util.List;

public record KeycloakUserDto(
        String id,
        String username,
        String fullName,
        String email,
        List<String> roles
) {
}
