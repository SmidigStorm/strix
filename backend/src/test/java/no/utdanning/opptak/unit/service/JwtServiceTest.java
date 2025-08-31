package no.utdanning.opptak.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtService Tests")
class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
  }

  @Test
  @DisplayName("Should generate token with all claims")
  void skalGenererTokenMedAlleClaims() {
    String brukerId = "BRUKER-001";
    String email = "test@example.com";
    String navn = "Test Bruker";
    List<String> roller = Arrays.asList("OPPTAKSLEDER", "SOKNADSBEHANDLER");
    String organisasjonId = "ORG-001";

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots
  }

  @Test
  @DisplayName("Should generate token without organisasjonId")
  void skalGenererTokenUtenOrganisasjonId() {
    String brukerId = "BRUKER-001";
    String email = "test@example.com";
    String navn = "Test Bruker";
    List<String> roller = Arrays.asList("SOKER");

    String token = jwtService.generateToken(brukerId, email, navn, roller, null);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
  }

  @Test
  @DisplayName("Should validate valid token and return claims")
  void skalValidereGyldigTokenOgReturnereClaims() {
    String brukerId = "BRUKER-001";
    String email = "test@example.com";
    String navn = "Test Bruker";
    List<String> roller = Arrays.asList("OPPTAKSLEDER");
    String organisasjonId = "ORG-001";

    String token = jwtService.generateToken(brukerId, email, navn, roller, organisasjonId);
    Claims claims = jwtService.validateToken(token);

    assertThat(claims).isNotNull();
    assertThat(claims.getSubject()).isEqualTo(brukerId);
    assertThat(claims.get("email", String.class)).isEqualTo(email);
    assertThat(claims.get("navn", String.class)).isEqualTo(navn);
    assertThat(claims.get("organisasjonId", String.class)).isEqualTo(organisasjonId);
  }

  @Test
  @DisplayName("Should throw exception for invalid token")
  void skalKasteExceptionForUgyldigToken() {
    String invalidToken = "invalid.jwt.token";

    SecurityException exception =
        assertThrows(SecurityException.class, () -> jwtService.validateToken(invalidToken));

    assertThat(exception.getMessage()).startsWith("Ugyldig JWT token:");
  }

  @Test
  @DisplayName("Should extract brukerId from claims")
  void skalExtraheereBrukerIdFraClaims() {
    String brukerId = "BRUKER-001";
    String token =
        jwtService.generateToken(
            brukerId, "test@example.com", "Test", Arrays.asList("SOKER"), null);
    Claims claims = jwtService.validateToken(token);

    String extractedBrukerId = jwtService.getBrukerId(claims);

    assertThat(extractedBrukerId).isEqualTo(brukerId);
  }

  @Test
  @DisplayName("Should extract email from claims")
  void skalExtrahereEmailFraClaims() {
    String email = "test@example.com";
    String token =
        jwtService.generateToken("BRUKER-001", email, "Test", Arrays.asList("SOKER"), null);
    Claims claims = jwtService.validateToken(token);

    String extractedEmail = jwtService.getEmail(claims);

    assertThat(extractedEmail).isEqualTo(email);
  }

  @Test
  @DisplayName("Should extract navn from claims")
  void skalExtrahereNavnFraClaims() {
    String navn = "Test Bruker";
    String token =
        jwtService.generateToken(
            "BRUKER-001", "test@example.com", navn, Arrays.asList("SOKER"), null);
    Claims claims = jwtService.validateToken(token);

    String extractedNavn = jwtService.getNavn(claims);

    assertThat(extractedNavn).isEqualTo(navn);
  }

  @Test
  @DisplayName("Should extract roller from claims")
  void skalExtrahereRollerFraClaims() {
    List<String> roller = Arrays.asList("OPPTAKSLEDER", "SOKNADSBEHANDLER");
    String token = jwtService.generateToken("BRUKER-001", "test@example.com", "Test", roller, null);
    Claims claims = jwtService.validateToken(token);

    List<String> extractedRoller = jwtService.getRoller(claims);

    assertThat(extractedRoller).containsExactlyElementsOf(roller);
  }

  @Test
  @DisplayName("Should extract organisasjonId from claims")
  void skalExtrahereOrganisasjonIdFraClaims() {
    String organisasjonId = "ORG-001";
    String token =
        jwtService.generateToken(
            "BRUKER-001", "test@example.com", "Test", Arrays.asList("SOKER"), organisasjonId);
    Claims claims = jwtService.validateToken(token);

    String extractedOrganisasjonId = jwtService.getOrganisasjonId(claims);

    assertThat(extractedOrganisasjonId).isEqualTo(organisasjonId);
  }

  @Test
  @DisplayName("Should return null when organisasjonId not in claims")
  void skalReturnereNullNaarOrganisasjonIdIkkeFinnes() {
    String token =
        jwtService.generateToken(
            "BRUKER-001", "test@example.com", "Test", Arrays.asList("SOKER"), null);
    Claims claims = jwtService.validateToken(token);

    String organisasjonId = jwtService.getOrganisasjonId(claims);

    assertThat(organisasjonId).isNull();
  }

  @Test
  @DisplayName("Should check if token is not expired")
  void skalSjekkeAtTokenIkkeErUtgatt() {
    String token =
        jwtService.generateToken(
            "BRUKER-001", "test@example.com", "Test", Arrays.asList("SOKER"), null);
    Claims claims = jwtService.validateToken(token);

    boolean isExpired = jwtService.isTokenExpired(claims);

    assertThat(isExpired).isFalse();
    assertThat(claims.getExpiration()).isAfter(new Date());
  }

  @Test
  @DisplayName("Should set issued at time")
  void skalSetteIssuedAtTime() {
    Date beforeGeneration = new Date(System.currentTimeMillis() - 1000); // 1 second before
    String token =
        jwtService.generateToken(
            "BRUKER-001", "test@example.com", "Test", Arrays.asList("SOKER"), null);
    Date afterGeneration = new Date(System.currentTimeMillis() + 1000); // 1 second after
    Claims claims = jwtService.validateToken(token);

    Date issuedAt = claims.getIssuedAt();
    assertThat(issuedAt).isNotNull();
    assertThat(issuedAt).isBetween(beforeGeneration, afterGeneration);
  }
}
