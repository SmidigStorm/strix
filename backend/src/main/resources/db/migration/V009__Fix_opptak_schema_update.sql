-- Fix opptak table schema update - replace V007
-- First add the missing organization for Samordnet opptak

-- Add Samordnet opptak organization 
INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, nettside, aktiv) VALUES
('SO-001', 'Samordnet opptak', 'SO', 'offentlig', '987654321', 'Postboks 1, 0131 Oslo', 'https://www.samordnetopptak.no', true);

-- Add administrator organization ID and samordnet flag to opptak table
ALTER TABLE opptak ADD COLUMN IF NOT EXISTS administrator_organisasjon_id VARCHAR(255);
ALTER TABLE opptak ADD COLUMN IF NOT EXISTS samordnet BOOLEAN NOT NULL DEFAULT FALSE;

-- Add foreign key constraint for administrator organization
ALTER TABLE opptak ADD CONSTRAINT IF NOT EXISTS fk_opptak_administrator 
    FOREIGN KEY (administrator_organisasjon_id) REFERENCES organisasjon(id);

-- Create opptak_tilgang table for managing access to samordnet opptak (if not exists)
CREATE TABLE IF NOT EXISTS opptak_tilgang (
    id VARCHAR(255) PRIMARY KEY,
    opptak_id VARCHAR(255) NOT NULL,
    organisasjon_id VARCHAR(255) NOT NULL,
    tildelt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tildelt_av VARCHAR(255),
    FOREIGN KEY (opptak_id) REFERENCES opptak(id),
    FOREIGN KEY (organisasjon_id) REFERENCES organisasjon(id),
    FOREIGN KEY (tildelt_av) REFERENCES bruker(id),
    UNIQUE(opptak_id, organisasjon_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_opptak_administrator ON opptak(administrator_organisasjon_id);
CREATE INDEX IF NOT EXISTS idx_opptak_samordnet ON opptak(samordnet);
CREATE INDEX IF NOT EXISTS idx_opptak_tilgang_opptak ON opptak_tilgang(opptak_id);
CREATE INDEX IF NOT EXISTS idx_opptak_tilgang_org ON opptak_tilgang(organisasjon_id);

-- Update existing opptak records with administrator organization (using correct IDs)
UPDATE opptak SET administrator_organisasjon_id = 'SO-001' WHERE type IN ('UHG', 'FSU');
UPDATE opptak SET administrator_organisasjon_id = 'ntnu' WHERE type = 'LOKALT' AND navn LIKE '%NTNU%';
UPDATE opptak SET administrator_organisasjon_id = 'uio' WHERE type = 'LOKALT' AND navn LIKE '%UiO%';
UPDATE opptak SET administrator_organisasjon_id = 'hvl' WHERE type = 'LOKALT' AND navn LIKE '%HVL%';

-- Set samordnet flag based on type (UHG and FSU are typically samordnet)
UPDATE opptak SET samordnet = TRUE WHERE type IN ('UHG', 'FSU');