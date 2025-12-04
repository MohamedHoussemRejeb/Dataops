package com.pfe.dataops.dataopsapi.catalog.settings;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SettingsDataLoader implements CommandLineRunner {

    private final CustomFieldRepository customRepo;
    private final SchemaTemplateRepository templateRepo;

    public SettingsDataLoader(CustomFieldRepository customRepo,
                              SchemaTemplateRepository templateRepo) {
        this.customRepo = customRepo;
        this.templateRepo = templateRepo;
    }

    @Override
    public void run(String... args) {
        if (customRepo.count() == 0) {
            // champs globaux
            CustomField sla = new CustomField();
            sla.setKey("sla");
            sla.setLabel("SLA (ex: daily 06:00)");
            sla.setType(FieldType.TEXT);
            customRepo.save(sla);

            CustomField crit = new CustomField();
            crit.setKey("criticality");
            crit.setLabel("Criticité");
            crit.setType(FieldType.SELECT);
            crit.setOptions("LOW|MEDIUM|HIGH");
            crit.setRequired(true);
            customRepo.save(crit);

            CustomField freq = new CustomField();
            freq.setKey("refresh_freq");
            freq.setLabel("Fréq. maj (min)");
            freq.setType(FieldType.NUMBER);
            customRepo.save(freq);
        }

        if (templateRepo.count() == 0) {
            // tu peux ici créer CRM / ERP / RH avec leurs champs
            // (si tu veux je peux te l’écrire aussi)
        }
    }
}
