package no.utdanning.opptak.domain;

import java.time.LocalDateTime;

/**
 * Representerer tilgang for en organisasjon til å delta i et samordnet opptak. Kun relevant for
 * opptak hvor samordnet=true.
 */
public class OpptakTilgang {
  private String id;
  private String opptakId;
  private String organisasjonId;
  private LocalDateTime tildelt;
  private String tildeltAv;

  // Relationships (loaded separately)
  private Opptak opptak;
  private Organisasjon organisasjon;

  public OpptakTilgang() {}

  public OpptakTilgang(
      String id, String opptakId, String organisasjonId, LocalDateTime tildelt, String tildeltAv) {
    this.id = id;
    this.opptakId = opptakId;
    this.organisasjonId = organisasjonId;
    this.tildelt = tildelt;
    this.tildeltAv = tildeltAv;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOpptakId() {
    return opptakId;
  }

  public void setOpptakId(String opptakId) {
    this.opptakId = opptakId;
  }

  public String getOrganisasjonId() {
    return organisasjonId;
  }

  public void setOrganisasjonId(String organisasjonId) {
    this.organisasjonId = organisasjonId;
  }

  public LocalDateTime getTildelt() {
    return tildelt;
  }

  public void setTildelt(LocalDateTime tildelt) {
    this.tildelt = tildelt;
  }

  public String getTildeltAv() {
    return tildeltAv;
  }

  public void setTildeltAv(String tildeltAv) {
    this.tildeltAv = tildeltAv;
  }

  public Opptak getOpptak() {
    return opptak;
  }

  public void setOpptak(Opptak opptak) {
    this.opptak = opptak;
  }

  public Organisasjon getOrganisasjon() {
    return organisasjon;
  }

  public void setOrganisasjon(Organisasjon organisasjon) {
    this.organisasjon = organisasjon;
  }
}
