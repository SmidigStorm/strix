# Service-lag Refaktoring Plan

## Problemanalyse

### Nåværende arkitektur problemer:
- `OrganisasjonQueryResolver` kaller `OrganisasjonRepository` direkte
- `UtdanningQueryResolver` har blandet ansvar: kaller både repository OG sikkerhetstjeneste  
- Forretningslogikk ligger i resolvers (burde være i services)
- Filtreringslogikk spredt utover i resolvers

### Ønsket arkitektur:
```
GraphQL Resolver → Service → Repository → Database
```

**Nå:** `OrganisasjonQueryResolver` → `OrganisasjonRepository`
**Skal bli:** `OrganisasjonQueryResolver` → `OrganisasjonService` → `OrganisasjonRepository`

## Implementeringsplan

### 1. OrganisasjonService (Prioritet 1 - Enklest)
**Oppgaver:**
- [ ] Lag `OrganisasjonService` klasse i `/service/` mappen
- [ ] Flytt all filtreringslogikk fra `OrganisasjonQueryResolver`
- [ ] Implementer `findAll()`, `findById()`, `findWithFilter()` metoder
- [ ] Refaktorer `OrganisasjonQueryResolver` til å kun kalle service
- [ ] Refaktorer `OrganisasjonMutationResolver` til å kun kalle service

**Forretningslogikk som skal flyttes:**
```java
// Fra OrganisasjonQueryResolver.java linje 25-54
- Filtrering på aktiv status
- Filtrering på organisasjonstype  
- Søk i navn (case-insensitive)
```

### 2. UtdanningService (Prioritet 2 - Mer kompleks)
**Oppgaver:**
- [ ] Lag `UtdanningService` klasse i `/service/` mappen
- [ ] Integrer med eksisterende `UtdanningSecurityService`
- [ ] Flytt all forretningslogikk fra `UtdanningQueryResolver`
- [ ] Implementer paginering og filtrering i service
- [ ] Refaktorer `UtdanningQueryResolver` til å kun kalle service
- [ ] Refaktorer `UtdanningMutationResolver` til å kun kalle service

**Kompleks forretningslogikk som skal flyttes:**
```java
// Fra UtdanningQueryResolver.java
- Sikkerhetstilgang per organisasjon (linje 41-47, 64-68, 105-111)
- Automatisk filtrering basert på brukerrolle
- Paginering og counting (linje 71-96)
- Schema mapping for organisasjon-feltet (linje 153-156)
```

### 3. Testing og validering
**Oppgaver:**
- [ ] Kjør alle eksisterende tester
- [ ] Verifiser at GraphQL queries fortsatt fungerer
- [ ] Test sikkerhet og tilgangskontroll
- [ ] Verifiser at filtreringen fungerer som før

## Gevinster ved refaktoring

✅ **Testbarhet** - Services kan enhetstestes isolert  
✅ **Gjenbruk** - Services kan brukes fra andre lag senere  
✅ **Separasjon** - Resolvers blir rene "controllere"  
✅ **Lesbarhet** - Tydelig ansvarsdeling  
✅ **Vedlikeholdbarhet** - Forretningslogikk samlet på ett sted

## Implementeringsstrategi

### Rekkefølge (viktig!):
1. **OrganisasjonService** - Enklest, ingen sikkerhet å ta hensyn til
2. **UtdanningService** - Mer kompleks pga sikkerhet og paginering  
3. **Refaktorer resolvers** - En og en, test underveis
4. **Fullstendig testing** - Sikre at alt fungerer som før

### Tekniske detaljer:
- Bruk `@Service` annotation på nye service-klasser
- Inject repositories via constructor
- Behold samme public interface (samme metode-signaturer)
- Flytt `@PreAuthorize` til service-lag hvis relevant
- Resolvers skal kun inneholde parameter-mapping og service-kall

## Status: PLANLAGT ✏️
**Dato opprettet:** 2025-08-30  
**Estimert tidsbruk:** 2-3 timer totalt  
**Blokkerer:** Ingen andre oppgaver  
**Avhenger av:** Ingenting - kan starte når som helst

---
*Denne planen skal implementeres av neste Claude-sesjon som jobber med backend-arkitektur.*