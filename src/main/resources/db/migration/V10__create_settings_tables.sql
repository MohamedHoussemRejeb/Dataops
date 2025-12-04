CREATE TABLE quality_summary (
                                 id SERIAL PRIMARY KEY,
                                 error_rate NUMERIC,
                                 freshness_min NUMERIC,
                                 null_rate NUMERIC,
                                 last_updated TIMESTAMP DEFAULT NOW()
);
CREATE TABLE quality_series (
                                id SERIAL PRIMARY KEY,
                                metric VARCHAR(50),               -- error_rate / freshness_min / null_rate
                                t TIMESTAMP,                      -- timestamp du point
                                v NUMERIC                         -- valeur
);
CREATE TABLE quality_heatmap (
                                 id SERIAL PRIMARY KEY,
                                 dataset VARCHAR(255),
                                 check_name VARCHAR(255),
                                 value NUMERIC                     -- 0=OK, 1=CRIT
);
INSERT INTO quality_summary(error_rate, freshness_min, null_rate)
VALUES (0.12, 45, 0.03);
DO $$
    DECLARE
        d INT;
    BEGIN
        FOR d IN 0..29 LOOP
                INSERT INTO quality_series(metric, t, v) VALUES
                                                             ('error_rate', NOW() - (d || ' days')::interval, random() * 0.2),
                                                             ('freshness_min', NOW() - (d || ' days')::interval, 20 + random()*200),
                                                             ('null_rate', NOW() - (d || ' days')::interval, random() * 0.1);
            END LOOP;
    END $$;
DO $$
    DECLARE
        ds INT;
        ck INT;
    BEGIN
        FOR ds IN 1..15 LOOP
                FOR ck IN 1..12 LOOP
                        INSERT INTO quality_heatmap(dataset, check_name, value)
                        VALUES (
                                   'dataset_' || ds,
                                   'check_' || ck,
                                   CASE WHEN random() < 0.1 THEN 1
                                        WHEN random() < 0.3 THEN 0.6
                                        ELSE 0.1 END
                               );
                    END LOOP;
            END LOOP;
    END $$;
-- Seed data for alerts table
-- Adjust table name or column names if needed.

