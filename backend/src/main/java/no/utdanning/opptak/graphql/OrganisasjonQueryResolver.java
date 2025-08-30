package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.OrganisasjonFilter;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/** GraphQL resolver for organisasjon queries */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class OrganisasjonQueryResolver {

  private final OrganisasjonRepository organisasjonRepository;

  public OrganisasjonQueryResolver(OrganisasjonRepository organisasjonRepository) {
    this.organisasjonRepository = organisasjonRepository;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public List<Organisasjon> organisasjoner(@Argument OrganisasjonFilter filter) {
    if (filter == null) {
      return organisasjonRepository.findAll();
    }

    // Start med alle organisasjoner
    List<Organisasjon> result = organisasjonRepository.findAll();

    // Filtrer på aktiv status hvis spesifisert
    if (filter.getAktiv() != null) {
      result = organisasjonRepository.findByAktiv(filter.getAktiv());
    }

    // Filtrer på type hvis spesifisert
    if (filter.getOrganisasjonstype() != null) {
      result =
          result.stream().filter(org -> org.getType() == filter.getOrganisasjonstype()).toList();
    }

    // Søk i navn hvis spesifisert
    if (filter.getNavnSok() != null && !filter.getNavnSok().trim().isEmpty()) {
      result =
          result.stream()
              .filter(
                  org -> org.getNavn().toLowerCase().contains(filter.getNavnSok().toLowerCase()))
              .toList();
    }

    return result;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
  public Organisasjon organisasjon(@Argument String id) {
    return organisasjonRepository.findById(id);
  }
}
