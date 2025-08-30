package no.utdanning.opptak.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.StudieForm;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.OppdaterUtdanningInput;
import no.utdanning.opptak.graphql.dto.OpprettUtdanningInput;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import no.utdanning.opptak.repository.UtdanningRepository;
import no.utdanning.opptak.service.UtdanningSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UtdanningMutationResolver - Sikkerhetstester")
class UtdanningMutationResolverSecurityTest {

  @Mock private UtdanningRepository utdanningRepository;
  @Mock private OrganisasjonRepository organisasjonRepository;
  @Mock private UtdanningSecurityService securityService;

  @InjectMocks private UtdanningMutationResolver mutationResolver;

  private OpprettUtdanningInput validOpprettInput;
  private OppdaterUtdanningInput validOppdaterInput;
  private Organisasjon testOrganisasjon;
  private Utdanning testUtdanning;

  @BeforeEach
  void setUp() {
    // Test organisasjon
    testOrganisasjon = new Organisasjon();
    testOrganisasjon.setId("ORG-NTNU-001");
    testOrganisasjon.setNavn("NTNU");

    // Test utdanning
    testUtdanning = new Utdanning();
    testUtdanning.setId("UTD-TEST-123");
    testUtdanning.setNavn("Datateknologi");
    testUtdanning.setStudienivaa("Bachelor");
    testUtdanning.setStudiepoeng(180);
    testUtdanning.setVarighet(3);
    testUtdanning.setStudiested("Trondheim");
    testUtdanning.setUndervisningssprak("Norsk");
    testUtdanning.setStarttidspunkt("Høst");
    testUtdanning.setStudieform(StudieForm.CAMPUS);
    testUtdanning.setOrganisasjonId("ORG-NTNU-001");
    testUtdanning.setAktiv(true);
    testUtdanning.setOpprettet(LocalDateTime.now());

    // Valid opprett input
    validOpprettInput = new OpprettUtdanningInput();
    validOpprettInput.setNavn("Ny Utdanning");
    validOpprettInput.setStudienivaa("Bachelor");
    validOpprettInput.setStudiepoeng(180);
    validOpprettInput.setVarighet(3);
    validOpprettInput.setStudiested("Trondheim");
    validOpprettInput.setUndervisningssprak("Norsk");
    validOpprettInput.setStarttidspunkt("Høst");
    validOpprettInput.setStudieform(StudieForm.CAMPUS);
    validOpprettInput.setOrganisasjonId("ORG-NTNU-001");

    // Valid oppdater input
    validOppdaterInput = new OppdaterUtdanningInput();
    validOppdaterInput.setId("UTD-TEST-123");
    validOppdaterInput.setNavn("Oppdatert Utdanning");
  }

  // ==================== OPPRETT UTDANNING SIKKERHET ====================

