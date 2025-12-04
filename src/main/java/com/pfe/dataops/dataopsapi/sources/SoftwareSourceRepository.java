// src/main/java/com/pfe/dataops/dataopsapi/sources/SoftwareSourceRepository.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoftwareSourceRepository extends JpaRepository<SoftwareSource, Long> {
    Optional<SoftwareSource> findByName(String name);
}
