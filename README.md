# Strix - Opptaksystem

Et moderne opptaksystem for norsk utdanning, bygget med Spring Boot, GraphQL og React.

**Live system**: http://opptaksapp.smidigakademiet.no/

![Strix Logo](requirements/design/Hubro%20Color.png)

## 🏛️ Arkitektur

### Development vs Production Setup

**🔧 Development (utvikling):**
- **Frontend**: Vite dev server på http://localhost:5173/ (hot reload, rask utvikling)
- **Backend**: Spring Boot på http://localhost:8080/ (GraphQL API + H2 in-memory)
- **Fordeler**: Rask utvikling med hot reload, separate servere for optimal utviklingsopplevelse

**🚀 Production (produksjon):**
- **Frontend**: Bygges til statiske filer og serveres av Spring Boot
- **Backend**: Spring Boot serverer både API og frontend fra samme server (port 80)
- **Database**: H2 file-based for persistens
- **Fordeler**: Enklere deployment, ingen CORS-problemer, kun én server å administrere

### Hvorfor denne tilnærmingen?

1. **Enkel deployment**: Kun én JAR-fil å deploye
2. **Ingen CORS-problemer**: Frontend og API på samme domene i produksjon
3. **Optimal utvikling**: Separate servere gir best utviklingsopplevelse
4. **Spring Boot routing**: Håndterer både API-kall og React SPA routing

## 🚀 Kom i gang

### Forutsetninger
- Java 21 eller nyere
- Node.js 18+ og npm (for frontend utvikling)
- Git

### Development Setup

1. **Klon repositoriet:**
   ```bash
   git clone <repository-url>
   cd strix
   ```

2. **Start backend (development):**
   ```bash
   cd backend
   ./start-dev.sh
   ```
   
3. **Start frontend (development):**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

4. **Åpne applikasjonen:**
   - **Frontend**: http://localhost:5173/ (Vite dev server med hot reload)
   - **Backend API**: http://localhost:8080/graphql
   - **GraphiQL**: http://localhost:8080/graphiql
   - **H2 Console**: http://localhost:8080/h2-console

### Production Deployment

**Komplett deployment prosess:**

1. **Bygg frontend til statiske filer:**
   ```bash
   cd frontend
   npm run build
   ```

2. **Kopier frontend build til backend:**
   ```bash
   # Fra root directory
   rm -rf backend/src/main/resources/static/assets/
   cp -r frontend/dist/* backend/src/main/resources/static/
   ```

3. **Commit endringene:**
   ```bash
   git add .
   git commit -m "Deploy frontend til produksjon"
   git push origin main
   ```

4. **Start production server:**
   ```bash
   cd backend
   sudo ./start-prod.sh  # Krever sudo for port 80
   ```

5. **Åpne production:**
   - **Full system**: http://opptaksapp.smidigakademiet.no/
   - **GraphQL API**: http://opptaksapp.smidigakademiet.no/graphql
   - **GraphiQL**: http://opptaksapp.smidigakademiet.no/graphiql

**⚠️ Viktig**: I produksjon serverer Spring Boot både frontend og API fra samme server, mens i development kjører de på separate porter.

## 📡 GraphQL API

Systemet tilbyr en GraphQL API på `http://localhost:8080/graphql`.

### GraphiQL Demo - 4 Spørringer

**Åpne GraphiQL:** http://localhost:8080/graphiql

#### 1. Innlogging - Få JWT token
```graphql
mutation Login {
  login(input: {
    email: "admin@strix.no"
    passord: "test123"
  }) {
    token
    bruker {
      navn
      email
      roller {
        navn
      }
      organisasjon {
        navn
        kortNavn
      }
    }
  }
}
```

#### 2. Hent alle organisasjoner (krever autentisering)
```graphql
query HentOrganisasjoner {
  organisasjoner {
    id
    navn
    kortNavn
    type
    organisasjonsnummer
    nettside
    aktiv
  }
}
```

#### 3. Hent opptak med detaljert info
```graphql
query HentOpptak {
  opptak {
    id
    navn
    type
    aar
    status
    samordnet
    soknadsfrist
    administrator {
      navn
      kortNavn
    }
    utdanninger {
      id
      antallPlasser
      aktivt
      utdanning {
        navn
        studienivaa
        organisasjon {
          navn
        }
      }
    }
  }
}
```

