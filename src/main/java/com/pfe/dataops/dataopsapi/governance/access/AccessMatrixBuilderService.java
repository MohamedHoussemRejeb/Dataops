// src/main/java/com/pfe/dataops/dataopsapi/governance/access/AccessMatrixBuilderService.java
package com.pfe.dataops.dataopsapi.governance.access;

import com.pfe.dataops.dataopsapi.catalog.entity.DatasetEntity;
import com.pfe.dataops.dataopsapi.catalog.repo.DatasetRepository;
import com.pfe.dataops.dataopsapi.governance.StewardRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessMatrixBuilderService {

    private final AccessMatrixRepository repo;
    private final DatasetRepository datasetRepo;
    private final KeycloakUserService keycloakUserService; // ton service Keycloak réel

    @Transactional
    public void rebuild() {
        // 1) on vide la table
        repo.deleteAll();

        // 2) on récupère les datasets
        List<DatasetEntity> datasets = datasetRepo.findAll();

        // 3) on récupère les users Keycloak
        List<KeycloakUserDto> users = keycloakUserService.getAllUsers();

        // 4) on génère les entrées
        for (KeycloakUserDto user : users) {
            StewardRole personRole = mapStewardRole(user.roles());

            for (DatasetEntity ds : datasets) {

                // ✅ IGNORER les datasets sans URN
                if (ds.getUrn() == null || ds.getUrn().isBlank()) {
                    System.out.println("[AccessMatrix] Dataset sans URN ignoré : " + ds.getName());
                    continue;
                }

                AccessMatrixEntryEntity entry = new AccessMatrixEntryEntity();

                // ---- personne ----
                entry.setPersonName(user.fullName());
                entry.setPersonEmail(user.email());
                entry.setPersonRole(personRole);

                // ---- dataset (dénormalisé) ----
                entry.setDatasetUrn(ds.getUrn());   // maintenant jamais null
                entry.setDatasetName(ds.getName());
                // (tu pourras remettre sensitivity / legalTags plus tard si tu veux)
                // entry.setSensitivity(ds.getSensitivity());
                // entry.setLegalTags(new HashSet<>(ds.getLegalTags()));

                // ---- accès ----
                entry.setAccessLevel(personRole);
                entry.setInherited(false);

                repo.save(entry);
            }
        }
    }

    /**
     * Très simple mapping Keycloak → StewardRole (à adapter selon tes rôles réels)
     */
    private StewardRole mapStewardRole(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return StewardRole.VIEWER;
        }

        // Normalisation
        List<String> lower = roles.stream()
                .map(String::toLowerCase)
                .toList();

        // 👑 Administrateur = OWNER
        if (lower.contains("admin")) {
            return StewardRole.OWNER;
        }

        // 🛠️ Steward = STEWARD
        if (lower.contains("steward")) {
            return StewardRole.STEWARD;
        }

        // 👤 User = VIEWER
        if (lower.contains("user")) {
            return StewardRole.VIEWER;
        }

        // Par défaut
        return StewardRole.VIEWER;
    }
}
