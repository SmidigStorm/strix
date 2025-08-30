package no.utdanning.opptak.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Opptak {
  private String id;
  private String navn;
  private OpptaksType type;
  private Integer aar;
  private LocalDate soknadsfrist;
  private LocalDate svarfrist;
  private Integer maxUtdanningerPerSoknad;
  private OpptaksStatus status;
  private String opptaksomgang;
  private String beskrivelse;
  private LocalDateTime opprettet;
  private Boolean aktiv;
  private Boolean samordnet;
  private String administratorOrganisasjonId;
  private List<UtdanningIOpptak> utdanninger;
  private Organisasjon administrator;

  public Opptak() {}

  public Opptak(
      String id,
      String navn,
      OpptaksType type,
      Integer aar,
      LocalDate soknadsfrist,
      LocalDate svarfrist,
      Integer maxUtdanningerPerSoknad,
      OpptaksStatus status,
      String opptaksomgang,
      String beskrivelse,
      LocalDateTime opprettet,
      Boolean aktiv,
      Boolean samordnet,
      String administratorOrganisasjonId) {
    this.id = id;
    this.navn = navn;
    this.type = type;
    this.aar = aar;
    this.soknadsfrist = soknadsfrist;
    this.svarfrist = svarfrist;
    this.maxUtdanningerPerSoknad = maxUtdanningerPerSoknad;
    this.status = status;
    this.opptaksomgang = opptaksomgang;
    this.beskrivelse = beskrivelse;
    this.opprettet = opprettet;
    this.aktiv = aktiv;
    this.samordnet = samordnet;
    this.administratorOrganisasjonId = administratorOrganisasjonId;
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

  public LocalDate getSoknadsfrist() {
    return soknadsfrist;
  }

  public void setSoknadsfrist(LocalDate soknadsfrist) {
    this.soknadsfrist = soknadsfrist;
  }

  public LocalDate getSvarfrist() {
    return svarfrist;
  }

  public void setSvarfrist(LocalDate svarfrist) {
    this.svarfrist = svarfrist;
  }

  public Integer getMaxUtdanningerPerSoknad() {
    return maxUtdanningerPerSoknad;
  }

  public void setMaxUtdanningerPerSoknad(Integer maxUtdanningerPerSoknad) {
    this.maxUtdanningerPerSoknad = maxUtdanningerPerSoknad;
  }

  public OpptaksStatus getStatus() {
    return status;
  }

  public void setStatus(OpptaksStatus status) {
    this.status = status;
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

  public List<UtdanningIOpptak> getUtdanninger() {
    return utdanninger;
  }

  public void setUtdanninger(List<UtdanningIOpptak> utdanninger) {
    this.utdanninger = utdanninger;
  }

  public Boolean getSamordnet() {
    return samordnet;
  }

  public void setSamordnet(Boolean samordnet) {
    this.samordnet = samordnet;
  }

  public String getAdministratorOrganisasjonId() {
    return administratorOrganisasjonId;
  }

  public void setAdministratorOrganisasjonId(String administratorOrganisasjonId) {
    this.administratorOrganisasjonId = administratorOrganisasjonId;
  }

  public Organisasjon getAdministrator() {
    return administrator;
  }

  public void setAdministrator(Organisasjon administrator) {
    this.administrator = administrator;
  }
}
