package no.utdanning.opptak.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Utdanning Input Validation Tests - Sikkerhet og dataintegritet")
class UtdanningInputValidationTest {

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
    // Setup valid organisasjon
    testOrganisasjon = new Organisasjon();
    testOrganisasjon.setId("ORG-NTNU-001");
    testOrganisasjon.setNavn("NTNU");

    // Setup valid utdanning
    testUtdanning = new Utdanning();
    testUtdanning.setId("UTD-TEST-123");
    testUtdanning.setNavn("Eksisterende Utdanning");

    // Setup valid opprett input
    validOpprettInput = new OpprettUtdanningInput();
    validOpprettInput.setNavn("Datateknologi");
    validOpprettInput.setStudienivaa("Bachelor");
    validOpprettInput.setStudiepoeng(180);
    validOpprettInput.setVarighet(3);
    validOpprettInput.setStudiested("Trondheim");
    validOpprettInput.setUndervisningssprak("Norsk");
    validOpprettInput.setBeskrivelse("Bachelor i datateknologi");
    validOpprettInput.setStarttidspunkt("Høst");
    validOpprettInput.setStudieform(StudieForm.CAMPUS);
    validOpprettInput.setOrganisasjonId("ORG-NTNU-001");

    // Setup valid oppdater input
    validOppdaterInput = new OppdaterUtdanningInput();
    validOppdaterInput.setId("UTD-TEST-123");
    validOppdaterInput.setNavn("Oppdatert Navn");

