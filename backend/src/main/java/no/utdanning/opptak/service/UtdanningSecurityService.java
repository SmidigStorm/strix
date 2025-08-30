package no.utdanning.opptak.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Service for håndtering av sikkerhet og tilgangskontroll for utdanninger */
@Service
public class UtdanningSecurityService {

  private final UtdanningRepository utdanningRepository;
  private final JwtService jwtService;

  public UtdanningSecurityService(UtdanningRepository utdanningRepository, JwtService jwtService) {
    this.utdanningRepository = utdanningRepository;
    this.jwtService = jwtService;
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

  /** Henter organisasjonId fra brukerens JWT token */
  private String getBrukerOrganisasjonId(Authentication auth) {
    // Hent JWT token fra request og ekstraher organisasjonId
    HttpServletRequest request = getCurrentRequest();
    if (request == null) {
      return null;
    }

    String token = extractTokenFromRequest(request);
    if (token == null) {
      return null;
    }

    try {
      Claims claims = jwtService.validateToken(token);
      return jwtService.getOrganisasjonId(claims);
    } catch (SecurityException e) {
      return null;
    }
  }

  /** Henter nåværende HTTP request */
  private HttpServletRequest getCurrentRequest() {
    try {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      return attributes.getRequest();
    } catch (IllegalStateException e) {
      return null;
    }
  }

  /** Ekstraherer JWT token fra Authorization header */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
