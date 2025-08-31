package no.utdanning.opptak.domain;

import java.io.Serializable;
import java.util.Objects;

public class BrukerRolleId implements Serializable {
  private String brukerId;
  private String rolleId;

  public BrukerRolleId() {}

  public BrukerRolleId(String brukerId, String rolleId) {
    this.brukerId = brukerId;
    this.rolleId = rolleId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BrukerRolleId that = (BrukerRolleId) o;
    return Objects.equals(brukerId, that.brukerId) && Objects.equals(rolleId, that.rolleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brukerId, rolleId);
  }
}
