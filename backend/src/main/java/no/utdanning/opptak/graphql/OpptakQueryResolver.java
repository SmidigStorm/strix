package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.service.OpptakService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** GraphQL Query resolver for Opptak operasjoner. */
@Controller
@PreAuthorize("isAuthenticated()")
public class OpptakQueryResolver {

  private final OpptakService opptakService;

  public OpptakQueryResolver(OpptakService opptakService) {
    this.opptakService = opptakService;
  }

  /** Henter alle opptak som bruker har tilgang til */
  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public List<Opptak> alleOpptak() {
    return opptakService.findAll();
  }

  /** Henter opptak ved ID */
  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public Opptak opptak(@Argument String id) {
    return opptakService.findById(id);
  }

  /** Henter opptak som en organisasjon administrerer */
  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public List<Opptak> opptakForAdministratorOrganisasjon(@Argument String organisasjonId) {
    return opptakService.findByAdministratorOrganisasjon(organisasjonId);
  }

  /** Henter opptak hvor en organisasjon kan legge til utdanninger */
  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public List<Opptak> tilgjengeligeOpptakForOrganisasjon(@Argument String organisasjonId) {
    return opptakService.findTilgjengeligeForOrganisasjon(organisasjonId);
  }

  /** Henter administrator organisasjon for opptak */
  @SchemaMapping(field = "administrator")
  public Organisasjon administrator(Opptak opptak) {
    return opptakService.getAdministratorOrganisasjon(opptak);
  }

  /** Henter organisasjoner som har tilgang til opptak */
  @SchemaMapping(field = "tillatteTilgangsorganisasjoner")
  public List<Organisasjon> tillatteTilgangsorganisasjoner(Opptak opptak) {
    return opptakService.getTillateTilgangsorganisasjoner(opptak);
  }
}
