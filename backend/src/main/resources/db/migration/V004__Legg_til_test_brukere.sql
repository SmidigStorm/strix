-- Legger til test-brukere for mock-autentisering
-- Passord-hashes er for passordet "test123"

-- Test organisasjoner for test-brukere (bare hvis de ikke finnes)
INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, opprettet, aktiv) 
SELECT * FROM (VALUES 
    ('ORG-NTNU-TEST', 'Norges teknisk-naturvitenskapelige universitet (TEST)', 'NTNU-TEST', 'UNIVERSITET', '974767881', 'Høgskoleringen 1, 7491 Trondheim', CURRENT_TIMESTAMP, true),
    ('ORG-UIO-TEST', 'Universitetet i Oslo (TEST)', 'UiO-TEST', 'UNIVERSITET', '971035855', 'Problemveien 7, 0315 Oslo', CURRENT_TIMESTAMP, true),
    ('ORG-SO-TEST', 'Samordnet Opptak (TEST)', 'SO-TEST', 'OFFENTLIG', '123456790', 'Postboks 9, 0101 Oslo', CURRENT_TIMESTAMP, true)
) AS new_orgs (id, navn, kort_navn, type, organisasjonsnummer, adresse, opprettet, aktiv)
WHERE NOT EXISTS (
    SELECT 1 FROM organisasjon WHERE organisasjon.id = new_orgs.id
);

-- Test-brukere (passord: test123)
-- BCrypt hash for "test123": $2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a
INSERT INTO bruker (id, email, navn, passord_hash, organisasjon_id, aktiv, opprettet) VALUES
('BRUKER-OPPTAKSLEDER-NTNU', 'opptaksleder@ntnu.no', 'Kari Opptaksleder', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'ORG-NTNU-TEST', true, CURRENT_TIMESTAMP),
('BRUKER-BEHANDLER-UIO', 'behandler@uio.no', 'Per Behandler', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'ORG-UIO-TEST', true, CURRENT_TIMESTAMP),
('BRUKER-SOKER', 'soker@student.no', 'Astrid Søker', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', NULL, true, CURRENT_TIMESTAMP),
('BRUKER-SO-ADMIN', 'admin@samordnetopptak.no', 'Bjørn SO-Administrator', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'ORG-SO-TEST', true, CURRENT_TIMESTAMP);

-- Tildel roller til brukere
INSERT INTO bruker_rolle (bruker_id, rolle_id, tildelt, tildelt_av) VALUES
('BRUKER-OPPTAKSLEDER-NTNU', 'OPPTAKSLEDER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-BEHANDLER-UIO', 'SOKNADSBEHANDLER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-SOKER', 'SOKER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-SO-ADMIN', 'OPPTAKSLEDER', CURRENT_TIMESTAMP, 'SYSTEM');