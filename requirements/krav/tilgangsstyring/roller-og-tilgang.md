# Tilgangsstyring - Roller og tilgang

## Oversikt
Dette dokumentet beskriver rollene i opptakssystemet og deres tilganger. Tilgangsstyring er basert på roller og organisasjonstilhørighet.

## Grunnprinsipper

1. **Organisasjonsbasert tilgang**: Brukere tilhører en organisasjon og har primært tilgang til data knyttet til sin organisasjon
2. **Eksplisitt tilgang**: Tilgang til andres data må gis eksplisitt (f.eks. ved samordnede opptak)
3. **Minste privilegium**: Brukere får kun tilgang til det de trenger for å utføre sine oppgaver
4. **Sporbarhet**: Alle endringer logges med hvem som gjorde hva når

## Roller

### 1. Administrator
**Formål**: Systemadministrasjon og overordnet styring av hele opptakssystemet

**Tilganger**:
- **Systemadministrasjon**:
  - Administrere alle organisasjoner (opprette, redigere, deaktivere)
  - Administrere alle brukere og roller
  - Se og redigere all data i systemet
  - Administrere systemkonfigurasjon og innstillinger
  
- **Opptak**:
  - Se og administrere alle opptak uavhengig av organisasjon
  - Overstyre opptaksinnstillinger ved behov
  - Administrere systemvide opptak (som Samordnet Opptak)
  
- **Støtte og vedlikehold**:
  - Tilgang til systemlogger og feilsøking
  - Kan utføre handlinger på vegne av andre brukere ved behov
  - Overstyre forretningsregler ved spesielle tilfeller

### 2. Opptaksleder
**Formål**: Administrere opptak og utdanninger for sin organisasjon

**Tilganger**:
- **Opptak**:
  - Opprette nye opptak for sin organisasjon
  - Administrere opptak som organisasjonen eier (endre status, frister, etc.)
  - Gi andre organisasjoners opptaksledere tilgang til egne opptak (samordning)
  - Fjerne tilgang for organisasjoner som ikke lenger skal delta
  - Se opptak hvor egen organisasjon har fått tilgang av andre
  
- **Utdanninger**:
  - Legge til egne organisasjons utdanninger i opptak (der de har tilgang)
  - Definere antall studieplasser for egne utdanninger
  - Aktivere/deaktivere egne utdanninger i opptak
  - Spesialtilfelle: Opptaksleder som eier opptaket kan legge til alle organisasjoners utdanninger

- **Organisasjon**:
  - Se og redigere egen organisasjonsinformasjon
  - Se andre organisasjoner (readonly)

### 3. Søknadsbehandler
**Formål**: Behandle søknader og tildele plasser

**Tilganger**:
- **Søknader**:
  - Se alle søknader til egen organisasjons utdanninger
  - Behandle søknader (godkjenne, avslå, sette på venteliste)
  - Registrere karakterer og poengberegning
  - Sende tilbud om studieplass

- **Opptak**:
  - Se opptak hvor egen organisasjon deltar (readonly)
  - Se statistikk for egne utdanninger

- **Søkere**:
  - Se søkerinformasjon for de som har søkt egne utdanninger
  - Kan IKKE se søkere som kun har søkt andre organisasjoners utdanninger

### 4. Søker
**Formål**: Søke på utdanninger og følge opp egen søknad

**Tilganger**:
- **Søknader**:
  - Opprette og endre egne søknader (innen frister)
  - Se status på egne søknader
  - Akseptere/avslå tilbud om studieplass
  - Laste opp dokumentasjon

- **Opptak**:
  - Se åpne opptak
  - Se alle tilgjengelige utdanninger i åpne opptak
  - Kan IKKE se andre søkeres informasjon

- **Egen profil**:
  - Oppdatere personopplysninger
  - Se egen søknadshistorikk

## Tilgangsmatrisen

