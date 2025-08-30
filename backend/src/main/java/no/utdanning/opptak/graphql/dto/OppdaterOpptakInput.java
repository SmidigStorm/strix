package no.utdanning.opptak.graphql.dto;

/** Input for oppdatering av eksisterende opptak. Kun felter som er spesifisert blir oppdatert. */
public class OppdaterOpptakInput {

  private String id;
  private String navn;
  private String soknadsfrist;
  private String svarfrist;
  private Integer maxUtdanningerPerSoknad;
  private String opptaksomgang;
  private String beskrivelse;

  public OppdaterOpptakInput() {}

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
