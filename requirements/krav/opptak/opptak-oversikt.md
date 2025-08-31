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

**Samordning og roller**
Samordning oppstår når en opptaksleder inviterer andre organisasjoner til sitt opptak:

**Prosess for samordning:**
1. Opptaksleder (Eier) oppretter opptak for sin organisasjon (samordnet=false)
2. Opptaksleder (Eier) inviterer andre organisasjoner → opptaket blir samordnet=true
3. Inviterte organisasjoner får Deltager-rolle i dette opptaket

**Roller og tilganger:**

- **Opptaksleder (Eier)**:
  - Kan endre alle opptaksinnstillinger (frister, status, navn)
  - Kan legge til utdanninger fra sin organisasjon
  - Kan se alle utdanninger i opptaket (fra alle organisasjoner)
  - Kan gi/fjerne tilgang til andre organisasjoner
  
- **Opptaksleder (Deltager)**:
  - Kan KUN legge til utdanninger fra sin egen organisasjon
  - Kan KUN se egne utdanninger og deres data
  - Kan IKKE endre opptaksinnstillinger
  - Kan IKKE se andre organisasjoners utdanninger eller søkere

- **Administrator**:
  - Kan legge til alle utdanninger til alle opptak
  - Kan administrere alle opptak på vegne av organisasjoner

**Spesialtilfeller:**
- Samordnet Opptak (SO) er egen organisasjon for nasjonale opptak (UHG, FSU)
- Enkeltorganisasjon kan ha egne, ikke-samordnede opptak (samordnet=false)

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

**Praktisk eksempel - Samordningslogikk:**

*Situasjon:* NTNU oppretter "IT-masteropptak H25" og inviterer UiO og UiB til å delta:

1. **Før invitasjon:** 
   - Opptak: "IT-masteropptak H25" (eier: NTNU, samordnet=false)
   - Kun NTNU kan legge til egne utdanninger

2. **Etter invitasjon av UiO og UiB:**
   - Opptak: "IT-masteropptak H25" (eier: NTNU, samordnet=true)
   - NTNU (Eier): Kan legge til NTNU-utdanninger, se alle utdanninger fra alle org
   - UiO (Deltager): Kan legge til UiO-utdanninger, ser kun UiO-utdanninger
   - UiB (Deltager): Kan legge til UiB-utdanninger, ser kun UiB-utdanninger
   - Administrator: Kan legge til utdanninger fra alle organisasjoner

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

### Utdanninger i opptak - Eier av opptak
- [ ] Som opptaksleder (eier) ønsker jeg å legge til utdanninger fra min organisasjon i mitt opptak
- [ ] Som opptaksleder (eier) ønsker jeg å fjerne utdanninger fra mitt opptak hvis de ikke skal tilbys
- [ ] Som opptaksleder (eier) ønsker jeg å angi antall studieplasser per utdanning i mitt opptak
- [ ] Som opptaksleder (eier) ønsker jeg å se alle utdanninger i mitt opptak uavhengig av organisasjon

### Utdanninger i opptak - Deltager i samordnet opptak
- [ ] Som opptaksleder (deltager) ønsker jeg å legge til utdanninger fra min organisasjon i samordnet opptak jeg har tilgang til
- [ ] Som opptaksleder (deltager) ønsker jeg å fjerne mine organisasjons utdanninger fra samordnet opptak
- [ ] Som opptaksleder (deltager) ønsker jeg å angi antall studieplasser for mine utdanninger i samordnet opptak
- [ ] Som opptaksleder (deltager) ønsker jeg å kun se mine organisasjons utdanninger i samordnet opptak

### Administrator - Full tilgang
- [ ] Som administrator ønsker jeg å legge til alle utdanninger til alle opptak uavhengig av organisasjon
- [ ] Som administrator ønsker jeg å administrere alle opptak på vegne av organisasjoner ved behov

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