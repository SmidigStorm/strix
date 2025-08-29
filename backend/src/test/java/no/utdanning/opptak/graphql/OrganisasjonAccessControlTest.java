package no.utdanning.opptak.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.graphql.dto.OpprettOrganisasjonInput;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Organisation Access Control Tests")
class OrganisasjonAccessControlTest {

  @Mock private OrganisasjonRepository organisasjonRepository;

  @InjectMocks private OrganisasjonMutationResolver organisasjonMutationResolver;

  @InjectMocks private OrganisasjonQueryResolver organisasjonQueryResolver;

  private Organisasjon testOrganisasjon;
  private OpprettOrganisasjonInput validInput;

  @BeforeEach
  void setUp() {
    // Set up test data
    testOrganisasjon = new Organisasjon();
    testOrganisasjon.setId("ORG-TEST-123");
    testOrganisasjon.setNavn("Test Universitet");
    testOrganisasjon.setKortNavn("TU");
    testOrganisasjon.setOrganisasjonsnummer("123456789");
    testOrganisasjon.setType(OrganisasjonsType.UNIVERSITET);
    testOrganisasjon.setEpost("kontakt@test.no");
    testOrganisasjon.setTelefon("12345678");
    testOrganisasjon.setAktiv(true);

    validInput = new OpprettOrganisasjonInput();
    validInput.setNavn("Ny Test Organisasjon");
    validInput.setKortNavn("NTO");
    validInput.setOrganisasjonsnummer("987654321");
    validInput.setOrganisasjonstype(OrganisasjonsType.HOGSKOLE);
    validInput.setEpost("test@ny.no");
    validInput.setTelefon("87654321");
  }

  @Test
  @DisplayName("Should create organisation with valid input")
  void testOpprettOrganisasjon_ValidInput() {
    // Arrange
    when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(false);
    when(organisasjonRepository.save(any(Organisasjon.class))).thenReturn(testOrganisasjon);

    // Act
    Organisasjon result = organisasjonMutationResolver.opprettOrganisasjon(validInput);

    // Assert
    assertNotNull(result, "Result should not be null");
    verify(organisasjonRepository).existsByOrganisasjonsnummer(validInput.getOrganisasjonsnummer());
    verify(organisasjonRepository).save(any(Organisasjon.class));
  }

  @Test
  @DisplayName("Should throw exception when organisation number already exists")
  void testOpprettOrganisasjon_DuplicateOrgNumber() {
    // Arrange
    when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(true);

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> organisasjonMutationResolver.opprettOrganisasjon(validInput));

