package no.utdanning.opptak.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.OpptaksType;
import no.utdanning.opptak.repository.JdbcOpptakRepository;
import no.utdanning.opptak.service.JwtService;
import no.utdanning.opptak.service.security.OpptakSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class OpptakSecurityServiceTest {

  @Mock private JdbcOpptakRepository opptakRepository;
  @Mock private JwtService jwtService;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletRequest request;
  @Mock private ServletRequestAttributes requestAttributes;

  private OpptakSecurityService securityService;

  @BeforeEach
  void setUp() {
    securityService = new OpptakSecurityService(opptakRepository, jwtService);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void skalReturnereTrueForAdministratorSomKanManageOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    setupAuthentication("ADMINISTRATOR");

    // Act
    boolean result = securityService.canManageOpptak(opptakId);

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void skalReturnereTrueForOpptakslederSomEierOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    String organisasjonId = "ntnu";
    
    setupAuthentication("OPPTAKSLEDER");
    setupJwtToken(organisasjonId);
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setAdministratorOrganisasjonId(organisasjonId);
    when(opptakRepository.findById(opptakId)).thenReturn(opptak);

    // Act
    boolean result = securityService.canManageOpptak(opptakId);

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void skalReturnereFalseForOpptakslederSomIkkeEierOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    
    setupAuthentication("OPPTAKSLEDER");
    setupJwtToken("uio");
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setAdministratorOrganisasjonId("ntnu"); // Annen organisasjon
    when(opptakRepository.findById(opptakId)).thenReturn(opptak);

    // Act
    boolean result = securityService.canManageOpptak(opptakId);

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  void skalReturnereFalseForUautentisertBruker() {
    // Arrange
    String opptakId = "opptak-1";
    when(securityContext.getAuthentication()).thenReturn(null);

    // Act
    boolean result = securityService.canManageOpptak(opptakId);

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  void skalReturnereTrueForCanManageOrganisasjonMedRiktigOrg() {
    // Arrange
    String organisasjonId = "ntnu";
    setupAuthentication("OPPTAKSLEDER");
    setupJwtToken(organisasjonId);

    // Act
    boolean result = securityService.canManageOrganisasjon(organisasjonId);

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void skalReturnereFalseForCanManageOrganisasjonMedFeilOrg() {
    // Arrange
    setupAuthentication("OPPTAKSLEDER");
    setupJwtToken("uio");

    // Act
    boolean result = securityService.canManageOrganisasjon("ntnu");

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  void skalReturnereTrueForIsAdministratorMedAdminRolle() {
    // Arrange
    setupAuthentication("ADMINISTRATOR");

    // Act
    boolean result = securityService.isAdministrator();

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void skalReturnereFalseForIsAdministratorUtenAdminRolle() {
    // Arrange
    setupAuthentication("OPPTAKSLEDER");

    // Act
    boolean result = securityService.isAdministrator();

    // Assert
    assertThat(result).isFalse();
  }

  @Test
  void skalHenteCurrentUserId() {
    // Arrange
    String userId = "user-123";
    Authentication auth = new UsernamePasswordAuthenticationToken(userId, null);
    when(securityContext.getAuthentication()).thenReturn(auth);

    // Act
    String result = securityService.getCurrentUserId();

    // Assert
    assertThat(result).isEqualTo(userId);
  }

  @Test
  void skalHenteCurrentUserOrganisasjonId() {
    // Arrange
    String organisasjonId = "ntnu";
    setupAuthentication("OPPTAKSLEDER");
    setupJwtToken(organisasjonId);

    // Act
    String result = securityService.getCurrentUserOrganisasjonId();

    // Assert
    assertThat(result).isEqualTo(organisasjonId);
  }

  @Test
  void skalReturnereNullForOrganisasjonIdForAdministrator() {
    // Arrange
    setupAuthentication("ADMINISTRATOR");

    // Act
    String result = securityService.getCurrentUserOrganisasjonId();

    // Assert
    assertThat(result).isNull(); // Administratorer har ikke organisasjonstilhørighet
  }

  private void setupAuthentication(String role) {
    Authentication auth = new UsernamePasswordAuthenticationToken(
        "user-1",
        null,
        Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)));
    when(securityContext.getAuthentication()).thenReturn(auth);
  }

  private void setupJwtToken(String organisasjonId) {
    String token = "test-token";
    when(requestAttributes.getRequest()).thenReturn(request);
    RequestContextHolder.setRequestAttributes(requestAttributes);
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtService.extractOrganisasjonId(token)).thenReturn(organisasjonId);
  }

  private Opptak createOpptak(String id) {
    Opptak opptak = new Opptak();
    opptak.setId(id);
    opptak.setNavn("Test Opptak");
    opptak.setType(OpptaksType.LOKALT);
    opptak.setAar(2025);
    opptak.setStatus(OpptaksStatus.FREMTIDIG);
    opptak.setAktiv(true);
    opptak.setAdministratorOrganisasjonId("ntnu");
    return opptak;
  }
}