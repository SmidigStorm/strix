package no.utdanning.opptak.graphql.dto;

import no.utdanning.opptak.domain.OrganisasjonsType;

/** Filter for organisasjon queries */
public class OrganisasjonFilter {
  private Boolean aktiv;
  private OrganisasjonsType organisasjonstype;
  private String navnSok;

  public Boolean getAktiv() {
    return aktiv;
  }

  public void setAktiv(Boolean aktiv) {
    this.aktiv = aktiv;
  }

  public OrganisasjonsType getOrganisasjonstype() {
    return organisasjonstype;
  }

  public void setOrganisasjonstype(OrganisasjonsType organisasjonstype) {
    this.organisasjonstype = organisasjonstype;
  }

  public String getNavnSok() {
    return navnSok;
  }

  public void setNavnSok(String navnSok) {
    this.navnSok = navnSok;
  }
}
