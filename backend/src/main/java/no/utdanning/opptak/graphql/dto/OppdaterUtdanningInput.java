package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.Studieform;

/** Input for å oppdatere eksisterende utdanning */
public class OppdaterUtdanningInput {
  private String id;
  private String navn;
  private String studienivaa;
  private Integer studiepoeng;
  private Integer varighet;
  private String studiested;
  private String undervisningssprak;
  private String beskrivelse;
  private String starttidspunkt;
  private Studieform studieform;
  private Boolean aktiv;

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

  public String getStudienivaa() {
    return studienivaa;
  }

  public void setStudienivaa(String studienivaa) {
    this.studienivaa = studienivaa;
  }

  public Integer getStudiepoeng() {
    return studiepoeng;
  }

  public void setStudiepoeng(Integer studiepoeng) {
    this.studiepoeng = studiepoeng;
  }

  public Integer getVarighet() {
    return varighet;
  }

  public void setVarighet(Integer varighet) {
    this.varighet = varighet;
  }

  public String getStudiested() {
    return studiested;
  }

  public void setStudiested(String studiested) {
    this.studiested = studiested;
  }

  public String getUndervisningssprak() {
    return undervisningssprak;
  }

  public void setUndervisningssprak(String undervisningssprak) {
    this.undervisningssprak = undervisningssprak;
  }

  public String getBeskrivelse() {
    return beskrivelse;
  }

  public void setBeskrivelse(String beskrivelse) {
    this.beskrivelse = beskrivelse;
  }

  public String getStarttidspunkt() {
    return starttidspunkt;
  }

  public void setStarttidspunkt(String starttidspunkt) {
    this.starttidspunkt = starttidspunkt;
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
