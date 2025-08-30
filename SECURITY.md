# Strix Sikkerhetssystem

## Oversikt

Strix bruker JWT-basert autentisering og rollebasert tilgangskontroll for å sikre GraphQL API-et. Dette dokumentet beskriver hvordan sikkerhetssystemet fungerer.

## Arkitektur

```
[Frontend] → [JWT Token] → [Spring Security Filter] → [GraphQL Resolver] → [Database]
     ↓              ↓                ↓                        ↓               ↓
   Login         Authorization    Token validering      Rolle-sjekk      Dataaksess
```

## Komponenter

### 1. JWT Authentication Filter (`JwtAuthenticationFilter`)

**Plassering**: `src/main/java/no/utdanning/opptak/config/JwtAuthenticationFilter.java`

**Ansvar**:
- Intercept alle HTTP requests
- Ekstraherer JWT token fra `Authorization: Bearer <token>` header
- Validerer token og setter opp Spring Security authentication context

**Arbeidsflyt**:
```java
1. Sjekk om request har Authorization header
2. Ekstraher token (fjern "Bearer " prefix)
3. Valider token med JwtService
4. Hvis gyldig: Set authentication i SecurityContext
5. Hvis ugyldig: La Spring Security håndtere unauthorized access
```

### 2. Security Configuration (`SecurityConfig`)

**Plassering**: `src/main/java/no/utdanning/opptak/config/SecurityConfig.java`

**Konfigurasjon**:
- **Stateless sessions**: Ingen server-side sessions
- **CORS**: Konfigurert for development og production domener
- **Filter chain**: JWT filter kjører før Spring Security's standard filters
- **Method security**: `@PreAuthorize` annotations aktivert

### 3. JWT Service (`JwtService`)

**Plassering**: `src/main/java/no/utdanning/opptak/service/JwtService.java`

**Spring Security Integration Methods**:
```java
// Ekstraherer bruker-ID fra token
public String extractUserId(String token)

// Ekstraherer roller fra token  
public List<String> extractRoles(String token)

// Sjekker om token er gyldig
public boolean isTokenValid(String token)
```

### 4. GraphQL Resolver Security

Alle GraphQL resolvers er beskyttet med `@PreAuthorize` annotations:

#### Query Resolvers
```java
@PreAuthorize("isAuthenticated()") // Krever gyldig JWT
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
```

#### Mutation Resolvers
```java
@PreAuthorize("hasRole('ADMINISTRATOR')") // Kun administratorer
```

## Roller og Tilganger

### Rollehierarki

| Rolle | Beskrivelse | Tilganger |
|-------|-------------|-----------|
| `ADMINISTRATOR` | Systemadministrator | Alt (CRUD på alle ressurser) |
| `OPPTAKSLEDER` | Leder for opptak | Les alle ressurser, administrer opptak |
| `SOKNADSBEHANDLER` | Behandler søknader | Les organisasjoner og opptak, behandle søknader |
| `SOKER` | Søker/student | Les begrenset info, sende søknader |

### Tilgangskontroll per GraphQL Operation

#### Organisasjoner
- **Vis organisasjoner**: Alle autentiserte roller
- **Opprett organisasjon**: Kun `ADMINISTRATOR`
- **Oppdater organisasjon**: Kun `ADMINISTRATOR` 
- **Deaktiver organisasjon**: Kun `ADMINISTRATOR`

#### Autentisering
- **Login**: Ingen autentisering krevd (offentlig)
- **Test brukere**: Ingen autentisering krevd (kun development)

## Token-struktur

### JWT Claims
```json
{
  "sub": "BRUKER-ID",
  "email": "bruker@example.no",
  "navn": "Navn Navnesen", 
  "roller": ["ADMINISTRATOR"],
  "iat": 1756534183,
  "exp": 1756620583,
  "organisasjonId": "ORG-123" // Hvis brukeren tilhører en organisasjon
}
```

### Token Lifetime
- **Utstedelse**: Ved successful login
- **Levetid**: 24 timer
- **Fornyelse**: Bruker må logge inn på nytt når token utløper

## Sikkerhetstesting

### Test Scenarios

#### 1. Unauthorized Access (401)
```bash
# Ingen token
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ organisasjoner { id navn } }"}'
# → AccessDeniedException
```

#### 2. Authorized Access (200)
```bash
# Med gyldig token
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <valid-jwt-token>" \
  -d '{"query": "{ organisasjoner { id navn } }"}'
# → Returnerer data
```

#### 3. Role-based Access (403)
```bash
# SOKNADSBEHANDLER prøver å opprette organisasjon (kun admin)
curl -X POST http://localhost:8080/graphql \
  -H "Authorization: Bearer <soknadsbehandler-token>" \
  -d '{"query": "mutation { opprettOrganisasjon(...) { id } }"}'
# → AccessDeniedException
```

## Development vs Production

### Development
- H2 in-memory database
- CORS tillater localhost porter
- Test-brukere tilgjengelig via GraphQL query
- Detaljerte debug logs

### Production  
- H2 file-based database
- CORS kun for produksjons-domener
- Ingen test-brukere eksponert
- Reduserte logs

## Feilhåndtering

### Security Exceptions
- **AuthenticationException**: 401 Unauthorized
- **AccessDeniedException**: 403 Forbidden  
- **JwtException**: 401 Unauthorized (invalid token)

### GraphQL Error Format
```json
{
  "errors": [{
    "message": "En intern feil oppstod",
    "locations": [{"line": 1, "column": 3}],
    "path": ["organisasjoner"],
    "extensions": {"classification": "INTERNAL_ERROR"}
  }],
  "data": null
}
```

## Vanlige Security Issues og Løsninger

### Problem: CORS errors
**Løsning**: Sjekk at frontend domenet er lagt til i `SecurityConfig.corsConfigurationSource()`

### Problem: Token utløpt
**Løsning**: Frontend må håndtere 401 response og redirecte til login

### Problem: Role ikke fungerer
**Løsning**: Sjekk at JWT inneholder riktig rolle-format og at `@PreAuthorize` bruker riktig rolle-navn

## Fremtidige Forbedringer

### Prioritet 2 (Planlagt)
- [ ] Role-based data filtering (brukere ser kun egen organisasjons data)
- [ ] Audit logging av sikkerhetshendelser
- [ ] Rate limiting for å forhindre brute force angrep

### Prioritet 3 (Ønskeliste)
- [ ] Feide-integrasjon for Single Sign-On
- [ ] Token refresh mechanism
- [ ] Multi-factor authentication
- [ ] Session management dashboard

## Referanser

- **Spring Security Dokumentasjon**: https://docs.spring.io/spring-security/reference/
- **JWT.io**: https://jwt.io/ (for debugging tokens)
- **GraphQL Security Best Practices**: https://leapgraph.com/graphql-security-best-practices

## Vedlikehold

Dette dokumentet skal oppdateres når:
- Nye roller legges til
- Tilgangskontroll endres
- Nye sikkerhetsfunksjoner implementeres
- Security vulnerabilities oppdages og fikses

**Sist oppdatert**: 2025-08-30  
**Av**: Claude Code (AI Assistant)