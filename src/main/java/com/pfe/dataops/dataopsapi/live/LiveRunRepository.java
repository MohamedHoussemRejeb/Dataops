package com.pfe.dataops.dataopsapi.live;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LiveRunRepository extends JpaRepository<LiveRun, String> {

    @Query("""
        select r from LiveRun r
        where r.status in ('RUNNING', 'PENDING')
        or r.startTime >= :since
        order by r.startTime desc
    """)
    List<LiveRun> findRunningOrRecent(@Param("since") LocalDateTime since);

    @Modifying
    @Transactional
    @Query("update LiveRun r set r.status = :status, r.endTime = :end where r.id = :id")
    void updateStatus(@Param("id") String id,
                      @Param("status") String status,
                      @Param("end") LocalDateTime end);
}