INSERT INTO alerts (id, acknowledged, created_at, dataset_urn, flow_type, message, run_id, severity, source)
VALUES
    ('3c4de722-5f17-4fcd-8796-9c6e472aaaa1', false, '2025-10-25 14:51:03+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes IN', NULL, 'WARN', 'SLA'),
    ('975c82fc-1fec-45ab-b7e3-30a1c4aaaaa2', false, '2025-10-25 14:51:30+00', NULL, NULL, 'Espace disque < 5% sur le serveur Talend', NULL, 'CRITICAL', 'SYSTEM'),
    ('5fadf5c7-738c-4994-b726-5b4763aaaaa3', true,  '2025-10-25 14:51:41+00', 'dataset:expeditions', 'EXPEDITIONS', 'FAILED: Timeout ETL job_daily_outbound_expeditions', 'run_2025_10_25_15h30', 'ERROR', 'RUN'),
    ('5071bed8-c61f-48dd-bd6c-1bbad1aaaaa4', true,  '2025-10-25 14:52:01+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 25 min : retard de traitement Talend', NULL, 'WARN', 'SLA'),
    ('c4a1880e-11bd-4bbd-ba34-4e01f2aaaaa5', false, '2025-10-25 15:42:35+00', NULL, 'COMMANDES', 'Incohérence Commandes : 124 commandes attendues, 92 reçues', 'r42', 'CRITICAL', 'FLOW'),
    ('25fb76ed-9664-4703-9a59-cee0e4aaaaa6', false, '2025-10-25 16:00:28+00', NULL, 'STOCK', 'Fichier Mouvement Stock corrompu : caractères invalides détectés', 'r42', 'CRITICAL', 'FLOW'),
    ('31abce94-da67-44f1-a59d-102e7b3aaaa7', true,  '2025-10-25 16:08:38+00', 'dataset://commandes', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes OUT', NULL, 'WARN', 'SLA'),
    ('a2c4be22-71a1-4d0a-912e-1f3d5baaaa08', false, '2025-10-26 08:12:10+00', 'dataset://commandes', 'COMMANDES', 'Fichier Commandes non reçu de COCACOLA depuis 45 min', NULL, 'CRITICAL', 'FLOW'),
    ('b7f5c9d3-6ea7-4796-b6f9-faa21baaaa09', false, '2025-10-26 08:17:44+00', 'dataset:mouvement_stock', 'STOCK', 'Retard de réception du fichier Mouvement Stock OUT (+18 min)', NULL, 'WARN', 'SLA'),
    ('c9e184de-7b9b-4f64-9017-1fbf4daaaa10', true,  '2025-10-26 09:02:11+00', 'dataset:articles', 'ARTICLE', 'Réception du flux Article terminée avec 0 anomalies', NULL, 'INFO', 'FLOW'),
    ('d3f8ab11-7a0d-4a61-9a11-9dbf9daaaa11', false, '2025-10-26 09:15:33+00', NULL, NULL, 'Memory usage Talend > 85%', NULL, 'WARN', 'SYSTEM'),
    ('e4a9c022-8132-4a1f-9e36-7cb9e1aaaa12', false, '2025-10-26 09:45:02+00', NULL, NULL, 'Serveur FTP inaccessible : timeout connexion', NULL, 'CRITICAL', 'SYSTEM'),
    ('f5b0d133-9243-4b29-a3d7-a8fb12aaaa13', false, '2025-10-26 10:05:19+00', NULL, NULL, 'Tentative d''accès non autorisé au dossier INBOUND', NULL, 'CRITICAL', 'SECURITY'),
    ('0a61e244-a354-4cc4-bd4d-1b2e23aaaa14', false, '2025-10-26 10:22:47+00', 'dataset:commande_journalieres', 'COMMANDES', 'Données personnelles manquantes dans le flux Commandes (GDPR)', NULL, 'CRITICAL', 'LEGAL'),
    ('1b72f355-b465-40d6-94aa-2c3f34aaaa15', true,  '2025-10-26 11:05:00+00', 'dataset:annulations', 'ANNULATIONS', 'Intégration des Annulations terminée (12 lignes)', NULL, 'INFO', 'FLOW');
INSERT INTO alerts (id, acknowledged, created_at, dataset_urn, flow_type, message, run_id, severity, source)
VALUES
    ('3c4de722-5f17-4fcd-8796-9c6e472aaaa1', false, '2025-10-25 14:51:03+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes IN', NULL, 'WARN', 'SLA'),
    ('975c82fc-1fec-45ab-b7e3-30a1c4aaaaa2', false, '2025-10-25 14:51:30+00', NULL, NULL, 'Espace disque < 5% sur le serveur Talend', NULL, 'CRITICAL', 'SYSTEM'),
    ('5fadf5c7-738c-4994-b726-5b4763aaaaa3', true,  '2025-10-25 14:51:41+00', 'dataset:expeditions', 'EXPEDITIONS', 'FAILED: Timeout ETL job_daily_outbound_expeditions', 'run_2025_10_25_15h30', 'ERROR', 'RUN'),
    ('5071bed8-c61f-48dd-bd6c-1bbad1aaaaa4', true,  '2025-10-25 14:52:01+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 25 min : retard de traitement Talend', NULL, 'WARN', 'SLA'),

    -- FLOW remplacé par RUN
    ('c4a1880e-11bd-4bbd-ba34-4e01f2aaaaa5', false, '2025-10-25 15:42:35+00', NULL, 'COMMANDES',
     'Incohérence Commandes : 124 commandes attendues, 92 reçues', 'r42', 'CRITICAL', 'RUN'),

    -- FLOW remplacé par RUN
    ('25fb76ed-9664-4703-9a59-cee0e4aaaaa6', false, '2025-10-25 16:00:28+00', NULL, 'STOCK',
     'Fichier Mouvement Stock corrompu : caractères invalides détectés', 'r42', 'CRITICAL', 'RUN'),

    ('31abce94-da67-44f1-a59d-102e7b3aaaa7', true,  '2025-10-25 16:08:38+00', 'dataset://commandes', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes OUT', NULL, 'WARN', 'SLA'),
    ('a2c4be22-71a1-4d0a-912e-1f3d5baaaa08', false, '2025-10-26 08:12:10+00', 'dataset://commandes', 'COMMANDES', 'Fichier Commandes non reçu de COCACOLA depuis 45 min', NULL, 'CRITICAL', 'SLA'),
    ('b7f5c9d3-6ea7-4796-b6f9-faa21baaaa09', false, '2025-10-26 08:17:44+00', 'dataset:mouvement_stock', 'STOCK', 'Retard de réception du fichier Mouvement Stock OUT (+18 min)', NULL, 'WARN', 'SLA'),
    ('c9e184de-7b9b-4f64-9017-1fbf4daaaa10', true,  '2025-10-26 09:02:11+00', 'dataset:articles', 'ARTICLE', 'Réception du flux Article terminée avec 0 anomalies', NULL, 'INFO', 'RUN'),
    ('d3f8ab11-7a0d-4a61-9a11-9dbf9daaaa11', false, '2025-10-26 09:15:33+00', NULL, NULL, 'Memory usage Talend > 85%', NULL, 'WARN', 'SYSTEM'),
    ('e4a9c022-8132-4a1f-9e36-7cb9e1aaaa12', false, '2025-10-26 09:45:02+00', NULL, NULL, 'Serveur FTP inaccessible : timeout connexion', NULL, 'CRITICAL', 'SYSTEM'),
    ('f5b0d133-9243-4b29-a3d7-a8fb12aaaa13', false, '2025-10-26 10:05:19+00', NULL, NULL, 'Tentative d''accès non autorisé au dossier INBOUND', NULL, 'CRITICAL', 'SECURITY'),
    ('0a61e244-a354-4cc4-bd4d-1b2e23aaaa14', false, '2025-10-26 10:22:47+00', 'dataset:commande_journalieres', 'COMMANDES', 'Données personnelles manquantes dans le flux Commandes (GDPR)', NULL, 'CRITICAL', 'LEGAL'),
    ('1b72f355-b465-40d6-94aa-2c3f34aaaa15', true,  '2025-10-26 11:05:00+00', 'dataset:annulations', 'ANNULATIONS', 'Intégration des Annulations terminée (12 lignes)', NULL, 'INFO', 'RUN');

INSERT INTO alerts (id, acknowledged, created_at, dataset_urn, flow_type, message, run_id, severity, source)
VALUES
    ('3c4de722-5f17-4fcd-8796-9c6e472aaaa1', false, '2025-10-25 14:51:03+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes IN', NULL, 'WARN', 'SLA'),
    ('975c82fc-1fec-45ab-b7e3-30a1c4aaaaa2', false, '2025-10-25 14:51:30+00', NULL, NULL, 'Espace disque < 5% sur le serveur Talend', NULL, 'CRITICAL', 'SYSTEM'),
    ('5fadf5c7-738c-4994-b726-5b4763aaaaa3', true,  '2025-10-25 14:51:41+00', 'dataset:expeditions', 'EXPEDITIONS', 'FAILED: Timeout ETL job_daily_outbound_expeditions', 'run_2025_10_25_15h30', 'ERROR', 'RUN'),
    ('5071bed8-c61f-48dd-bd6c-1bbad1aaaaa4', true,  '2025-10-25 14:52:01+00', 'dataset:commande_journalieres', 'COMMANDES', 'SLA dépassé de 25 min : retard de traitement Talend', NULL, 'WARN', 'SLA'),

    ('c4a1880e-11bd-4bbd-ba34-4e01f2aaaaa5', false, '2025-10-25 15:42:35+00', NULL, 'COMMANDES',
     'Incohérence Commandes : 124 commandes attendues, 92 reçues', 'r42', 'CRITICAL', 'RUN'),

    ('25fb76ed-9664-4703-9a59-cee0e4aaaaa6', false, '2025-10-25 16:00:28+00', NULL, 'STOCK',
     'Fichier Mouvement Stock corrompu : caractères invalides détectés', 'r42', 'CRITICAL', 'RUN'),

    ('31abce94-da67-44f1-a59d-102e7b3aaaa7', true,  '2025-10-25 16:08:38+00', 'dataset://commandes', 'COMMANDES', 'SLA dépassé de 12 min pour le flux Commandes OUT', NULL, 'WARN', 'SLA'),
    ('a2c4be22-71a1-4d0a-912e-1f3d5baaaa08', false, '2025-10-26 08:12:10+00', 'dataset://commandes', 'COMMANDES', 'Fichier Commandes non reçu de COCACOLA depuis 45 min', NULL, 'CRITICAL', 'SLA'),
    ('b7f5c9d3-6ea7-4796-b6f9-faa21baaaa09', false, '2025-10-26 08:17:44+00', 'dataset:mouvement_stock', 'STOCK', 'Retard de réception du fichier Mouvement Stock OUT (+18 min)', NULL, 'WARN', 'SLA'),
    ('c9e184de-7b9b-4f64-9017-1fbf4daaaa10', true,  '2025-10-26 09:02:11+00', 'dataset:articles', 'ARTICLE', 'Réception du flux Article terminée avec 0 anomalies', NULL, 'INFO', 'RUN'),
    ('d3f8ab11-7a0d-4a61-9a11-9dbf9daaaa11', false, '2025-10-26 09:15:33+00', NULL, NULL, 'Memory usage Talend > 85%', NULL, 'WARN', 'SYSTEM'),
    ('e4a9c022-8132-4a1f-9e36-7cb9e1aaaa12', false, '2025-10-26 09:45:02+00', NULL, NULL, 'Serveur FTP inaccessible : timeout connexion', NULL, 'CRITICAL', 'SYSTEM'),
    ('f5b0d133-9243-4b29-a3d7-a8fb12aaaa13', false, '2025-10-26 10:05:19+00', NULL, NULL, 'Tentative d''accès non autorisé au dossier INBOUND', NULL, 'CRITICAL', 'SYSTEM'),
    ('0a61e244-a354-4cc4-bd4d-1b2e23aaaa14', false, '2025-10-26 10:22:47+00', 'dataset:commande_journalieres', 'COMMANDES', 'Données personnelles manquantes dans le flux Commandes (GDPR)', NULL, 'CRITICAL', 'LEGAL'),
    ('1b72f355-b465-40d6-94aa-2c3f34aaaa15', true,  '2025-10-26 11:05:00+00', 'dataset:annulations', 'ANNULATIONS', 'Intégration des Annulations terminée (12 lignes)', NULL, 'INFO', 'RUN');

INSERT INTO alerts (id, acknowledged, created_at, dataset_urn, flow_type, message, run_id, severity, source)
VALUES
    ('3c4de722-5f17-4fcd-8796-9c6e472aaaa1', false, '2025-10-25T14:51:03Z',
     'dataset:commande_journalieres', 'COMMANDES',
     'SLA dépassé de 12 min pour le flux Commandes IN', NULL,
     'WARN', 'SLA'),

    ('975c82fc-1fec-45ab-b7e3-30a1c4aaaaa2', false, '2025-10-25T14:51:30Z',
     NULL, NULL,
     'Espace disque < 5% sur le serveur Talend', NULL,
     'CRITICAL', 'SYSTEM'),

    ('5fadf5c7-738c-4994-b726-5b4763aaaaa3', true, '2025-10-25T14:51:41Z',
     'dataset:expeditions', 'EXPEDITIONS',
     'FAILED: Timeout ETL job_daily_outbound_expeditions', 'run_2025_10_25_15h30',
     'ERROR', 'RUN'),

    ('5071bed8-c61f-48dd-bd6c-1bbad1aaaaa4', true, '2025-10-25T14:52:01Z',
     'dataset:commande_journalieres', 'COMMANDES',
     'SLA dépassé de 25 min : retard de traitement Talend', NULL,
     'WARN', 'SLA'),

    ('c4a1880e-11bd-4bbd-ba34-4e01f2aaaaa5', false, '2025-10-25T15:42:35Z',
     NULL, 'COMMANDES',
     'Incohérence Commandes : 124 commandes attendues, 92 reçues', 'r42',
     'CRITICAL', 'RUN'),

    ('25fb76ed-9664-4703-9a59-cee0e4aaaaa6', false, '2025-10-25T16:00:28Z',
     NULL, 'STOCK',
     'Fichier Mouvement Stock corrompu : caractères invalides détectés', 'r42',
     'CRITICAL', 'RUN'),

    ('31abce94-da67-44f1-a59d-102e7b3aaaa7', true, '2025-10-25T16:08:38Z',
     'dataset://commandes', 'COMMANDES',
     'SLA dépassé de 12 min pour le flux Commandes OUT', NULL,
     'WARN', 'SLA'),

    ('a2c4be22-71a1-4d0a-912e-1f3d5baaaa08', false, '2025-10-26T08:12:10Z',
     'dataset://commandes', 'COMMANDES',
     'Fichier Commandes non reçu de COCACOLA depuis 45 min', NULL,
     'CRITICAL', 'SLA'),

    ('b7f5c9d3-6ea7-4796-b6f9-faa21baaaa09', false, '2025-10-26T08:17:44Z',
     'dataset:mouvement_stock', 'STOCK',
     'Retard de réception du fichier Mouvement Stock OUT (+18 min)', NULL,
     'WARN', 'SLA'),

    ('c9e184de-7b9b-4f64-9017-1fbf4daaaa10', true, '2025-10-26T09:02:11Z',
     'dataset:articles', 'ARTICLE',
     'Réception du flux Article terminée avec 0 anomalies', NULL,
     'INFO', 'RUN'),

    ('d3f8ab11-7a0d-4a61-9a11-9dbf9daaaa11', false, '2025-10-26T09:15:33Z',
     NULL, NULL,
     'Memory usage Talend > 85%', NULL,
     'WARN', 'SYSTEM'),

    ('e4a9c022-8132-4a1f-9e36-7cb9e1aaaa12', false, '2025-10-26T09:45:02Z',
     NULL, NULL,
     'Serveur FTP inaccessible : timeout connexion', NULL,
     'CRITICAL', 'SYSTEM'),

    ('f5b0d133-9243-4b29-a3d7-a8fb12aaaa13', false, '2025-10-26T10:05:19Z',
     NULL, NULL,
     'Tentative d accès non autorisé au dossier INBOUND', NULL,
     'CRITICAL', 'SYSTEM'),

    ('0a61e244-a354-4cc4-bd4d-1b2e23aaaa14', false, '2025-10-26T10:22:47Z',
     'dataset:commande_journalieres', 'COMMANDES',
     'Données personnelles manquantes dans le flux Commandes (GDPR)', NULL,
     'CRITICAL', 'RUN'),

    ('1b72f355-b465-40d6-94aa-2c3f34aaaa15', true, '2025-10-26T11:05:00Z',
     'dataset:annulations', 'ANNULATIONS',
     'Intégration des Annulations terminée (12 lignes)', NULL,
     'INFO', 'RUN');

-- ============================
-- 1) DATASETS de base
-- ============================

INSERT INTO datasets (
    urn, name, description, domain,
    sensitivity, trust, risk,
    lastStatus, lastEndedAt, lastDurationSec,
    slaFrequency, slaExpectedBy, slaMaxDelayMin,
    owner_id
)
VALUES
-- Commandes journalières (IN)
(
    'dataset:commande_journalieres',
    'Commandes journalières',
    'Flux des commandes clients reçues quotidiennement depuis COCACOLA (IN).',
    'COMMANDES',
    NULL,               -- sensitivity (ENUM) -> à mettre si tu veux (ex: ''INTERNAL'')
    NULL,               -- trust
    NULL,               -- risk
    NULL,               -- lastStatus
    NULL,               -- lastEndedAt
    NULL,               -- lastDurationSec
    NULL,               -- slaFrequency (ENUM)
    NULL,               -- slaExpectedBy (ex: ''08:00'')
    NULL,               -- slaMaxDelayMin
    NULL                -- owner_id (si tu as déjà des OwnerEntity)
),

-- Commandes système / métier (vue globale)
(
    'dataset://commandes',
    'Commandes système (vue globale)',
    'Dataset logique agrégé des commandes, utilisé par les contrôles de cohérence IN/OUT.',
    'COMMANDES',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
),

-- Expéditions (OUT)
(
    'dataset:expeditions',
    'Expéditions',
    'Flux des expéditions de commandes envoyées de Viapost vers COCACOLA (OUT).',
    'EXPEDITIONS',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
),

-- Mouvement de stock (OUT)
(
    'dataset:mouvement_stock',
    'Mouvements de stock',
    'Flux des mouvements de stock (sorties, entrées, ajustements) entre Viapost et COCACOLA.',
    'STOCK',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
),

-- Articles / Produits
(
    'dataset:articles',
    'Articles',
    'Référentiel des articles / produits COCACOLA utilisé pour les contrôles de cohérence.',
    'REFERENTIEL',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
),

-- Annulations
(
    'dataset:annulations',
    'Annulations de commandes',
    'Flux des annulations de commandes envoyées ou reçues entre Viapost et COCACOLA.',
    'COMMANDES',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
);

-- ============================
-- 2) TAGS “libres” (dataset_tags)
-- ============================
-- NOTE : on utilise des sous-select sur l'URN pour récupérer l'id généré

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'COCACOLA'
FROM datasets
WHERE urn IN (
              'dataset:commande_journalieres',
              'dataset://commandes',
              'dataset:expeditions',
              'dataset:mouvement_stock',
              'dataset:articles',
              'dataset:annulations'
    );

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'VIAPOST'
FROM datasets
WHERE urn IN (
              'dataset:commande_journalieres',
              'dataset:expeditions',
              'dataset:mouvement_stock',
              'dataset:annulations'
    );

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'COMMANDES'
FROM datasets
WHERE urn IN ('dataset:commande_journalieres', 'dataset://commandes', 'dataset:annulations');

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'STOCK'
FROM datasets
WHERE urn = 'dataset:mouvement_stock';

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'REFERENTIEL'
FROM datasets
WHERE urn = 'dataset:articles';

