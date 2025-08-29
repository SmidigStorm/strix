package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.OrganisasjonsType;

/** Input for å oppdatere eksisterende organisasjon */
public class OppdaterOrganisasjonInput {
  private String id;
  private String navn;
  private OrganisasjonsType organisasjonstype;
  private String epost;
  private String telefon;
  private String adresse;
  private String poststed;
  private String postnummer;

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
