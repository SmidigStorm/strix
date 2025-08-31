package no.utdanning.opptak.service;

import java.util.List;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptakTilgang;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.EndreOpptaksStatusInput;
import no.utdanning.opptak.graphql.dto.OppdaterOpptakInput;
import no.utdanning.opptak.graphql.dto.OpprettOpptakInput;
import no.utdanning.opptak.repository.JdbcOpptakRepository;
import no.utdanning.opptak.repository.JdbcOpptakTilgangRepository;
import no.utdanning.opptak.repository.JdbcOrganisasjonRepository;
import no.utdanning.opptak.service.security.OpptakSecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Opptak forretningslogikk. Håndterer CRUD operasjoner, validering, sikkerhet og
 * samordning.
 */
@Service
@Transactional
public class OpptakService {

  private final JdbcOpptakRepository opptakRepository;
  private final JdbcOpptakTilgangRepository tilgangRepository;
  private final JdbcOrganisasjonRepository organisasjonRepository;
  private final OpptakSecurityService securityService;

  public OpptakService(
      JdbcOpptakRepository opptakRepository,
      JdbcOpptakTilgangRepository tilgangRepository,
      JdbcOrganisasjonRepository organisasjonRepository,
      OpptakSecurityService securityService) {
    this.opptakRepository = opptakRepository;
    this.tilgangRepository = tilgangRepository;
    this.organisasjonRepository = organisasjonRepository;
    this.securityService = securityService;
  }

  /** Henter alle opptak med sikkerhetskontroll */
  public List<Opptak> findAll() {
    // Administratorer ser alt, andre ser kun opptak de har tilgang til
    if (securityService.isAdministrator()) {
      return opptakRepository.findByAktiv(true);
    }

    String userOrgId = securityService.getCurrentUserOrganisasjonId();
    if (userOrgId == null) {
      return List.of(); // Ingen organisasjonstilhørighet
    }

    // Find opptak hvor bruker er administrator eller har tilgang
    List<Opptak> adminOpptak = opptakRepository.findByAdministratorOrganisasjonId(userOrgId);
    List<OpptakTilgang> tilganger = tilgangRepository.findByOrganisasjonId(userOrgId);

    // Add opptak where user has access but is not admin
    for (OpptakTilgang tilgang : tilganger) {
      Opptak opptak = opptakRepository.findById(tilgang.getOpptakId());
      if (opptak != null && opptak.getAktiv() && !adminOpptak.contains(opptak)) {
        adminOpptak.add(opptak);
      }
    }

    return adminOpptak;
  }

  /** Henter opptak ved ID med sikkerhetskontroll */
  public Opptak findById(String id) {
    Opptak opptak = opptakRepository.findById(id);
    if (opptak == null) {
      return null;
    }

    // Sjekk tilgang
    if (!hasAccessToOpptak(opptak)) {
      return null; // Ingen tilgang
    }

    return opptak;
  }

  /** Henter opptak som en organisasjon administrerer */
  public List<Opptak> findByAdministratorOrganisasjon(String organisasjonId) {
    // Sjekk at bruker har tilgang til denne organisasjonen
    if (!securityService.hasAccessToOrganisasjon(organisasjonId)) {
      throw new SecurityException("Ingen tilgang til organisasjon: " + organisasjonId);
    }

    return opptakRepository.findByAdministratorOrganisasjonId(organisasjonId);
  }