#### 4. Hent utdanninger med organisasjonsinfo
```graphql
query HentUtdanninger {
  utdanninger {
    id
    navn
    studienivaa
    studiepoeng
    varighet
    studiested
    organisasjon {
      navn
      kortNavn
      type
    }
  }
}
```

### Autentisering i GraphiQL

**Steg 1:** Kjør først innlogging-mutation for å få token  
**Steg 2:** Kopier token fra responsen  
**Steg 3:** Legg til HTTP header i GraphiQL (under query-ruten):

```json
{
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBzdHJpeC5ubyIsInJvbGxlciI6WyJBRE1JTklTVFJBVE9SIl0sIm9yZ2FuaXNhc2pvbklkIjpudWxsLCJpYXQiOjE3MjUxMjYyOTAsImV4cCI6MTcyNTEyOTg5MH0.DITT_TOKEN_HER"
}
```

**Test-brukere tilgjengelig:**
- `admin@strix.no` (Administrator) - passord: `test123`
- `opptaksleder@ntnu.no` (Opptaksleder NTNU) - passord: `test123`  
- `behandler@uio.no` (Søknadsbehandler UiO) - passord: `test123`

### Andre nyttige queries:

#### Test-brukere (for utvikling)
```graphql
query {
  testBrukere {
    email
    navn
    roller
    organisasjon
    passord
  }
}
```

#### Systeminformasjon
```graphql
query {
  systemInfo {
    navn
    versjon
    beskrivelse
  }
}
```

## 🏗️ Prosjektstruktur

```
strix/
├── CLAUDE.md                   # Prosjektets hukommelse og dokumentasjon
├── README.md                   # Denne filen
├── requirements/               # Krav og dokumentasjon
│   ├── design/                 # Logo og fargepalett
│   └── krav/                   # Domenekrav og templates
├── frontend/                   # React + Vite + TailwindCSS
│   ├── src/
│   │   ├── components/         # UI komponenter
│   │   │   ├── app-sidebar.tsx # Hovednavigasjon
│   │   │   ├── dashboard.tsx   # Dashboard
│   │   │   └── ui/            # shadcn/ui komponenter
│   │   └── lib/               # Utilities
│   ├── public/owl-logo.png    # Logo og favicon
│   └── package.json           # Frontend avhengigheter
└── backend/                   # Spring Boot + GraphQL
    ├── src/main/
    │   ├── java/no/utdanning/opptak/
    │   │   ├── controller/    # Web controllers (Frontend & GraphiQL)
    │   │   ├── domain/        # Domeneklasser
    │   │   ├── graphql/       # GraphQL resolvers
    │   │   ├── service/       # Forretningslogikk
    │   │   ├── repository/    # Database-tilgang
    │   │   └── config/        # Konfigurasjon
    │   └── resources/
    │       ├── static/        # Produksjons frontend filer
    │       ├── graphql/       # GraphQL schemas
    │       └── db/migration/  # Database migrations
    ├── data/                  # H2 database filer (produksjon)
    ├── start-dev.sh          # Development startup
    ├── start-prod.sh         # Production startup
    └── pom.xml               # Maven konfigurasjon
```

## 🛠️ Utvikling

### Backend Kommandoer (fra backend/ mappen)

- **Start development:** `./start-dev.sh` (H2 in-memory, port 8080)
- **Start production:** `sudo ./start-prod.sh` (H2 file-based, port 80)
- **Kjør tester:** `./mvnw test`
- **Bygg prosjekt:** `./mvnw clean install`
- **Kodeformatering:** `./mvnw spotless:apply`
- **Kodekvalitet:** `./mvnw checkstyle:check`
- **Stopp applikasjon:** `Ctrl+C`

### Frontend Kommandoer (fra frontend/ mappen)