-- ============================
-- 3) DÉPENDANCES (dataset_dependencies) – optionnel
-- ============================

-- Ex: Les mouvements de stock dépendent des commandes et des articles
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, dep.urn
FROM datasets d
         JOIN LATERAL (
    VALUES
        ('dataset:commande_journalieres'),
        ('dataset:articles')
    ) AS dep(urn) ON true
WHERE d.urn = 'dataset:mouvement_stock';

-- Ex: Expéditions dépendent des commandes
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:commande_journalieres'
FROM datasets d
WHERE d.urn = 'dataset:expeditions';

-- Annulations dépendent des commandes
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:commande_journalieres'
FROM datasets d
WHERE d.urn = 'dataset:annulations';
INSERT INTO datasets (
    urn, name, description, domain,
    sensitivity, trust, risk,
    lastStatus, lastEndedAt, lastDurationSec,
    slaFrequency, slaExpectedBy, slaMaxDelayMin,
    owner_id
)
VALUES
-- 1) Commandes journalières (IN)
(
    'dataset:commande_journalieres',
    'Commandes journalières',
    'Flux IN quotidien des commandes reçues de COCACOLA.',
    'COMMANDES',
    NULL,      -- sensitivity
    NULL,      -- trust
    NULL,      -- risk
    NULL,      -- lastStatus
    NULL,      -- lastEndedAt
    NULL,      -- lastDurationSec
    NULL,      -- slaFrequency
    NULL,      -- slaExpectedBy
    NULL,      -- slaMaxDelayMin
    NULL       -- owner_id
),

