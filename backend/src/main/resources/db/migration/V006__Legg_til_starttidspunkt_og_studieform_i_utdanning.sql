-- Legg til manglende felter i utdanning tabell basert på kravspesifikasjon

-- Legg til starttidspunkt kolonne
ALTER TABLE utdanning ADD COLUMN starttidspunkt VARCHAR(50) NOT NULL DEFAULT 'HØST_2025';

-- Legg til studieform kolonne med enum constraint
ALTER TABLE utdanning ADD COLUMN studieform VARCHAR(10) NOT NULL DEFAULT 'HELTID' 
    CHECK (studieform IN ('HELTID', 'DELTID'));

-- Oppdater eksisterende testdata med realistiske verdier
UPDATE utdanning SET 
    starttidspunkt = 'HØST_2025',
    studieform = 'HELTID'
WHERE id LIKE '%-h25';

-- Oppdater eventuelle vår-utdanninger
UPDATE utdanning SET 
    starttidspunkt = 'VÅR_2026',
    studieform = 'HELTID'  
WHERE id LIKE '%-v26';

-- Opprett indeks for bedre ytelse på nye felter
CREATE INDEX idx_utdanning_starttidspunkt ON utdanning(starttidspunkt);
CREATE INDEX idx_utdanning_studieform ON utdanning(studieform);