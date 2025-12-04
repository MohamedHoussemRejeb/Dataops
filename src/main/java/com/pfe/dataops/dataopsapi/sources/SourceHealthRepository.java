// src/main/java/com/pfe/dataops/dataopsapi/sources/SourceHealthRepository.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceHealthRepository extends JpaRepository<SourceHealth, Long> {
    Optional<SourceHealth> findBySource(SoftwareSource source);
}