  @Test
  @DisplayName("opprettUtdanning: Administrator skal kunne opprette utdanning for alle organisasjoner")
  void opprettUtdanning_administratorSkalKunneOppretteForAlleOrganisasjoner() {
    // Given
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When
    Utdanning result = mutationResolver.opprettUtdanning(validOpprettInput);

    // Then
    assertNotNull(result);
    verify(securityService).canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId());
    verify(organisasjonRepository).findById(validOpprettInput.getOrganisasjonId());
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: OPPTAKSLEDER skal kun kunne opprette for egen organisasjon")
  void opprettUtdanning_opptakslederSkalKunKunneOppretteForEgenOrganisasjon() {
    // Given
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When
    Utdanning result = mutationResolver.opprettUtdanning(validOpprettInput);

    // Then
    assertNotNull(result);
    verify(securityService).canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal blokke forsøk på å opprette for annen organisasjon")
  void opprettUtdanning_skalBlokkereForsokPaAOppretteForAnnenOrganisasjon() {
    // Given
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(false);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Du har ikke tilgang til å opprette utdanninger for denne organisasjonen", 
                 exception.getMessage());
    verify(securityService).canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId());
    verify(organisasjonRepository, never()).findById(anyString());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal validere at organisasjon eksisterer")
  void opprettUtdanning_skalValidereAtOrganisasjonEksisterer() {
    // Given
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Organisasjon ikke funnet: " + validOpprettInput.getOrganisasjonId(), 
                 exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  // ==================== INPUT VALIDATION SIKKERHET ====================

  @Test
  @DisplayName("opprettUtdanning: Skal blokke tom/null navn")
  void opprettUtdanning_skalBlokkereTomNavn() {
    // Given
    validOpprettInput.setNavn(null);
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Utdanningsnavn er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal blokke negativt studiepoeng")
  void opprettUtdanning_skalBlokkreNegativtStudiepoeng() {
    // Given
    validOpprettInput.setStudiepoeng(-10);
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Studiepoeng må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal blokke null studiepoeng")
  void opprettUtdanning_skalBlokkreNullStudiepoeng() {
    // Given
    validOpprettInput.setStudiepoeng(null);
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Studiepoeng må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal blokke negativ varighet")
  void opprettUtdanning_skalBlokereNegativVarighet() {
    // Given
    validOpprettInput.setVarighet(-1);
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));

    assertEquals("Varighet må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Skal blokke alle påkrevde felter som null")
  void opprettUtdanning_skalBlokkeAllePakrevdeFelterSomNull() {
    // Given
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);

    String[] requiredFields = {
        "Studienivå er påkrevd",
        "Studiested er påkrevd", 
        "Undervisningsspråk er påkrevd",
        "Starttidspunkt er påkrevd",
        "Studieform er påkrevd"
    };

    // Test studienivaa
    validOpprettInput.setStudienivaa(null);
    RuntimeException exception1 = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    assertEquals(requiredFields[0], exception1.getMessage());

    validOpprettInput.setStudienivaa("Bachelor"); // Reset

    // Test studiested
    validOpprettInput.setStudiested(null);
    RuntimeException exception2 = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    assertEquals(requiredFields[1], exception2.getMessage());

    validOpprettInput.setStudiested("Trondheim"); // Reset

    // Test undervisningssprak
    validOpprettInput.setUndervisningssprak(null);
    RuntimeException exception3 = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    assertEquals(requiredFields[2], exception3.getMessage());

    validOpprettInput.setUndervisningssprak("Norsk"); // Reset

    // Test starttidspunkt
    validOpprettInput.setStarttidspunkt(null);
    RuntimeException exception4 = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    assertEquals(requiredFields[3], exception4.getMessage());

    validOpprettInput.setStarttidspunkt("Høst"); // Reset

    // Test studieform
    validOpprettInput.setStudieform(null);
    RuntimeException exception5 = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    assertEquals(requiredFields[4], exception5.getMessage());

    verify(utdanningRepository, never()).save(any());
  }

  // ==================== OPPDATER UTDANNING SIKKERHET ====================

  @Test
  @DisplayName("oppdaterUtdanning: Skal tillate oppdatering med gyldig tilgang")
  void oppdaterUtdanning_skalTillateOppdateringMedGyldigTilgang() {
    // Given
    when(utdanningRepository.findById(validOppdaterInput.getId())).thenReturn(testUtdanning);
    when(securityService.hasAccessToUtdanning(validOppdaterInput.getId())).thenReturn(true);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When
    Utdanning result = mutationResolver.oppdaterUtdanning(validOppdaterInput);

    // Then
    assertNotNull(result);
    verify(securityService).hasAccessToUtdanning(validOppdaterInput.getId());
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("oppdaterUtdanning: Skal blokke oppdatering uten tilgang")
  void oppdaterUtdanning_skalBlokereOppdateringUtenTilgang() {
    // Given
    when(utdanningRepository.findById(validOppdaterInput.getId())).thenReturn(testUtdanning);
    when(securityService.hasAccessToUtdanning(validOppdaterInput.getId())).thenReturn(false);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.oppdaterUtdanning(validOppdaterInput));

    assertEquals("Du har ikke tilgang til å oppdatere denne utdanningen", exception.getMessage());
    verify(securityService).hasAccessToUtdanning(validOppdaterInput.getId());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("oppdaterUtdanning: Skal blokke oppdatering av ikke-eksisterende utdanning")
  void oppdaterUtdanning_skalBlokereOppdateringAvIkkeEksisterendeUtdanning() {
    // Given
    when(utdanningRepository.findById(validOppdaterInput.getId())).thenReturn(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.oppdaterUtdanning(validOppdaterInput));

    assertEquals("Utdanning ikke funnet: " + validOppdaterInput.getId(), exception.getMessage());
    verify(securityService, never()).hasAccessToUtdanning(anyString());
    verify(utdanningRepository, never()).save(any());
  }

  // ==================== DEAKTIVER/AKTIVER SIKKERHET ====================

  @Test
  @DisplayName("deaktiverUtdanning: Skal tillate deaktivering med gyldig tilgang")
  void deaktiverUtdanning_skalTillateDeaktiveringMedGyldigTilgang() {
    // Given
    String utdanningId = "UTD-TEST-123";
    when(securityService.hasAccessToUtdanning(utdanningId)).thenReturn(true);
    when(utdanningRepository.findById(utdanningId)).thenReturn(testUtdanning);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When
    Utdanning result = mutationResolver.deaktiverUtdanning(utdanningId);

    // Then
    assertNotNull(result);
    verify(securityService).hasAccessToUtdanning(utdanningId);
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("deaktiverUtdanning: Skal blokke deaktivering uten tilgang")
  void deaktiverUtdanning_skalBlokereDeaktiveringUtenTilgang() {
    // Given
    String utdanningId = "UTD-TEST-123";
    when(securityService.hasAccessToUtdanning(utdanningId)).thenReturn(false);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.deaktiverUtdanning(utdanningId));

    assertEquals("Du har ikke tilgang til å deaktivere denne utdanningen", exception.getMessage());
    verify(securityService).hasAccessToUtdanning(utdanningId);
    verify(utdanningRepository, never()).findById(anyString());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("aktiverUtdanning: Skal tillate aktivering med gyldig tilgang")
  void aktiverUtdanning_skalTillateAktiveringMedGyldigTilgang() {
    // Given
    String utdanningId = "UTD-TEST-123";
    testUtdanning.setAktiv(false); // Start deactivated
    when(securityService.hasAccessToUtdanning(utdanningId)).thenReturn(true);
    when(utdanningRepository.findById(utdanningId)).thenReturn(testUtdanning);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When
    Utdanning result = mutationResolver.aktiverUtdanning(utdanningId);

    // Then
    assertNotNull(result);
    verify(securityService).hasAccessToUtdanning(utdanningId);
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("aktiverUtdanning: Skal blokke aktivering uten tilgang")
  void aktiverUtdanning_skalBlokereAktiveringUtenTilgang() {
    // Given
    String utdanningId = "UTD-TEST-123";
    when(securityService.hasAccessToUtdanning(utdanningId)).thenReturn(false);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.aktiverUtdanning(utdanningId));

    assertEquals("Du har ikke tilgang til å aktivere denne utdanningen", exception.getMessage());
    verify(securityService).hasAccessToUtdanning(utdanningId);
    verify(utdanningRepository, never()).findById(anyString());
    verify(utdanningRepository, never()).save(any());
  }

  // ==================== SLETT UTDANNING SIKKERHET ====================

  @Test
  @DisplayName("slettUtdanning: Skal tillate sletting for eksisterende utdanning")
  void slettUtdanning_skalTillateSlettingForEksisterendeUtdanning() {
    // Given
    String utdanningId = "UTD-TEST-123";
    when(utdanningRepository.existsById(utdanningId)).thenReturn(true);
    when(utdanningRepository.deleteById(utdanningId)).thenReturn(true);

    // When
    Boolean result = mutationResolver.slettUtdanning(utdanningId);

    // Then
    assertTrue(result);
    verify(utdanningRepository).existsById(utdanningId);
    verify(utdanningRepository).deleteById(utdanningId);
  }

  @Test
  @DisplayName("slettUtdanning: Skal blokke sletting av ikke-eksisterende utdanning")
  void slettUtdanning_skalBlokereSlettingAvIkkeEksisterendeUtdanning() {
    // Given
    String utdanningId = "UTD-FINNES-IKKE";
    when(utdanningRepository.existsById(utdanningId)).thenReturn(false);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.slettUtdanning(utdanningId));

    assertEquals("Utdanning ikke funnet: " + utdanningId, exception.getMessage());
    verify(utdanningRepository).existsById(utdanningId);
    verify(utdanningRepository, never()).deleteById(anyString());
  }

  // ==================== EDGE CASES OG BOUNDARY TESTING ====================

  @Test
  @DisplayName("opprettUtdanning: Skal trimme tekst-input for å unngå whitespace attacks")
  void opprettUtdanning_skalTrimmeTextInput() {
    // Given
    validOpprettInput.setNavn("  Spacey Utdanning  ");
    validOpprettInput.setStudienivaa("  Bachelor  ");
    validOpprettInput.setStudiested("  Trondheim  ");
    
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      assertEquals("Spacey Utdanning", saved.getNavn(), "Navn skal være trimmet");
      assertEquals("Bachelor", saved.getStudienivaa(), "Studienivå skal være trimmet");
      assertEquals("Trondheim", saved.getStudiested(), "Studiested skal være trimmet");
      return saved;
    });

    // When
    mutationResolver.opprettUtdanning(validOpprettInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere maksimalt lange strenger")
  void opprettUtdanning_skalHandtereMaksimaltLangeStrenger() {
    // Given
    String longString = "A".repeat(1000); // Very long string
    validOpprettInput.setNavn(longString);
    
    when(securityService.canCreateUtdanningForOrganisasjon(validOpprettInput.getOrganisasjonId())).thenReturn(true);
    when(organisasjonRepository.findById(validOpprettInput.getOrganisasjonId())).thenReturn(testOrganisasjon);
    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When & Then
    // Should not throw exception for long strings (database constraints will handle this)
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("oppdaterUtdanning: Skal kun oppdatere felter som er satt (partial update)")
  void oppdaterUtdanning_skalKunOppdatereFelterSomErSatt() {
    // Given
    OppdaterUtdanningInput partialInput = new OppdaterUtdanningInput();
    partialInput.setId("UTD-TEST-123");
    partialInput.setNavn("Nytt navn"); // Only set name
    // All other fields are null

    when(utdanningRepository.findById(partialInput.getId())).thenReturn(testUtdanning);
    when(securityService.hasAccessToUtdanning(partialInput.getId())).thenReturn(true);
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning updated = invocation.getArgument(0);
      assertEquals("Nytt navn", updated.getNavn(), "Navn skal være oppdatert");
      assertEquals("Bachelor", updated.getStudienivaa(), "Studienivå skal være uendret");
      assertEquals(Integer.valueOf(180), updated.getStudiepoeng(), "Studiepoeng skal være uendret");
      return updated;
    });

    // When
    mutationResolver.oppdaterUtdanning(partialInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }
}