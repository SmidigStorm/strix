# Analyse av moderne kravskrivingsmetoder for AI og mennesker (2024)

## Sammendrag
Basert på forskning og moderne praksis i 2024, anbefaler vi en hybrid tilnærming som kombinerer strukturerte, maskinlesbare formater med menneskevennlig språk. Dette gir optimal støtte for både AI-assistert utvikling og menneskelig forståelse.

## Analyse av nåværende tilnærming i Strix

### 🟢 Det vi gjør godt
- **Example Mapping struktur**: Bruk av fargekoder (📝🔵✅❓) gir visuell struktur
- **Given-When-Then** format i eksempler følger BDD-prinsipper
- **Konkrete eksempler**: Realistiske scenarioer som "Opprett NTNU"
- **Systematisk organisering**: Templates og konsistent mappestruktur
- **Prioritering**: MVP/Fase 2/Fase 3 prioritering er tydelig

### 🟡 Forbedringsområder
- **Manglende maskinlesbarhet**: Krav er kun i prosa-format
- **Inkonsistent struktur**: Variasjoner mellom moduler
- **Begrenset automatisering**: Vanskelig å generere tester automatisk
- **Manglende sporbarhet**: Ingen unike ID-er på krav
- **AI-parsing utfordringer**: Fri tekst er vanskeligere for AI å fortolke

## Moderne AI-vennlige kravskrivingsmetoder (2024)

### 1. Strukturerte format-kombinasjoner

#### A) YAML + Gherkin hybrid
```yaml
requirement:
  id: "REQ-ORG-001"
  title: "Opprett organisasjon"
  priority: "MVP"
  epic: "Organisasjon administrasjon"
  
user_story: |
  Som administrator ønsker jeg å kunne opprette ny organisasjon 
  slik at nye utdanningsinstitusjoner kan delta i opptaket

acceptance_criteria:
  - rule: "Organisasjonsnavn validering"
    scenarios:
      - name: "Gyldig navn opprett"
        given: "Jeg er logget inn som administrator"
        when: "Jeg oppgir organisasjonsnavn 'NTNU' (minst 3 tegn)"
        then: "Organisasjonen opprettes"
        
  - rule: "Organisasjonsnummer validering" 
    scenarios:
      - name: "Unikt organisasjonsnummer"
        given: "Organisasjonsnummer '974767880' ikke eksisterer"
        when: "Jeg oppgir dette nummeret"
        then: "Organisasjonen opprettes"
        
questions:
  - "Skal vi integrere med Brønnøysundregistrene?"
  - "Hvilke organisasjonstyper skal være forhåndsdefinerte?"
```

#### B) JSON Schema for strukturerte krav
```json
{
  "requirement": {
    "id": "REQ-ORG-001",
    "metadata": {
      "created": "2024-08-30",
      "module": "organisasjon",
      "priority": "MVP",
      "status": "implemented"
    },
    "specification": {
      "user_story": "Som administrator ønsker jeg...",
      "business_rules": [
        {
          "rule": "Organisasjonsnavn må være minst 3 tegn",
          "examples": ["NTNU ✅", "UiO ✅", "NO ❌"]
        }
      ]
    }
  }
}
```

### 2. AI-assistert kravsgenerering

#### Template-basert generering
- AI kan bruke eksisterende krav som templates
- Automatisk foreslå lignende krav for nye moduler
- Konsistenssjekking på tvers av moduler

#### Naturlig språkprosessering
- Konverter prosa til strukturerte format automatisk
- Ekstrahér forretningsregler fra eksempler
- Generer testscenarioer basert på krav

### 3. Hybrid menneske-AI tilnærming

#### Iterativ kravsspesifikasjon
1. **Menneske**: Definerer user story og formål
2. **AI**: Foreslår forretningsregler og valideringskriterier  
3. **Menneske**: Validerer og justerer
4. **AI**: Genererer testscenarioer og edge cases
5. **Menneske**: Godkjenner og dokumenterer spørsmål

## Anbefalte forbedringer for Strix

### Fase 1: Strukturert metadata (Umiddelbar implementering)

Legg til YAML front matter i eksisterende markdown-filer:

```markdown
---
id: REQ-ORG-001
module: organisasjon
priority: MVP
status: implemented
epic: "Organisasjon administrasjon"
created: 2024-08-30
updated: 2024-08-30
---

# Example Mapping - Opprette ny organisasjon
[Resten av innholdet uendret]
```

### Fase 2: Strukturerte tester (Neste iterasjon)

Utvid example mapping med eksplisitt BDD-format:

```markdown
## ✅ Akseptansekriterier (Gherkin)

```gherkin
Feature: Opprett organisasjon
  Som administrator
  Ønsker jeg å kunne opprette ny organisasjon
  Slik at nye utdanningsinstitusjoner kan delta

Scenario: Opprett gyldig organisasjon
  Given jeg er logget inn som administrator
  And organisasjonsnummer "974767880" ikke eksisterer
  When jeg fyller ut organisasjonsformen:
    | navn                | NTNU                    |
    | organisasjonsnummer | 974767880               |
    | type               | UNIVERSITET              |
  Then organisasjonen opprettes med status "aktiv"
  And jeg får bekraftelse "Organisasjon opprettet: NTNU"
```
```

### Fase 3: AI-integrerte verktøy (Fremtidig)

- **Krav-til-test generator**: Automatisk generering av integrasjonstester
- **Konsistenssjekker**: AI validerer krav på tvers av moduler
- **Automatisk dokumentasjon**: Generer API-dokumentasjon fra krav

## Fordeler med hybrid tilnærming

### For AI-systemer
- **Strukturerte data**: YAML/JSON front matter kan parses automatisk
- **Forutsigbare mønstre**: Konsistente templates letter AI-forståelse
- **Maskinlesbare tester**: Gherkin kan kjøres direkte som tester
- **Metadata-sporing**: Enkel kobling mellom krav og implementering

### For mennesker  
- **Bevare lesbarhet**: Markdown med naturlig språk beholdes
- **Visuell struktur**: Emojier og fargekoding fungerer fortsatt
- **Gradvis innføring**: Eksisterende dokumentasjon kan oppgraderes inkrementelt
- **Kjent format**: Team kjenner allerede markdown og example mapping

## Implementeringsplan

### Uke 1-2: Pilot på organisasjonsmodul
- Legg til YAML front matter på eksisterende filer
- Standardiser structure på tvers av example mappings
- Test AI-parsing av strukturerte data

### Uke 3-4: Utvidelse til alle moduler
- Migrer alle krav til hybrid format
- Opprett AI-vennlige templates
- Implementer validering av kravkonsistens

### Måned 2-3: Automatiseringsverktøy
- Utvikle script for krav-til-test generering
- Implementer AI-assistert kravskriving
- Integrer med eksisterende utviklingsverktøy

## Konklusjon

Ved å kombinere det beste fra våre eksisterende Example Mapping-metoder med moderne strukturerte format, kan vi skape et kravssystem som:

1. **Støtter AI-assistert utvikling** uten å miste menneskelig lesbarhet
2. **Automatiserer repetitive oppgaver** som testgenerering og validering
3. **Bevarer dagens arbeidsflyt** mens vi legger til verdi
4. **Skalerer til større prosjekter** med bedre sporbarhet og konsistens

Dette gir oss det beste fra begge verdener: Strukturerte, maskinlesbare krav som fortsatt er intuitive og praktiske for menneskelige utviklere.