- **Start dev server:** `npm run dev` (http://localhost:5173/)
- **Bygg for produksjon:** `npm run build`
- **Forhåndsvis build:** `npm run preview`
- **Lint kode:** `npm run lint`

### Database

**Development** (H2 in-memory):
- **URL:** `jdbc:h2:mem:devdb`
- **Bruker:** `sa`
- **Passord:** (tomt)
- **Console:** http://localhost:8080/h2-console

**Production** (H2 file-based):
- **URL:** `jdbc:h2:file:./data/opptaksystem`
- **Bruker:** `sa`
- **Passord:** (tomt)
- **Fil:** `backend/data/opptaksystem.mv.db`

### Autentisering og Test-brukere

Systemet bruker JWT tokens for autentisering. I utviklingsmiljø har vi følgende test-brukere:

| Email | Navn | Rolle | Organisasjon | Passord |
|-------|------|-------|--------------|---------|
| `opptaksleder@ntnu.no` | Kari Opptaksleder | OPPTAKSLEDER | NTNU-TEST | test123 |
| `behandler@uio.no` | Per Behandler | SOKNADSBEHANDLER | UiO-TEST | test123 |
| `soker@student.no` | Astrid Søker | SOKER | - | test123 |
| `admin@samordnetopptak.no` | Bjørn SO-Administrator | OPPTAKSLEDER | SO-TEST | test123 |

**Slik logger du inn:**
1. Kjør `testBrukere` query for å få oversikt over tilgjengelige brukere
2. Bruk en av test-brukernes email/passord kombinasjoner
3. Ved innlogging får du et JWT token som brukes for videre API-kall
4. Bruk `meg` query for å se hvilken bruker du er logget inn som

**Test-brukerne opprettes automatisk** via database-migrering (`V004__Legg_til_test_brukere.sql`).

### GraphQL Utvikling

1. **Schema-filer:** Alle GraphQL schemas ligger i `src/main/resources/graphql/`
2. **Resolvers:** Java-klasser i `src/main/java/.../graphql/`
3. **Testing:** Bruk GraphiQL på http://localhost:8080/graphiql

## 📚 Teknisk Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **API:** GraphQL med Spring GraphQL
- **Database:** H2 (både utvikling og produksjon)
- **Migrering:** Flyway
- **Java:** 21
- **Build:** Maven
- **Kodekvalitet:** Spotless + Google Java Format + Checkstyle
- **Sikkerhet:** JWT tokens

### Frontend
- **Framework:** React 19.1.1 med TypeScript
- **Build:** Vite 7.1.3
- **Styling:** TailwindCSS v4
- **UI Library:** shadcn/ui
- **Ikoner:** Lucide React
- **Deployment:** Statiske filer servert av Spring Boot

### Produksjon
- **Server:** Spring Boot (embedded Tomcat)
- **Port:** 80 (krever sudo)
- **URL:** http://opptaksapp.smidigakademiet.no/
- **CORS:** Konfigurert for produksjon og utvikling

## 🎯 Utviklingsmetodikk

Dette prosjektet følger Domain-Driven Design (DDD) prinsipper:

1. **User Story Mapping** - Kartlegger brukeraktiviteter
2. **Entity Maps** - Beskriver domenet slik brukere snakker om det  
3. **Example Mapping** - Detaljerte forretningsregler per user story

Se `CLAUDE.md` for full dokumentasjon av utviklingsmetodikk.

## 🏢 Domenemoduler

Systemet er organisert i 5 hovedmoduler:

1. **Regelverk** - Administrerer opptaksregler og kriterier
2. **Opptak** - Håndterer opptaksrunder og -prosesser  
3. **Søknadsregistrering** - Registrering av søknader
4. **Søknadsbehandling** - Behandling og vurdering av søknader
5. **Plasstildeling** - Tildeling av studieplasser

**Frontend navigasjon** viser 3 hovedkategorier:
- **Opptak** - Oversikt over opptaksrunder
- **Organisasjon** - Administrasjon av organisasjoner
- **Utdanning** - Håndtering av utdanningstilbud

**GraphiQL** er tilgjengelig via sidebar-lenke for API-testing.

## 🤝 Bidra

1. Les `CLAUDE.md` for detaljert utviklingsmetodikk og prosjektets hukommelse
2. Følg Domain-Driven Design prinsipper
3. Skriv tester for ny funksjonalitet
4. Bruk norsk språk i kode og dokumentasjon
5. Kjør `./mvnw spotless:apply` for kodeformatering før commit
6. Commit ofte med beskrivende norske meldinger
7. Test både frontend og backend før deploy

### Utviklingsflyt

1. **Backend utvikling:** Start med `./start-dev.sh`, bruk GraphiQL for testing
2. **Frontend utvikling:** Start med `npm run dev`, hot reload aktivt
3. **Full testing:** Bygg frontend og test i produksjonsmodus
4. **Deploy:** Følg deployment-prosessen i CLAUDE.md

## 📄 Lisens

[Spesifiser lisens]

---

*Dette prosjektet er utviklet med AI-assistert tilnærming for rask og kvalitetssikret utvikling.*