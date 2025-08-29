-- Test data for enhetstester
-- Dette kjøres før hver test

-- Rens data først
DELETE FROM utdanning_i_opptak;
DELETE FROM utdanning;
DELETE FROM opptak;
DELETE FROM organisasjon;

-- Legg til test-organisasjoner
INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, nettside, opprettet, aktiv) VALUES
('NTNU-001', 'NTNU', 'NTNU', 'universitet', '974767880', 'Høgskoleringen 1, 7491 Trondheim', 'https://www.ntnu.no', '2024-01-01 10:00:00', true),
('UIO-001', 'Universitetet i Oslo', 'UiO', 'universitet', '971035854', 'Problemveien 7, 0315 Oslo', 'https://www.uio.no', '2024-01-01 10:00:00', true),
('SO-001', 'Samordnet Opptak', 'SO', 'koordinator', '123456789', 'Oslo', 'https://www.samordnetopptak.no', '2024-01-01 10:00:00', true);

-- Legg til test-utdanninger
INSERT INTO utdanning (id, navn, studienivaa, studiepoeng, varighet, studiested, undervisningssprak, beskrivelse, opprettet, aktiv, organisasjon_id) VALUES
('NTNU-INF-001', 'Bachelor i informatikk', 'bachelor', 180, 3, 'Trondheim', 'norsk', 'Bachelor i informatikk ved NTNU', '2024-01-01 10:00:00', true, 'NTNU-001'),
('NTNU-INF-002', 'Master i kunstig intelligens', 'master', 120, 2, 'Trondheim', 'engelsk', 'Master i AI ved NTNU', '2024-01-01 10:00:00', true, 'NTNU-001'),
('UIO-MED-001', 'Bachelor i medisin', 'bachelor', 360, 6, 'Oslo', 'norsk', 'Medisinstudiet ved UiO', '2024-01-01 10:00:00', true, 'UIO-001');

-- Legg til test-opptak
INSERT INTO opptak (id, navn, type, aar, soknadsfrist, svarfrist, max_utdanninger_per_soknad, status, opptaksomgang, beskrivelse, opprettet, aktiv) VALUES
('SO-H25-001', 'Samordnet opptak H25', 'UHG', 2025, '2025-03-01', '2025-07-20', 10, 'FREMTIDIG', 'HAUST', 'Samordnet opptak høst 2025', '2024-08-01 10:00:00', true),
('NTNU-L25-001', 'NTNU Lokalt opptak H25', 'LOKALT', 2025, '2025-04-15', '2025-06-01', 5, 'FREMTIDIG', 'HAUST', 'NTNU lokalt opptak høst 2025', '2024-08-01 10:00:00', true);

-- Legg til test-koblinger mellom utdanning og opptak
INSERT INTO utdanning_i_opptak (id, utdanning_id, opptak_id, antall_plasser, aktivt, opprettet) VALUES
('UIO-SO-001', 'NTNU-INF-001', 'SO-H25-001', 100, true, '2024-08-01 10:00:00'),
('UIO-SO-002', 'UIO-MED-001', 'SO-H25-001', 50, true, '2024-08-01 10:00:00'),
('NTNU-L-001', 'NTNU-INF-002', 'NTNU-L25-001', 25, true, '2024-08-01 10:00:00');