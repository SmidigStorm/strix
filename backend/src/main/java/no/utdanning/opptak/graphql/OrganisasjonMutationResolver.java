package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.OppdaterOrganisasjonInput;
import no.utdanning.opptak.graphql.dto.OpprettOrganisasjonInput;
import no.utdanning.opptak.service.OrganisasjonService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** 
 * GraphQL resolver for organisasjon mutations.
 * Delegerer all forretningslogikk til OrganisasjonService.
 */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class OrganisasjonMutationResolver {

  private final OrganisasjonService organisasjonService;

  public OrganisasjonMutationResolver(OrganisasjonService organisasjonService) {
    this.organisasjonService = organisasjonService;
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Organisasjon opprettOrganisasjon(@Argument OpprettOrganisasjonInput input) {
    return organisasjonService.opprettOrganisasjon(input);
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Organisasjon oppdaterOrganisasjon(@Argument OppdaterOrganisasjonInput input) {
    return organisasjonService.oppdaterOrganisasjon(input);
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Organisasjon deaktiverOrganisasjon(@Argument String id) {
    return organisasjonService.deaktiverOrganisasjon(id);
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Organisasjon reaktiverOrganisasjon(@Argument String id) {
    return organisasjonService.reaktiverOrganisasjon(id);
  }
}
