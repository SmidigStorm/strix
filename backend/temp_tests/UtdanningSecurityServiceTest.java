package no.utdanning.opptak.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
@DisplayName("UtdanningSecurityService - Sikkerhetstester")
class UtdanningSecurityServiceTest {

  @Mock private UtdanningRepository utdanningRepository;
  @Mock private JwtService jwtService;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;
  @Mock private HttpServletRequest httpServletRequest;
  @Mock private ServletRequestAttributes servletRequestAttributes;
  @Mock private Claims claims;

  @InjectMocks private UtdanningSecurityService utdanningSecurityService;

  private Utdanning testUtdanning;

  @BeforeEach
  void setUp() {
    testUtdanning = new Utdanning();
    testUtdanning.setId("UTD-TEST-123");
    testUtdanning.setOrganisasjonId("ORG-NTNU-001");
    testUtdanning.setNavn("Test Utdanning");

    // Set up security context
    when(securityContext.getAuthentication()).thenReturn(authentication);
  }

  // ==================== ADMINISTRATOR TILGANG ====================

  @Test
  @DisplayName("Administrator skal ha tilgang til alle utdanninger")
  void administrator_skalHaTilgangTilAlleUtdanninger() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-ANY-123");

      // Then
      assertTrue(hasAccess, "Administrator skal ha tilgang til alle utdanninger");
      verify(utdanningRepository, never()).findById(anyString()); // Ingen DB lookup nødvendig
    }
  }

  @Test
  @DisplayName("Administrator skal kunne opprette utdanninger for alle organisasjoner")
  void administrator_skalKunneOppretteUtdanningerForAlleOrganisasjoner() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean canCreate = utdanningSecurityService.canCreateUtdanningForOrganisasjon("ORG-ANY-123");

      // Then
      assertTrue(canCreate, "Administrator skal kunne opprette utdanninger for alle organisasjoner");
    }
  }

  // ==================== OPPTAKSLEDER TILGANG ====================

  @Test
  @DisplayName("OPPTAKSLEDER skal ha tilgang til utdanninger fra egen organisasjon")
  void opptaksleder_skalHaTilgangTilEgenOrganisasjon() {
    // Given
    String brukerOrganisasjonId = "ORG-NTNU-001";
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(testUtdanning);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
    when(jwtService.validateToken("valid.jwt.token")).thenReturn(claims);
    when(jwtService.getOrganisasjonId(claims)).thenReturn(brukerOrganisasjonId);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertTrue(hasAccess, "OPPTAKSLEDER skal ha tilgang til utdanninger fra egen organisasjon");
      verify(utdanningRepository).findById("UTD-TEST-123");
      verify(jwtService).validateToken("valid.jwt.token");
      verify(jwtService).getOrganisasjonId(claims);
    }
  }

  @Test
  @DisplayName("OPPTAKSLEDER skal IKKE ha tilgang til utdanninger fra andre organisasjoner")
  void opptaksleder_skalIkkeHaTilgangTilAndreOrganisasjoner() {
    // Given
    String brukerOrganisasjonId = "ORG-UIO-002"; // Forskjellig fra testUtdanning.organisasjonId
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(testUtdanning);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
    when(jwtService.validateToken("valid.jwt.token")).thenReturn(claims);
    when(jwtService.getOrganisasjonId(claims)).thenReturn(brukerOrganisasjonId);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertFalse(hasAccess, "OPPTAKSLEDER skal IKKE ha tilgang til utdanninger fra andre organisasjoner");
    }
  }

  @Test
  @DisplayName("OPPTAKSLEDER skal kunne opprette utdanninger kun for egen organisasjon")
  void opptaksleder_skalKunneOppretteUtdanningerForEgenOrganisasjon() {
    // Given
    String brukerOrganisasjonId = "ORG-NTNU-001";
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
    when(jwtService.validateToken("valid.jwt.token")).thenReturn(claims);
    when(jwtService.getOrganisasjonId(claims)).thenReturn(brukerOrganisasjonId);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean canCreate = utdanningSecurityService.canCreateUtdanningForOrganisasjon(brukerOrganisasjonId);

      // Then
      assertTrue(canCreate, "OPPTAKSLEDER skal kunne opprette utdanninger for egen organisasjon");
    }
  }

  @Test
  @DisplayName("OPPTAKSLEDER skal IKKE kunne opprette utdanninger for andre organisasjoner")
  void opptaksleder_skalIkkeKunneOppretteUtdanningerForAndreOrganisasjoner() {
    // Given
    String brukerOrganisasjonId = "ORG-NTNU-001";
    String annenOrganisasjonId = "ORG-UIO-002";
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
    when(jwtService.validateToken("valid.jwt.token")).thenReturn(claims);
    when(jwtService.getOrganisasjonId(claims)).thenReturn(brukerOrganisasjonId);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean canCreate = utdanningSecurityService.canCreateUtdanningForOrganisasjon(annenOrganisasjonId);

      // Then
      assertFalse(canCreate, "OPPTAKSLEDER skal IKKE kunne opprette utdanninger for andre organisasjoner");
    }
  }

  // ==================== UNAUTHORIZED TILGANG ====================

  @Test
  @DisplayName("Uautentiserte brukere skal ikke ha tilgang til utdanninger")
  void uautentiserteBrukere_skalIkkeHaTilgang() {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      // Given - ingen authentication
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(null);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertFalse(hasAccess, "Uautentiserte brukere skal ikke ha tilgang til utdanninger");
    }
  }

  @Test
  @DisplayName("Brukere uten OPPTAKSLEDER eller ADMINISTRATOR rolle skal ikke ha tilgang")
  void brukereMedFeilRolle_skalIkkeHaTilgang() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_SOKNADSBEHANDLER"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertFalse(hasAccess, "SOKNADSBEHANDLER skal ikke ha skrivetilgang til utdanninger");
    }
  }

  // ==================== EDGE CASES OG FEILHÅNDTERING ====================

  @Test
  @DisplayName("Skal håndtere ikke-eksisterende utdanning gracefully")
  void skalHandtereIkkeEksisterendeUtdanning() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(utdanningRepository.findById("UTD-FINNES-IKKE")).thenReturn(null);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-FINNES-IKKE");

      // Then
      assertFalse(hasAccess, "Skal returnere false for ikke-eksisterende utdanninger");
    }
  }

  @Test
  @DisplayName("Skal håndtere manglende JWT token gracefully")
  void skalHandtereManglendeJwtToken() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(testUtdanning);
    when(httpServletRequest.getHeader("Authorization")).thenReturn(null); // Ingen token

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertFalse(hasAccess, "Skal returnere false når JWT token mangler");
    }
  }

  @Test
  @DisplayName("Skal håndtere ugyldig JWT token gracefully")
  void skalHandtereUgyldigJwtToken() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(testUtdanning);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
    when(jwtService.validateToken("invalid.jwt.token")).thenThrow(new SecurityException("Invalid token"));

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      boolean hasAccess = utdanningSecurityService.hasAccessToUtdanning("UTD-TEST-123");

      // Then
      assertFalse(hasAccess, "Skal returnere false når JWT token er ugyldig");
    }
  }

  @Test
  @DisplayName("isAdministrator() skal returnere true for ADMINISTRATOR rolle")
  void isAdministrator_skalReturnereTrueForAdministrator() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean isAdmin = utdanningSecurityService.isAdministrator();

      // Then
      assertTrue(isAdmin, "isAdministrator skal returnere true for ADMINISTRATOR rolle");
    }
  }

  @Test
  @DisplayName("isAdministrator() skal returnere false for ikke-administrator roller")
  void isAdministrator_skalReturnereFalseForIkkeAdministrator() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      boolean isAdmin = utdanningSecurityService.isAdministrator();

      // Then
      assertFalse(isAdmin, "isAdministrator skal returnere false for ikke-administrator roller");
    }
  }

  @Test
  @DisplayName("getCurrentUserOrganisasjonId() skal returnere null for ADMINISTRATOR")
  void getCurrentUserOrganisasjonId_skalReturnerNullForAdministrator() {
    // Given
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);

      // When
      String orgId = utdanningSecurityService.getCurrentUserOrganisasjonId();

      // Then
      assertNull(orgId, "Administrator skal ikke ha organisasjonstilhørighet");
    }
  }

  @Test
  @DisplayName("getCurrentUserOrganisasjonId() skal returnere organisasjonId for OPPTAKSLEDER")
  void getCurrentUserOrganisasjonId_skalReturnerOrganisasjonIdForOpptaksleder() {
    // Given
    String forventetOrganisasjonId = "ORG-NTNU-001";
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_OPPTAKSLEDER"));
    
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getAuthorities()).thenReturn(authorities);
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
    when(jwtService.validateToken("valid.jwt.token")).thenReturn(claims);
    when(jwtService.getOrganisasjonId(claims)).thenReturn(forventetOrganisasjonId);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContext = mockStatic(SecurityContextHolder.class);
         MockedStatic<RequestContextHolder> mockedRequestContext = mockStatic(RequestContextHolder.class)) {
      
      mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      mockedRequestContext.when(RequestContextHolder::currentRequestAttributes).thenReturn(servletRequestAttributes);
      when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);

      // When
      String orgId = utdanningSecurityService.getCurrentUserOrganisasjonId();

      // Then
      assertEquals(forventetOrganisasjonId, orgId, "Skal returnere organisasjonId fra JWT token");
    }
  }
}