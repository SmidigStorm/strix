package no.utdanning.opptak.graphql.dto;

import java.util.List;

public class TestBruker {
  private String email;
  private String navn;
  private List<String> roller;
  private String organisasjon;
  private String passord;

  public TestBruker() {}

  public TestBruker(
      String email, String navn, List<String> roller, String organisasjon, String passord) {
    this.email = email;
    this.navn = navn;
    this.roller = roller;
    this.organisasjon = organisasjon;
    this.passord = passord;
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

  public List<String> getRoller() {
    return roller;
  }

  public void setRoller(List<String> roller) {
    this.roller = roller;
  }

  public String getOrganisasjon() {
    return organisasjon;
  }

  public void setOrganisasjon(String organisasjon) {
    this.organisasjon = organisasjon;
  }

  public String getPassord() {
    return passord;
  }

  public void setPassord(String passord) {
    this.passord = passord;
  }
}
