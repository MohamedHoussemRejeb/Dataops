package com.pfe.dataops.dataopsapi.backend.etl.catalog;

import com.pfe.dataops.dataopsapi.backend.etl.catalog.dto.DatasetDto;
import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.LegalTag;
import com.pfe.dataops.dataopsapi.backend.etl.catalog.model.Sensitivity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
@Profile("mock")
@Service
public class CatalogService {

    public List<DatasetDto> listAll() {
        List<DatasetDto> list = new ArrayList<>();

        // ARTICLES
        {
            DatasetDto d = new DatasetDto();
            d.id = "ARTICLES";
            d.urn = "urn:talend:ARTICLES";
            d.name = "Catalogue Articles";
            d.domain = "CRM";
            d.sensitivity = Sensitivity.INTERNAL;
            d.legal = List.of(LegalTag.GDPR);
            d.trust = 65; d.risk = "RISK";

            d.owner = new DatasetDto.Owner();
            d.owner.name = "Karim Dupont";
            d.owner.email = "karim.dupont@example.com";

            d.sla = new DatasetDto.Sla();
            d.sla.frequency = "daily";
            d.sla.expectedBy = "06:00";
            d.sla.maxDelayMin = 45;

            d.lastLoad = new DatasetDto.LastLoad();
            d.lastLoad.status = "LATE";          // OK | LATE | FAILED | RUNNING | UNKNOWN
            d.lastLoad.endedAt = OffsetDateTime.now().minusHours(1).toString();
            d.lastLoad.durationSec = 560;

            d.tags = List.of("crm", "ref");
            d.dependencies = List.of("src_crm_contacts");
            list.add(d);
        }

        // COMMANDES
        {
            DatasetDto d = new DatasetDto();
            d.id = "COMMANDES";
            d.urn = "urn:talend:COMMANDES";
            d.name = "Commandes Clients";
            d.domain = "Sales";
            d.sensitivity = Sensitivity.SENSITIVE;
            d.legal = List.of(LegalTag.GDPR, LegalTag.FINANCE);
            d.trust = 80; d.risk = "critical";

            d.owner = new DatasetDto.Owner();
            d.owner.name = "Alice Martin";
            d.owner.email = "alice.martin@example.com";

            d.sla = new DatasetDto.Sla();
            d.sla.frequency = "daily";
            d.sla.expectedBy = "06:00";
            d.sla.maxDelayMin = 30;

            d.lastLoad = new DatasetDto.LastLoad();
            d.lastLoad.status = "OK";
            d.lastLoad.endedAt = OffsetDateTime.now().minusMinutes(10).toString();
            d.lastLoad.durationSec = 182;

            d.tags = List.of("orders");
            d.dependencies = List.of("src_shopify_orders","src_pos_sales");
            list.add(d);
        }

        // EXPEDITIONS
        {
            DatasetDto d = new DatasetDto();
            d.id = "EXPEDITIONS";
            d.urn = "urn:talend:EXPEDITIONS";
            d.name = "Expéditions logistique";
            d.domain = "Log";
            d.sensitivity = Sensitivity.INTERNAL;
            d.legal = List.of(LegalTag.LOG);
            d.trust = 72; d.risk = "warning";

            d.lastLoad = new DatasetDto.LastLoad();
            d.lastLoad.status = "RUNNING";
            d.lastLoad.endedAt = null;
            d.lastLoad.durationSec = null;

            d.tags = List.of("ship");
            d.dependencies = List.of("src_shipify_orders");
            list.add(d);
        }

        // ANNULATIONS
        {
            DatasetDto d = new DatasetDto();
            d.id = "ANNULATIONS";
            d.urn = "urn:talend:ANNULATIONS";
            d.name = "Annulations commandes";
            d.domain = "Sales";
            d.sensitivity = Sensitivity.SENSITIVE;
            d.legal = List.of(LegalTag.GDPR);
            d.trust = 60; d.risk = "critical";

            d.lastLoad = new DatasetDto.LastLoad();
            d.lastLoad.status = "FAILED";
            d.lastLoad.endedAt = OffsetDateTime.now().minusMinutes(5).toString();
            d.lastLoad.durationSec = 90;

            d.tags = List.of("cancel");
            d.dependencies = List.of("src_google_ads");
            list.add(d);
        }

        // MOUVEMENTS
        {
            DatasetDto d = new DatasetDto();
            d.id = "MOUVEMENTS";
            d.urn = "urn:talend:MOUVEMENTS";
            d.name = "Mouvements de stock";
            d.domain = "Supply";
            d.sensitivity = Sensitivity.INTERNAL;
            d.legal = List.of(LegalTag.SOX);
            d.trust = 70; d.risk = "warning";

            d.lastLoad = new DatasetDto.LastLoad();
            d.lastLoad.status = "UNKNOWN";
            d.lastLoad.endedAt = null;
            d.lastLoad.durationSec = null;

            list.add(d);
        }

        return list;
    }
}