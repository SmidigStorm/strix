package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.graphql.dto.EndreOpptaksStatusInput;
import no.utdanning.opptak.graphql.dto.OppdaterOpptakInput;
import no.utdanning.opptak.graphql.dto.OpprettOpptakInput;
import no.utdanning.opptak.service.OpptakService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** GraphQL Mutation resolver for Opptak operasjoner. */
@Controller
@PreAuthorize("isAuthenticated()")
public class OpptakMutationResolver {

  private final OpptakService opptakService;

  public OpptakMutationResolver(OpptakService opptakService) {
    this.opptakService = opptakService;
  }

  /** Oppretter nytt opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak opprettOpptak(@Argument OpprettOpptakInput input) {
    return opptakService.opprettOpptak(input);
  }

  /** Oppdaterer eksisterende opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak oppdaterOpptak(@Argument OppdaterOpptakInput input) {
    return opptakService.oppdaterOpptak(input);
  }

  /** Endrer status på opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak endreOpptakStatus(@Argument EndreOpptaksStatusInput input) {
    return opptakService.endreStatus(input);
  }

  /** Deaktiverer opptak (soft delete) */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak deaktiverOpptak(@Argument String opptakId) {
    return opptakService.deaktiverOpptak(opptakId);
  }

  /** Reaktiverer opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak reaktiverOpptak(@Argument String opptakId) {
    return opptakService.reaktiverOpptak(opptakId);
  }

  /** Gir en organisasjon tilgang til samordnet opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak giOrganisasjonOpptakTilgang(
      @Argument String opptakId, @Argument String organisasjonId) {
    return opptakService.giOrganisasjonTilgang(opptakId, organisasjonId);
  }

  /** Fjerner organisasjons tilgang til opptak */
  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Opptak fjernOrganisasjonOpptakTilgang(
      @Argument String opptakId, @Argument String organisasjonId) {
    return opptakService.fjernOrganisasjonTilgang(opptakId, organisasjonId);
  }
}
