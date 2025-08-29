package no.utdanning.opptak.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "bruker")
public class Bruker {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "navn", nullable = false)
  private String navn;

  @Column(name = "passord_hash", nullable = false)
  private String passordHash;

  @Column(name = "organisasjon_id")
  private String organisasjonId;

  @Column(name = "aktiv", nullable = false)
  private Boolean aktiv = true;

  @Column(name = "opprettet", nullable = false)
  private LocalDateTime opprettet = LocalDateTime.now();

  @Column(name = "sist_innlogget")
  private LocalDateTime sistInnlogget;

  @OneToMany(mappedBy = "bruker", fetch = FetchType.EAGER)
  private Set<BrukerRolle> roller;

  public Bruker() {}

  public Bruker(String id, String email, String navn, String passordHash, String organisasjonId) {
    this.id = id;
    this.email = email;
    this.navn = navn;
    this.passordHash = passordHash;
    this.organisasjonId = organisasjonId;
    this.opprettet = LocalDateTime.now();
    this.aktiv = true;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNavn() {
    return navn;
  }

  public void setNavn(String navn) {
    this.navn = navn;
  }

  public String getPassordHash() {
    return passordHash;
  }

  public void setPassordHash(String passordHash) {
    this.passordHash = passordHash;
  }

  public String getOrganisasjonId() {
    return organisasjonId;
  }

  public void setOrganisasjonId(String organisasjonId) {
    this.organisasjonId = organisasjonId;
  }

  public Boolean getAktiv() {
    return aktiv;
  }

  public void setAktiv(Boolean aktiv) {
    this.aktiv = aktiv;
  }

  public LocalDateTime getOpprettet() {
    return opprettet;
  }

  public void setOpprettet(LocalDateTime opprettet) {
    this.opprettet = opprettet;
  }

  public LocalDateTime getSistInnlogget() {
    return sistInnlogget;
  }

  public void setSistInnlogget(LocalDateTime sistInnlogget) {
    this.sistInnlogget = sistInnlogget;
  }

  public Set<BrukerRolle> getRoller() {
    return roller;
  }

  public void setRoller(Set<BrukerRolle> roller) {
    this.roller = roller;
  }
}
