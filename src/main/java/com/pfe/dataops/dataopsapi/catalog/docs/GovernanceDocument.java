package com.pfe.dataops.dataopsapi.catalog.docs;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "governance_documents")
public class GovernanceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 50)
    private DocumentType docType;

    @Column(length = 255)
    private String tags;          // stocké "rgpd|law25"

    @Column(length = 1000)
    private String summary;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    // ===== getters / setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public DocumentType getDocType() { return docType; }
    public void setDocType(DocumentType docType) { this.docType = docType; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

