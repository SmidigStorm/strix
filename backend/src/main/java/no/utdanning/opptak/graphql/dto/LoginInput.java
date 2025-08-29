package no.utdanning.opptak.graphql.dto;

public class LoginInput {
  private String email;
  private String passord;

  public LoginInput() {}

  public LoginInput(String email, String passord) {
    this.email = email;
    this.passord = passord;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassord() {
    return passord;
  }

  public void setPassord(String passord) {
    this.passord = passord;
  }
}
