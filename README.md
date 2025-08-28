# Norsk Opptaksystem

Et moderne opptaksystem for norsk utdanning, bygget med Spring Boot og GraphQL.

## 🚀 Kom i gang

### Forutsetninger
- Java 21 eller nyere
- Git

### Installasjon

1. **Klon repositoriet:**
   ```bash
   git clone <repository-url>
   cd opptaksystem
   ```

2. **Start applikasjonen:**
   ```bash
   ./start.sh
   ```
   
   Eller manuelt:
   ```bash
   chmod +x mvnw
   MAVEN_OPTS="-Dmaven.multiModuleProjectDirectory=$PWD" ./mvnw spring-boot:run
   ```

3. **Åpne GraphiQL i nettleseren:**
   - GraphiQL: http://localhost:8080/graphiql
   - H2 Database Console: http://localhost:8080/h2-console

## 📡 GraphQL API

Systemet tilbyr en GraphQL API på `http://localhost:8080/graphql`.

### Eksempel queries:

#### Hei-melding
```graphql
query {
  hei(navn: "Ditt Navn")
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
opptaksystem/
├── src/
│   ├── main/
│   │   ├── java/no/utdanning/opptak/
│   │   │   ├── domain/          # Domeneklasser
│   │   │   ├── graphql/         # GraphQL resolvers
│   │   │   ├── service/         # Forretningslogikk
│   │   │   ├── repository/      # Database-tilgang
│   │   │   └── config/          # Konfigurasjon
│   │   └── resources/
│   │       ├── graphql/         # GraphQL schema-filer
│   │       └── application.yml  # App-konfigurasjon
│   └── test/                    # Enhetstester
├── krav/                        # Domenekrav og dokumentasjon
├── CLAUDE.md                    # AI-assistert utviklingsnotat
└── README.md                    # Denne filen
```

## 🛠️ Utvikling

### Kommandoer

- **Start applikasjon:** `./start.sh`
- **Kjør tester:** `./mvnw test`
- **Bygg prosjekt:** `./mvnw clean install`
- **Stopp applikasjon:** `Ctrl+C`

### Database

Systemet bruker H2 in-memory database for utvikling:
- **URL:** `jdbc:h2:mem:testdb`
- **Bruker:** `sa`
- **Passord:** (tomt)
- **Console:** http://localhost:8080/h2-console

### GraphQL Utvikling

1. **Schema-filer:** Alle GraphQL schemas ligger i `src/main/resources/graphql/`
2. **Resolvers:** Java-klasser i `src/main/java/.../graphql/`
3. **Testing:** Bruk GraphiQL på http://localhost:8080/graphiql

## 📚 Teknisk Stack

- **Backend:** Spring Boot 3.2.0
- **API:** GraphQL
- **Database:** H2 (utvikling), PostgreSQL (produksjon)
- **Database-tilgang:** JOOQ
- **Migrering:** Flyway
- **Java:** 21
- **Build:** Maven

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

## 🤝 Bidra

1. Les `CLAUDE.md` for detaljert utviklingsmetodikk
2. Følg Domain-Driven Design prinsipper
3. Skriv tester for ny funksjonalitet
4. Bruk norsk språk i kode og dokumentasjon
5. Commit ofte med beskrivende meldinger

## 📄 Lisens

[Spesifiser lisens]

---

*Dette prosjektet er utviklet med AI-assistert tilnærming for rask og kvalitetssikret utvikling.*