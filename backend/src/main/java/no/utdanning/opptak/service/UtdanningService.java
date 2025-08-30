package no.utdanning.opptak.service;

import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.OppdaterUtdanningInput;
import no.utdanning.opptak.graphql.dto.OpprettUtdanningInput;
import no.utdanning.opptak.graphql.dto.PageInput;
import no.utdanning.opptak.graphql.dto.UtdanningFilter;
import no.utdanning.opptak.graphql.dto.UtdanningPage;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service-lag for utdanning-operasjoner. Inneholder all forretningslogikk relatert til utdanninger.
 * Integrerer med UtdanningSecurityService for sikkerhetskontroll.
 */
@Service
@Transactional
public class UtdanningService {

  private final UtdanningRepository utdanningRepository;
  private final OrganisasjonRepository organisasjonRepository;
  private final UtdanningSecurityService securityService;

  public UtdanningService(
      UtdanningRepository utdanningRepository,
      OrganisasjonRepository organisasjonRepository,
      UtdanningSecurityService securityService) {
    this.utdanningRepository = utdanningRepository;
    this.organisasjonRepository = organisasjonRepository;
    this.securityService = securityService;
  }

  /** Henter en spesifikk utdanning basert på ID. Sjekker tilgang for ikke-administratorer. */
  public Utdanning findById(String id) {
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

  /**
   * Henter utdanninger med filtrering og paginering. For ikke-administratorer filtreres automatisk
   * på egen organisasjon.
   */
  public UtdanningPage findAll(UtdanningFilter filter, PageInput page) {
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
    List<Utdanning> content =
        utdanningRepository.findWithFilters(
            filter.getNavn(),
            filter.getStudienivaa(),
            filter.getStudiested(),
            filter.getOrganisasjonId(),
            filter.getStudieform(),
            filter.getAktiv(),
            page.getSize(),
            offset);

    // Tell totalt antall for paginering
    long totalElements =
        utdanningRepository.countWithFilters(
            filter.getNavn(),
            filter.getStudienivaa(),
            filter.getStudiested(),
            filter.getOrganisasjonId(),
            filter.getStudieform(),
            filter.getAktiv());

    return new UtdanningPage(content, totalElements, page.getPage(), page.getSize());
  }

  /** Henter utdanninger for en spesifikk organisasjon. Sjekker tilgang for ikke-administratorer. */
  public UtdanningPage findByOrganisasjon(
      String organisasjonId, UtdanningFilter filter, PageInput page) {
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

    // Hent filtrerte resultater
    List<Utdanning> content =
        utdanningRepository.findWithFilters(
            filter.getNavn(),
            filter.getStudienivaa(),
            filter.getStudiested(),
            organisasjonId,
            filter.getStudieform(),
            filter.getAktiv(),
            page.getSize(),
            offset);

    // Tell totalt antall for paginering
    long totalElements =
        utdanningRepository.countWithFilters(
            filter.getNavn(),
            filter.getStudienivaa(),
            filter.getStudiested(),
            organisasjonId,
            filter.getStudieform(),
            filter.getAktiv());

    return new UtdanningPage(content, totalElements, page.getPage(), page.getSize());
  }

  /** Oppretter en ny utdanning. Validerer organisasjonstilgang og påkrevde felter. */
  public Utdanning opprettUtdanning(OpprettUtdanningInput input) {
    // Valider at organisasjonen eksisterer
    Organisasjon organisasjon = organisasjonRepository.findById(input.getOrganisasjonId());
    if (organisasjon == null) {
      throw new IllegalArgumentException("Organisasjon ikke funnet: " + input.getOrganisasjonId());
    }

    // Sjekk tilgang - kun administratorer eller opptaksledere for egen organisasjon
    if (!securityService.canCreateUtdanningForOrganisasjon(input.getOrganisasjonId())) {
      throw new SecurityException(
          "Ingen tilgang til å opprette utdanning for denne organisasjonen");
    }

    // Opprett ny utdanning
    Utdanning utdanning = new Utdanning();
    utdanning.setNavn(input.getNavn());
    utdanning.setStudienivaa(input.getStudienivaa());
    utdanning.setStudiepoeng(input.getStudiepoeng());
    utdanning.setVarighet(input.getVarighet());
    utdanning.setStudiested(input.getStudiested());
    utdanning.setUndervisningssprak(input.getUndervisningssprak());
    utdanning.setBeskrivelse(input.getBeskrivelse());
    utdanning.setStarttidspunkt(input.getStarttidspunkt());
    utdanning.setStudieform(input.getStudieform());
    utdanning.setOrganisasjonId(input.getOrganisasjonId());
    utdanning.setAktiv(true); // Nye utdanninger er aktive som standard

    return utdanningRepository.save(utdanning);
  }

  /** Oppdaterer en eksisterende utdanning. Validerer organisasjonstilgang. */
  public Utdanning oppdaterUtdanning(OppdaterUtdanningInput input) {
    Utdanning eksisterende = utdanningRepository.findById(input.getId());
    if (eksisterende == null) {
      throw new IllegalArgumentException("Utdanning ikke funnet: " + input.getId());
    }

    // Sjekk tilgang
    if (!securityService.hasAccessToUtdanning(input.getId())) {
      throw new SecurityException("Ingen tilgang til å oppdatere denne utdanningen");
    }

    // Oppdater kun felter som er spesifisert
    if (input.getNavn() != null) {
      eksisterende.setNavn(input.getNavn());
    }
    if (input.getStudienivaa() != null) {
      eksisterende.setStudienivaa(input.getStudienivaa());
    }
    if (input.getStudiepoeng() != null) {
      eksisterende.setStudiepoeng(input.getStudiepoeng());
    }
    if (input.getVarighet() != null) {
      eksisterende.setVarighet(input.getVarighet());
    }
    if (input.getStudiested() != null) {
      eksisterende.setStudiested(input.getStudiested());
    }
    if (input.getUndervisningssprak() != null) {
      eksisterende.setUndervisningssprak(input.getUndervisningssprak());
    }
    if (input.getBeskrivelse() != null) {
      eksisterende.setBeskrivelse(input.getBeskrivelse());
    }
    if (input.getStarttidspunkt() != null) {
      eksisterende.setStarttidspunkt(input.getStarttidspunkt());
    }
    if (input.getStudieform() != null) {
      eksisterende.setStudieform(input.getStudieform());
    }
    if (input.getAktiv() != null) {
      eksisterende.setAktiv(input.getAktiv());
    }

    return utdanningRepository.save(eksisterende);
  }

  /** Deaktiverer en utdanning (soft delete). Validerer organisasjonstilgang. */
  public Utdanning deaktiverUtdanning(String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    if (utdanning == null) {
      throw new IllegalArgumentException("Utdanning ikke funnet: " + id);
    }

    // Sjekk tilgang
    if (!securityService.hasAccessToUtdanning(id)) {
      throw new SecurityException("Ingen tilgang til å deaktivere denne utdanningen");
    }

    utdanning.setAktiv(false);
    return utdanningRepository.save(utdanning);
  }

  /** Aktiverer en deaktivert utdanning. Validerer organisasjonstilgang. */
  public Utdanning aktiverUtdanning(String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    if (utdanning == null) {
      throw new IllegalArgumentException("Utdanning ikke funnet: " + id);
    }

    // Sjekk tilgang
    if (!securityService.hasAccessToUtdanning(id)) {
      throw new SecurityException("Ingen tilgang til å aktivere denne utdanningen");
    }

    utdanning.setAktiv(true);
    return utdanningRepository.save(utdanning);
  }

  /** Sletter en utdanning permanent. Kun for administratorer. */
  public boolean slettUtdanning(String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    if (utdanning == null) {
      return false;
    }

    // Kun administratorer kan slette permanent
    if (!securityService.isAdministrator()) {
      throw new SecurityException("Kun administratorer kan slette utdanninger permanent");
    }

    return utdanningRepository.deleteById(id);
  }

  /** Henter organisasjon for en utdanning. Brukes for GraphQL schema mapping. */
  public Organisasjon getOrganisasjon(Utdanning utdanning) {
    return organisasjonRepository.findById(utdanning.getOrganisasjonId());
  }
}
