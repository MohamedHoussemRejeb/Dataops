// src/main/java/com/pfe/dataops/dataopsapi/alerts/AlertRepository.java
package com.pfe.dataops.dataopsapi.alerts;

import com.pfe.dataops.dataopsapi.dashboard.SlaDayProjection;
import com.pfe.dataops.dataopsapi.dashboard.TopError;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {

    // 🔹 Pour les séries SLA (déjà utilisées dans DashboardService.getTimeseries)
    @Query(value = """
        SELECT 
            DATE(a.created_at) AS day,
            SUM(CASE WHEN a.severity = 'INFO'     THEN 1 ELSE 0 END) AS ok,
            SUM(CASE WHEN a.severity = 'WARN'     THEN 1 ELSE 0 END) AS late,
            SUM(CASE WHEN a.severity = 'CRITICAL' THEN 1 ELSE 0 END) AS failed
        FROM alerts a
        WHERE a.created_at >= :since
        GROUP BY DATE(a.created_at)
        ORDER BY day
        """,
            nativeQuery = true)
    List<SlaDayProjection> findSlaStatsSince(@Param("since") Instant since);

    // 🔹 Pour les "Top erreurs récurrentes" sur les 7 derniers jours
    @Query(value = """
        SELECT 
            a.code    AS code,
            a.message AS message,
            COUNT(*)  AS cnt
        FROM alerts a
        WHERE a.created_at >= :since
        GROUP BY a.code, a.message
        ORDER BY cnt DESC
        """,
            nativeQuery = true)
    List<TopErrorProjection> findTopErrorsSince(@Param("since") Instant since, Pageable pageable);

    interface TopErrorProjection {
        String getCode();
        String getMessage();
        long getCnt();
    }
}
