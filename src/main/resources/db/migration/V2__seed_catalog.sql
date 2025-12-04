\dt
SELECT tablename FROM pg_tables WHERE schemaname='public';
SELECT current_database();
CREATE TABLE IF NOT EXISTS owners (
                                      id      BIGSERIAL PRIMARY KEY,
                                      name    VARCHAR(120) NOT NULL,
                                      email   VARCHAR(180) NOT NULL UNIQUE
);

SELECT current_database(), current_schema();
SET search_path TO information_schema, public;
SELECT schemaname, tablename
FROM pg_tables
WHERE tablename IN ('owners', 'datasets');
SET search_path TO public;
INSERT INTO public.owners(name, email) VALUES
                                           ('Alice Martin', 'alice.martin@example.com'),
                                           ('Karim Dupont', 'karim.dupont@example.com'),
                                           ('Zoé Tran',     'zoe.tran@example.com')
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.datasets
(urn, name, description, domain, owner_id,
 sensitivity, trust, risk, last_status, last_ended_at, last_duration_sec,
 sla_frequency, sla_expected_by, sla_max_delay_min)
VALUES
-- ARTICLES
('urn:talend:ARTICLES',
 'Catalogue Articles',
 'Référentiel articles.',
 'CRM',
 (SELECT id FROM public.owners WHERE email='alice.martin@example.com'),
 'sensitive', 80, 'UNKNOWN', 'UNKNOWN', NULL, NULL,
 'daily', '06:00', 30),

-- COMMANDES
('urn:talend:COMMANDES',
 'Commandes Clients',
 'Commandes consolidées (e-com + retail).',
 'Sales',
 (SELECT id FROM public.owners WHERE email='alice.martin@example.com'),
 'pii', 92, 'OK', 'OK', NOW() - INTERVAL '1 hour', 182,
 'daily', '06:00', 30),

-- EXPEDITIONS
('urn:talend:EXPEDITIONS',
 'Expéditions logistique',
 'Flux d''expédition et suivi colis.',
 'Sales',
 (SELECT id FROM public.owners WHERE email='zoe.tran@example.com'),
 'public_data', 70, 'UNKNOWN', 'UNKNOWN', NULL, NULL,
 'weekly', '08:00', 60),

-- ANNULATIONS
('urn:talend:ANNULATIONS',
 'Annulations commandes',
 'Annulations et remboursements.',
 'CRM',
 (SELECT id FROM public.owners WHERE email='karim.dupont@example.com'),
 'sensitive', 65, 'RISK', 'FAILED', NOW() - INTERVAL '25 minutes', 90,
 'hourly', '00:10', 10),

-- MOUVEMENTS
('urn:talend:MOUVEMENTS',
 'Mouvements de stock',
 'Inventaires et mouvements.',
 'Sales',
 (SELECT id FROM public.owners WHERE email='karim.dupont@example.com'),
 'public_data', 75, 'OK', 'RUNNING', NULL, NULL,
 'daily', '07:00', 45)
ON CONFLICT (urn) DO NOTHING;