  /** Henter opptak hvor en organisasjon kan legge til utdanninger */
  public List<Opptak> findTilgjengeligeForOrganisasjon(String organisasjonId) {
    // Sjekk at bruker har tilgang til denne organisasjonen
    if (!securityService.hasAccessToOrganisasjon(organisasjonId)) {
      throw new SecurityException("Ingen tilgang til organisasjon: " + organisasjonId);
    }

    // Find opptak hvor organisasjon er administrator eller har tilgang
    List<Opptak> adminOpptak = opptakRepository.findByAdministratorOrganisasjonId(organisasjonId);
    List<OpptakTilgang> tilganger = tilgangRepository.findByOrganisasjonId(organisasjonId);

    for (OpptakTilgang tilgang : tilganger) {
      Opptak opptak = opptakRepository.findById(tilgang.getOpptakId());
      if (opptak != null && opptak.getAktiv() && !adminOpptak.contains(opptak)) {
        adminOpptak.add(opptak);
      }
    }

    return adminOpptak;
  }

  /** Oppretter nytt opptak */
  public Opptak opprettOpptak(OpprettOpptakInput input) {
    // Valider input
    validerOpprettInput(input);

    // Sjekk tilgang til administrator organisasjon
    if (!securityService.canManageOrganisasjon(input.getAdministratorOrganisasjonId())) {
      throw new SecurityException(
          "Ingen tilgang til å opprette opptak for organisasjon: "
              + input.getAdministratorOrganisasjonId());
    }

    // Sjekk at organisasjon eksisterer
    Organisasjon adminOrg = organisasjonRepository.findById(input.getAdministratorOrganisasjonId());
    if (adminOrg == null) {
      throw new IllegalArgumentException(
          "Administrator organisasjon ikke funnet: " + input.getAdministratorOrganisasjonId());
    }

    // Sjekk at navn ikke er tatt
    if (opptakRepository.existsByNavn(input.getNavn())) {
      throw new IllegalArgumentException(
          "Opptak med navn '" + input.getNavn() + "' eksisterer allerede");
    }

    // Opprett opptak
    Opptak opptak = new Opptak();
    opptak.setNavn(input.getNavn());
    opptak.setType(input.getType());
    opptak.setAar(input.getAar());
    opptak.setAdministratorOrganisasjonId(input.getAdministratorOrganisasjonId());
    opptak.setSamordnet(input.getSamordnet() != null ? input.getSamordnet() : false);
    opptak.setSoknadsfrist(
        input.getSoknadsfrist() != null
            ? java.time.LocalDate.parse(input.getSoknadsfrist())
            : null);
    opptak.setSvarfrist(
        input.getSvarfrist() != null ? java.time.LocalDate.parse(input.getSvarfrist()) : null);
    opptak.setMaxUtdanningerPerSoknad(
        input.getMaxUtdanningerPerSoknad() != null ? input.getMaxUtdanningerPerSoknad() : 10);
    opptak.setStatus(OpptaksStatus.FREMTIDIG);
    opptak.setOpptaksomgang(input.getOpptaksomgang());
    opptak.setBeskrivelse(input.getBeskrivelse());
    opptak.setAktiv(true);

    return opptakRepository.save(opptak);
  }

  /** Oppdaterer eksisterende opptak */
  public Opptak oppdaterOpptak(OppdaterOpptakInput input) {
    Opptak eksisterende = opptakRepository.findById(input.getId());
    if (eksisterende == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + input.getId());
    }

    // Sjekk tilgang - kun administrator av opptak kan oppdatere
    if (!securityService.canManageOpptak(input.getId())) {
      throw new SecurityException("Ingen tilgang til å oppdatere opptak: " + input.getId());
    }

    // Oppdater kun felter som er spesifisert
    if (input.getNavn() != null && !input.getNavn().trim().isEmpty()) {
      // Sjekk at navn ikke er tatt av andre opptak
      if (opptakRepository.existsByNavnAndIdNot(input.getNavn(), input.getId())) {
        throw new IllegalArgumentException(
            "Opptak med navn '" + input.getNavn() + "' eksisterer allerede");
      }
      eksisterende.setNavn(input.getNavn().trim());
    }

    if (input.getSoknadsfrist() != null) {
      eksisterende.setSoknadsfrist(java.time.LocalDate.parse(input.getSoknadsfrist()));
    }

