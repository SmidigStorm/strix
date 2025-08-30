package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.OrganisasjonFilter;
import no.utdanning.opptak.service.OrganisasjonService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** 
 * GraphQL resolver for organisasjon queries.
 * Delegerer all forretningslogikk til OrganisasjonService.
 */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class OrganisasjonQueryResolver {

  private final OrganisasjonService organisasjonService;

  public OrganisasjonQueryResolver(OrganisasjonService organisasjonService) {
    this.organisasjonService = organisasjonService;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public List<Organisasjon> organisasjoner(@Argument OrganisasjonFilter filter) {
    return organisasjonService.findAll(filter);
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public Organisasjon organisasjon(@Argument String id) {
    return organisasjonService.findById(id);
  }
}
