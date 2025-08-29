# Example Mapping - Opprette nytt opptak

## 📝 User Story
Som opptaksleder ønsker jeg å opprette et nytt opptak med navn slik at søkere kan begynne å søke

## 📏 Regler

### Obligatoriske felter må fylles ut
✅ **Eksempler:**
- Opptak med navn "Samordnet opptak H25" og type "UHG" → Godkjent
- Opptak uten navn → Feilmelding "Navn er påkrevd"
- Opptak uten type → Feilmelding "Opptakstype er påkrevd"

### Navn må være unikt
✅ **Eksempler:**
- "Samordnet opptak H25", ingen eksisterende → Godkjent
- "Samordnet opptak H25", allerede eksisterer → Feilmelding "Opptak med dette navnet eksisterer allerede"
- "Lokalt opptak vårkull 2025" → Godkjent

### Opptakstype må velges
✅ **Eksempler:**
- Type "UHG" (Universiteter og høgskoler) → Godkjent
- Type "FSU" (Fagskoleutdanning) → Godkjent
- Type "Lokalt" → Godkjent

### Opptak opprettes med standardverdier
✅ **Eksempler:**
- Nytt opptak → Status settes til "fremtidig"
- Nytt opptak → Maksimalt antall utdanninger per søknad settes til 10 (standardverdi)

## ❓ Spørsmål
- Hvem har rettighet til å opprette opptak - kun organisasjonens administratorer?
- Skal det være mulig å kopiere innstillinger fra et tidligere opptak?