package no.utdanning.opptak.service;

import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** Service for håndtering av sikkerhet og tilgangskontroll for utdanninger */
@Service
public class UtdanningSecurityService {

  private final UtdanningRepository utdanningRepository;

  public UtdanningSecurityService(UtdanningRepository utdanningRepository) {
    this.utdanningRepository = utdanningRepository;
  }

  /**
   * Sjekker om nåværende bruker har tilgang til å se/redigere en utdanning
   *
   * @param utdanningId ID til utdanningen
   * @return true hvis brukeren har tilgang
   */
  public boolean hasAccessToUtdanning(String utdanningId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Administratorer har tilgang til alt
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      return true;
    }

    // For OPPTAKSLEDER - sjekk at utdanningen tilhører brukerens organisasjon
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"))) {
      Utdanning utdanning = utdanningRepository.findById(utdanningId);
      if (utdanning == null) {
        return false;
      }

      // Hent brukerens organisasjonId fra JWT principal
      String brukerOrganisasjonId = getBrukerOrganisasjonId(auth);
      return brukerOrganisasjonId != null
          && brukerOrganisasjonId.equals(utdanning.getOrganisasjonId());
    }

    return false;
  }

  /**
   * Sjekker om nåværende bruker har tilgang til å opprette utdanning for gitt organisasjon
   *
   * @param organisasjonId ID til organisasjonen
   * @return true hvis brukeren har tilgang
   */
  public boolean canCreateUtdanningForOrganisasjon(String organisasjonId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Administratorer kan opprette for alle organisasjoner
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      return true;
    }

    // OPPTAKSLEDER kan kun opprette for egen organisasjon
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"))) {
      String brukerOrganisasjonId = getBrukerOrganisasjonId(auth);
      return brukerOrganisasjonId != null && brukerOrganisasjonId.equals(organisasjonId);
    }

    return false;
  }

  /**
   * Henter organisasjonId for nåværende bruker
   *
   * @return organisasjonId eller null for ADMINISTRATOR
   */
  public String getCurrentUserOrganisasjonId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }

    // Administratorer har ikke organisasjonstilhørighet
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      return null;
    }

    return getBrukerOrganisasjonId(auth);
  }

  /**
   * Sjekker om nåværende bruker er administrator
   *
   * @return true hvis bruker har ADMINISTRATOR rolle
   */
  public boolean isAdministrator() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null
        && auth.isAuthenticated()
        && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
  }

  /** Henter organisasjonId fra brukerens authentication principal */
  private String getBrukerOrganisasjonId(Authentication auth) {
    if (auth.getPrincipal() instanceof Bruker) {
      Bruker bruker = (Bruker) auth.getPrincipal();
      return bruker.getOrganisasjonId();
    }
    return null;
  }
}
