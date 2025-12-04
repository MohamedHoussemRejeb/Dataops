// src/main/java/com/pfe/dataops/dataopsapi/governance/access/AccessMatrixController.java
package com.pfe.dataops.dataopsapi.governance.access;

import com.pfe.dataops.dataopsapi.governance.access.dto.AccessMatrixResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/governance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccessMatrixController {

    private final AccessMatrixService service;
    private final AccessMatrixBuilderService builder;

    @GetMapping("/access-matrix")
    public AccessMatrixResponse list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String sensitivity,
            @RequestParam(required = false) String legal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        return service.list(q, role, sensitivity, legal, page, size);
    }

    // 🔁 endpoint pour reconstruire la matrice depuis Keycloak + datasets
    @PostMapping("/access-matrix/rebuild")
    public void rebuild() {
        builder.rebuild();
    }
}
