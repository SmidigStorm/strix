# Strix - Opptakssystem Prosjekt Kunnskapsbase

## Prosjektoversikt
- **Navn**: Strix (norsk navn for hubro/owl)
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

   **Forenklet navigasjon**: I frontend vises foreløpig kun 3 hovedkategorier (Opptak, Organisasjon, Utdanning) som organiserer de 5 modulene.

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
  - **Frontend**: React + Vite + TailwindCSS + shadcn/ui ✅ IMPLEMENTERT
  - **Database**: PostgreSQL med JOOQ (typesikker SQL)
  - **Utviklingsstrategi**: Fullstack utvikling med moderne frontend

#### Tekniske vurderinger
- **Quarkus vs Spring Boot**: Valgte Spring Boot pga bedre dokumentasjon og større community for nybegynnere
- **Java-rammeverk**: Spring Boot (besluttet)

## Prosjektstruktur (2025-08-29 - Refaktorert)

### Atual mappestruktur (ren organisering):
```
strix/
├── requirements/               # Alle krav og dokumentasjon
│   ├── design/                 # Design og fargepalett (Hubro/owl logo)
│   └── krav/                   # Domenekrav og templates
│       ├── opptak/             # Modul: Opptak
│       ├── templates/          # Maler for domenekrav
│       └── tilgangsstyring/    # Roller og tilgang
├── frontend/                   # React + Vite + TailwindCSS + shadcn/ui
│   ├── src/
│   │   ├── components/         # UI komponenter
│   │   │   ├── app-sidebar.tsx # Hovednavigasjon
│   │   │   ├── dashboard.tsx   # Dashboard med statistikk
│   │   │   └── ui/            # shadcn/ui komponenter
│   │   ├── hooks/             # React hooks
│   │   └── lib/               # Utilities
│   ├── public/                # Statiske filer (owl logo)
│   └── package.json           # Frontend avhengigheter
└── backend/                   # Spring Boot + GraphQL backend
    ├── .mvn/                  # Maven wrapper
    ├── src/main/java/no/utdanning/opptak/
    │   ├── domain/            # Domeneklasser
    │   ├── graphql/           # GraphQL resolvers
    │   ├── service/           # Forretningslogikk
    │   ├── repository/        # Database-tilgang
    │   └── config/            # Konfigurasjon
    ├── src/main/resources/
    │   ├── graphql/           # GraphQL schemas
    │   └── db/migration/      # Database migrations
    ├── data/                  # Database filer
    ├── start-*.sh             # Startup scripts
    └── pom.xml                # Maven konfigurasjon
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

## Frontend Implementasjon (2025-08-29)

### Teknisk Stack
- **Framework**: React 19.1.1 med TypeScript
- **Build tool**: Vite 7.1.3 for rask utvikling
- **Styling**: TailwindCSS v4 med tilpasset fargepalett
- **UI Library**: shadcn/ui for konsistente komponenter
- **Ikon**: Lucide React ikoner

### Design System
- **Navn**: Strix (viser owl-logo i stedet for emoji)
- **Fargepalett** (fra `requirements/design/design.md`):
  - Primary: `#1F261E` (mørk grønn/charcoal)
  - Accent: `#F2C53D` (gul/gull)  
  - Secondary: `#F27C38` (oransje)
  - Background: `#F2F2F2` (lys grå)
  - Light accent: `#F2E3B3` (lys krem/beige)
- **Logo**: Hubro/owl bilde fra requirements/design/

### Arkitektur
- **Layout**: Professional admin dashboard med sidebar
- **Navigation**: 3 hovedmoduler (Opptak, Organisasjon, Utdanning)
- **Responsiv**: Mobilvennlig med collapse sidebar
- **Komponenter**: 
  - `app-sidebar.tsx` - Hovednavigasjon med rolle-switcher
  - `dashboard.tsx` - Statistikk og KPI-kort
  - Gjenbrukbare shadcn/ui komponenter

### Utvikling
- **Dev server**: http://localhost:5173/ (Vite)
- **Hot reload**: Automatisk oppdatering under utvikling
- **TypeScript**: Strikt type-sjekking aktivert
- **Import alias**: `@/` peker til `src/` mappen

## Claude Code Samarbeidsregler

### Kodekvalitet - Java/Spring Boot
- **Formattering**: Bruk standard Java-konvensjoner (4 spaces indent)
- **Navngiving**: camelCase for variabler/metoder, PascalCase for klasser
- **Pakkestruktur**: Følg domenedrevet design
- **Testing**: JUnit 5 + Mockito for enhetstester

### Kommandoer for kvalitetssikring

#### Backend (fra backend/ mappen)
- **Build**: `./mvnw clean install`
- **Test**: `./mvnw test`
- **Spesifikk test**: `./mvnw test -Dtest=KlasseNavn`
- **Start development**: `./start-dev.sh` (H2, port 8080)
- **Start production**: `sudo ./start-prod.sh` (PostgreSQL, port 80)
- **GraphQL Playground**: 
  - Dev: http://localhost:8080/graphiql
  - Prod: http://opptaksapp.smidigakademiet.no/graphiql

#### Frontend (fra frontend/ mappen)
- **Start dev server**: `npm run dev` (http://localhost:5173/)
- **Build**: `npm run build`
- **Test build**: `npm run preview`
- **Lint**: `npm run lint`

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
- `requirements/krav/templates/` - Templates for domenekrav dokumentasjon
- `requirements/krav/*/` - Domenekrav per modul
- `requirements/design/` - Logo og fargepalett
- `backend/src/main/resources/graphql/` - GraphQL schemas
- `backend/src/main/resources/application.yml` - Backend konfigurasjon
- `frontend/src/components/` - React komponenter
- `frontend/package.json` - Frontend avhengigheter

### Kravdokumentasjon Templates (requirements/krav/templates/)
- `user-story-map-template.md` - Template for user story mapping
- `entity-map-template.md` - Template for entitetsdiagram (Mermaid ER)
- `example-mapping-template.md` - Template for example mapping per brukerhistorie

### Arbeidsregler
- **Start hver sesjon**: Les CLAUDE.md for kontekst
- **Før større endringer**: Diskuter tilnærming først
- **Ved feil**: Forklar årsak og løsning
- **Nye konsepter**: Teori først, så implementering
- **INGEN YAK SHAVING**: Ved problemer - STOPP, diskuter og be om hjelp. Ingen quick fixes som skaper teknisk gjeld
