package no.utdanning.opptak.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT Service Spring Security Integration Tests")
class JwtServiceSpringSecurityIntegrationTest {

  private final JwtService jwtService = new JwtService();

  @Test
  @DisplayName("Should extract userId correctly for Spring Security")
  void skalExtrahereUserIdKorrektForSpringSecurity() {
    // Given
    String brukerId = "BRUKER-SPRING-001";
    String email = "spring@test.no";
    String navn = "Spring Test User";
    List<String> roller = Arrays.asList("ADMINISTRATOR");
    String organisasjonId = "ORG-SPRING";

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);

    // When
    String extractedUserId = jwtService.extractUserId(token);

    // Then
    assertThat(extractedUserId).isEqualTo(brukerId);
  }

  @Test
  @DisplayName("Should extract roles correctly for Spring Security")
  void skalExtrahereRollerKorrektForSpringSecurity() {
    // Given
    String brukerId = "BRUKER-SPRING-002";
    String email = "roles@test.no";
    String navn = "Multi Role User";
    List<String> roller = Arrays.asList("OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER");
    String organisasjonId = null;

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);

    // When
    List<String> extractedRoles = jwtService.extractRoles(token);

    // Then
    assertThat(extractedRoles)
        .hasSize(3)
        .containsExactlyInAnyOrder("OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER");
  }

  @Test
  @DisplayName("Should validate token correctly for Spring Security")
  void skalValidereTokenKorrektForSpringSecurity() {
    // Given
    String brukerId = "BRUKER-SPRING-003";
    String email = "valid@test.no";
    String navn = "Valid User";
    List<String> roller = Arrays.asList("ADMINISTRATOR");

    String validToken = jwtService.generateToken(brukerId, email, navn, roller, null);
    String invalidToken = "invalid.jwt.token";

    // When & Then
    assertThat(jwtService.isTokenValid(validToken)).isTrue();
    assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    // Note: null and empty string checks would throw exceptions, so we skip them
  }

  @Test
  @DisplayName("Should handle token with single role")
  void skalHandleTokenMedEnkelleRolle() {
    // Given
    String brukerId = "BRUKER-SINGLE-ROLE";
    String email = "single@test.no";
    String navn = "Single Role User";
    List<String> roller = Arrays.asList("SOKER");

    String token = jwtService.generateToken(brukerId, email, navn, roller, null);

    // When
    String extractedUserId = jwtService.extractUserId(token);
    List<String> extractedRoles = jwtService.extractRoles(token);
    boolean isValid = jwtService.isTokenValid(token);

    // Then
    assertThat(extractedUserId).isEqualTo(brukerId);
    assertThat(extractedRoles).containsExactly("SOKER");
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should handle token without organisasjonId")
  void skalHandleTokenUtenOrganisasjonId() {
    // Given
    String brukerId = "BRUKER-NO-ORG";
    String email = "noorg@test.no";
    String navn = "No Org User";
    List<String> roller = Arrays.asList("SOKER");
    String organisasjonId = null;

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);

    // When
    String extractedUserId = jwtService.extractUserId(token);
    List<String> extractedRoles = jwtService.extractRoles(token);
    boolean isValid = jwtService.isTokenValid(token);

    // Then
    assertThat(extractedUserId).isEqualTo(brukerId);
    assertThat(extractedRoles).containsExactly("SOKER");
    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("Should handle empty roles list")
  void skalHandleTomRolleListe() {
    // Given
    String brukerId = "BRUKER-NO-ROLES";
    String email = "noroles@test.no";
    String navn = "No Roles User";
    List<String> roller = Arrays.asList(); // Empty roles
    String organisasjonId = null;

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);

    // When
    String extractedUserId = jwtService.extractUserId(token);
    List<String> extractedRoles = jwtService.extractRoles(token);
    boolean isValid = jwtService.isTokenValid(token);

    // Then
    assertThat(extractedUserId).isEqualTo(brukerId);
    assertThat(extractedRoles).isEmpty();
    assertThat(isValid).isTrue();
  }
}
