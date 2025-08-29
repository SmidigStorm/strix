package no.utdanning.opptak.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bruker_rolle")
@IdClass(BrukerRolleId.class)
public class BrukerRolle {

  @Id
  @Column(name = "bruker_id")
  private String brukerId;

  @Id
  @Column(name = "rolle_id")
  private String rolleId;

  @Column(name = "tildelt", nullable = false)
  private LocalDateTime tildelt = LocalDateTime.now();

  @Column(name = "tildelt_av")
  private String tildeltAv;

  @ManyToOne
  @JoinColumn(
      name = "bruker_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private Bruker bruker;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rolle_id", referencedColumnName = "id", insertable = false, updatable = false)
  private Rolle rolle;

  public BrukerRolle() {}

  public BrukerRolle(String brukerId, String rolleId) {
    this.brukerId = brukerId;
    this.rolleId = rolleId;
    this.tildelt = LocalDateTime.now();
  }

  public BrukerRolle(String brukerId, String rolleId, String tildeltAv) {
    this.brukerId = brukerId;
    this.rolleId = rolleId;
    this.tildeltAv = tildeltAv;
    this.tildelt = LocalDateTime.now();
  }

  public String getBrukerId() {
    return brukerId;
  }

  public void setBrukerId(String brukerId) {
    this.brukerId = brukerId;
  }

  public String getRolleId() {
    return rolleId;
  }

  public void setRolleId(String rolleId) {
    this.rolleId = rolleId;
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

  public Bruker getBruker() {
    return bruker;
  }

  public void setBruker(Bruker bruker) {
    this.bruker = bruker;
  }

  public Rolle getRolle() {
    return rolle;
  }

  public void setRolle(Rolle rolle) {
    this.rolle = rolle;
  }
}
