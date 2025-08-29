# Entity Map - Organisasjon

## Entitetsdiagram

```mermaid
erDiagram
    ORGANISASJON {
        string id PK
        string navn
        string organisasjonsnummer
        string organisasjonstype
        string epost
        string telefon
        string adresse
        string poststed
        string postnummer
        boolean aktiv
        datetime opprettet
        datetime oppdatert
    }
    
    KONTAKTPERSON {
        string id PK
        string organisasjonId FK
        string navn
        string epost
        string telefon
        string rolle
        boolean hovedkontakt
        datetime opprettet
    }
    
    UTDANNINGSTILBUD {
        string id PK
        string organisasjonId FK
        string navn
        string programkode
        string utdanningsnivaa
        int studiepoeng
        int varighet
        int studieplasser
        boolean aktiv
    }
    
    ORGANISASJON ||--o{ KONTAKTPERSON : "har"
    ORGANISASJON ||--o{ UTDANNINGSTILBUD : "tilbyr"
```

## Entitetsbeskrivelser

### Organisasjon
**Formål**: Representerer utdanningsinstitusjoner som deltar i opptakssystemet

**Hovedegenskaper**:
- **navn**: Offisielt navn på organisasjonen
- **organisasjonsnummer**: Unikt juridisk identifikasjonsnummer
- **organisasjonstype**: Kategori (Universitet, Høgskole, Fagskole, Privat)
- **kontaktinformasjon**: E-post, telefon og adresse
- **aktiv**: Om organisasjonen er aktiv i systemet

### Kontaktperson
**Formål**: Kontaktpersoner for organisasjonen

**Hovedegenskaper**:
- **rolle**: Funksjon (Opptaksleder, Administrator, Saksbehandler)
- **hovedkontakt**: En person per organisasjon som hovedkontakt

### Utdanningstilbud
**Formål**: Utdanningstilbud som organisasjonen tilbyr

**Relasjon**: En organisasjon kan ha mange utdanningstilbud