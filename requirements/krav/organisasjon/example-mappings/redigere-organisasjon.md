# Example Mapping - Redigere organisasjon

## 📝 User Story (Gul)
Som administrator ønsker jeg å kunne redigere organisasjonsdetaljer slik at informasjonen holdes oppdatert.

## 📏 Regler (Blå)

### Validering
- Samme valideringsregler som ved opprettelse
- Organisasjonsnummer kan ikke endres etter opprettelse
- System-ID kan aldri endres
- Må ha minst én aktiv kontaktperson

### Forretningsregler
- Oppdateringsdato settes automatisk ved endring
- Historikk over endringer logges for revisjon
- Endringer på kritiske felter krever bekreftelse
- Kan ikke slette organisasjon som har aktive utdanningstilbud

### Sikkerhet
- Kun administratorer kan redigere organisasjoner
- Organisasjoner kan kun redigere egne data (fremtidig funksjon)

## ✅ Eksempler (Grønn)

### Eksempel 1: Endre kontaktinformasjon
**Gitt** at NTNU eksisterer i systemet  
**Når** jeg endrer:
- E-post fra "opptak@ntnu.no" til "nyopptak@ntnu.no"
- Telefon fra "73595000" til "73595100"

**Så** lagres endringene  
**Og** oppdateringsdato settes til nå  
**Og** jeg får bekraftelse "Organisasjon oppdatert"

### Eksempel 2: Forsøk på å endre organisasjonsnummer
**Gitt** at jeg prøver å endre organisasjonsnummer  
**Så** er feltet deaktivert/skjult  
**Og** jeg ser melding "Organisasjonsnummer kan ikke endres"

### Eksempel 3: Legge til kontaktperson
**Gitt** at jeg er på redigeringssiden for NTNU  
**Når** jeg legger til ny kontaktperson:
- Navn: "Per Olsen"
- E-post: "per.olsen@ntnu.no" 
- Rolle: "Saksbehandler"

**Så** legges kontaktpersonen til  
**Og** NTNU har nå 2 kontaktpersoner

### Eksempel 4: Slette organisasjon med aktive tilbud
**Gitt** at NTNU har 5 aktive utdanningstilbud  
**Når** jeg prøver å slette organisasjonen  
**Så** får jeg feilmelding "Kan ikke slette organisasjon med aktive utdanningstilbud"  
**Og** organisasjonen slettes ikke

## ❓ Spørsmål (Rød)

1. Hvilke felter skal kreve ekstra bekreftelse ved endring?
2. Hvor lenge skal endringshistorikk oppbevares?
3. Skal organisasjoner kunne redigere sine egne data i fremtiden?
4. Trenger vi notifikasjoner ved kritiske endringer?