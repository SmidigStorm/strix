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
  - **Backend**: Java (Spring Boot) ✅ IMPLEMENTERT
  - **Frontend**: React + Vite + TailwindCSS + shadcn/ui ✅ IMPLEMENTERT
  - **Database**: H2 (dev & prod) - ingen PostgreSQL/JOOQ ennå
  - **Code Quality**: Spotless + Google Java Format ✅ IMPLEMENTERT
  - **Utviklingsstrategi**: Fullstack utvikling med moderne frontend

#### Tekniske vurderinger
- **Quarkus vs Spring Boot**: Valgte Spring Boot pga bedre dokumentasjon og større community for nybegynnere
- **Java-rammeverk**: Spring Boot (besluttet)

## Prosjektstruktur (2025-08-29 - Oppdatert)

### Faktisk mappestruktur (etter fullstændig implementering):
```
strix/
├── CLAUDE.md                   # Prosjektets hukommelse og dokumentasjon
├── README.md                   # Brukerrettet dokumentasjon
├── requirements/               # Alle krav og dokumentasjon
│   ├── design/                 # Design og fargepalett (Hubro/owl logo)
│   │   ├── design.md           # Fargepalett og designsystem
│   │   └── Hubro Color.png     # Logo referanse
│   └── krav/                   # Domenekrav og templates
│       ├── opptak/             # Modul: Opptak
│       ├── templates/          # Maler for domenekrav
│       └── tilgangsstyring/    # Roller og tilgang
├── frontend/                   # React + Vite + TailwindCSS + shadcn/ui ✅
│   ├── src/
│   │   ├── components/         # UI komponenter
│   │   │   ├── app-sidebar.tsx # Hovednavigasjon med GraphiQL-lenke ✅
│   │   │   ├── dashboard.tsx   # Dashboard med statistikk ✅
│   │   │   └── ui/            # shadcn/ui komponenter ✅
│   │   ├── hooks/             # React hooks ✅
│   │   ├── lib/               # Utilities ✅
│   │   └── index.css          # TailwindCSS med custom fargepalett ✅
│   ├── public/                # Statiske filer
│   │   └── owl-logo.png       # Logo og favicon ✅
│   ├── index.html             # HTML template med "Strix" title ✅
│   ├── dist/                  # Bygget frontend (kopieres til backend) ✅
│   └── package.json           # Frontend avhengigheter ✅
└── backend/                   # Spring Boot + GraphQL backend ✅
    ├── src/main/java/no/utdanning/opptak/
    │   ├── controller/        # Web controllers
    │   │   ├── FrontendController.java  # React SPA routing ✅
    │   │   └── GraphiQLController.java  # GraphiQL endpoint ✅
    │   ├── domain/            # Domeneklasser ✅
    │   ├── graphql/           # GraphQL resolvers ✅
    │   ├── service/           # Forretningslogikk (AuthService, JwtService) ✅
    │   ├── repository/        # Database-tilgang ✅
    │   └── config/            # Konfigurasjon (WebConfig med CORS) ✅
    ├── src/main/resources/
    │   ├── static/            # Produksjons frontend filer ✅
    │   │   ├── assets/        # CSS, JS og andre assets ✅
    │   │   ├── index.html     # React entry point ✅
    │   │   └── owl-logo.png   # Logo for produksjon ✅
    │   ├── graphql/           # GraphQL schemas ✅
    │   ├── db/migration/      # Flyway database migrations ✅
    │   ├── application.yml    # Hovedkonfigurasjon ✅
    │   ├── application-dev.yml # Development profil (H2 in-memory) ✅
    │   └── application-prod.yml # Production profil (H2 file-based) ✅
    ├── data/                  # H2 database filer (produksjon) ✅
    ├── start-dev.sh           # Development startup ✅
    ├── start-prod.sh          # Production startup ✅
    ├── pom.xml                # Maven med Spotless og Checkstyle ✅
    └── checkstyle.xml         # Kodekvalitet regler ✅
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
- **Navn**: Strix (viser owl-logo)
- **Favicon**: Ugla/owl-logo.png ✅ IMPLEMENTERT
- **Browser title**: "Strix" ✅ IMPLEMENTERT
- **Fargepalett** (fra `requirements/design/design.md`):
  - Primary: `#1F261E` (mørk grønn/charcoal)
  - Accent: `#F2C53D` (gul/gull)  
  - Secondary: `#F27C38` (oransje)
  - Background: `#F2F2F2` (lys grå)
  - Light accent: `#F2E3B3` (lys krem/beige)
- **Logo**: Hubro/owl bilde fra requirements/design/
- **Sidebar branding**: 10x10px ugla-ikon med "Strix" tekst ✅ IMPLEMENTERT

### Arkitektur
- **Layout**: Professional admin dashboard med sidebar ✅ IMPLEMENTERT
- **Navigation**: 3 hovedmoduler (Opptak, Organisasjon, Utdanning) ✅ IMPLEMENTERT
- **Responsiv**: Mobilvennlig med collapse sidebar ✅ IMPLEMENTERT
- **GraphiQL integrasjon**: Enkelt lenke i sidebar til /graphiql ✅ IMPLEMENTERT
- **Production ready**: Frontend serveres fra Spring Boot static resources ✅ IMPLEMENTERT
- **Komponenter**: 
  - `app-sidebar.tsx` - Hovednavigasjon med rolle-switcher og GraphiQL-lenke
  - `dashboard.tsx` - Statistikk og KPI-kort
  - `FrontendController.java` - Serverer React SPA
  - Gjenbrukbare shadcn/ui komponenter

