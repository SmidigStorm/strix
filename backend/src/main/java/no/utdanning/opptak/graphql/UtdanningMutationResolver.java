package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.OppdaterUtdanningInput;
import no.utdanning.opptak.graphql.dto.OpprettUtdanningInput;
import no.utdanning.opptak.service.UtdanningService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** 
 * GraphQL resolver for utdanning mutations.
 * Delegerer all forretningslogikk til UtdanningService.
 */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class UtdanningMutationResolver {

  private final UtdanningService utdanningService;

  public UtdanningMutationResolver(UtdanningService utdanningService) {
    this.utdanningService = utdanningService;
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning opprettUtdanning(@Argument OpprettUtdanningInput input) {
    return utdanningService.opprettUtdanning(input);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning oppdaterUtdanning(@Argument OppdaterUtdanningInput input) {
    return utdanningService.oppdaterUtdanning(input);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning deaktiverUtdanning(@Argument String id) {
    return utdanningService.deaktiverUtdanning(id);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning aktiverUtdanning(@Argument String id) {
    return utdanningService.aktiverUtdanning(id);
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Boolean slettUtdanning(@Argument String id) {
    return utdanningService.slettUtdanning(id);
  }
}
