# Strix - Utviklingsbacklog

## 🚀 Høy prioritet

### Arkitektur forbedringer
- [ ] Legge til Service-lag for Organisasjon og Utdanning (nå: Resolver → Repository, burde være: Resolver → Service → Repository)
- [ ] Konsistent repository pattern (velge mellom JPA eller In-Memory for alle entiteter)
- [ ] Refaktorere GraphQL resolvers til å bruke services

### Frontend utvikling  
- [ ] **Fikse modal overflow-problemer** - Tekst blir kuttet av i modaler ved lange navn/beskrivelser
- [ ] Implementere organisasjon CRUD i React frontend
- [ ] Implementere utdanning CRUD i React frontend
- [ ] Forbedre dashboard med ekte data fra GraphQL

## 🔧 Medium prioritet

### Sikkerhet
- [ ] Implementere Feide-integrasjon
- [ ] Forbedre JWT token håndtering
- [ ] Rolle-basert tilgangskontroll i frontend

### Database
- [ ] Migrere fra H2 til PostgreSQL (produksjon)
- [ ] Implementere database migrations (Flyway)
- [ ] Optimalisere database queries

### Testing
- [ ] Enhetstester for alle services
- [ ] Integrasjonstester for GraphQL endpoints
- [ ] Frontend testing med Vitest/Jest

## 📚 Lav prioritet

### Dokumentasjon
- [ ] API dokumentasjon (GraphQL schema descriptions)
- [ ] Brukerhåndbok
- [ ] Deployment guide

### Performance
- [ ] GraphQL query optimalisering
- [ ] Caching strategi
- [ ] Database indexering

### Monitoring
- [ ] Logging strategi
- [ ] Metrics og monitoring
- [ ] Error tracking

## 🎯 Backlog notes

**Beslutningskriterier:**
- Høy: Kritisk for MVP eller blokkerer andre oppgaver
- Medium: Forbedrer kvalitet eller brukeropplevelse
- Lav: Nice-to-have eller fremtidige forbedringer

**Oppdatert:** 2025-08-30