package no.utdanning.opptak.domain;

public enum OpptaksStatus {
  FREMTIDIG, // Opptak som ikke er åpnet enda
  APENT, // Opptak som tar imot søknader
  STENGT, // Opptak som er stengt for søknader
  AVSLUTTET // Opptak som er ferdig behandlet
}
