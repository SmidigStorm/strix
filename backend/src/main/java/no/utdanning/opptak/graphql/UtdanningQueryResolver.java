package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.PageInput;
import no.utdanning.opptak.graphql.dto.UtdanningFilter;
import no.utdanning.opptak.graphql.dto.UtdanningPage;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import no.utdanning.opptak.repository.UtdanningRepository;
import no.utdanning.opptak.service.UtdanningSecurityService;
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
  private final UtdanningSecurityService securityService;

  public UtdanningQueryResolver(
      UtdanningRepository utdanningRepository,
      OrganisasjonRepository organisasjonRepository,
      UtdanningSecurityService securityService) {
    this.utdanningRepository = utdanningRepository;
    this.organisasjonRepository = organisasjonRepository;
    this.securityService = securityService;
  }

  @QueryMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER')")
  public Utdanning utdanning(@Argument String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    
    // Sjekk tilgang for OPPTAKSLEDER
    if (utdanning != null && !securityService.isAdministrator()) {
      String userOrgId = securityService.getCurrentUserOrganisasjonId();
      if (userOrgId != null && !userOrgId.equals(utdanning.getOrganisasjonId())) {
        // OPPTAKSLEDER kan kun se utdanninger fra egen organisasjon
        return null;
      }
    }
    
    return utdanning;
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

    // For OPPTAKSLEDER og SØKNADSBEHANDLER - filtrer automatisk på egen organisasjon
    if (!securityService.isAdministrator()) {
      String userOrgId = securityService.getCurrentUserOrganisasjonId();
      if (userOrgId != null) {
        filter.setOrganisasjonId(userOrgId);
      }
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

    // Sjekk tilgang for ikke-administratorer
    if (!securityService.isAdministrator()) {
      String userOrgId = securityService.getCurrentUserOrganisasjonId();
      if (userOrgId != null && !userOrgId.equals(organisasjonId)) {
        // Ikke-administratorer kan kun se egen organisasjons utdanninger
        // Returner tom side
        return new UtdanningPage(List.of(), 0, 0, 20);
      }
    }

    if (page == null) {
      page = new PageInput();
    }
    if (filter == null) {
      filter = new UtdanningFilter();
    }

    // Force organisasjonId i filter
    filter.setOrganisasjonId(organisasjonId);

    // Beregn offset for paginering
    int offset = page.getPage() * page.getSize();

    // Hent filtrerte resultater direkte (ikke via utdanninger() for å unngå dobbel filtrering)
    List<Utdanning> content = utdanningRepository.findWithFilters(
        filter.getNavn(),
        filter.getStudienivaa(),
        filter.getStudiested(),
        organisasjonId,
        filter.getStudieform(),
        filter.getAktiv(),
        page.getSize(),
        offset);

    // Tell totalt antall for paginering
    long totalElements = utdanningRepository.countWithFilters(
        filter.getNavn(),
        filter.getStudienivaa(),
        filter.getStudiested(),
        organisasjonId,
        filter.getStudieform(),
        filter.getAktiv());

    return new UtdanningPage(content, totalElements, page.getPage(), page.getSize());
  }

  /**
   * Schema mapping for å fylle organisasjon-feltet i Utdanning
   */
  @SchemaMapping(typeName = "Utdanning", field = "organisasjon")
  public Object organisasjon(Utdanning utdanning) {
    return organisasjonRepository.findById(utdanning.getOrganisasjonId());
  }
}