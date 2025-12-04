// src/main/java/com/pfe/dataops/dataopsapi/sources/SoftwareSourceDto.java
package com.pfe.dataops.dataopsapi.sources;

import java.util.List;

public record SoftwareSourceDto(
        Long id,
        String name,
        String vendor,
        String type,
        String licence,
        String owner,
        List<String> tags,
        String connectionUrl,
        String host,
        Integer port,
        String dbName,
        String username
) {
    public static SoftwareSourceDto fromEntity(SoftwareSource s) {
        return new SoftwareSourceDto(
                s.getId(),
                s.getName(),
                s.getVendor(),
                s.getType(),
                s.getLicence(),
                s.getOwner(),
                s.getTags(),
                s.getConnectionUrl(),
                s.getHost(),
                s.getPort(),
                s.getDbName(),
                s.getUsername()
        );
    }
}
