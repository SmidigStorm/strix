package no.utdanning.opptak.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import no.utdanning.opptak.config.JwtAuthenticationFilter;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should set authentication context with valid JWT token")
  void skalSetteAuthenticationContextMedGyldigJwtToken() throws ServletException, IOException {
    // Given
    String validToken = "valid.jwt.token";
    String userId = "BRUKER-001";
    List<String> roles = Arrays.asList("ADMINISTRATOR");

    when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
    when(jwtService.isTokenValid(validToken)).thenReturn(true);
    when(jwtService.extractUserId(validToken)).thenReturn(userId);
    when(jwtService.extractRoles(validToken)).thenReturn(roles);

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getName()).isEqualTo(userId);
    assertThat(authentication.getAuthorities())
        .hasSize(1)
        .extracting("authority")
        .containsExactly("ROLE_ADMINISTRATOR");

    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Should not set authentication with invalid JWT token")
  void skalIkkeSetteAuthenticationMedUgyldigJwtToken() throws ServletException, IOException {
    // Given
    String invalidToken = "invalid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
    when(jwtService.isTokenValid(invalidToken)).thenReturn(false);

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();

    verify(jwtService, never()).extractUserId(anyString());
    verify(jwtService, never()).extractRoles(anyString());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Should not set authentication when no Authorization header")
  void skalIkkeSetteAuthenticationNaarIngenAuthorizationHeader()
      throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();

    verify(jwtService, never()).isTokenValid(anyString());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Should not set authentication when Authorization header missing Bearer prefix")
  void skalIkkeSetteAuthenticationNaarAuthorizationHeaderManglerBearerPrefix()
      throws ServletException, IOException {
    // Given
    when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();

    verify(jwtService, never()).isTokenValid(anyString());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Should handle JWT validation exception gracefully")
  void skalHaandtereJwtValideringExceptionGracefully() throws ServletException, IOException {
    // Given
    String malformedToken = "malformed.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + malformedToken);
    when(jwtService.isTokenValid(malformedToken)).thenReturn(false);

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNull();

    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("Should set authentication context with multiple roles")
  void skalSetteAuthenticationContextMedFlereRoller() throws ServletException, IOException {
    // Given
    String validToken = "valid.jwt.token";
    String userId = "BRUKER-002";
    List<String> roles = Arrays.asList("OPPTAKSLEDER", "SOKNADSBEHANDLER");

    when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
    when(jwtService.isTokenValid(validToken)).thenReturn(true);
    when(jwtService.extractUserId(validToken)).thenReturn(userId);
    when(jwtService.extractRoles(validToken)).thenReturn(roles);

    // When
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getName()).isEqualTo(userId);
    assertThat(authentication.getAuthorities())
        .hasSize(2)
        .extracting("authority")
        .containsExactlyInAnyOrder("ROLE_OPPTAKSLEDER", "ROLE_SOKNADSBEHANDLER");

    verify(filterChain).doFilter(request, response);
  }
}
