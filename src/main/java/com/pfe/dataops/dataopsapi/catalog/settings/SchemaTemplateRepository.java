package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SchemaTemplateRepository extends JpaRepository<SchemaTemplate, String> {
}

