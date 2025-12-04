// src/main/java/com/pfe/dataops/dataopsapi/governance/Sensitivity.java
package com.pfe.dataops.dataopsapi.governance;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Sensitivity {
    PUBLIC,
    INTERNAL,
    CONFIDENTIAL,
    SENSITIVE,
    PII,
    PHI,
    SECRET;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(); // "public","internal",...
    }

    public static Sensitivity fromString(String v) {
        if (v == null) return null;
        return Sensitivity.valueOf(v.toUpperCase());
    }
}
