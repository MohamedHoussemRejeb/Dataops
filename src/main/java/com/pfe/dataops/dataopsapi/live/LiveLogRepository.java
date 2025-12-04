package com.pfe.dataops.dataopsapi.live;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveLogRepository extends JpaRepository<LiveLog, Long> {

    List<LiveLog> findByRunIdOrderByTsAsc(String runId);
}
