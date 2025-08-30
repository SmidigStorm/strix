package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.OpptaksType;

/** Input for opprettelse av nytt opptak. */
public class OpprettOpptakInput {

  private String navn;
  private OpptaksType type;
  private Integer aar;
  private String administratorOrganisasjonId;
  private Boolean samordnet;
  private String soknadsfrist;
  private String svarfrist;
  private Integer maxUtdanningerPerSoknad;
  private String opptaksomgang;
  private String beskrivelse;

  public OpprettOpptakInput() {}

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public OpptaksType getType() {
    return type;
  }

  public void setType(OpptaksType type) {
    this.type = type;
  }

  public Integer getAar() {
    return aar;
  }

  public void setAar(Integer aar) {
    this.aar = aar;
  }

  public String getAdministratorOrganisasjonId() {
    return administratorOrganisasjonId;
  }

  public void setAdministratorOrganisasjonId(String administratorOrganisasjonId) {
    this.administratorOrganisasjonId = administratorOrganisasjonId;
  }

  public Boolean getSamordnet() {
    return samordnet;
  }

  public void setSamordnet(Boolean samordnet) {
    this.samordnet = samordnet;
  }

  public String getSoknadsfrist() {
    return soknadsfrist;
  }

  public void setSoknadsfrist(String soknadsfrist) {
    this.soknadsfrist = soknadsfrist;
  }

  public String getSvarfrist() {
    return svarfrist;
  }

  public void setSvarfrist(String svarfrist) {
    this.svarfrist = svarfrist;
  }

  public Integer getMaxUtdanningerPerSoknad() {
    return maxUtdanningerPerSoknad;
  }

  public void setMaxUtdanningerPerSoknad(Integer maxUtdanningerPerSoknad) {
    this.maxUtdanningerPerSoknad = maxUtdanningerPerSoknad;
  }

  public String getOpptaksomgang() {
    return opptaksomgang;
  }

  public void setOpptaksomgang(String opptaksomgang) {
    this.opptaksomgang = opptaksomgang;
  }

  public String getBeskrivelse() {
    return beskrivelse;
  }

  public void setBeskrivelse(String beskrivelse) {
    this.beskrivelse = beskrivelse;
  }
}
