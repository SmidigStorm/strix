-- Opprett grunnleggende tabeller for opptakssystem
-- Basert på entity map i krav/opptak/entity-map.md

-- Organisasjon tabell
CREATE TABLE organisasjon (
    id VARCHAR(255) PRIMARY KEY,
    navn VARCHAR(255) NOT NULL,
    kort_navn VARCHAR(100),
    type VARCHAR(50) NOT NULL,
    organisasjonsnummer VARCHAR(20) NOT NULL UNIQUE,
    adresse TEXT,
    nettside VARCHAR(500),
    opprettet TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE
);

-- Utdanning tabell
CREATE TABLE utdanning (
    id VARCHAR(255) PRIMARY KEY,
    navn VARCHAR(255) NOT NULL,
    studienivaa VARCHAR(50) NOT NULL,
    studiepoeng INTEGER NOT NULL,
    varighet INTEGER NOT NULL,
    studiested VARCHAR(100) NOT NULL,
    undervisningssprak VARCHAR(50) NOT NULL,
    beskrivelse TEXT,
    opprettet TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,
    organisasjon_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (organisasjon_id) REFERENCES organisasjon(id)
);

-- Opptak tabell
CREATE TABLE opptak (
    id VARCHAR(255) PRIMARY KEY,
    navn VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('UHG', 'FSU', 'LOKALT')),
    aar INTEGER NOT NULL,
    soknadsfrist DATE,
    svarfrist DATE,
    max_utdanninger_per_soknad INTEGER NOT NULL DEFAULT 10,
    status VARCHAR(20) NOT NULL DEFAULT 'FREMTIDIG' CHECK (status IN ('FREMTIDIG', 'APENT', 'STENGT', 'AVSLUTTET')),
    opptaksomgang VARCHAR(50),
    beskrivelse TEXT,
    opprettet TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE
);

-- Koblingstabellen mellom utdanning og opptak
CREATE TABLE utdanning_i_opptak (
    id VARCHAR(255) PRIMARY KEY,
    utdanning_id VARCHAR(255) NOT NULL,
    opptak_id VARCHAR(255) NOT NULL,
    antall_plasser INTEGER NOT NULL,
    aktivt BOOLEAN NOT NULL DEFAULT TRUE,
    opprettet TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utdanning_id) REFERENCES utdanning(id),
    FOREIGN KEY (opptak_id) REFERENCES opptak(id),
    UNIQUE(utdanning_id, opptak_id)
);

-- Indekser for bedre ytelse
CREATE INDEX idx_utdanning_organisasjon ON utdanning(organisasjon_id);
CREATE INDEX idx_utdanning_i_opptak_utdanning ON utdanning_i_opptak(utdanning_id);
CREATE INDEX idx_utdanning_i_opptak_opptak ON utdanning_i_opptak(opptak_id);
CREATE INDEX idx_opptak_aar ON opptak(aar);
CREATE INDEX idx_opptak_type ON opptak(type);
CREATE INDEX idx_opptak_status ON opptak(status);