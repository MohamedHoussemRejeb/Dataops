// src/main/java/com/pfe/dataops/dataopsapi/catalog/docs/DocumentController.java
package com.pfe.dataops.dataopsapi.catalog.docs;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    // GET /api/docs?type=policy&tag=rgpd&q=politique
    @GetMapping
    public List<DocumentDto> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, name = "q") String q) {
        return service.list(type, tag, q);
    }

    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Upload d'un nouveau document (ADMIN ONLY)
     * FormData:
     *  - meta : JSON DocumentUploadRequest
     *  - file : fichier PDF
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentDto create(
            @RequestPart("meta") DocumentUploadRequest meta,
            @RequestPart("file") MultipartFile file) throws IOException {
        return service.create(meta, file);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DocumentDto update(@PathVariable Long id,
                              @RequestBody DocumentDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Download du fichier (tout le monde peut y accéder)
     */
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource res = service.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(res);
    }
}
