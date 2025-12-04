package com.pfe.dataops.dataopsapi.sources;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SoftwareSourceDataLoader implements CommandLineRunner {

    private final SoftwareSourceRepository repo;

    public SoftwareSourceDataLoader(SoftwareSourceRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        SoftwareSource s1 = new SoftwareSource();
        s1.setName("Salesforce");
        s1.setVendor("Salesforce");
        s1.setType("saas");
        s1.setLicence("Ent");
        s1.setOwner("Ops");
        s1.setTags(List.of("CRM", "Cloud"));

        SoftwareSource s2 = new SoftwareSource();
        s2.setName("PostgreSQL DW");
        s2.setVendor("Postgres");
        s2.setType("db");
        s2.setLicence("OSS");
        s2.setOwner("Data");
        s2.setTags(List.of("DW"));

        repo.saveAll(List.of(s1, s2));
    }
}
