package no.utdanning.opptak.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Organisasjon {
  private String id;
  private String navn;
  private String kortNavn;
  private String type;
  private String organisasjonsnummer;
  private String adresse;
  private String nettside;
  private LocalDateTime opprettet;
  private Boolean aktiv;
  private List<Utdanning> utdanninger;

  public Organisasjon() {}

  public Organisasjon(
      String id,
      String navn,
      String kortNavn,
      String type,
      String organisasjonsnummer,
      String adresse,
      String nettside,
      LocalDateTime opprettet,
      Boolean aktiv) {
    this.id = id;
    this.navn = navn;
    this.kortNavn = kortNavn;
    this.type = type;
    this.organisasjonsnummer = organisasjonsnummer;
    this.adresse = adresse;
    this.nettside = nettside;
    this.opprettet = opprettet;
    this.aktiv = aktiv;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public String getKortNavn() {
    return kortNavn;
  }

  public void setKortNavn(String kortNavn) {
    this.kortNavn = kortNavn;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getOrganisasjonsnummer() {
    return organisasjonsnummer;
  }

  public void setOrganisasjonsnummer(String organisasjonsnummer) {
    this.organisasjonsnummer = organisasjonsnummer;
  }

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  public String getNettside() {
    return nettside;
  }

  public void setNettside(String nettside) {
    this.nettside = nettside;
  }

  public LocalDateTime getOpprettet() {
    return opprettet;
  }

  public void setOpprettet(LocalDateTime opprettet) {
    this.opprettet = opprettet;
  }

  public Boolean getAktiv() {
    return aktiv;
  }

  public void setAktiv(Boolean aktiv) {
    this.aktiv = aktiv;
  }

  public List<Utdanning> getUtdanninger() {
    return utdanninger;
  }

  public void setUtdanninger(List<Utdanning> utdanninger) {
    this.utdanninger = utdanninger;
  }
}
