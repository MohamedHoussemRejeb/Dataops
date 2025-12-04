package com.pfe.dataops.dataopsapi.catalog.docs;

public enum DocumentType {
    POLICY,
    GUIDELINE,
    PROCEDURE,
    OTHER;

    public static DocumentType fromString(String value) {
        if (value == null) return OTHER;
        try {
            return DocumentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return OTHER;
        }
    }
}

