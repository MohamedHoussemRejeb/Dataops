// src/main/java/com/pfe/dataops/dataopsapi/alerts/AlertSpecifications.java
package com.pfe.dataops.dataopsapi.alerts;

import org.springframework.data.jpa.domain.Specification;

public final class AlertSpecifications {

    private AlertSpecifications() {}

    public static Specification<Alert> textSearch(String q) {
        if (q == null || q.isBlank()) return null;
        final String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("message")), like),
                cb.like(cb.lower(root.get("flowType")), like),
                cb.like(cb.lower(root.get("source").as(String.class)), like)
        );
    }

    public static Specification<Alert> hasSeverity(AlertSeverity sev) {
        if (sev == null) return null;
        return (root, query, cb) -> cb.equal(root.get("severity"), sev);
    }

    public static Specification<Alert> onlyOpen(Boolean onlyOpen) {
        if (onlyOpen == null || !onlyOpen) return null;
        return (root, query, cb) -> cb.isFalse(root.get("acknowledged"));
    }
}