    assertTrue(
        exception.getMessage().contains("Organisasjonsnummer er allerede registrert"),
        "Exception message should indicate duplicate organisation number");
    verify(organisasjonRepository).existsByOrganisasjonsnummer(validInput.getOrganisasjonsnummer());
    verify(organisasjonRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception for invalid organisation number format")
  void testOpprettOrganisasjon_InvalidOrgNumberFormat() {
    // Arrange
    validInput.setOrganisasjonsnummer("12345"); // Too short
    when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(false);

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> organisasjonMutationResolver.opprettOrganisasjon(validInput));

    assertTrue(
        exception.getMessage().contains("Ugyldig organisasjonsnummer"),
        "Exception message should indicate invalid format");
    verify(organisasjonRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should validate organisation number has exactly 9 digits")
  void testOpprettOrganisasjon_OrgNumberValidation() {
    // Test various invalid formats
    String[] invalidNumbers = {"12345678", "1234567890", "abc123456", "123-456-789", ""};

    for (String invalidNumber : invalidNumbers) {
      // Arrange
      validInput.setOrganisasjonsnummer(invalidNumber);
      when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(false);

      // Act & Assert
      RuntimeException exception =
          assertThrows(
              RuntimeException.class,
              () -> organisasjonMutationResolver.opprettOrganisasjon(validInput),
              "Should reject invalid org number: " + invalidNumber);

      assertTrue(
          exception.getMessage().contains("Ugyldig organisasjonsnummer"),
          "Exception message should indicate invalid format for: " + invalidNumber);
    }

    verify(organisasjonRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should deactivate organisation instead of deleting")
  void testDeaktiverOrganisasjon() {
    // Arrange
    String orgId = "ORG-TEST-123";
    when(organisasjonRepository.findById(orgId)).thenReturn(testOrganisasjon);
    when(organisasjonRepository.save(any(Organisasjon.class))).thenReturn(testOrganisasjon);

    // Act
    Organisasjon result = organisasjonMutationResolver.deaktiverOrganisasjon(orgId);

    // Assert
    assertNotNull(result, "Result should not be null");
    verify(organisasjonRepository).findById(orgId);
    verify(organisasjonRepository)
        .save(
            argThat(
                org -> {
                  assertFalse(org.getAktiv(), "Organisation should be deactivated");
                  return true;
                }));
  }

  @Test
  @DisplayName("Should reactivate deactivated organisation")
  void testReaktiverOrganisasjon() {
    // Arrange
    String orgId = "ORG-TEST-123";
    testOrganisasjon.setAktiv(false); // Start with inactive organisation
    when(organisasjonRepository.findById(orgId)).thenReturn(testOrganisasjon);
    when(organisasjonRepository.save(any(Organisasjon.class))).thenReturn(testOrganisasjon);

    // Act
    Organisasjon result = organisasjonMutationResolver.reaktiverOrganisasjon(orgId);

    // Assert
    assertNotNull(result, "Result should not be null");
    verify(organisasjonRepository).findById(orgId);
    verify(organisasjonRepository)
        .save(
            argThat(
                org -> {
                  assertTrue(org.getAktiv(), "Organisation should be reactivated");
                  return true;
                }));
  }

  @Test
  @DisplayName("Should throw exception when trying to modify non-existent organisation")
  void testDeaktiverOrganisasjon_NotFound() {
    // Arrange
    String nonExistentId = "ORG-NOT-FOUND";
    when(organisasjonRepository.findById(nonExistentId)).thenReturn(null);

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> organisasjonMutationResolver.deaktiverOrganisasjon(nonExistentId));

    assertTrue(
        exception.getMessage().contains("Organisasjon ikke funnet"),
        "Exception message should indicate organisation not found");
    verify(organisasjonRepository).findById(nonExistentId);
    verify(organisasjonRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should get all organisations regardless of role (query resolver)")
  void testGetOrganisasjoner() {
    // Arrange
    Organisasjon org1 = new Organisasjon();
    org1.setId("ORG-1");
    org1.setNavn("Organisasjon 1");
    org1.setAktiv(true);

    Organisasjon org2 = new Organisasjon();
    org2.setId("ORG-2");
    org2.setNavn("Organisasjon 2");
    org2.setAktiv(false);

    when(organisasjonRepository.findAll()).thenReturn(Arrays.asList(org1, org2));

    // Act
    var result = organisasjonQueryResolver.organisasjoner(null);

    // Assert
    assertNotNull(result, "Result should not be null");
    assertEquals(2, result.size(), "Should return all organisations");
    verify(organisasjonRepository).findAll();
  }

  @Test
  @DisplayName("Should preserve kort navn when provided")
  void testOpprettOrganisasjon_WithKortNavn() {
    // Arrange
    validInput.setKortNavn("KORT");
    when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(false);
    when(organisasjonRepository.save(any(Organisasjon.class)))
        .thenAnswer(
            invocation -> {
              Organisasjon saved = invocation.getArgument(0);
              assertEquals("KORT", saved.getKortNavn(), "KortNavn should be preserved");
              return saved;
            });

    // Act
    organisasjonMutationResolver.opprettOrganisasjon(validInput);

    // Assert
    verify(organisasjonRepository).save(any(Organisasjon.class));
  }

  @Test
  @DisplayName("Should handle null kort navn gracefully")
  void testOpprettOrganisasjon_NullKortNavn() {
    // Arrange
    validInput.setKortNavn(null);
    when(organisasjonRepository.existsByOrganisasjonsnummer(anyString())).thenReturn(false);
    when(organisasjonRepository.save(any(Organisasjon.class)))
        .thenAnswer(
            invocation -> {
              Organisasjon saved = invocation.getArgument(0);
              // Should not throw exception with null kortNavn
              return saved;
            });

    // Act & Assert
    assertDoesNotThrow(() -> organisasjonMutationResolver.opprettOrganisasjon(validInput));
    verify(organisasjonRepository).save(any(Organisasjon.class));
  }
}