# Organisasjon - Detaljerte scenarioer

Dette dokumentet inneholder example mapping for utvalgte user stories fra backloggen.

---

# Scenario: Etablere nytt samarbeid
**User story:** Som administrator ønsker jeg å opprette ny organisasjon slik at nye utdanningsinstitusjoner kan delta i opptaket

## Overordnet flyt
* En ny institusjon ønsker å delta i opptak
* Administrator registrerer institusjonen i systemet
* Institusjonens kontaktperson får tilgang
* Samarbeidet er etablert og klart for opptak

## Example Mapping

### 📏 Regel: Organisasjon må ha unikt organisasjonsnummer
✅ **Eksempler:**
- NTNU med orgnr 974767880 → OK, opprettes
- Ny institusjon med samme 974767880 → Feil, "Organisasjonsnummer finnes allerede"

### 📏 Regel: Organisasjon må ha minst én kontaktperson
✅ **Eksempler:**
- Registrerer "Kari Hansen" som opptaksleder → OK, organisasjon opprettes
- Ingen kontaktperson registrert → Feil, "Kontaktperson påkrevd"

### 📏 Regel: Organisasjonstype må velges
✅ **Eksempler:**
- NTNU registreres som "Universitet" → OK
- Kristiania registreres som "Høgskole" → OK
- Ingen type valgt → Feil, "Organisasjonstype må velges"

## ❓ Spørsmål for dette scenarioet
- Skal administrator kunne registrere organisasjon uten at institusjonen har bedt om det?
- Hvem bestemmer hvilken organisasjonstype en institusjon skal ha?

---

# Scenario: Administrere eksisterende samarbeid  
**User story:** Som administrator ønsker jeg å redigere organisasjonsinformasjon slik at data holdes oppdatert

## Overordnet flyt
* Organisasjonen endrer kontaktperson/info
* Administrator oppdaterer systemet
* Nye tilganger etableres hvis nødvendig
* Endringene er synlige for relevante brukere

## Example Mapping

### 📏 Regel: Kun administrator kan endre organisasjonsdata
✅ **Eksempler:**
- Administrator endrer NTNU sitt telefonnummer → OK, endres
- Opptaksleder fra NTNU prøver å endre eget telefonnummer → ?

### 📏 Regel: Bytte av kontaktperson krever ny tilgangsstyring
✅ **Eksempler:**
- Kari Hansen erstattes av Ole Olsen som opptaksleder → Ole får tilgang, Kari mister tilgang
- Kari Hansen får ny e-postadresse → Oppdateres, samme tilganger

## ❓ Spørsmål for dette scenarioet
- Skal organisasjonen selv kunne endre noe av sin informasjon?
- Hva skjer med pågående opptak når kontaktperson endres?

---

# Scenario: Avslutte samarbeid
**User story:** Som administrator ønsker jeg å deaktivere organisasjoner slik at de ikke lenger kan delta

## Overordnet flyt
* Organisasjonen beslutter å ikke delta lenger (eller administrator beslutter det)
* Administrator setter organisasjonen som inaktiv
* Kontaktpersoner mister tilgang til nye opptak
* Historiske data bevares for rapportering

## Example Mapping

### 📏 Regel: Inaktive organisasjoner kan ikke opprette nye opptak
✅ **Eksempler:**
- NTNU settes inaktiv → Kan ikke lage opptak for neste år
- NTNU har pågående opptak → Pågående opptak påvirkes ikke

### 📏 Regel: Historiske data bevares alltid
✅ **Eksempler:**
- NTNU deaktiveres → Tidligere opptak og søknader er fortsatt synlige
- Sletter NTNU helt → Nei, ikke mulig

## ❓ Spørsmål for dette scenarioet
- Kan en inaktiv organisasjon reaktiveres?
- Hvor lenge skal vi beholde data fra inaktive organisasjoner?