-- 2) Commandes globales (logique)
(
    'dataset://commandes',
    'Commandes système',
    'Dataset logique des commandes IN/OUT.',
    'COMMANDES',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 3) Expéditions (OUT)
(
    'dataset:expeditions',
    'Expéditions',
    'Flux OUT des expéditions quotidiennes vers COCACOLA.',
    'EXPEDITIONS',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 4) Mouvement de stock
(
    'dataset:mouvement_stock',
    'Mouvements de stock',
    'Flux OUT des mouvements de stock Viapost → COCACOLA.',
    'STOCK',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 5) Articles (référentiel)
(
    'dataset:articles',
    'Articles',
    'Référentiel des produits / SKU COCACOLA.',
    'REFERENTIEL',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 6) Annulations
(
    'dataset:annulations',
    'Annulations de commandes',
    'Flux des annulations de commandes (IN/OUT).',
    'COMMANDES',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
);
ALTER TABLE datasets ADD COLUMN IF NOT EXISTS lastStatus VARCHAR(255);
ALTER TABLE datasets ADD COLUMN IF NOT EXISTS lastEndedAt TIMESTAMP;
ALTER TABLE datasets ADD COLUMN IF NOT EXISTS lastDurationSec INTEGER;

ALTER TABLE datasets ADD COLUMN IF NOT EXISTS slaFrequency VARCHAR(100);
ALTER TABLE datasets ADD COLUMN IF NOT EXISTS slaExpectedBy VARCHAR(10);
ALTER TABLE datasets ADD COLUMN IF NOT EXISTS slaMaxDelayMin INTEGER;
INSERT INTO datasets (
    urn, name, description, domain,
    sensitivity, trust, risk,
    lastStatus, lastEndedAt, lastDurationSec,
    slaFrequency, slaExpectedBy, slaMaxDelayMin,
    owner_id
)
VALUES
-- 1) Commandes journalières
(
    'dataset:commande_journalieres',
    'Commandes journalières',
    'Flux IN des commandes reçues quotidiennement depuis COCACOLA.',
    'COMMANDES',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 2) Commandes globales
