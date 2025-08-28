# Entity Map - Opptak

## Entitetsdiagram

```mermaid
erDiagram
    ORGANISASJON {
        string id
        string navn
        string kortNavn
        string type
        string organisasjonsnummer
        string adresse
        string nettside
        datetime opprettet
        boolean aktiv
    }
    
    UTDANNING {
        string id
        string navn
        string studienivaa
        int studiepoeng
        int varighet
        string studiested
        string undervisningssprak
        string beskrivelse
        datetime opprettet
        boolean aktiv
    }
    
    OPPTAK {
        string id
        string navn
        string type
        int aar
        date soknadsfrist
        date svarfrist
        int maxUtdanningerPerSoknad
        string status
        string opptaksomgang
        string beskrivelse
        datetime opprettet
        boolean aktiv
    }
    
    UTDANNING_I_OPPTAK {
        string id
        int antallPlasser
        boolean aktivt
        datetime opprettet
    }
    
    ORGANISASJON ||--o{ UTDANNING : "eier"
    UTDANNING ||--|| UTDANNING_I_OPPTAK : "har_opptak"
    OPPTAK ||--o{ UTDANNING_I_OPPTAK : "inneholder"
```

## Relasjonsbeskrivelser

### Organisasjon EIER Utdanning
**Kardinalitet**: En-til-mange (1:N)
- En organisasjon kan eie mange utdanninger
- En utdanning eies av nøyaktig én organisasjon
- Eksempel: NTNU eier "Bachelor i informatikk H25", "Master i AI H25", etc.

### Utdanning TILBYS_I Opptak
**Kardinalitet**: Mange-til-en (N:1)
- Hver utdanning tilbys i nøyaktig ett opptak
- Et opptak kan tilby mange utdanninger
- Eksempel: "Bachelor i informatikk H25" og "Master i AI H25" tilbys begge i "Samordnet opptak H25"

**Metadata på relasjonen:**
- Antall studieplasser som er tilgjengelig for utdanningen i dette opptaket
- Om utdanningen er aktiv i opptaket (kan midlertidig deaktiveres)