    if (input.getSvarfrist() != null) {
      eksisterende.setSvarfrist(java.time.LocalDate.parse(input.getSvarfrist()));
    }

    if (input.getMaxUtdanningerPerSoknad() != null && input.getMaxUtdanningerPerSoknad() > 0) {
      eksisterende.setMaxUtdanningerPerSoknad(input.getMaxUtdanningerPerSoknad());
    }

    if (input.getOpptaksomgang() != null) {
      eksisterende.setOpptaksomgang(input.getOpptaksomgang());
    }

    if (input.getBeskrivelse() != null) {
      eksisterende.setBeskrivelse(input.getBeskrivelse());
    }

    if (input.getType() != null) {
      eksisterende.setType(input.getType());
    }

    if (input.getAar() != null && input.getAar() > 0) {
      eksisterende.setAar(input.getAar());
    }

    if (input.getAdministratorOrganisasjonId() != null) {
      // Valider at organisasjonen eksisterer
      if (organisasjonRepository.findById(input.getAdministratorOrganisasjonId()) == null) {
        throw new IllegalArgumentException("Organisasjon ikke funnet: " + input.getAdministratorOrganisasjonId());
      }
      eksisterende.setAdministratorOrganisasjonId(input.getAdministratorOrganisasjonId());
    }

    if (input.getSamordnet() != null) {
      eksisterende.setSamordnet(input.getSamordnet());
    }