(
    'dataset://commandes',
    'Commandes (vue globale)',
    'Dataset logique représentant la totalité des commandes IN/OUT.',
    'COMMANDES',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 3) Expéditions
(
    'dataset:expeditions',
    'Expéditions',
    'Flux OUT des expéditions transmises à COCACOLA.',
    'EXPEDITIONS',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 4) Mouvement de stock
(
    'dataset:mouvement_stock',
    'Mouvements de stock',
    'Flux des mises à jour du stock entre Viapost et COCACOLA.',
    'STOCK',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 5) Articles
(
    'dataset:articles',
    'Articles',
    'Référentiel produits (SKU) COCACOLA.',
    'REFERENTIEL',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
),

-- 6) Annulations
(
    'dataset:annulations',
    'Annulations commandes',
    'Flux IN/OUT des annulations de commandes.',
    'COMMANDES',
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL, NULL,
    NULL
);
-- Tags communs
INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'COCACOLA'
FROM datasets
WHERE urn LIKE 'dataset:%';

-- Tags métiers
INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'COMMANDES'
FROM datasets
WHERE urn IN ('dataset:commande_journalieres', 'dataset://commandes', 'dataset:annulations');

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'STOCK'
FROM datasets
WHERE urn = 'dataset:mouvement_stock';

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'EXPEDITIONS'
FROM datasets
WHERE urn = 'dataset:expeditions';

