// src/main/java/com/pfe/dataops/dataopsapi/backend/etl/EtlRunRepository.java
package com.pfe.dataops.dataopsapi.backend.etl;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun.Status;
import com.pfe.dataops.dataopsapi.dashboard.RunDayProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EtlRunRepository
        extends JpaRepository<EtlRun, Long>, JpaSpecificationExecutor<EtlRun> {

    @Query(value = """
        SELECT 
            DATE(r.started_at) AS day,
            COUNT(*)           AS total,
            SUM(CASE WHEN r.status = 'FAILED' THEN 1 ELSE 0 END) AS failed
        FROM etl_runs r
        WHERE r.started_at >= :since
        GROUP BY DATE(r.started_at)
        ORDER BY day
        """,
            nativeQuery = true)
    List<RunDayProjection> findRunStatsSince(@Param("since") Instant since);

    long countByStartedAtGreaterThanEqual(Instant since);

    long countByStartedAtGreaterThanEqualAndStatus(Instant since, Status status);

    List<EtlRun> findTop2ByOrderByCreatedAtDesc();
}
