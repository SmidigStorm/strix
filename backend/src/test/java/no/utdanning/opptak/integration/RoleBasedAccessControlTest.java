package no.utdanning.opptak.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import no.utdanning.opptak.domain.Rolle;
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
@DisplayName("Role-Based Access Control Tests")
class RoleBasedAccessControlTest {

  @Autowired private RolleRepository rolleRepository;

  @Test
  @DisplayName("Should have all required roles in database")
  void testAllRolesExist() {
    // Act
    List<Rolle> allRoles = rolleRepository.findAll();

    // Assert
    assertEquals(
        4,
        allRoles.size(),
        "Should have 4 roles: ADMINISTRATOR, OPPTAKSLEDER, SOKNADSBEHANDLER, SOKER");

    // Verify specific roles exist
    assertTrue(
        allRoles.stream().anyMatch(r -> "ADMINISTRATOR".equals(r.getId())),
        "ADMINISTRATOR role should exist");
    assertTrue(
        allRoles.stream().anyMatch(r -> "OPPTAKSLEDER".equals(r.getId())),
        "OPPTAKSLEDER role should exist");
    assertTrue(
        allRoles.stream().anyMatch(r -> "SOKNADSBEHANDLER".equals(r.getId())),
        "SOKNADSBEHANDLER role should exist");
    assertTrue(
        allRoles.stream().anyMatch(r -> "SOKER".equals(r.getId())), "SOKER role should exist");
  }

  @Test
  @DisplayName("Administrator role should have correct description")
  void testAdministratorRoleDescription() {
    // Act
    Rolle adminRole = rolleRepository.findById("ADMINISTRATOR").orElse(null);

    // Assert
    assertNotNull(adminRole, "Administrator role should exist");
    assertEquals("Administrator", adminRole.getNavn(), "Role name should be 'Administrator'");
    assertEquals(
        "Systemadministrasjon og overordnet styring av hele opptakssystemet",
        adminRole.getBeskrivelse(),
        "Role description should match requirements");
  }

  @Test
  @DisplayName("All roles should have Norwegian descriptions")
  void testAllRolesHaveNorwegianDescriptions() {
    // Act
    List<Rolle> allRoles = rolleRepository.findAll();

    // Assert
    for (Rolle role : allRoles) {
      assertNotNull(role.getBeskrivelse(), "Role " + role.getId() + " should have a description");
      assertFalse(
          role.getBeskrivelse().trim().isEmpty(),
          "Role " + role.getId() + " should have a non-empty description");

      // Verify specific descriptions match requirements
      switch (role.getId()) {
        case "ADMINISTRATOR":
          assertEquals(
              "Systemadministrasjon og overordnet styring av hele opptakssystemet",
              role.getBeskrivelse());
          break;
        case "OPPTAKSLEDER":
          assertEquals(
              "Kan administrere opptak og utdanninger for sin organisasjon", role.getBeskrivelse());
          break;
        case "SOKNADSBEHANDLER":
          assertEquals("Kan behandle søknader og tildele plasser", role.getBeskrivelse());
          break;
        case "SOKER":
          assertEquals("Kan søke på utdanninger og følge opp egen søknad", role.getBeskrivelse());
          break;
        default:
          fail("Unknown role: " + role.getId());
      }
    }
  }

  @Test
  @DisplayName("Should be able to find role by ID")
  void testFindRoleById() {
    // Arrange & Act
    Rolle opptakslederRole = rolleRepository.findById("OPPTAKSLEDER").orElse(null);
    Rolle nonExistentRole = rolleRepository.findById("NON_EXISTENT").orElse(null);

    // Assert
    assertNotNull(opptakslederRole, "Opptaksleder role should be found");
    assertEquals("OPPTAKSLEDER", opptakslederRole.getId());
    assertEquals("Opptaksleder", opptakslederRole.getNavn());

    assertNull(nonExistentRole, "Non-existent role should return null");
  }

  @Test
  @DisplayName("Role names should be properly capitalized")
  void testRoleNamesCapitalization() {
    // Act
    List<Rolle> allRoles = rolleRepository.findAll();

    // Assert - Check that role names are properly formatted
    for (Rolle role : allRoles) {
      assertNotNull(role.getNavn(), "Role name should not be null");
      assertFalse(role.getNavn().trim().isEmpty(), "Role name should not be empty");

      // Verify specific role name formatting
      switch (role.getId()) {
        case "ADMINISTRATOR":
          assertEquals("Administrator", role.getNavn());
          break;
        case "OPPTAKSLEDER":
          assertEquals("Opptaksleder", role.getNavn());
          break;
        case "SOKNADSBEHANDLER":
          assertEquals("Søknadsbehandler", role.getNavn());
          break;
        case "SOKER":
          assertEquals("Søker", role.getNavn());
          break;
        default:
          fail("Unknown role ID: " + role.getId());
      }
    }
  }
}