### Utvikling og Produksjon
- **Frontend dev server**: http://localhost:5173/ (Vite)
- **Backend dev server**: http://localhost:8080/ (Spring Boot)
- **Production**: http://opptaksapp.smidigakademiet.no/ (port 80) ✅ IMPLEMENTERT
- **Hot reload**: Automatisk oppdatering under utvikling
- **TypeScript**: Strikt type-sjekking aktivert
- **Import alias**: `@/` peker til `src/` mappen
- **Build process**: Frontend bygges og kopieres til backend/static/ ✅ IMPLEMENTERT

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
- **Code formatting**: `./mvnw spotless:apply` ✅ IMPLEMENTERT
- **Code quality check**: `./mvnw checkstyle:check` ✅ IMPLEMENTERT
- **Spesifikk test**: `./mvnw test -Dtest=KlasseNavn`
- **Start development**: `./start-dev.sh` (H2 in-memory, port 8080)
- **Start production**: `sudo ./start-prod.sh` (H2 file-based, port 80)
- **GraphQL Endpoints**: 
  - Dev: http://localhost:8080/graphiql
  - Prod: http://opptaksapp.smidigakademiet.no/graphiql
  - API: http://localhost:8080/graphql (dev) / http://opptaksapp.smidigakademiet.no/graphql (prod)

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
- **Kjør code formatting** med `./mvnw spotless:apply` før commit ✅ IMPLEMENTERT
- **Commit hyppig** med beskrivende norske meldinger
- **GraphQL først**: Definer schema før implementasjon
- **Forklar GraphQL-konsepter** grundig under implementering
- **Bruk @Description** annotations i GraphQL schema for god dokumentasjon
- **Frontend build**: Kjør `npm run build` og kopier til backend/static/ før produksjon ✅ IMPLEMENTERT

### Viktige filer å huske
- `CLAUDE.md` - Denne filen, prosjektets hukommelse
- `README.md` - Brukerrettet dokumentasjon
- `requirements/krav/templates/` - Templates for domenekrav dokumentasjon
- `requirements/krav/*/` - Domenekrav per modul
- `requirements/design/` - Logo og fargepalett
- `backend/src/main/resources/graphql/` - GraphQL schemas
- `backend/src/main/resources/application.yml` - Backend konfigurasjon
- `backend/src/main/resources/static/` - Produksjons frontend filer ✅ IMPLEMENTERT
- `backend/src/main/java/.../controller/FrontendController.java` - React SPA routing ✅ IMPLEMENTERT
- `frontend/src/components/` - React komponenter
- `frontend/index.html` - HTML template med Strix branding ✅ IMPLEMENTERT
- `frontend/public/owl-logo.png` - Logo og favicon ✅ IMPLEMENTERT
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

## Produksjon og Deployment (2025-08-29)

### Nåværende Production Setup ✅ IMPLEMENTERT

**URL**: http://opptaksapp.smidigakademiet.no/

**Arkitektur**:
- Spring Boot backend serverer både API og frontend
- React frontend bygget som statiske filer i backend/static/
- H2 file-based database (./data/opptaksystem.mv.db)
- Port 80 (krever sudo)

**Endpoints**:
- **Frontend**: http://opptaksapp.smidigakademiet.no/
- **GraphQL API**: http://opptaksapp.smidigakademiet.no/graphql  
- **GraphiQL**: http://opptaksapp.smidigakademiet.no/graphiql

### Deployment Prosess

**Steg for å deploye frontend endringer**:
1. `cd frontend && npm run build`
2. `rm -rf backend/src/main/resources/static/assets/`
3. `cp -r frontend/dist/* backend/src/main/resources/static/`
4. `git add . && git commit -m "..."` 
5. `git push origin main`
6. På server: `sudo ./start-prod.sh`

**Database**:
- **Development**: H2 in-memory (jdbc:h2:mem:devdb)
- **Production**: H2 file-based (jdbc:h2:file:./data/opptaksystem)
- **Migreringer**: Flyway kjører automatisk ved oppstart

### CORS Konfigurasjon ✅ IMPLEMENTERT

**Development**: 
- http://localhost:3000, http://localhost:5173, http://localhost:4200

**Production**:
- https://opptaksapp.smidigakademiet.no
- https://www.smidigakademiet.no  
- Samt localhost for testing

### Sikkerhet

**Nåværende status**:
- JWT-basert autentisering ✅ IMPLEMENTERT
- Mock test-brukere for utvikling ✅ IMPLEMENTERT
- CORS konfigurert for tillatte domener ✅ IMPLEMENTERT
- GraphiQL beskyttet av backend-autentisering

**Fremtidige forbedringer**:
- Feide-integrasjon
- HTTPS redirect
- Rate limiting
- Database kryptering
