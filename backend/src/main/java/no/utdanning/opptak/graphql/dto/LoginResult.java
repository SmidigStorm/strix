package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.Bruker;

public class LoginResult {
  private String token;
  private Bruker bruker;

  public LoginResult() {}

  public LoginResult(String token, Bruker bruker) {
    this.token = token;
    this.bruker = bruker;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Bruker getBruker() {
    return bruker;
  }

  public void setBruker(Bruker bruker) {
    this.bruker = bruker;
  }
}
