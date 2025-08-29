# Example Mapping - Se oversikt over organisasjoner

## 📝 User Story (Gul)
Som administrator ønsker jeg å kunne se oversikt over alle organisasjoner slik at jeg har full kontroll over systemet.

## 📏 Regler (Blå)

### Visning
- Organisasjoner vises i tabell med viktigste informasjon
- Standard sortering: alfabetisk på navn
- Paginering: 20 organisasjoner per side
- Status (aktiv/inaktiv) vises tydelig
- Rask tilgang til redigering og detaljer

### Søk og filtrering
- Søk på navn og organisasjonsnummer
- Filtrer på organisasjonstype
- Filtrer på status (aktiv/inaktiv/alle)
- Søkeresultater oppdateres live ved skriving

### Performance
- Lazy loading av data for store lister
- Caching av søkeresultater
- Maksimalt 2 sekunder lastetid

## ✅ Eksempler (Grønn)

### Eksempel 1: Standard visning
**Gitt** at systemet har 50 organisasjoner  
**Når** jeg går til organisasjonsoversikten  
**Så** ser jeg:
- Tabell med kolonner: Navn, Type, Status, Ant. tilbud, Handlinger
- 20 første organisasjoner (alfabetisk sortert)
- Paginering nederst (Side 1 av 3)
- Søkefelt øverst
- Filter-knapper for type og status

### Eksempel 2: Søk på navn
**Gitt** at jeg er på organisasjonsoversikten  
**Når** jeg skriver "NTNU" i søkefeltet  
**Så** vises kun organisasjoner som inneholder "NTNU" i navnet  
**Og** søket er ikke case-sensitive
**Og** resultatet oppdateres mens jeg skriver

### Eksempel 3: Filtrer på type
**Gitt** at jeg velger filter "Universitet"  
**Så** vises kun organisasjoner av type "Universitet"  
**Og** antall resultater vises: "Viser 12 av 50 organisasjoner"

### Eksempel 4: Kombinert søk og filter
**Gitt** at jeg søker på "høgskole" OG filtrerer på "Aktiv"  
**Så** vises kun aktive organisasjoner som inneholder "høgskole"  
**Og** både søk og filter er aktive samtidig

### Eksempel 5: Tom liste
**Gitt** at ingen organisasjoner matcher søket  
**Så** vises melding "Ingen organisasjoner funnet"  
**Og** knapp for "Nullstill søk"

### Eksempel 6: Organisasjon med mange tilbud
**Gitt** at NTNU har 45 utdanningstilbud  
**Så** vises "45" i kolonnen "Ant. tilbud"  
**Og** tallet er klikkbar for å se tilbudene

## ❓ Spørsmål (Rød)

1. Hvilke kolonner er viktigst å vise i tabellen?
2. Skal vi ha bulk-handlinger (aktivere/deaktivere flere)?
3. Trenger vi eksport til Excel/CSV?
4. Skal inaktive organisasjoner vises med forskjellig styling?
5. Hvor mange organisasjoner per side er optimalt?