| Funksjon | Administrator | Opptaksleder | Søknadsbehandler | Søker |
|----------|---------------|--------------|------------------|--------|
| **Systemadministrasjon** |
| Administrere organisasjoner | ✅ Alle | ❌ | ❌ | ❌ |
| Administrere brukere og roller | ✅ | ❌ | ❌ | ❌ |
| Systemkonfigurasjon | ✅ | ❌ | ❌ | ❌ |
| **Opptak** |
| Opprette opptak | ✅ Alle | ✅ Egen org | ❌ | ❌ |
| Administrere opptak | ✅ Alle | ✅ Egen org | ❌ | ❌ |
| Gi tilgang til opptak | ✅ Alle | ✅ Eget opptak | ❌ | ❌ |
| Se opptak | ✅ Alle | ✅ Alle | ✅ Med tilgang | ✅ Åpne |
| **Utdanninger** |
| Legge til utdanning i opptak | ✅ Alle | ✅ Egen org* | ❌ | ❌ |
| Endre studieplasser | ✅ Alle | ✅ Egen org | ❌ | ❌ |
| Se utdanninger | ✅ Alle | ✅ Alle | ✅ Alle | ✅ Alle |
| **Søknader** |
| Se søknader | ✅ Alle | ❌ | ✅ Egen org | ✅ Egne |
| Behandle søknader | ✅ Alle | ❌ | ✅ Egen org | ❌ |
| Opprette søknad | ❌ | ❌ | ❌ | ✅ |
| **Søkere** |
| Se søkerinformasjon | ✅ Alle | ❌ | ✅ Egne søkere | ✅ Seg selv |
| Endre søkerinformasjon | ✅ Alle | ❌ | ❌ | ✅ Seg selv |

*Med tilgang til opptaket

## Spesielle regler for samordning

### Opptak eid av egen organisasjon
Når en opptaksleder oppretter et opptak for sin organisasjon:
- Opptaksleder har full kontroll over opptaket
- Kan velge hvilke andre organisasjoners opptaksledere som får tilgang
- Kan legge til utdanninger fra alle organisasjoner (hvis nødvendig)
- Andre organisasjoners opptaksledere kan kun legge til egne utdanninger (når de har fått tilgang)

### Samordnet Opptak (SO)
- SO er en egen organisasjon med opptaksledere
- SO-opptaksledere administrerer nasjonale samordnede opptak
- SO-opptaksledere gir tilgang til relevante utdanningsinstitusjoner
- Institusjoner med tilgang kan kun legge til egne utdanninger

### Lokale opptak
- Administreres av opptaksledere ved den enkelte institusjon
- Institusjonen kan velge å samordne med andre ved å gi eksplisitt tilgang
- Standard er at kun egen organisasjon har tilgang

## Eksempler på tilgangsstyring

### Eksempel 1: Samordnet opptak
1. SO-opptaksleder oppretter "Samordnet opptak H25"
2. SO-opptaksleder gir NTNU, UiO og UiB tilgang
3. NTNU-opptaksleder kan nå legge til NTNU-utdanninger
4. UiO-opptaksleder kan legge til UiO-utdanninger
5. UiB-opptaksleder kan legge til UiB-utdanninger

### Eksempel 2: Bilateral samordning
1. NTNU-opptaksleder oppretter "NTNU-UiO Felles masteropptak"
2. NTNU-opptaksleder gir UiO tilgang
3. Både NTNU og UiO opptaksledere kan legge til sine egne utdanninger
4. NTNU-opptaksleder kan (som eier) også legge til UiO-utdanninger hvis nødvendig

### Eksempel 3: Lokalt opptak
1. NTNU-opptaksleder oppretter "NTNU Lokalt opptak H25"
2. Ingen andre organisasjoner gis tilgang
3. Kun NTNU-opptaksleder kan legge til utdanninger

## Fremtidige utvidelser
- **Fakultet/Institutt-nivå**: Tilgangsstyring på undernivå i organisasjonen
- **Delegering**: Mulighet for å delegere rettigheter til andre brukere
- **Tidsbegrenset tilgang**: Tilgang som automatisk utløper
- **Les-tilgang**: Mulighet for å gi andre organisasjoner innsyn uten redigeringsrettigheter