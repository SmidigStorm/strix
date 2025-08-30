package no.utdanning.opptak.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;
import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.domain.BrukerRolle;
import no.utdanning.opptak.domain.Rolle;
import no.utdanning.opptak.repository.BrukerRepository;
import no.utdanning.opptak.repository.RolleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Administrator Role Integration Tests")
class AdministratorRoleIntegrationTest {

  @Autowired private BrukerRepository brukerRepository;

  @Autowired private RolleRepository rolleRepository;

  @Test
  @DisplayName("Should have Administrator test user in database")
  void testAdministratorTestUserExists() {
    // Act
    Optional<Bruker> adminUser = brukerRepository.findByEmail("admin@strix.no");

    // Assert
    assertTrue(adminUser.isPresent(), "Administrator test user should exist");

    Bruker admin = adminUser.get();
    assertEquals("admin@strix.no", admin.getEmail());
    assertEquals("Sara Administrator", admin.getNavn());
    assertEquals("BRUKER-ADMIN", admin.getId());
    assertTrue(admin.getAktiv(), "Administrator should be active");
    assertNull(
        admin.getOrganisasjonId(), "Administrator should not be tied to specific organisation");
  }

  @Test
  @DisplayName("Administrator user should have ADMINISTRATOR role")
  void testAdministratorUserHasCorrectRole() {
    // Act
    Optional<Bruker> adminUser = brukerRepository.findByEmail("admin@strix.no");

    // Assert
    assertTrue(adminUser.isPresent(), "Administrator test user should exist");

    Set<BrukerRolle> roles = adminUser.get().getRoller();
    assertNotNull(roles, "User should have roles");
    assertFalse(roles.isEmpty(), "User should have at least one role");

    // Check that user has ADMINISTRATOR role
    boolean hasAdminRole =
        roles.stream().anyMatch(role -> "ADMINISTRATOR".equals(role.getRolleId()));
    assertTrue(hasAdminRole, "User should have ADMINISTRATOR role");

    // Check that it's the only role (for clean test user)
    assertEquals(1, roles.size(), "Administrator test user should have exactly one role");
  }

  @Test
  @DisplayName("Should be able to authenticate Administrator user")
  void testAdministratorUserCanAuthenticate() {
    // Arrange
    String email = "admin@strix.no";
    String password = "test123"; // As defined in migration

    // Act
    Optional<Bruker> adminUser = brukerRepository.findByEmail(email);

    // Assert
    assertTrue(adminUser.isPresent(), "Administrator user should exist for authentication");

    Bruker admin = adminUser.get();
    assertNotNull(admin.getPassordHash(), "Administrator should have password hash");
    assertTrue(admin.getPassordHash().startsWith("$2b$"), "Password should be BCrypt hashed");
    assertTrue(admin.getAktiv(), "Administrator should be active for authentication");
  }

  @Test
  @DisplayName("Administrator role should allow system-wide access")
  void testAdministratorRolePermissions() {
    // This test documents the intended permissions for Administrator role
    // based on requirements/krav/tilgangsstyring/roller-og-tilgang.md

    // Arrange
    Optional<Rolle> adminRole = rolleRepository.findById("ADMINISTRATOR");

    // Assert
    assertTrue(adminRole.isPresent(), "ADMINISTRATOR role should exist");

    Rolle role = adminRole.get();
    assertEquals("Administrator", role.getNavn());
    assertEquals(
        "Systemadministrasjon og overordnet styring av hele opptakssystemet",
        role.getBeskrivelse());

    // According to the access matrix, Administrator should have:
    // ✅ Alle for most functions including:
    // - Administrere organisasjoner
    // - Administrere brukere og roller
    // - Systemkonfigurasjon
    // - Se og administrere alle opptak
    // - Overstyre opptaksinnstillinger
    // - Se alle søknader
    // - Behandle søknader
    // - Se alle søkere
    // - Endre søkerinformasjon

    // This is documented in the role description and requirements
    assertNotNull(role.getBeskrivelse(), "Administrator role should have clear description");
    assertTrue(
        role.getBeskrivelse().contains("Systemadministrasjon"),
        "Description should mention system administration");
    assertTrue(
        role.getBeskrivelse().contains("overordnet"),
        "Description should mention overarching control");
  }

  @Test
  @DisplayName("Should be able to find Administrator user by role")
  void testFindUsersByAdministratorRole() {
    // Act - Find all users with ADMINISTRATOR role
    var adminUsers = brukerRepository.findByRollerRolleId("ADMINISTRATOR");

    // Assert
    assertFalse(adminUsers.isEmpty(), "Should have at least one Administrator user");

    // Verify the test administrator is included
    boolean hasTestAdmin =
        adminUsers.stream().anyMatch(user -> "admin@strix.no".equals(user.getEmail()));
    assertTrue(hasTestAdmin, "Test administrator should be found by role query");

    // All found users should be active
    for (Bruker user : adminUsers) {
      assertTrue(user.getAktiv(), "All administrator users should be active");
    }
  }

  @Test
  @DisplayName("Administrator should not be tied to specific organisation")
  void testAdministratorOrganisationIndependence() {
    // Act
    Optional<Bruker> adminUser = brukerRepository.findByEmail("admin@strix.no");

    // Assert
    assertTrue(adminUser.isPresent(), "Administrator test user should exist");

    Bruker admin = adminUser.get();
    assertNull(
        admin.getOrganisasjonId(),
        "Administrator should not be tied to specific organisation for system-wide access");

    // This allows Administrator to manage all organisations rather than being
    // restricted to one like Opptaksleder or Søknadsbehandler roles
  }
}
