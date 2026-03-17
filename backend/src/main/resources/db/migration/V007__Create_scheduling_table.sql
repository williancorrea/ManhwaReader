-- Tabela de agendamento para rotinas da aplicacao
CREATE TABLE scheduling (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    interval_value INT NOT NULL,
    interval_unit VARCHAR(20) NOT NULL,
    monday BOOLEAN DEFAULT FALSE,
    tuesday BOOLEAN DEFAULT FALSE,
    wednesday BOOLEAN DEFAULT FALSE,
    thursday BOOLEAN DEFAULT FALSE,
    friday BOOLEAN DEFAULT FALSE,
    saturday BOOLEAN DEFAULT FALSE,
    sunday BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    last_execution TIMESTAMP NULL,
    next_execution TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_interval_unit CHECK (interval_unit IN ('MINUTES', 'HOURS', 'DAYS', 'WEEKS'))
);
