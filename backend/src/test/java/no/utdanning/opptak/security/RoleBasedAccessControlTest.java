package no.utdanning.opptak.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import no.utdanning.opptak.graphql.OrganisasjonMutationResolver;
import no.utdanning.opptak.graphql.OrganisasjonQueryResolver;
import no.utdanning.opptak.graphql.dto.OppdaterOrganisasjonInput;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Role-Based Access Control Tests")
class RoleBasedAccessControlTest {

  @Autowired private OrganisasjonQueryResolver organisasjonQueryResolver;

  @Autowired private OrganisasjonMutationResolver organisasjonMutationResolver;

  @Autowired private JwtService jwtService;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should allow all authenticated roles to view organisasjoner")
  void skalTillateAlleAutentiserteRollerAaSeOrganisasjoner() {
    String[] roles = {"ADMINISTRATOR", "OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER"};

    for (String role : roles) {
      // Given
      setupAuthenticationContext("USER_" + role, Arrays.asList(role));

      // When & Then
      List<no.utdanning.opptak.domain.Organisasjon> result =
          organisasjonQueryResolver.organisasjoner(null);

      assertThat(result).isNotNull().as("Role %s should be able to view organizations", role);

      // Clean up
      SecurityContextHolder.clearContext();
    }
  }

  @Test
  @DisplayName("Should only allow ADMINISTRATOR to perform mutations")
  void skalKunTillateAdministratorAaUtforeeMutasjoner() {
    // Test that ADMINISTRATOR can perform mutations
    setupAuthenticationContext("ADMIN_USER", Arrays.asList("ADMINISTRATOR"));

    OppdaterOrganisasjonInput input = new OppdaterOrganisasjonInput();
    input.setId("org-1");
    input.setNavn("Updated Organization Name");

    // Should not throw exception
    no.utdanning.opptak.domain.Organisasjon result =
        organisasjonMutationResolver.oppdaterOrganisasjon(input);
    assertThat(result).isNotNull();
    assertThat(result.getNavn()).isEqualTo("Updated Organization Name");
  }

  @Test
  @DisplayName("Should block non-administrator roles from mutations")
  void skalBlokkerIkkeAdministratorRollerFraMutasjoner() {
    String[] nonAdminRoles = {"OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER"};

    for (String role : nonAdminRoles) {
      // Given
      setupAuthenticationContext("USER_" + role, Arrays.asList(role));

      OppdaterOrganisasjonInput input = new OppdaterOrganisasjonInput();
      input.setId("org-1");
      input.setNavn("Unauthorized Update");

      // When & Then
      assertThrows(
          AccessDeniedException.class,
          () -> organisasjonMutationResolver.oppdaterOrganisasjon(input),
          "Role %s should not be able to perform mutations".formatted(role));

      // Clean up
      SecurityContextHolder.clearContext();
    }
  }

  @Test
  @DisplayName("Should handle user with multiple roles correctly")
  void skalHandtereBrukerMedFlereRollerKorrekt() {
    // Given - User with multiple roles including ADMINISTRATOR
    List<String> multipleRoles = Arrays.asList("ADMINISTRATOR", "OPPTAKSLEDER", "SOKNADSBEHANDLER");
    setupAuthenticationContext("MULTI_ROLE_USER", multipleRoles);

    // When & Then - Should be able to perform both queries and mutations
    List<no.utdanning.opptak.domain.Organisasjon> queryResult =
        organisasjonQueryResolver.organisasjoner(null);
    assertThat(queryResult).isNotNull();

    OppdaterOrganisasjonInput input = new OppdaterOrganisasjonInput();
    input.setId("org-2");
    input.setNavn("Multi Role Update");

    no.utdanning.opptak.domain.Organisasjon mutationResult =
        organisasjonMutationResolver.oppdaterOrganisasjon(input);
    assertThat(mutationResult).isNotNull();
    assertThat(mutationResult.getNavn()).isEqualTo("Multi Role Update");
  }

  @Test
  @DisplayName("Should handle user with multiple roles but no ADMINISTRATOR")
  void skalHandtereBrukerMedFlereRollerUtenAdministrator() {
    // Given - User with multiple roles but NO ADMINISTRATOR
    List<String> nonAdminRoles = Arrays.asList("OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER");
    setupAuthenticationContext("MULTI_NON_ADMIN_USER", nonAdminRoles);

    // When & Then - Should be able to query but not mutate
    List<no.utdanning.opptak.domain.Organisasjon> queryResult =
        organisasjonQueryResolver.organisasjoner(null);
    assertThat(queryResult).isNotNull();

    OppdaterOrganisasjonInput input = new OppdaterOrganisasjonInput();
    input.setId("org-3");
    input.setNavn("Unauthorized Multi Role Update");

    assertThrows(
        AccessDeniedException.class,
        () -> organisasjonMutationResolver.oppdaterOrganisasjon(input),
        "User with multiple non-admin roles should not be able to perform mutations");
  }

  @Test
  @DisplayName("Should test deaktiver and reaktiver operations")
  void skalTesteDeaktiverOgReaktiverOperasjoner() {
    // Given - Administrator role
    setupAuthenticationContext("DEAKTIVER_USER", Arrays.asList("ADMINISTRATOR"));

    // When & Then - Should be able to deactivate and reactivate
    no.utdanning.opptak.domain.Organisasjon deactivatedOrg =
        organisasjonMutationResolver.deaktiverOrganisasjon("org-1");
    assertThat(deactivatedOrg).isNotNull();
    assertThat(deactivatedOrg.getAktiv()).isFalse();

    no.utdanning.opptak.domain.Organisasjon reactivatedOrg =
        organisasjonMutationResolver.reaktiverOrganisasjon("org-1");
    assertThat(reactivatedOrg).isNotNull();
    assertThat(reactivatedOrg.getAktiv()).isTrue();
  }

  @Test
  @DisplayName("Should block non-admin from deaktiver and reaktiver")
  void skalBlokkerIkkeAdminFraDeaktiverOgReaktiver() {
    // Given - Non-admin role
    setupAuthenticationContext("NON_ADMIN_USER", Arrays.asList("SOKNADSBEHANDLER"));

    // When & Then
    assertThrows(
        AccessDeniedException.class,
        () -> organisasjonMutationResolver.deaktiverOrganisasjon("org-1"),
        "Non-admin should not be able to deactivate organizations");

    assertThrows(
        AccessDeniedException.class,
        () -> organisasjonMutationResolver.reaktiverOrganisasjon("org-1"),
        "Non-admin should not be able to reactivate organizations");
  }

  @Test
  @DisplayName("Should verify organisasjon single item query access")
  void skalVerifisereOrganisasjonEnkeltElementQueryTilgang() {
    String[] roles = {"ADMINISTRATOR", "OPPTAKSLEDER", "SOKNADSBEHANDLER", "SOKER"};

    for (String role : roles) {
      // Given
      setupAuthenticationContext("SINGLE_" + role, Arrays.asList(role));

      // When & Then
      no.utdanning.opptak.domain.Organisasjon result =
          organisasjonQueryResolver.organisasjon("org-1");

      assertThat(result).isNotNull().as("Role %s should be able to view single organization", role);

      // Clean up
      SecurityContextHolder.clearContext();
    }
  }

  private void setupAuthenticationContext(String userId, List<String> roles) {
    List<SimpleGrantedAuthority> authorities =
        roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userId, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
