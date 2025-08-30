package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.Studieform;

/** Filter for utdanning queries */
public class UtdanningFilter {
  private String navn;
  private String studienivaa;
  private String studiested;
  private String organisasjonId;
  private Studieform studieform;
  private Boolean aktiv;

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public String getStudienivaa() {
    return studienivaa;
  }

  public void setStudienivaa(String studienivaa) {
    this.studienivaa = studienivaa;
  }

  public String getStudiested() {
    return studiested;
  }

  public void setStudiested(String studiested) {
    this.studiested = studiested;
  }

  public String getOrganisasjonId() {
    return organisasjonId;
  }

  public void setOrganisasjonId(String organisasjonId) {
    this.organisasjonId = organisasjonId;
  }

  public Studieform getStudieform() {
    return studieform;
  }

  public void setStudieform(Studieform studieform) {
    this.studieform = studieform;
  }

  public Boolean getAktiv() {
    return aktiv;
  }

  public void setAktiv(Boolean aktiv) {
    this.aktiv = aktiv;
  }
}
