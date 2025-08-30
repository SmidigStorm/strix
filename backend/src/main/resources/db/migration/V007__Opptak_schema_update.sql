-- Update opptak table with missing fields for samordnet functionality
-- Based on updated GraphQL schema requirements

-- Add administrator organization ID and samordnet flag to opptak table
ALTER TABLE opptak ADD COLUMN administrator_organisasjon_id VARCHAR(255);
ALTER TABLE opptak ADD COLUMN samordnet BOOLEAN NOT NULL DEFAULT FALSE;

-- Add foreign key constraint for administrator organization
ALTER TABLE opptak ADD CONSTRAINT fk_opptak_administrator 
    FOREIGN KEY (administrator_organisasjon_id) REFERENCES organisasjon(id);

-- Create opptak_tilgang table for managing access to samordnet opptak
CREATE TABLE opptak_tilgang (
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
CREATE INDEX idx_opptak_administrator ON opptak(administrator_organisasjon_id);
CREATE INDEX idx_opptak_samordnet ON opptak(samordnet);
CREATE INDEX idx_opptak_tilgang_opptak ON opptak_tilgang(opptak_id);
CREATE INDEX idx_opptak_tilgang_org ON opptak_tilgang(organisasjon_id);

-- Update existing opptak records with administrator organization
-- This assumes we have some test data to update
UPDATE opptak SET administrator_organisasjon_id = 'so' WHERE type IN ('UHG', 'FSU');
UPDATE opptak SET administrator_organisasjon_id = 'ntnu' WHERE type = 'LOKALT' AND navn LIKE '%NTNU%';
UPDATE opptak SET administrator_organisasjon_id = 'uio' WHERE type = 'LOKALT' AND navn LIKE '%UiO%';
UPDATE opptak SET administrator_organisasjon_id = 'hvl' WHERE type = 'LOKALT' AND navn LIKE '%HVL%';

-- Set samordnet flag based on type (UHG and FSU are typically samordnet)
UPDATE opptak SET samordnet = TRUE WHERE type IN ('UHG', 'FSU');