    // Default mocks for successful scenarios
    when(securityService.canCreateUtdanningForOrganisasjon(anyString())).thenReturn(true);
    when(organisasjonRepository.findById(anyString())).thenReturn(testOrganisasjon);
    when(utdanningRepository.findById(anyString())).thenReturn(testUtdanning);
    when(securityService.hasAccessToUtdanning(anyString())).thenReturn(true);
  }

  // ==================== OPPRETT UTDANNING INPUT VALIDATION ====================

  @Test
  @DisplayName("opprettUtdanning: Navn kan ikke være null")
  void opprettUtdanning_navnKanIkkeVaereNull() {
    // Given
    validOpprettInput.setNavn(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Utdanningsnavn er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Navn kan ikke være tom streng")
  void opprettUtdanning_navnKanIkkeVaereTomStreng() {
    // Given
    validOpprettInput.setNavn("");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Utdanningsnavn er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Navn kan ikke være kun whitespace")
  void opprettUtdanning_navnKanIkkeVaereKunWhitespace() {
    // Given
    validOpprettInput.setNavn("   ");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Utdanningsnavn er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studienivå kan ikke være null")
  void opprettUtdanning_studienivaaKanIkkeVaereNull() {
    // Given
    validOpprettInput.setStudienivaa(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studienivå er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studienivå kan ikke være tom streng")
  void opprettUtdanning_studienivaaKanIkkeVaereTomStreng() {
    // Given
    validOpprettInput.setStudienivaa("");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studienivå er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, -100, -999})
  @DisplayName("opprettUtdanning: Studiepoeng må være positivt tall")
  void opprettUtdanning_studiepongMaVaerePositivtTall(int ugyldigStudiepoeng) {
    // Given
    validOpprettInput.setStudiepoeng(ugyldigStudiepoeng);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studiepoeng må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studiepoeng kan ikke være null")
  void opprettUtdanning_studiepoengKanIkkeVaereNull() {
    // Given
    validOpprettInput.setStudiepoeng(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studiepoeng må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, -5, -10})
  @DisplayName("opprettUtdanning: Varighet må være positivt tall")
  void opprettUtdanning_varighetMaVaerePositivtTall(int ugyldigVarighet) {
    // Given
    validOpprettInput.setVarighet(ugyldigVarighet);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Varighet må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Varighet kan ikke være null")
  void opprettUtdanning_varighetKanIkkeVaereNull() {
    // Given
    validOpprettInput.setVarighet(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Varighet må være et positivt tall", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studiested kan ikke være null")
  void opprettUtdanning_studiestedKanIkkeVaereNull() {
    // Given
    validOpprettInput.setStudiested(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studiested er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studiested kan ikke være tom streng")
  void opprettUtdanning_studiestedKanIkkeVaereTomStreng() {
    // Given
    validOpprettInput.setStudiested("");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studiested er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Undervisningsspråk kan ikke være null")
  void opprettUtdanning_undervisningssprakKanIkkeVaereNull() {
    // Given
    validOpprettInput.setUndervisningssprak(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Undervisningsspråk er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Undervisningsspråk kan ikke være tom streng")
  void opprettUtdanning_undervisningssprakKanIkkeVaereTomStreng() {
    // Given
    validOpprettInput.setUndervisningssprak("");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Undervisningsspråk er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Starttidspunkt kan ikke være null")
  void opprettUtdanning_starttidspunktKanIkkeVaereNull() {
    // Given
    validOpprettInput.setStarttidspunkt(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Starttidspunkt er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Starttidspunkt kan ikke være tom streng")
  void opprettUtdanning_starttidspunktKanIkkeVaereTomStreng() {
    // Given
    validOpprettInput.setStarttidspunkt("");

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Starttidspunkt er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  @Test
  @DisplayName("opprettUtdanning: Studieform kan ikke være null")
  void opprettUtdanning_studieformKanIkkeVaereNull() {
    // Given
    validOpprettInput.setStudieform(null);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> 
        mutationResolver.opprettUtdanning(validOpprettInput));
    
    assertEquals("Studieform er påkrevd", exception.getMessage());
    verify(utdanningRepository, never()).save(any());
  }

  // ==================== XSS OG INJECTION PROTECTION ====================

  @Test
  @DisplayName("opprettUtdanning: Skal trimme input for å fjerne whitespace attacks")
  void opprettUtdanning_skalTrimmeInputForAFjerneWhitespaceAttacks() {
    // Given
    validOpprettInput.setNavn("  Datateknologi  ");
    validOpprettInput.setStudienivaa("  Bachelor  ");
    validOpprettInput.setStudiested("  Trondheim  ");
    validOpprettInput.setUndervisningssprak("  Norsk  ");
    validOpprettInput.setStarttidspunkt("  Høst  ");
    
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      assertEquals("Datateknologi", saved.getNavn(), "Navn skal være trimmet");
      assertEquals("Bachelor", saved.getStudienivaa(), "Studienivå skal være trimmet");
      assertEquals("Trondheim", saved.getStudiested(), "Studiested skal være trimmet");
      assertEquals("Norsk", saved.getUndervisningssprak(), "Undervisningsspråk skal være trimmet");
      assertEquals("Høst", saved.getStarttidspunkt(), "Starttidspunkt skal være trimmet");
      return saved;
    });

    // When
    mutationResolver.opprettUtdanning(validOpprettInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere potensielle XSS-angrep i tekst-felter")
  void opprettUtdanning_skalHandterePotensielleXssAngrepITekstFelter() {
    // Given - XSS payload
    String xssPayload = "<script>alert('xss')</script>";
    validOpprettInput.setNavn("Utdanning " + xssPayload);
    validOpprettInput.setBeskrivelse("Beskrivelse " + xssPayload);

    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      // Input should be stored as-is (escaping happens at presentation layer)
      assertTrue(saved.getNavn().contains(xssPayload));
      assertTrue(saved.getBeskrivelse().contains(xssPayload));
      return saved;
    });

    // When - should not crash or fail validation
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere SQL injection forsøk i tekst-felter")
  void opprettUtdanning_skalHandtereSqlInjectionForsokITekstFelter() {
    // Given - SQL injection payload
    String sqlPayload = "'; DROP TABLE utdanning; --";
    validOpprettInput.setNavn("Utdanning" + sqlPayload);
    validOpprettInput.setStudiested("Trondheim" + sqlPayload);

    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      // Should be stored as-is (parameterized queries prevent SQL injection)
      assertTrue(saved.getNavn().contains(sqlPayload));
      assertTrue(saved.getStudiested().contains(sqlPayload));
      return saved;
    });

    // When - should not cause SQL injection
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  // ==================== BOUNDARY TESTING ====================

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere maksimalt store tall for studiepoeng")
  void opprettUtdanning_skalHandtereMaksimaltStoreTallForStudiepoeng() {
    // Given
    validOpprettInput.setStudiepoeng(Integer.MAX_VALUE);

    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When - should not throw exception for large numbers
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere maksimalt store tall for varighet")
  void opprettUtdanning_skalHandtereMaksimaltStoreTallForVarighet() {
    // Given
    validOpprettInput.setVarighet(Integer.MAX_VALUE);

    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When - should not throw exception for large numbers
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere ekstremt lange strenger")
  void opprettUtdanning_skalHandtereEkstremtLangeStrenger() {
    // Given
    String veryLongString = "A".repeat(10000);
    validOpprettInput.setNavn(veryLongString);
    validOpprettInput.setBeskrivelse(veryLongString);

    when(utdanningRepository.save(any(Utdanning.class))).thenReturn(testUtdanning);

    // When - should not throw validation exception (database constraints may apply)
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  // ==================== OPPDATER UTDANNING VALIDATION ====================

  @Test
  @DisplayName("oppdaterUtdanning: Skal kun oppdatere felter som ikke er null/tomme")
  void oppdaterUtdanning_skalKunOppdatereFelterSomIkkeErNullTomme() {
    // Given - partial input med kun noen felter satt
    OppdaterUtdanningInput partialInput = new OppdaterUtdanningInput();
    partialInput.setId("UTD-TEST-123");
    partialInput.setNavn("Oppdatert navn");
    partialInput.setStudiepoeng(240); // Valid positive number
    partialInput.setVarighet(null); // This should not update
    partialInput.setStudiested(""); // This should not update (empty)
    partialInput.setUndervisningssprak("   "); // This should not update (whitespace)

    Utdanning existing = new Utdanning();
    existing.setId("UTD-TEST-123");
    existing.setNavn("Gammelt navn");
    existing.setStudiepoeng(180);
    existing.setVarighet(3);
    existing.setStudiested("Gammelt studiested");
    existing.setUndervisningssprak("Gammelt språk");

    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(existing);
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning updated = invocation.getArgument(0);
      assertEquals("Oppdatert navn", updated.getNavn(), "Navn skal være oppdatert");
      assertEquals(Integer.valueOf(240), updated.getStudiepoeng(), "Studiepoeng skal være oppdatert");
      assertEquals(Integer.valueOf(3), updated.getVarighet(), "Varighet skal være uendret");
      assertEquals("Gammelt studiested", updated.getStudiested(), "Studiested skal være uendret");
      assertEquals("Gammelt språk", updated.getUndervisningssprak(), "Undervisningsspråk skal være uendret");
      return updated;
    });

    // When
    mutationResolver.oppdaterUtdanning(partialInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("oppdaterUtdanning: Skal ikke oppdatere med negative studiepoeng")
  void oppdaterUtdanning_skalIkkeOppdatereMedNegativeStudiepoeng() {
    // Given
    validOppdaterInput.setStudiepoeng(-100);

    Utdanning existing = new Utdanning();
    existing.setId("UTD-TEST-123");
    existing.setStudiepoeng(180);

    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(existing);
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning updated = invocation.getArgument(0);
      assertEquals(Integer.valueOf(180), updated.getStudiepoeng(), "Studiepoeng skal ikke oppdateres med negativ verdi");
      return updated;
    });

    // When
    mutationResolver.oppdaterUtdanning(validOppdaterInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("oppdaterUtdanning: Skal ikke oppdatere med negativ varighet")
  void oppdaterUtdanning_skalIkkeOppdatereMedNegativVarighet() {
    // Given
    validOppdaterInput.setVarighet(-5);

    Utdanning existing = new Utdanning();
    existing.setId("UTD-TEST-123");
    existing.setVarighet(3);

    when(utdanningRepository.findById("UTD-TEST-123")).thenReturn(existing);
    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning updated = invocation.getArgument(0);
      assertEquals(Integer.valueOf(3), updated.getVarighet(), "Varighet skal ikke oppdateres med negativ verdi");
      return updated;
    });

    // When
    mutationResolver.oppdaterUtdanning(validOppdaterInput);

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  // ==================== EDGE CASE TESTING ====================

  @Test
  @DisplayName("opprettUtdanning: Beskrivelse kan være null (optional felt)")
  void opprettUtdanning_beskrivelseKanVaereNull() {
    // Given
    validOpprettInput.setBeskrivelse(null);

    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      assertNull(saved.getBeskrivelse(), "Beskrivelse kan være null");
      return saved;
    });

    // When - should not throw exception
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }

  @Test
  @DisplayName("opprettUtdanning: Skal håndtere Unicode-tegn i tekst-felter")
  void opprettUtdanning_skalHandtereUnicodeTegnITekstFelter() {
    // Given - Unicode characters (Norwegian, emojis, etc.)
    validOpprettInput.setNavn("Datateknologi 🎓 æøå");
    validOpprettInput.setStudiested("Trondheim 🏔️");
    validOpprettInput.setUndervisningssprak("Norsk 🇳🇴");
    validOpprettInput.setBeskrivelse("Beskrivelse med æøå og emojis 😊");

    when(utdanningRepository.save(any(Utdanning.class))).thenAnswer(invocation -> {
      Utdanning saved = invocation.getArgument(0);
      assertTrue(saved.getNavn().contains("🎓"));
      assertTrue(saved.getStudiested().contains("🏔️"));
      assertTrue(saved.getUndervisningssprak().contains("🇳🇴"));
      assertTrue(saved.getBeskrivelse().contains("😊"));
      return saved;
    });

    // When - should handle Unicode properly
    assertDoesNotThrow(() -> mutationResolver.opprettUtdanning(validOpprettInput));

    // Then
    verify(utdanningRepository).save(any(Utdanning.class));
  }
}