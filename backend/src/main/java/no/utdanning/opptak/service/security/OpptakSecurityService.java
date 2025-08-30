package no.utdanning.opptak.service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.repository.JdbcOpptakRepository;
import no.utdanning.opptak.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Service for håndtering av sikkerhet og tilgangskontroll for opptak operasjoner. */
@Service
public class OpptakSecurityService {

  private final JdbcOpptakRepository opptakRepository;
  private final JwtService jwtService;

  public OpptakSecurityService(JdbcOpptakRepository opptakRepository, JwtService jwtService) {
    this.opptakRepository = opptakRepository;
    this.jwtService = jwtService;
  }

  /** Sjekker om nåværende bruker har tilgang til å administrere et opptak */
  public boolean canManageOpptak(String opptakId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Administratorer kan administrere alle opptak
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      return true;
    }

    // OPPTAKSLEDER kan kun administrere opptak hvor de er administrator organisasjon
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"))) {
      Opptak opptak = opptakRepository.findById(opptakId);
      if (opptak == null) {
        return false;
      }

      String brukerOrganisasjonId = getCurrentUserOrganisasjonId();
      return brukerOrganisasjonId != null
          && brukerOrganisasjonId.equals(opptak.getAdministratorOrganisasjonId());
    }

    return false;
  }

  /** Sjekker om nåværende bruker har tilgang til å administrere en organisasjon */
  public boolean canManageOrganisasjon(String organisasjonId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return false;
    }

    // Administratorer kan administrere alle organisasjoner
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
      return true;
    }

    // OPPTAKSLEDER kan kun administrere sin egen organisasjon
    if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"))) {
      String brukerOrganisasjonId = getCurrentUserOrganisasjonId();
      return brukerOrganisasjonId != null && brukerOrganisasjonId.equals(organisasjonId);
    }

    return false;
  }

  /** Sjekker om bruker har tilgang til å se data for en organisasjon */
  public boolean hasAccessToOrganisasjon(String organisasjonId) {
    return canManageOrganisasjon(organisasjonId); // For nå samme logikk
  }

  /** Henter organisasjonId for nåværende bruker */
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

  /** Henter bruker ID for nåværende bruker */
  public String getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }

    // Hent bruker ID fra JWT token
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
      return claims.getSubject(); // Bruker ID er i subject
    } catch (SecurityException e) {
      return null;
    }
  }

  /** Sjekker om nåværende bruker er administrator */
  public boolean isAdministrator() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null
        && auth.isAuthenticated()
        && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
  }

  /** Henter organisasjonId fra brukerens JWT token */
  private String getBrukerOrganisasjonId(Authentication auth) {
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
