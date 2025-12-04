-- Schéma de base

CREATE TABLE IF NOT EXISTS owners (
                                      id      BIGSERIAL PRIMARY KEY,
                                      name    VARCHAR(120) NOT NULL,
    email   VARCHAR(180) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS datasets (
                                        id                BIGSERIAL PRIMARY KEY,
                                        urn               VARCHAR(500) UNIQUE,
    name              VARCHAR(160) NOT NULL,
    description       TEXT,
    domain            VARCHAR(120),

    owner_id          BIGINT REFERENCES owners(id),

    -- Gouvernance
    sensitivity       VARCHAR(20),     -- values: public_data | pii | sensitive
    trust             INTEGER,         -- 0..100
    risk              VARCHAR(20),     -- values: OK | RISK | UNKNOWN

-- Dernier run
    last_status       VARCHAR(20),     -- values: OK | LATE | FAILED | RUNNING | UNKNOWN
    last_ended_at     TIMESTAMPTZ,
    last_duration_sec INTEGER,

    -- SLA
    sla_frequency     VARCHAR(20),     -- values: hourly | daily | weekly
    sla_expected_by   VARCHAR(10),
    sla_max_delay_min INTEGER
    );

-- Collections simples
CREATE TABLE IF NOT EXISTS dataset_tags (
                                            dataset_id BIGINT NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    tag        VARCHAR(80) NOT NULL,
    PRIMARY KEY (dataset_id, tag)
    );

CREATE TABLE IF NOT EXISTS dataset_dependencies (
                                                    dataset_id BIGINT NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    dependency VARCHAR(200) NOT NULL,
    PRIMARY KEY (dataset_id, dependency)
    );

CREATE TABLE IF NOT EXISTS dataset_legal (
                                             dataset_id BIGINT NOT NULL REFERENCES datasets(id) ON DELETE CASCADE,
    tag        VARCHAR(40) NOT NULL,    -- values: rgpd | law25 | hipaa
    PRIMARY KEY (dataset_id, tag)
    );
