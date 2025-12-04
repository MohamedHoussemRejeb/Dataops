// src/main/java/com/pfe/dataops/dataopsapi/governance/StewardRole.java
package com.pfe.dataops.dataopsapi.governance;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StewardRole {
    OWNER,
    STEWARD,
    VIEWER;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(); // "owner","steward","viewer"
    }

    public static StewardRole fromString(String v) {
        if (v == null) return null;
        return StewardRole.valueOf(v.toUpperCase());
    }
}
