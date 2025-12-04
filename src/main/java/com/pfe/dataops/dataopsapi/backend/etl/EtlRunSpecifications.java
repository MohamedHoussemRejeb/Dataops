// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRunSpecifications.java
package com.pfe.dataops.dataopsapi.backend.etl;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public final class EtlRunSpecifications {
    private EtlRunSpecifications(){}

    public static Specification<EtlRun> jobContains(String job){
        if (job == null || job.isBlank()) return null;
        final String like = "%" + job.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("jobName")), like);
    }

    public static Specification<EtlRun> statusIs(EtlRun.Status status){
        return (status == null) ? null : (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<EtlRun> createdFrom(Instant from){
        return (from == null) ? null : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<EtlRun> createdTo(Instant to){
        return (to == null) ? null : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
