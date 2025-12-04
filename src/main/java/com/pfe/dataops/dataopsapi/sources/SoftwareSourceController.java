// src/main/java/com/pfe/dataops/dataopsapi/sources/SoftwareSourceController.java
package com.pfe.dataops.dataopsapi.sources;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sources")
@CrossOrigin(origins = "http://localhost:4200")   // ton front
public class SoftwareSourceController {

    private final SoftwareSourceService service;

    public SoftwareSourceController(SoftwareSourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<SoftwareSourceDto> list() {
        return service.list();
    }

    /**
     * Import d'un fichier de contexte (Talend / CSV / etc.)
     * Le front envoie un FormData avec un champ "file".
     */
    @PostMapping("/import-context")
    public void importContext(@RequestParam("file") MultipartFile file) throws IOException {
        service.importContextFile(file);
    }
}
