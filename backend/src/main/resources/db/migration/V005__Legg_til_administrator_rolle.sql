-- Legg til Administrator rolle og test-bruker

-- Legg til Administrator rolle
INSERT INTO rolle (id, navn, beskrivelse) VALUES
('ADMINISTRATOR', 'Administrator', 'Systemadministrasjon og overordnet styring av hele opptakssystemet');

-- Opprett test administrator-bruker
INSERT INTO bruker (id, email, navn, passord_hash, organisasjon_id, aktiv, opprettet) VALUES
('BRUKER-ADMIN', 'admin@strix.no', 'Sara Administrator', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', NULL, true, CURRENT_TIMESTAMP);

-- Tildel Administrator rolle
INSERT INTO bruker_rolle (bruker_id, rolle_id, tildelt, tildelt_av) VALUES
('BRUKER-ADMIN', 'ADMINISTRATOR', CURRENT_TIMESTAMP, 'SYSTEM');