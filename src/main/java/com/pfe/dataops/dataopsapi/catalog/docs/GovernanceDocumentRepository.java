package com.pfe.dataops.dataopsapi.catalog.docs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GovernanceDocumentRepository extends JpaRepository<GovernanceDocument, Long> {

    List<GovernanceDocument> findByDocType(DocumentType docType);

    // simple filtre sur les tags (contient)
    List<GovernanceDocument> findByTagsContainingIgnoreCase(String tag);
}