INSERT INTO dataset_tags (dataset_id, tag)
SELECT id, 'REFERENTIEL'
FROM datasets
WHERE urn = 'dataset:articles';

-- Mouvement stock dépend des articles + commandes
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:articles'
FROM datasets d WHERE d.urn='dataset:mouvement_stock';

INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:commande_journalieres'
FROM datasets d WHERE d.urn='dataset:mouvement_stock';

-- Expéditions dépendent des commandes
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:commande_journalieres'
FROM datasets d WHERE d.urn='dataset:expeditions';

-- Annulations dépendent des commandes
INSERT INTO dataset_dependencies (dataset_id, dependency)
SELECT d.id, 'dataset:commande_journalieres'
FROM datasets d WHERE d.urn='dataset:annulations';

CREATE TABLE IF NOT EXISTS workflow_edges (
                                              id BIGSERIAL PRIMARY KEY,
                                              from_job VARCHAR(200) NOT NULL,
    to_job   VARCHAR(200) NOT NULL,
    enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    on_success_only BOOLEAN NOT NULL DEFAULT TRUE
    );
CREATE INDEX IF NOT EXISTS idx_workflow_edges_fromjob
    ON workflow_edges(from_job);
INSERT INTO workflow_edges(from_job, to_job, enabled, on_success_only)
VALUES ('COCACOLA_VIAPOST_INTEGRATIONARTICLE',
        'COCACOLA_VIAPOST_INTEGRATIONCOMMANDE',
        true,
        true);
