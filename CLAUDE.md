# Norsk Opptaksystem - Prosjekt Kunnskapsbase

## Prosjektoversikt
- **Formål**: Opptaksystem for norsk utdanning
- **Utviklingstilnærming**: Enkel, AI-assistert utvikling
- **Språk**: Norsk (hele systemet)

## Spørsmål & Svar Logg

### Første runde svar (2025-08-28)

#### Grunnleggende informasjon
1. **Hovedformål**: Opptaksystem
2. **Brukergrupper**: 
   - Søkere
   - Opptaksledere
   - Søknadsbehandlere
3. **Kjernefunksjonalitet** (5 hovedmoduler):
   - Regelverk
   - Opptak
   - Søknadsregistrering
   - Søknadsbehandling
   - Plasstildeling

#### Systemkrav
- **Utdanningsnivå**: Generisk system for alle nivåer
- **Opptakstyper**: Både lokale og nasjonale opptak
- **Skala**: Opp til 100 000 søkere (samordnet opptak bachelor)
- **Utviklingsmetode**: Inkrementell utvikling

#### Utviklerinfo
- **Programmeringserfaring**: Begynner (god konseptforståelse)
- **Utviklingsmiljø**: Claude Code i VSCode
- **Tech stack**: 
  - **API**: GraphQL (domene-drevet design - "slik brukerne snakker om domenet")
  - **Backend**: Java (Spring Boot)
  - **Frontend**: React (senere - ikke i første fase)
  - **Database**: PostgreSQL med JOOQ (typesikker SQL)
  - **Utviklingsstrategi**: GraphQL-først tilnærming, backend først

#### Tekniske vurderinger
- **Quarkus vs Spring Boot**: Valgte Spring Boot pga bedre dokumentasjon og større community for nybegynnere
- **Java-rammeverk**: Spring Boot (besluttet)

## Prosjektstruktur (Spring Boot + GraphQL)

### Foreslått mappestruktur:
```
opptaksystem/
├── krav/                       # Domenekrav og dokumentasjon
│   ├── regelverk/              # Modul: Regelverk
│   │   ├── user-stories.md
│   │   ├── entity-map.md
│   │   └── example-mappings/
│   ├── opptak/                 # Modul: Opptak
│   │   ├── user-stories.md
│   │   ├── entity-map.md
│   │   └── example-mappings/
│   ├── soknadsregistrering/    # Modul: Søknadsregistrering
│   │   ├── user-stories.md
│   │   ├── entity-map.md
│   │   └── example-mappings/
│   ├── soknadsbehandling/      # Modul: Søknadsbehandling
│   │   ├── user-stories.md
│   │   ├── entity-map.md
│   │   └── example-mappings/
│   └── plasstildeling/         # Modul: Plasstildeling
│       ├── user-stories.md
│       ├── entity-map.md
│       └── example-mappings/
├── src/main/java/no/utdanning/opptak/
│   ├── domain/                 # Domeneklasser (Søker, Opptak, etc.)
│   ├── graphql/                # GraphQL resolvers og datatyper
│   ├── service/                # Forretningslogikk
│   ├── repository/             # Database-tilgang
│   └── config/                 # Konfigurasjon
├── src/main/resources/
│   ├── graphql/                # GraphQL schema-filer (.graphqls)
│   └── application.yml         # App-konfigurasjon
├── CLAUDE.md                   # AI-assistert utviklingsnotat
└── pom.xml / build.gradle
```

## Utviklingsmetodikk (Besluttet)

### Arbeidsprosess
- **Iterativ utvikling**: Én GraphQL query/mutation av gangen
- **Testing**: Enhetstester skrives etter implementering (ikke TDD)
- **Domain-Driven Design**: Dokumentere domenet grundig først
- **Fokus**: Domenemodellering er prioritet

### Domenemodellering - Tre-stegs tilnærming

1. **User Story Mapping**: 
   - Kartlegger alle aktiviteter brukere skal gjøre
   - Fungerer som produktbacklog
   - Organisert etter brukerreise

2. **Entity Maps**: 
   - Beskriver domenet slik brukere snakker om det
   - Grunnlag for GraphQL-typer
   - Viser relasjoner mellom konsepter

