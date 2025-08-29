package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.OrganisasjonsType;

/** Input for å opprette ny organisasjon */
public class OpprettOrganisasjonInput {
  private String navn;
  private String organisasjonsnummer;
  private OrganisasjonsType organisasjonstype;
  private String epost;
  private String telefon;
  private String adresse;
  private String poststed;
  private String postnummer;

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public String getOrganisasjonsnummer() {
    return organisasjonsnummer;
  }

  public void setOrganisasjonsnummer(String organisasjonsnummer) {
    this.organisasjonsnummer = organisasjonsnummer;
  }

  public OrganisasjonsType getOrganisasjonstype() {
    return organisasjonstype;
  }

  public void setOrganisasjonstype(OrganisasjonsType organisasjonstype) {
    this.organisasjonstype = organisasjonstype;
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

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
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
}
