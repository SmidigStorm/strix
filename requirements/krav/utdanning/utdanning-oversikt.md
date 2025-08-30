# Utdanning - Domeneforståelse

> **Filnavn:** `utdanning-oversikt.md`  
> **Formål:** Overordnet domeneforståelse og user story backlog

## Domene-entiteter

**Utdanning** 
En konkret utdanning som tilbys av en organisasjon - kan være studieprogram, emne, eller kurs
- Identitet: navn, utdanningskategori, varighet
- Tidspunkt: starttidspunkt (f.eks. høst 2025, vår 2026)
- Studieform: heltid eller deltid
- Eierskap: tilhører alltid én organisasjon
- Grunnlag: kan være basert på samme utdanningsspesifikasjon som andre organisasjoners utdanninger
- Kapasitet: antall studieplasser tilgjengelige

**Utdanningsspesifikasjon**
Standardisert mal/beskrivelse som flere organisasjoners utdanninger kan baseres på
- Eksempel: "Bachelor i sykepleie" som NTNU, UiO og OsloMet alle tilbyr
- Felles rammeverk, men hver organisasjon har sin unike implementering

**Utdanningskategori**
Kategorisering av utdanningsnivå og -lengde
- Nivå: bachelor, master, PhD, fagskole, videregående
- Lengde: antall år/semestre
- Formalisering: grad, diplom, sertifikat, etc.

## Domene-eksempler
**Utdanningskategorier:**
- Bachelor-programmer (Informatikk, Sykepleie, Økonomi)
- Master-programmer (MBA, Engineering, Medisin)
- Fagskole (Tanntekniker, Ambulansefag, IT-drift)
- Kurs/Emner (Enkeltfag, videreutdanning, sertifikater)

**Eksempler på samme spesifikasjon, forskjellige organisasjoner:**
- "Bachelor i sykepleie" tilbys av NTNU, UiO, OsloMet, USN
- "Master i informatikk" tilbys av NTNU, UiO, UiB
- "Fagskole i IT-drift" tilbys av forskjellige fagskoler

**Typiske utdanningsdata:**
- Navn: "Bachelor i informatikk"
- Organisasjon: "NTNU" 
- Type: "Bachelor"
- Varighet: "3 år"
- Studieplasser: "120 plasser"

## User Stories (Backlog)

### Administrere utdanninger ✅ FULLSTENDIG IMPLEMENTERT
- [x] Som opptaksleder ønsker jeg å opprette ny utdanning for min organisasjon slik at vi kan tilby studieplasser
- [x] Som opptaksleder ønsker jeg å se oversikt over mine organisasjons utdanninger slik at jeg har kontroll
- [x] Som opptaksleder ønsker jeg å redigere utdanningsinformasjon slik at data er oppdatert
- [x] Som opptaksleder ønsker jeg å deaktivere utdanninger som ikke lenger tilbys slik at systemet er ryddig

### Utdanningsinformasjon 🔄 DELVIS IMPLEMENTERT (2 av 4)
- [x] Som opptaksleder ønsker jeg å registrere utdanningsnavn og -type slik at utdanningen kan identifiseres korrekt
- [ ] Som opptaksleder ønsker jeg å angi antall studieplasser slik at kapasiteten er kjent
- [x] Som opptaksleder ønsker jeg å angi utdanningens varighet slik at søkere vet hvor lang utdanningen er
- [ ] Som opptaksleder ønsker jeg å koble utdanning til utdanningsspesifikasjon slik at den følger standarder

### Søk og oversikt 🔄 DELVIS IMPLEMENTERT (3 av 4)
- [x] Som opptaksleder ønsker jeg å søke etter utdanninger på tvers av organisasjoner slik at jeg kan se hva andre tilbyr *(kun administratorer kan se på tvers)*
- [x] Som administrator ønsker jeg å se alle utdanninger i systemet slik at jeg har oversikt
- [x] Som administrator ønsker jeg å filtrere utdanninger etter type slik at jeg kan analysere tilbudet
- [ ] Som opptaksleder ønsker jeg å se statistikk over mine utdanninger slik at jeg kan planlegge

### Utdanningsspesifikasjoner ❌ IKKE IMPLEMENTERT (0 av 3)
- [ ] Som administrator ønsker jeg å administrere utdanningsspesifikasjoner slik at organisasjoner kan basere sine utdanninger på standarder
- [ ] Som opptaksleder ønsker jeg å se tilgjengelige spesifikasjoner slik at jeg kan basere mine utdanninger på etablerte standarder
- [ ] Som administrator ønsker jeg å se hvilke organisasjoner som tilbyr samme spesifikasjon slik at jeg kan koordinere

## Åpne domene-spørsmål
- Hvordan håndterer vi utdanninger som endrer navn eller type?
- Skal vi ha validering av utdanningsnavn mot nasjonale standarder?
- Hvordan kobler vi utdanninger til faktiske studieplaner og pensum?
- Kan en utdanning ha flere kontaktpersoner (studiekoordinatorer)?
- Kan samme utdanningsspesifikasjon ha både heltid og deltid varianter?

---

## Implementasjonsstatus (Sist oppdatert: 2025-08-30)

**📊 Totalt: 9 av 15 user stories implementert (60%)**

**Fullstendig implementerte områder:**
- ✅ **Administrere utdanninger**: Komplett CRUD med rollbasert sikkerhet
- ✅ **GraphQL API**: Omfattende schema med queries, mutations og filtrering
- ✅ **Frontend**: Modern React-komponent med full funksjonalitet
- ✅ **Sikkerhet**: Organisasjon-scoped tilgangskontroll

**Tekniske forbedringer utover krav:**
- Soft delete (aktiv/inaktiv status)
- Paginering for store datasett  
- Utvidede felter: studiepoeng, studiested, undervisningsspråk, starttidspunkt
- Studieform enum (HELTID/DELTID)
- JWT-autentisering og autorisasjon

**Neste prioriteringer:**
1. **Antall studieplasser** - Legg til kapasitetsfelt i utdanning
2. **Statistikk** - Dashboard for opptaksledere
3. **Utdanningsspesifikasjoner** - Standardmaler og felles rammeverk