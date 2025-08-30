package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.PageInput;
import no.utdanning.opptak.graphql.dto.UtdanningFilter;
import no.utdanning.opptak.graphql.dto.UtdanningPage;
import no.utdanning.opptak.service.UtdanningService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** GraphQL resolver for utdanning queries. Delegerer all forretningslogikk til UtdanningService. */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class UtdanningQueryResolver {

  private final UtdanningService utdanningService;

  public UtdanningQueryResolver(UtdanningService utdanningService) {
    this.utdanningService = utdanningService;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public Utdanning utdanning(@Argument String id) {
    return utdanningService.findById(id);
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public UtdanningPage utdanninger(@Argument UtdanningFilter filter, @Argument PageInput page) {
    return utdanningService.findAll(filter, page);
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public UtdanningPage utdanningerForOrganisasjon(
      @Argument String organisasjonId, @Argument UtdanningFilter filter, @Argument PageInput page) {
    return utdanningService.findByOrganisasjon(organisasjonId, filter, page);
  }

  /** Schema mapping for å fylle organisasjon-feltet i Utdanning */
  @SchemaMapping(typeName = "Utdanning", field = "organisasjon")
  public Organisasjon organisasjon(Utdanning utdanning) {
    return utdanningService.getOrganisasjon(utdanning);
  }
}
