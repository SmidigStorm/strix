# Organisasjon - Domeneforståelse

## Domene-entiteter

**Organisasjon** 
En utdanningsinstitusjon som kan tilby studieplasser gjennom systemet
- Identitet: navn, organisasjonsnummer, type
- Kontakt: hovedkontakt, adresse, kommunikasjonskanaler
- Status: aktiv/inaktiv i systemet

**Kontaktperson**
Nøkkelperson hos organisasjonen som håndterer opptak
- Rolle i organisasjonen (opptaksleder, koordinator, etc.)
- Ansvar og myndighet i systemet

## Domene-eksempler
**Organisasjonstyper vi jobber med:**
- Universiteter (NTNU, UiO, UiB)
- Høgskoler (OsloMet, Kristiania, Noroff)
- Fagskoler (Bjørknes, Westerdals)

**Typiske kontaktroller:**
- Opptaksleder (strategisk ansvar)
- Opptakskoordinator (daglig drift)
- IT-kontakt (teknisk støtte)

## User Stories (Backlog)

### Administrere organisasjoner
- [x] Som administrator ønsker jeg å opprette ny organisasjon slik at nye utdanningsinstitusjoner kan delta i opptaket
- [x] Som administrator ønsker jeg å se oversikt over alle organisasjoner slik at jeg har kontroll over systemet
- [ ] Som administrator ønsker jeg å redigere organisasjonsinformasjon slik at data holdes oppdatert
- [x] Som administrator ønsker jeg å deaktivere organisasjoner slik at de ikke lenger kan delta

### Organisasjonsinformasjon
- [x] Som administrator ønsker jeg å registrere kontaktperson slik at vi kan kommunisere med organisasjonen
- [ ] Som administrator ønsker jeg å bytte kontaktperson slik at riktig person har tilgang
- [ ] Som administrator ønsker jeg å se kontakthistorikk slik at jeg forstår hvem som har hatt ansvar

### Søk og navigering
- [ ] Som administrator ønsker jeg å søke etter organisasjoner slik at jeg raskt finner den jeg trenger
- [ ] Som administrator ønsker jeg å filtrere på organisasjonstype slik at jeg ser relevante institusjoner
- [ ] Som administrator ønsker jeg å se organisasjoner per region slik at jeg kan koordinere lokalt

### Rapportering
- [ ] Som administrator ønsker jeg å se statistikk over aktive organisasjoner slik at jeg kan rapportere til departementet
- [ ] Som administrator ønsker jeg å eksportere organisasjonsdata slik at jeg kan dele med andre systemer

## Åpne domene-spørsmål
- Kan en organisasjon ha flere kontaktpersoner?
- Hvordan håndterer vi fusjoner mellom institusjoner?
- Skal vi støtte internasjonale institusjoner?
- Hvilken informasjon trenger vi for rapportering til departementet?