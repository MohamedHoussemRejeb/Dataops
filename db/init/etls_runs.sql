CREATE TABLE "SA".etl_runs (
  run_id          BIGSERIAL PRIMARY KEY,          -- identifiant unique auto-incrémenté
  job_name        VARCHAR(200) NOT NULL,          -- nom du job Talend
  start_ts        TIMESTAMP NOT NULL,             -- heure de début
  end_ts          TIMESTAMP,                      -- heure de fin
  duration_sec    INT,                            -- durée en secondes
  status          VARCHAR(20) NOT NULL,           -- RUNNING / SUCCESS / FAILED
  files_processed INT,                            -- (optionnel) nombre de fichiers traités
  rows_in         BIGINT,                         -- (optionnel) nombre de lignes en entrée
  rows_out        BIGINT,                         -- (optionnel) nombre de lignes en sortie
  error_count     INT DEFAULT 0,                  -- (optionnel) nombre d'erreurs
  error_type      VARCHAR(120),                   -- (optionnel) type d'erreur
  source_system   VARCHAR(60),                    -- (optionnel) ex: COCACOLA, Ooredoo...
  target_table    VARCHAR(120),                   -- (optionnel) table de destination
  env             VARCHAR(20),                    -- (optionnel) DEV / REC / PROD
  created_at      TIMESTAMP DEFAULT now()         -- date d’enregistrement
);

