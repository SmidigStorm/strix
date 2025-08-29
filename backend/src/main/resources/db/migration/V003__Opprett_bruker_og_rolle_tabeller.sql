-- Opprett tabeller for brukere og roller
-- For mock-autentisering i utviklingsfasen

-- Rolle tabell
CREATE TABLE rolle (
    id VARCHAR(50) PRIMARY KEY,
    navn VARCHAR(100) NOT NULL UNIQUE,
    beskrivelse TEXT
);

-- Bruker tabell
CREATE TABLE bruker (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    navn VARCHAR(255) NOT NULL,
    passord_hash VARCHAR(255) NOT NULL, -- BCrypt hash
    organisasjon_id VARCHAR(255),
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    opprettet TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sist_innlogget TIMESTAMP,
    FOREIGN KEY (organisasjon_id) REFERENCES organisasjon(id)
);

-- Koblingstabell mellom bruker og rolle
CREATE TABLE bruker_rolle (
    bruker_id VARCHAR(255) NOT NULL,
    rolle_id VARCHAR(50) NOT NULL,
    tildelt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tildelt_av VARCHAR(255),
    PRIMARY KEY (bruker_id, rolle_id),
    FOREIGN KEY (bruker_id) REFERENCES bruker(id),
    FOREIGN KEY (rolle_id) REFERENCES rolle(id)
);

-- Indekser for bedre ytelse
CREATE INDEX idx_bruker_email ON bruker(email);
CREATE INDEX idx_bruker_organisasjon ON bruker(organisasjon_id);
CREATE INDEX idx_bruker_rolle_bruker ON bruker_rolle(bruker_id);
CREATE INDEX idx_bruker_rolle_rolle ON bruker_rolle(rolle_id);

-- Sett inn standard roller
INSERT INTO rolle (id, navn, beskrivelse) VALUES
('OPPTAKSLEDER', 'Opptaksleder', 'Kan administrere opptak og utdanninger for sin organisasjon'),
('SOKNADSBEHANDLER', 'Søknadsbehandler', 'Kan behandle søknader og tildele plasser'),
('SOKER', 'Søker', 'Kan søke på utdanninger og følge opp egen søknad');