// src/main/java/com/pfe/dataops/dataopsapi/catalog/CatalogBulkController.java
package com.pfe.dataops.dataopsapi.catalog;

import com.pfe.dataops.dataopsapi.catalog.dto.BulkImportDtos.BulkDatasetRowDto;
import com.pfe.dataops.dataopsapi.catalog.dto.BulkImportDtos.BulkImportPayloadDto;
import com.pfe.dataops.dataopsapi.catalog.dto.BulkImportDtos.BulkImportResultDto;
import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CatalogBulkController {

    private final DatasetRepository datasetRepository;

    @PostMapping("/bulk")
    public BulkImportResultDto bulkImport(@RequestBody BulkImportPayloadDto payload) {

        BulkImportResultDto result = new BulkImportResultDto();
        result.ok = true;
        result.created = 0;
        result.failed = 0;
        result.txId = UUID.randomUUID().toString();

        if (payload == null || payload.rows == null || payload.rows.isEmpty()) {
            result.ok = false;
            result.addError(0, "Payload vide (aucune ligne à importer).");
            return result;
        }

        int rowIndex = 0;
        for (BulkDatasetRowDto r : payload.rows) {
            rowIndex++;

            // 1) validation simple
            if (r.name == null || r.name.isBlank()) {
                result.addError(rowIndex, "Le champ 'name' est obligatoire.");
                continue;
            }

            // 2) éviter les doublons sur le nom (case-insensitive)
            if (datasetRepository.existsByNameIgnoreCase(r.name)) {
                result.addError(rowIndex, "Dataset déjà existant : " + r.name);
                continue;
            }

            try {
                DatasetEntity ds = new DatasetEntity();
                ds.setName(r.name);
                ds.setDomain(r.domain);
                ds.setDescription(r.description);

                // 🔹 ownerName / ownerEmail directement sur l'entité
                if (r.owner_email != null && !r.owner_email.isBlank()) {
                    ds.setOwnerEmail(r.owner_email.trim());
                    if (r.owner_name != null && !r.owner_name.isBlank()) {
                        ds.setOwnerName(r.owner_name.trim());
                    } else {
                        ds.setOwnerName(r.owner_email.trim());
                    }
                } else if (r.owner_name != null && !r.owner_name.isBlank()) {
                    ds.setOwnerName(r.owner_name.trim());
                }

                // 🔹 tags (optionnel)
                if (r.tags != null) {
                    ds.setTags(new java.util.ArrayList<>(r.tags));
                }

                // TODO : mapper r.fields → si plus tard tu as une table "dataset_fields"

                datasetRepository.save(ds);
                result.created++;

            } catch (Exception ex) {
                result.addError(rowIndex,
                        "Erreur technique lors de la création du dataset : " + ex.getMessage());
            }
        }

        result.ok = (result.failed == 0);
        return result;
    }
}
