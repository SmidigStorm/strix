-- Testdata for opptakssystem

-- Organisasjoner
INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, nettside) VALUES
('ntnu', 'Norges teknisk-naturvitenskapelige universitet', 'NTNU', 'universitet', '974767880', 'Høgskoleringen 1, 7491 Trondheim', 'https://www.ntnu.no'),
('uio', 'Universitetet i Oslo', 'UiO', 'universitet', '971035854', 'Problemveien 7, 0315 Oslo', 'https://www.uio.no'),
('hvl', 'Høgskulen på Vestlandet', 'HVL', 'høgskole', '991825827', 'Inndalsveien 28, 5020 Bergen', 'https://www.hvl.no'),
('fagskolen-innlandet', 'Fagskolen Innlandet', 'FSI', 'fagskole', '123456789', 'Storgata 1, 2400 Elverum', 'https://www.fagskolen-innlandet.no');

-- Utdanninger
INSERT INTO utdanning (id, navn, studienivaa, studiepoeng, varighet, studiested, undervisningssprak, beskrivelse, organisasjon_id) VALUES
('ntnu-informatikk-h25', 'Bachelor i informatikk H25', 'bachelor', 180, 6, 'Trondheim', 'norsk', 'Bachelor i informatikk med fokus på programmering og systemutvikling', 'ntnu'),
('ntnu-bygg-h25', 'Bachelor i bygg- og miljøteknikk H25', 'bachelor', 180, 6, 'Trondheim', 'norsk', 'Utdanning innen bygg- og miljøteknikk', 'ntnu'),
('uio-informatikk-h25', 'Bachelor i informatikk H25', 'bachelor', 180, 6, 'Oslo', 'norsk', 'Informatikkutdanning ved UiO', 'uio'),
('hvl-sykepleie-h25', 'Bachelor i sykepleie H25', 'bachelor', 180, 6, 'Bergen', 'norsk', 'Sykepleieutdanning ved HVL', 'hvl'),
('fsi-elektro-h25', 'Høyere fagskole - Elektro H25', 'fagskole', 120, 4, 'Elverum', 'norsk', 'Fagskoleutdanning innen elektro og automatisering', 'fagskolen-innlandet');

-- Opptak
INSERT INTO opptak (id, navn, type, aar, soknadsfrist, svarfrist, max_utdanninger_per_soknad, status, beskrivelse) VALUES
('samordnet-uhg-h25', 'Samordnet opptak høst 2025', 'UHG', 2025, '2025-04-15', '2025-07-20', 12, 'FREMTIDIG', 'Det samordnede opptaket for universiteter og høgskoler høst 2025'),
('samordnet-fsu-h25', 'Fagskoleopptak høst 2025', 'FSU', 2025, '2025-03-01', '2025-06-15', 5, 'FREMTIDIG', 'Det samordnede opptaket for fagskoleutdanninger høst 2025'),
('ntnu-lokalt-v26', 'NTNU lokalt opptak vår 2026', 'LOKALT', 2026, '2025-10-15', '2025-12-01', 3, 'FREMTIDIG', 'Lokalt opptak ved NTNU for vårsemester 2026');

-- Utdanninger i opptak (koblingstabellen)
INSERT INTO utdanning_i_opptak (id, utdanning_id, opptak_id, antall_plasser) VALUES
('ntnu-informatikk-samordnet-h25', 'ntnu-informatikk-h25', 'samordnet-uhg-h25', 120),
('ntnu-bygg-samordnet-h25', 'ntnu-bygg-h25', 'samordnet-uhg-h25', 80),
('uio-informatikk-samordnet-h25', 'uio-informatikk-h25', 'samordnet-uhg-h25', 150),
('hvl-sykepleie-samordnet-h25', 'hvl-sykepleie-h25', 'samordnet-uhg-h25', 60),
('fsi-elektro-fagskole-h25', 'fsi-elektro-h25', 'samordnet-fsu-h25', 25);