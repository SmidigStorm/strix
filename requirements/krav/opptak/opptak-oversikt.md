# Opptak - Domeneforståelse

> **Filnavn:** `opptak-oversikt.md`  
> **Formål:** Overordnet domeneforståelse og user story backlog

## Domene-entiteter

**Opptak** 
En søknadsrunde hvor søkere kan søke om studieplasser på utdanninger
- Identitet: navn, beskrivelse, semester/år
- Type: lokalt (én organisasjon) eller samordnet (flere organisasjoner)
- Tidsramme: søknadsfrist, svarfrist, studiestart
- Status: FREMTIDIG, APENT, STENGT, AVSLUTTET (gjelder også løpende opptak)
- Eierskap: organisasjonen som opprettet opptaket
- Tilgang: hvilke organisasjoner som kan legge til utdanninger
- Fleksibilitet: en utdanning kan tilbys i flere opptak samtidig

**Opptaksrunde**
En fase i opptaket med egne tidsfrister og behandling
- Hovedrunde: første og primære tildeling av plasser
- Suppleringsrunde: tildeling av ledige plasser etter hovedrunde
- Restplasser: løpende tildeling av gjenværende plasser
- Kun én runde kan være åpen av gangen (foreløpig)
- Overgang mellom runder skjer manuelt av opptaksleder
- Egen søknadsfrist per runde

**Løpende opptak**
Opptak som ikke følger tradisjonelle runder med faste frister
- Kontinuerlig åpent for søknader
- Behandling skjer fortløpende
- Tilbud gis så snart søknad er vurdert
- Typisk for videreutdanning, EVU-kurs, restplasser
- Kan ha rullerende oppstart gjennom året

**Samordning**
Når flere organisasjoner samarbeider om et opptak (samordnet=true)
- Eier-organisasjon administrerer og gir tilgang
- Eier har full kontroll (endre innstillinger, frister, status)
- Deltakende organisasjoner kan KUN:
  - Legge til egne utdanninger
  - Se egne utdanninger og deres søkere
  - IKKE se andre organisasjoners data
- Samordnet Opptak (SO) er egen organisasjon for nasjonale opptak
- Enkeltorganisasjon kan også ha egne opptak (samordnet=false)

## Domene-eksempler

**Lokale opptak (samordnet=false):**
- "NTNU Lokalt opptak H25" - kun NTNU-utdanninger
- "OsloMet Videreutdanning V26" - kun OsloMet
- "Kristiania Restplasser H25" - privat høgskole

**Løpende opptak:**
- "NTNU EVU-kurs 2025" - kontinuerlig opptak hele året
- "BI Executive MBA" - rullerende oppstart hver måned
- "Noroff Nettstudier" - start når som helst

**Samordnede opptak (samordnet=true):**
- "Samordnet opptak H25" - UHG, nasjonalt opptak via SO
- "Fagskoleopptak H25" - FSU, nasjonalt opptak for fagskoler
- "NTNU-UiO Felles masteropptak" - LOKALT, bilateral samordning
- "Vestlandssamarbeidet H25" - LOKALT, regional samordning

**Typiske opptaksdata:**
- Navn: "Samordnet opptak H25"
- Type: UHG
- Samordnet: true
- Søknadsfrist: "15. april 2025"
- Status: FREMTIDIG → APENT → STENGT → AVSLUTTET
- Administrator: "Samordna opptak (SO)"

**Eksempel på samme utdanning i flere opptak:**
- "Master i Informatikk (NTNU)" kan være i:
  - "Samordnet opptak H25" (UHG, samordnet)
  - "NTNU Lokalt opptak H25" (LOKALT, ikke-samordnet)
  - "Nordisk IT-samarbeid H25" (LOKALT, samordnet)

## User Stories (Backlog)

### Administrere opptak
- [ ] Som opptaksleder ønsker jeg å opprette nytt opptak for min organisasjon slik at vi kan ta opp studenter
- [ ] Som opptaksleder ønsker jeg å se oversikt over opptak jeg har tilgang til slik at jeg har kontroll
- [ ] Som opptaksleder ønsker jeg å endre opptaksinformasjon (frister, beskrivelse) slik at søkere får riktig info
- [ ] Som opptaksleder ønsker jeg å endre status på opptak slik at det følger søknadsprosessen

### Samordning og tilgang
- [ ] Som opptaksleder (eier) ønsker jeg å gi andre organisasjoner tilgang til mitt opptak slik at vi kan samordne
- [ ] Som opptaksleder (eier) ønsker jeg å fjerne tilgang for organisasjoner som ikke lenger skal delta
- [ ] Som opptaksleder ønsker jeg å se hvilke organisasjoner som har tilgang til opptaket
- [ ] Som opptaksleder ønsker jeg å legge egne utdanninger i opptak jeg har tilgang til

### Utdanninger i opptak
- [ ] Som opptaksleder ønsker jeg å legge til utdanninger fra min organisasjon i opptaket
- [ ] Som opptaksleder ønsker jeg å fjerne utdanninger fra opptaket hvis de ikke skal tilbys
- [ ] Som opptaksleder ønsker jeg å angi antall studieplasser per utdanning i opptaket
- [ ] Som opptaksleder (eier) ønsker jeg å se alle utdanninger i mitt opptak uavhengig av organisasjon

### Søkere og opptak
- [ ] Som søker ønsker jeg å se åpne opptak slik at jeg vet hvor jeg kan søke
- [ ] Som søker ønsker jeg å se hvilke utdanninger som tilbys i et opptak
- [ ] Som søker ønsker jeg å se søknadsfrister og viktige datoer
- [ ] Som søker ønsker jeg å filtrere opptak etter type, sted, studienivå

### Statistikk og rapportering
- [ ] Som opptaksleder ønsker jeg å se antall søkere til mine utdanninger i opptaket
- [ ] Som opptaksleder ønsker jeg å eksportere søkerlister for mine utdanninger
- [ ] Som administrator ønsker jeg å se statistikk på tvers av alle opptak
- [ ] Som administrator ønsker jeg å rapportere til departementet om opptaksaktivitet

## Åpne domene-spørsmål
- Hvordan håndterer vi opptak som går over årsskiftet (f.eks. starter i januar)?
- Hvordan setter vi opp regler for automatisk overgang mellom runder?
- Hvordan håndterer vi internasjonale opptak med andre tidsfrister?
- Kan et opptak ha forskjellige søknadsfrister for forskjellige utdanninger?
- Hvordan arkiverer vi gamle opptak og søknadsdata?
- Skal søkere kunne søke på flere opptak samtidig?
- Skal løpende opptak ha maksgrense for hvor lenge de kan være åpne?