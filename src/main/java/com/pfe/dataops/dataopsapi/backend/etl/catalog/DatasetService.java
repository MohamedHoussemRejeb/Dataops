// src/main/java/com/pfe/dataops/dataopsapi/catalog/DatasetService.java
package com.pfe.dataops.dataopsapi.backend.etl.catalog;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.dto.DatasetDto;
import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.LegalTag;
import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.Sensitivity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {

    public List<DatasetDto> listAll() {
        List<DatasetDto> list = new ArrayList<>();

        // --- ARTICLES
        {
            DatasetDto d = new DatasetDto();
            d.id = "ARTICLES";
            d.urn = "urn:talend:ARTICLES";
            d.name = "Catalogue Articles";
            d.sensitivity = Sensitivity.INTERNAL;          // ✅ enum
            d.legal = List.of(LegalTag.GDPR);              // ✅ enum
            list.add(d);
        }

        // --- COMMANDES
        {
            DatasetDto d = new DatasetDto();
            d.id = "COMMANDES";
            d.urn = "urn:talend:COMMANDES";
            d.name = "Commandes Clients";
            d.sensitivity = Sensitivity.SENSITIVE;
            d.legal = List.of(LegalTag.GDPR, LegalTag.FINANCE);
            list.add(d);
        }

        // --- EXPEDITIONS
        {
            DatasetDto d = new DatasetDto();
            d.id = "EXPEDITIONS";
            d.urn = "urn:talend:EXPEDITIONS";
            d.name = "Expéditions logistique";
            d.sensitivity = Sensitivity.INTERNAL;
            d.legal = List.of(LegalTag.LOG);
            list.add(d);
        }

        // --- ANNULATIONS
        {
            DatasetDto d = new DatasetDto();
            d.id = "ANNULATIONS";
            d.urn = "urn:talend:ANNULATIONS";
            d.name = "Annulations commandes";
            d.sensitivity = Sensitivity.SENSITIVE;
            d.legal = List.of(LegalTag.GDPR);
            list.add(d);
        }

        // --- MOUVEMENTS
        {
            DatasetDto d = new DatasetDto();
            d.id = "MOUVEMENTS";
            d.urn = "urn:talend:MOUVEMENTS";
            d.name = "Mouvements de stock";
            d.sensitivity = Sensitivity.INTERNAL;
            d.legal = List.of(LegalTag.SOX);
            list.add(d);
        }

        return list;
    }

    public Optional<DatasetDto> findById(String id) {
        return listAll().stream().filter(d -> d.id.equals(id)).findFirst();
    }
}
