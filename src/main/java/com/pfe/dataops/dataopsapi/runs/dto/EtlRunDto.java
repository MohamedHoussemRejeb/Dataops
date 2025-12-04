package com.pfe.dataops.dataopsapi.runs.dto;

public class EtlRunDto {
    public String id;
    public String flowType;   // ARTICLES / COMMANDES / ...
    public String status;     // RUNNING / SUCCESS / FAILED / PENDING

    public String startTime;  // ISO8601
    public String endTime;    // ISO8601 or null

    public Integer rowsIn;
    public Integer rowsOut;
    public Integer rowsError;
    public Long durationMs;

    public String sourceFile;
    public String message;    // "OK", "Erreur parsing colonne date", etc.
    public String trigger;    // MANUAL | SCHEDULED

    public Integer progress;  // 0..100
    public Boolean dryRun;    // true si dry-run

    // 👇 pour l'enrichissement gouvernance côté front
    // (on ne met pas ici directement sensitivity/legal pour ne pas casser le front ;
    //   le front fusionne ça avec /api/datasets)
    public String datasetId;      // ou dataset URN si tu veux
    public String datasetUrn;
}

