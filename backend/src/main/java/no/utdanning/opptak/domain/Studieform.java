package no.utdanning.opptak.domain;

/** Enum som definerer studieformen for en utdanning */
public enum Studieform {
  HELTID("Heltid"),
  DELTID("Deltid");

  private final String displayName;

  Studieform(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