    return opptakRepository.save(eksisterende);
  }

  /** Endrer status på opptak */
  public Opptak endreStatus(EndreOpptaksStatusInput input) {
    Opptak opptak = opptakRepository.findById(input.getOpptakId());
    if (opptak == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + input.getOpptakId());
    }

    // Kun administrator av opptak kan endre status
    if (!securityService.canManageOpptak(input.getOpptakId())) {
      throw new SecurityException(
          "Ingen tilgang til å endre status på opptak: " + input.getOpptakId());
    }

    // Valider statusovergang (kan utvides med forretningsregler)
    opptak.setStatus(input.getNyStatus());

    return opptakRepository.save(opptak);
  }

  /** Deaktiverer opptak (soft delete) */
  public Opptak deaktiverOpptak(String opptakId) {
    Opptak opptak = opptakRepository.findById(opptakId);
    if (opptak == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + opptakId);
    }

    if (!securityService.canManageOpptak(opptakId)) {
      throw new SecurityException("Ingen tilgang til å deaktivere opptak: " + opptakId);
    }

    opptak.setAktiv(false);
    return opptakRepository.save(opptak);
  }

  /** Reaktiverer opptak */
  public Opptak reaktiverOpptak(String opptakId) {
    Opptak opptak = opptakRepository.findById(opptakId);
    if (opptak == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + opptakId);
    }

    if (!securityService.canManageOpptak(opptakId)) {
      throw new SecurityException("Ingen tilgang til å reaktivere opptak: " + opptakId);
    }

    opptak.setAktiv(true);
    return opptakRepository.save(opptak);
  }

  /** Gir en organisasjon tilgang til samordnet opptak */
  public Opptak giOrganisasjonTilgang(String opptakId, String organisasjonId) {
    Opptak opptak = opptakRepository.findById(opptakId);
    if (opptak == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + opptakId);
    }

    if (!opptak.getSamordnet()) {
      throw new IllegalArgumentException("Kan kun gi tilgang til samordnede opptak");
    }

    if (!securityService.canManageOpptak(opptakId)) {
      throw new SecurityException(
          "Ingen tilgang til å gi organisasjon tilgang til opptak: " + opptakId);
    }

    // Sjekk at organisasjon eksisterer
    Organisasjon org = organisasjonRepository.findById(organisasjonId);
    if (org == null) {
      throw new IllegalArgumentException("Organisasjon ikke funnet: " + organisasjonId);
    }

    // Sjekk at tilgang ikke allerede eksisterer
    if (tilgangRepository.hasAccess(opptakId, organisasjonId)) {
      throw new IllegalArgumentException("Organisasjon har allerede tilgang til dette opptaket");
    }

    // Opprett tilgang
    OpptakTilgang tilgang = new OpptakTilgang();
    tilgang.setOpptakId(opptakId);
    tilgang.setOrganisasjonId(organisasjonId);
    tilgang.setTildeltAv(securityService.getCurrentUserId());

    tilgangRepository.save(tilgang);

    return opptak;
  }

  /** Fjerner organisasjons tilgang til opptak */
  public Opptak fjernOrganisasjonTilgang(String opptakId, String organisasjonId) {
    Opptak opptak = opptakRepository.findById(opptakId);
    if (opptak == null) {
      throw new IllegalArgumentException("Opptak ikke funnet: " + opptakId);
    }

    if (!securityService.canManageOpptak(opptakId)) {
      throw new SecurityException(
          "Ingen tilgang til å fjerne organisasjon tilgang fra opptak: " + opptakId);
    }

    OpptakTilgang tilgang =
        tilgangRepository.findByOpptakIdAndOrganisasjonId(opptakId, organisasjonId);
    if (tilgang == null) {
      throw new IllegalArgumentException("Organisasjon har ikke tilgang til dette opptaket");
    }

    tilgangRepository.deleteById(tilgang.getId());

    return opptak;
  }

  /** Henter administrator organisasjon for opptak */
  public Organisasjon getAdministratorOrganisasjon(Opptak opptak) {
    if (opptak.getAdministratorOrganisasjonId() == null) {
      return null;
    }
    return organisasjonRepository.findById(opptak.getAdministratorOrganisasjonId());
  }

  /** Henter organisasjoner som har tilgang til opptak */
  public List<Organisasjon> getTillateTilgangsorganisasjoner(Opptak opptak) {
    List<OpptakTilgang> tilganger = tilgangRepository.findByOpptakId(opptak.getId());
    return tilganger.stream()
        .map(tilgang -> organisasjonRepository.findById(tilgang.getOrganisasjonId()))
        .filter(org -> org != null)
        .toList();
  }

  /** Sjekker om bruker har tilgang til opptak */
  private boolean hasAccessToOpptak(Opptak opptak) {
    if (securityService.isAdministrator()) {
      return true;
    }

    String userOrgId = securityService.getCurrentUserOrganisasjonId();
    if (userOrgId == null) {
      return false;
    }

    // Sjekk om bruker er administrator av opptak
    if (userOrgId.equals(opptak.getAdministratorOrganisasjonId())) {
      return true;
    }

    // Sjekk om bruker har tilgang gjennom organisasjon
    return tilgangRepository.hasAccess(opptak.getId(), userOrgId);
  }

  /** Validerer input for opprettelse av opptak */
  private void validerOpprettInput(OpprettOpptakInput input) {
    if (input.getNavn() == null || input.getNavn().trim().isEmpty()) {
      throw new IllegalArgumentException("Opptak navn er påkrevd");
    }

    if (input.getType() == null) {
      throw new IllegalArgumentException("Opptak type er påkrevd");
    }

    if (input.getAar() == null || input.getAar() < 2020 || input.getAar() > 2030) {
      throw new IllegalArgumentException("Opptak år må være mellom 2020 og 2030");
    }

    if (input.getAdministratorOrganisasjonId() == null
        || input.getAdministratorOrganisasjonId().trim().isEmpty()) {
      throw new IllegalArgumentException("Administrator organisasjon er påkrevd");
    }

    if (input.getMaxUtdanningerPerSoknad() != null
        && (input.getMaxUtdanningerPerSoknad() < 1 || input.getMaxUtdanningerPerSoknad() > 20)) {
      throw new IllegalArgumentException(
          "Maksimalt antall utdanninger per søknad må være mellom 1 og 20");
    }
  }
}
