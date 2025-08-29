# Example Mapping - Opprette ny organisasjon

## 📝 User Story (Gul)
Som administrator ønsker jeg å kunne opprette ny organisasjon slik at nye utdanningsinstitusjoner kan delta i opptaket.

## 📏 Regler (Blå)

### Validering
- Organisasjonsnavn må være minst 3 tegn langt
- Organisasjonsnummer må være gyldig norsk organisasjonsnummer (9 siffer)
- Organisasjonsnummer må være unikt i systemet
- E-post må være gyldig e-postadresse
- Minst én kontaktperson må registreres ved opprettelse
- Organisasjonstype må velges fra forhåndsdefinert liste

### Forretningsregler
- Nye organisasjoner opprettes som aktive som standard
- System genererer unik ID automatisk
- Opprettelsesdato settes automatisk
- Hovedkontakt flagges automatisk hvis kun én kontaktperson registreres

### Sikkerhet
- Kun administratorer kan opprette organisasjoner
- All input valideres mot XSS og SQL injection

## ✅ Eksempler (Grønn)

### Eksempel 1: Opprett NTNU
**Gitt** at jeg er logget inn som administrator  
**Når** jeg fyller ut organisasjonsformen:
- Navn: "Norges teknisk-naturvitenskapelige universitet"
- Organisasjonsnummer: "974767880"
- Type: "Universitet"
- E-post: "opptak@ntnu.no"
- Telefon: "73595000"
- Adresse: "Høgskoleringen 1, 7491 Trondheim"

**Og** registrerer kontaktperson:
- Navn: "Kari Hansen"
- E-post: "kari.hansen@ntnu.no"
- Rolle: "Opptaksleder"

**Så** opprettes organisasjonen med status "aktiv"  
**Og** jeg får bekraftelse "Organisasjon opprettet: NTNU"  
**Og** organisasjonen vises i organisasjonsoversikten

### Eksempel 2: Duplikat organisasjonsnummer
**Gitt** at organisasjonsnummer "974767880" allerede eksisterer  
**Når** jeg prøver å opprette organisasjon med samme nummer  
**Så** får jeg feilmelding "Organisasjonsnummer er allerede registrert"  
**Og** organisasjonen opprettes ikke

### Eksempel 3: Ugyldig organisasjonsnummer
**Gitt** at jeg fyller ut organisasjonsnummer "123"  
**Når** jeg prøver å opprette organisasjonen  
**Så** får jeg feilmelding "Ugyldig organisasjonsnummer"  
**Og** organisasjonen opprettes ikke

## ❓ Spørsmål (Rød)

1. Skal vi integrere med Brønnøysundregistrene for å validere organisasjonsnummer?
2. Hvilke organisasjonstyper skal være forhåndsdefinerte?
3. Skal vi støtte internasjonale utdanningsinstitusjoner uten norsk org.nr?
4. Hvem kan se/redigere organisasjoner etter opprettelse?
5. Trenger vi godkjenningsprosess før organisasjoner blir aktive?