ALTER TABLE datasets
    ADD COLUMN IF NOT EXISTS sla_config JSONB;
ALTER TABLE software_sources
    ADD COLUMN IF NOT EXISTS last_status VARCHAR(32),
    ADD COLUMN IF NOT EXISTS last_checked_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_success_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_latency_ms INTEGER,
    ADD COLUMN IF NOT EXISTS last_message VARCHAR(1000);
ALTER TABLE software_sources
    ADD COLUMN IF NOT EXISTS connection_url VARCHAR(1000);
SELECT id, urn, name
FROM datasets
ORDER BY id
    LIMIT 30;
INSERT INTO dataset_columns (dataset_id, name, type, sensitivity)
VALUES
    (23, 'email_source', 'VARCHAR', NULL),
    (23, 'email',        'VARCHAR', 'CONFIDENTIAL'),
    (23, 'user_id',      'INTEGER', NULL);
SELECT id, dataset_id, name, type, sensitivity
FROM dataset_columns
WHERE dataset_id = 23;
INSERT INTO column_lineage_edge (from_column_id, to_column_id, kind)
VALUES
    (1, 3, 'copy');
SELECT * FROM column_lineage_edge;
SELECT * FROM catalog_lineage_node WHERE dataset_id = 23;
SELECT * FROM catalog_lineage_edge WHERE dataset_id = 23;
-- 1) le dataset lui-même comme nœud
INSERT INTO catalog_lineage_node (dataset_id, technical_id, label, layer, owner)
VALUES
    (23, 'ds23', 'Dataset 23', 'RAW', 'system');

