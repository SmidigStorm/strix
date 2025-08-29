package no.utdanning.opptak.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Organisasjon {
  private String id;
  private String navn;
  private String kortNavn;
  private OrganisasjonsType type;
  private String organisasjonsnummer;
  private String epost;
  private String telefon;
  private String adresse;
  private String poststed;
  private String postnummer;
  private String nettside;
  private LocalDateTime opprettet;
  private LocalDateTime oppdatert;
  private Boolean aktiv;
  private List<Utdanning> utdanninger;

  public Organisasjon() {}

  public Organisasjon(
      String id,
      String navn,
      String kortNavn,
      OrganisasjonsType type,
      String organisasjonsnummer,
      String epost,
      String telefon,
      String adresse,
      String poststed,
      String postnummer,
      String nettside,
      LocalDateTime opprettet,
      LocalDateTime oppdatert,
      Boolean aktiv) {
    this.id = id;
    this.navn = navn;
    this.kortNavn = kortNavn;
    this.type = type;
    this.organisasjonsnummer = organisasjonsnummer;
    this.epost = epost;
    this.telefon = telefon;
    this.adresse = adresse;
    this.poststed = poststed;
    this.postnummer = postnummer;
    this.nettside = nettside;
    this.opprettet = opprettet;
    this.oppdatert = oppdatert;
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

  public OrganisasjonsType getType() {
    return type;
  }

  public void setType(OrganisasjonsType type) {
    this.type = type;
  }

  public String getEpost() {
    return epost;
  }

  public void setEpost(String epost) {
    this.epost = epost;
  }

  public String getTelefon() {
    return telefon;
  }

  public void setTelefon(String telefon) {
    this.telefon = telefon;
  }

  public String getPoststed() {
    return poststed;
  }

  public void setPoststed(String poststed) {
    this.poststed = poststed;
  }

  public String getPostnummer() {
    return postnummer;
  }

  public void setPostnummer(String postnummer) {
    this.postnummer = postnummer;
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

  public LocalDateTime getOppdatert() {
    return oppdatert;
  }

  public void setOppdatert(LocalDateTime oppdatert) {
    this.oppdatert = oppdatert;
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
