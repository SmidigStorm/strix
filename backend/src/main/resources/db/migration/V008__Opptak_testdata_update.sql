-- Update existing test data and add new realistic opptak examples
-- Based on updated domain understanding with samordnet flag

-- First, ensure we have a 'so' (Samordna opptak) organization if it doesn't exist
INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, nettside, opprettet, aktiv)
VALUES ('so', 'Samordna opptak', 'SO', 'OFFENTLIG', '123456789', 'Oslo', 'https://samordnaopptak.no', CURRENT_TIMESTAMP, TRUE)
ON DUPLICATE KEY UPDATE navn = navn; -- H2 syntax for INSERT IGNORE

-- Clear existing opptak data to start fresh
DELETE FROM utdanning_i_opptak;
DELETE FROM opptak_tilgang;
DELETE FROM opptak;

-- Insert realistic opptak examples
INSERT INTO opptak (id, navn, type, aar, administrator_organisasjon_id, samordnet, soknadsfrist, svarfrist, max_utdanninger_per_soknad, status, opptaksomgang, beskrivelse, opprettet, aktiv) VALUES
-- National coordinated opptak (samordnet=true)
('samordnet-h25', 'Samordnet opptak H25', 'UHG', 2025, 'so', TRUE, '2025-04-15', '2025-07-20', 12, 'FREMTIDIG', 'Hovedrunde', 'Nasjonalt samordnet opptak for universiteter og høgskoler høst 2025', CURRENT_TIMESTAMP, TRUE),

('fagskole-h25', 'Fagskoleopptak H25', 'FSU', 2025, 'so', TRUE, '2025-02-15', '2025-05-01', 8, 'FREMTIDIG', 'Hovedrunde', 'Nasjonalt opptak for fagskoleutdanninger høst 2025', CURRENT_TIMESTAMP, TRUE),

-- Local opptak (samordnet=false) 
('ntnu-lokalt-h25', 'NTNU Lokalt opptak H25', 'LOKALT', 2025, 'ntnu', FALSE, '2025-03-01', '2025-06-15', 5, 'FREMTIDIG', 'Restplasser', 'Lokalt opptak for NTNU-spesifikke programmer og restplasser', CURRENT_TIMESTAMP, TRUE),

('uio-master-v26', 'UiO Masteropptak V26', 'LOKALT', 2026, 'uio', FALSE, '2025-12-01', '2026-02-15', 3, 'FREMTIDIG', 'Vår', 'Lokalt opptak for UiO masterprogrammer vårsemester 2026', CURRENT_TIMESTAMP, TRUE),

-- Collaborative local opptak (samordnet=true)
('vestlands-h25', 'Vestlandssamarbeidet H25', 'LOKALT', 2025, 'hvl', TRUE, '2025-03-15', '2025-06-01', 6, 'FREMTIDIG', 'Hovedrunde', 'Samordnet opptak mellom vestlandsinstitusjoner', CURRENT_TIMESTAMP, TRUE);

-- Add access for collaborative opptak
INSERT INTO opptak_tilgang (id, opptak_id, organisasjon_id, tildelt, tildelt_av) VALUES
-- Samordnet opptak H25: All major institutions
('tilgang-samordnet-ntnu', 'samordnet-h25', 'ntnu', CURRENT_TIMESTAMP, 'admin'),
('tilgang-samordnet-uio', 'samordnet-h25', 'uio', CURRENT_TIMESTAMP, 'admin'), 
('tilgang-samordnet-hvl', 'samordnet-h25', 'hvl', CURRENT_TIMESTAMP, 'admin'),

-- Fagskoleopptak: Add some fagskole organizations (would need to be created)
-- ('tilgang-fagskole-example', 'fagskole-h25', 'fagskole-id', CURRENT_TIMESTAMP, 'admin'),

-- Vestlandssamarbeidet: UiB, NTNU Ålesund would have access
('tilgang-vestland-ntnu', 'vestlands-h25', 'ntnu', CURRENT_TIMESTAMP, 'hvl-admin'); -- NTNU Ålesund

-- Add some utdanninger to opptak (examples)
INSERT INTO utdanning_i_opptak (id, utdanning_id, opptak_id, antall_plasser, aktivt, opprettet) VALUES
('uio-samordnet-informatikk', 'ntnu-informatikk-h25', 'samordnet-h25', 120, TRUE, CURRENT_TIMESTAMP),
('uio-samordnet-bygg', 'ntnu-bygg-h25', 'samordnet-h25', 80, TRUE, CURRENT_TIMESTAMP),
('hvl-samordnet-sykepleie', 'hvl-sykepleie-h25', 'samordnet-h25', 60, TRUE, CURRENT_TIMESTAMP),

-- Local opptak examples
('ntnu-lokalt-informatikk', 'ntnu-informatikk-h25', 'ntnu-lokalt-h25', 15, TRUE, CURRENT_TIMESTAMP),
('hvl-vestland-sykepleie', 'hvl-sykepleie-h25', 'vestlands-h25', 25, TRUE, CURRENT_TIMESTAMP);