package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomFieldRepository extends JpaRepository<CustomField, Long> {
    Optional<CustomField> findByKey(String key);
    boolean existsByKey(String key);
    void deleteByKey(String key);
}