-- 2) un job ETL en aval du dataset
INSERT INTO catalog_lineage_node (dataset_id, technical_id, label, layer, owner)
VALUES
    (23, 'job23_etl', 'Job ETL vers reporting', 'JOB', 'etl-user');

-- 3) un report (dashboard / report BI)
INSERT INTO catalog_lineage_node (dataset_id, technical_id, label, layer, owner)
VALUES
    (23, 'rep23', 'Rapport Power BI ventes', 'REPORT', 'bi-user');
-- 1) edge Dataset 23  --->  Job ETL
INSERT INTO catalog_lineage_edge (dataset_id, from_id, to_id)
VALUES
    (23, 'ds23', 'job23_etl');

-- 2) edge Job ETL   --->  Report
INSERT INTO catalog_lineage_edge (dataset_id, from_id, to_id)
VALUES
    (23, 'job23_etl', 'rep23');
SELECT urn FROM datasets WHERE id = 23;
SELECT id, urn, name
FROM datasets
WHERE id = 23;
INSERT INTO catalog_lineage_node (dataset_id, technical_id, label, layer, owner)
VALUES
    (23, 'ds23',        'Dataset 23 (RAW)',         'RAW',      'system'),
    (23, 'job23_etl',   'Job ETL vers reporting',   'JOB',      'etl-user'),
    (23, 'rep23',       'Rapport Power BI ventes',  'REPORT',   'bi-user');
INSERT INTO catalog_lineage_edge (dataset_id, from_id, to_id)
VALUES
    (23, 'ds23',      'job23_etl'),
    (23, 'job23_etl', 'rep23');
SELECT * FROM catalog_lineage_node  WHERE dataset_id = 23;
SELECT * FROM catalog_lineage_edge  WHERE dataset_id = 23;
UPDATE catalog_lineage_node
SET layer = 'DATA'
WHERE layer = 'RAW';
SELECT technical_id, COUNT(*)
FROM catalog_lineage_node
GROUP BY technical_id
HAVING COUNT(*) > 1;
DELETE FROM catalog_lineage_node n
    USING (
        SELECT id,
               ROW_NUMBER() OVER (PARTITION BY technical_id ORDER BY id) AS rn
        FROM catalog_lineage_node
    ) d
WHERE n.id = d.id
  AND d.rn > 1;
DELETE FROM catalog_lineage_edge e
    USING (
        SELECT id,
               ROW_NUMBER() OVER (
                   PARTITION BY dataset_id, from_id, to_id
                   ORDER BY id
                   ) AS rn
        FROM catalog_lineage_edge
    ) d
WHERE e.id = d.id
  AND d.rn > 1;
SELECT technical_id, COUNT(*)
FROM catalog_lineage_node
GROUP BY technical_id
HAVING COUNT(*) > 1;
SELECT dataset_id, from_id, to_id, COUNT(*)
FROM catalog_lineage_edge
GROUP BY dataset_id, from_id, to_id
HAVING COUNT(*) > 1;