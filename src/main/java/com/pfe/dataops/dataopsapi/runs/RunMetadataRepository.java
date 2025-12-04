package com.pfe.dataops.dataopsapi.runs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunMetadataRepository extends JpaRepository<RunMetadata, Long> {

    Optional<RunMetadata> findByEtlRunId(Long etlRunId);
}