3. **Example Mapping** (per User Story):
   - Forstå aktiviteter i detalj
   - Identifisere forretningsregler
   - Konkrete eksempler på bruk
   - **4 komponenter:**
     - 📝 **User Story** (gul): Funksjonaliteten vi diskuterer
     - 📏 **Regler** (blå): Forretningsregler og akseptansekriterier
     - ✅ **Eksempler** (grønn): Konkrete scenarioer som illustrerer reglene
     - ❓ **Spørsmål** (rød): Uklarheter som må avklares

### Kobling til GraphQL
- **User Stories** → GraphQL Queries & Mutations
- **Entity Maps** → GraphQL Types
- **Example Mapping** → Forretningslogikk & valideringsregler

### Besluttet - Utviklingsmetodikk

#### AI-samarbeidsmodell
- **Koding**: Claude skriver koden
- **GraphQL-læring**: Går sakte gjennom queries, mutations og types sammen
- **Feilhåndtering**: Claude fikser og forklarer

#### Kodekvalitet
- **Prioritet**: Lesbar kode, lite kompleksitet
- **Kommentering**: 
  - Backend: Kun det viktigste
  - GraphQL: God dokumentasjon i schema (queries, types, mutations)

#### Arbeidsflyt
- **Sesjonsmål**: Tydelige mål per user story eller entitet
- **Debugging**: Rask fix og fortsett
- **Læringsstil**: Teori først, så praksis

### Besluttet - Teknisk strategi

#### Git-strategi
- **Trunk Based Development**: Én branch (main)
- **Commit-frekvens**: Veldig ofte, etter hver fungerende endring
- **Commit-språk**: Norsk
- **CI**: Kontinuerlig integrasjon

#### Deployment/Testing
- **Docker**: Nei (problemer med Claude Code)
- **Testing**: Automatisk testing lokalt
- **Kjøremiljø**: Lokalt utvikling

#### Sikkerhet & Performance
- **Autentisering**: Mock i starten
- **Feide**: Vurderes senere
- **Performance**: Optimalisere senere (ikke prematur optimalisering)
- **Caching**: Vurderes ved behov

## Claude Code Samarbeidsregler

### Kodekvalitet - Java/Spring Boot
- **Formattering**: Bruk standard Java-konvensjoner (4 spaces indent)
- **Navngiving**: camelCase for variabler/metoder, PascalCase for klasser
- **Pakkestruktur**: Følg domenedrevet design
- **Testing**: JUnit 5 + Mockito for enhetstester

### Kommandoer for kvalitetssikring
- **Build**: `./mvnw clean install`
- **Test**: `./mvnw test`
- **Spesifikk test**: `./mvnw test -Dtest=KlasseNavn`
- **Start development**: `./start-dev.sh` (H2, port 8080)
- **Start production**: `sudo ./start-prod.sh` (PostgreSQL, port 80)
- **GraphQL Playground**: 
  - Dev: http://localhost:8080/graphiql
  - Prod: http://opptaksapp.smidigakademiet.no/graphiql

**Maven wrapper fix**: 
- ✅ FIKSET: La til `-Dmaven.multiModuleProjectDirectory=$APP_HOME` i mvnw
- Nå fungerer alle maven-kommandoer uten ekstra miljøvariabler

### Claude Code instruksjoner
- **Alltid kjør tester** etter større endringer
- **Commit hyppig** med beskrivende norske meldinger
- **GraphQL først**: Definer schema før implementasjon
- **Forklar GraphQL-konsepter** grundig under implementering
- **Bruk @Description** annotations i GraphQL schema for god dokumentasjon

### Viktige filer å huske
- `CLAUDE.md` - Denne filen, prosjektets hukommelse
- `krav/templates/` - Templates for domenekrav dokumentasjon
- `krav/*/` - Domenekrav per modul
- `src/main/resources/graphql/` - GraphQL schemas
- `application.yml` - Konfigurasjon

### Kravdokumentasjon Templates
- `krav/templates/user-story-map-template.md` - Template for user story mapping
- `krav/templates/entity-map-template.md` - Template for entitetsdiagram (Mermaid ER)
- `krav/templates/example-mapping-template.md` - Template for example mapping per brukerhistorie

### Arbeidsregler
- **Start hver sesjon**: Les CLAUDE.md for kontekst
- **Før større endringer**: Diskuter tilnærming først
- **Ved feil**: Forklar årsak og løsning
- **Nye konsepter**: Teori først, så implementering
- **INGEN YAK SHAVING**: Ved problemer - STOPP, diskuter og be om hjelp. Ingen quick fixes som skaper teknisk gjeld
