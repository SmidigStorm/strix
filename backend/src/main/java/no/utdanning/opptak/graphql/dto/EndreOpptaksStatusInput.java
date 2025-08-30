package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.OpptaksStatus;

/** Input for endring av status på opptak. */
public class EndreOpptaksStatusInput {

  private String opptakId;
  private OpptaksStatus nyStatus;

  public EndreOpptaksStatusInput() {}

  public String getOpptakId() {
    return opptakId;
  }

  public void setOpptakId(String opptakId) {
    this.opptakId = opptakId;
  }

  public OpptaksStatus getNyStatus() {
    return nyStatus;
  }

  public void setNyStatus(OpptaksStatus nyStatus) {
    this.nyStatus = nyStatus;
  }
}
