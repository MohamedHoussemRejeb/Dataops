// src/main/java/com/pfe/dataops/dataopsapi/governance/LegalTag.java
package com.pfe.dataops.dataopsapi.governance;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LegalTag {
    RGPD,
    LAW25,
    HIPAA,
    SOX,
    PCI;

    @JsonValue
    public String toJson() {
        return switch (this) {
            case RGPD -> "rgpd";
            case LAW25 -> "law25";
            case HIPAA -> "hipaa";
            case SOX -> "sox";
            case PCI -> "pci";
        };
    }

    public static LegalTag fromString(String v) {
        if (v == null) return null;
        return switch (v.toLowerCase()) {
            case "rgpd" -> RGPD;
            case "law25" -> LAW25;
            case "hipaa" -> HIPAA;
            case "sox" -> SOX;
            case "pci", "pci_dss", "pci dss" -> PCI;
            default -> throw new IllegalArgumentException("Unknown legal tag: " + v);
        };
    }
}
