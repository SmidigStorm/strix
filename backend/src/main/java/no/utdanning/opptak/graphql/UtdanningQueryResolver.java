package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.PageInput;
import no.utdanning.opptak.graphql.dto.UtdanningFilter;
import no.utdanning.opptak.graphql.dto.UtdanningPage;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL resolver for utdanning queries
 */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class UtdanningQueryResolver {

  private final UtdanningRepository utdanningRepository;
  private final OrganisasjonRepository organisasjonRepository;

  public UtdanningQueryResolver(
      UtdanningRepository utdanningRepository,
      OrganisasjonRepository organisasjonRepository) {
    this.utdanningRepository = utdanningRepository;
    this.organisasjonRepository = organisasjonRepository;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public Utdanning utdanning(@Argument String id) {
    return utdanningRepository.findById(id);
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public UtdanningPage utdanninger(
      @Argument UtdanningFilter filter,
      @Argument PageInput page) {

    if (page == null) {
      page = new PageInput();
    }
    if (filter == null) {
      filter = new UtdanningFilter();
    }

    // Beregn offset for paginering
    int offset = page.getPage() * page.getSize();

    // Hent filtrerte resultater
    List<Utdanning> content = utdanningRepository.findWithFilters(
        filter.getNavn(),
        filter.getStudienivaa(),
        filter.getStudiested(),
        filter.getOrganisasjonId(),
        filter.getStudieform(),
        filter.getAktiv(),
        page.getSize(),
        offset);

    // Tell totalt antall for paginering
    long totalElements = utdanningRepository.countWithFilters(
        filter.getNavn(),
        filter.getStudienivaa(),
        filter.getStudiested(),
        filter.getOrganisasjonId(),
        filter.getStudieform(),
        filter.getAktiv());

    return new UtdanningPage(content, totalElements, page.getPage(), page.getSize());
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public UtdanningPage utdanningerForOrganisasjon(
      @Argument String organisasjonId,
      @Argument UtdanningFilter filter,
      @Argument PageInput page) {

    if (page == null) {
      page = new PageInput();
    }
    if (filter == null) {
      filter = new UtdanningFilter();
    }

    // Force organisasjonId i filter
    filter.setOrganisasjonId(organisasjonId);

    return utdanninger(filter, page);
  }

  /**
   * Schema mapping for å fylle organisasjon-feltet i Utdanning
   */
  @SchemaMapping(typeName = "Utdanning", field = "organisasjon")
  public Object organisasjon(Utdanning utdanning) {
    return organisasjonRepository.findById(utdanning.getOrganisasjonId());
  }
}