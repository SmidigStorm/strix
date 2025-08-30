-- Legger til test-brukere for mock-autentisering
-- Passord-hashes er for passordet "test123"
-- Bruker reelle organisasjoner (ikke test-organisasjoner)

-- Test-brukere (passord: test123) 
-- BCrypt hash for "test123": $2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a
INSERT INTO bruker (id, email, navn, passord_hash, organisasjon_id, aktiv, opprettet) VALUES
('BRUKER-OPPTAKSLEDER-NTNU', 'opptaksleder@ntnu.no', 'Kari Opptaksleder', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'ntnu', true, CURRENT_TIMESTAMP),
('BRUKER-BEHANDLER-UIO', 'behandler@uio.no', 'Per Behandler', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'uio', true, CURRENT_TIMESTAMP),
('BRUKER-OPPTAKSLEDER-HVL', 'opptaksleder@hvl.no', 'Liv Vestland', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'hvl', true, CURRENT_TIMESTAMP),
('BRUKER-SOKER', 'soker@student.no', 'Astrid Søker', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', NULL, true, CURRENT_TIMESTAMP),
('BRUKER-FAGSKOLE-ADMIN', 'admin@fagskolen-innlandet.no', 'Bjørn Fagskole', '$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a', 'fagskolen-innlandet', true, CURRENT_TIMESTAMP);

-- Tildel roller til brukere
INSERT INTO bruker_rolle (bruker_id, rolle_id, tildelt, tildelt_av) VALUES
('BRUKER-OPPTAKSLEDER-NTNU', 'OPPTAKSLEDER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-BEHANDLER-UIO', 'SOKNADSBEHANDLER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-OPPTAKSLEDER-HVL', 'OPPTAKSLEDER', CURRENT_TIMESTAMP, 'SYSTEM'), 
('BRUKER-SOKER', 'SOKER', CURRENT_TIMESTAMP, 'SYSTEM'),
('BRUKER-FAGSKOLE-ADMIN', 'OPPTAKSLEDER', CURRENT_TIMESTAMP, 'SYSTEM');