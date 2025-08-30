package no.utdanning.opptak.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.StudieForm;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.PageInput;
import no.utdanning.opptak.graphql.dto.UtdanningFilter;
import no.utdanning.opptak.graphql.dto.UtdanningPage;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("UtdanningQueryResolver - Sikkerhetstester")
class UtdanningQueryResolverSecurityTest {

  @Mock private UtdanningRepository utdanningRepository;
  @Mock private OrganisasjonRepository organisasjonRepository;
  @Mock private UtdanningSecurityService securityService;

  @InjectMocks private UtdanningQueryResolver queryResolver;

  private Utdanning ntnu1Utdanning;
  private Utdanning ntnu2Utdanning;
  private Utdanning uioUtdanning;
  private Organisasjon ntnu;
  private Organisasjon uio;

  @BeforeEach
  void setUp() {
    // NTNU organisasjon
    ntnu = new Organisasjon();
    ntnu.setId("ORG-NTNU-001");
    ntnu.setNavn("NTNU");

    // UiO organisasjon  
    uio = new Organisasjon();
    uio.setId("ORG-UIO-002");
    uio.setNavn("Universitetet i Oslo");

    // NTNU utdanning 1
    ntnu1Utdanning = new Utdanning();
    ntnu1Utdanning.setId("UTD-NTNU-123");
    ntnu1Utdanning.setNavn("Datateknologi");
    ntnu1Utdanning.setStudienivaa("Bachelor");
    ntnu1Utdanning.setStudiepoeng(180);
    ntnu1Utdanning.setVarighet(3);
    ntnu1Utdanning.setStudiested("Trondheim");
    ntnu1Utdanning.setUndervisningssprak("Norsk");
    ntnu1Utdanning.setStarttidspunkt("Høst");
    ntnu1Utdanning.setStudieform(StudieForm.CAMPUS);
    ntnu1Utdanning.setOrganisasjonId("ORG-NTNU-001");
    ntnu1Utdanning.setAktiv(true);
    ntnu1Utdanning.setOpprettet(LocalDateTime.now());

    // NTNU utdanning 2
    ntnu2Utdanning = new Utdanning();
    ntnu2Utdanning.setId("UTD-NTNU-456");
    ntnu2Utdanning.setNavn("Informatikk");
    ntnu2Utdanning.setStudienivaa("Master");
    ntnu2Utdanning.setStudiepoeng(120);
    ntnu2Utdanning.setVarighet(2);
    ntnu2Utdanning.setStudiested("Trondheim");
    ntnu2Utdanning.setUndervisningssprak("Norsk");
    ntnu2Utdanning.setStarttidspunkt("Høst");
    ntnu2Utdanning.setStudieform(StudieForm.CAMPUS);
    ntnu2Utdanning.setOrganisasjonId("ORG-NTNU-001");
    ntnu2Utdanning.setAktiv(true);
    ntnu2Utdanning.setOpprettet(LocalDateTime.now());

    // UiO utdanning
    uioUtdanning = new Utdanning();
    uioUtdanning.setId("UTD-UIO-789");
    uioUtdanning.setNavn("Juridisk utdanning");
    uioUtdanning.setStudienivaa("Bachelor");
    uioUtdanning.setStudiepoeng(180);
    uioUtdanning.setVarighet(3);
    uioUtdanning.setStudiested("Oslo");
    uioUtdanning.setUndervisningssprak("Norsk");
    uioUtdanning.setStarttidspunkt("Høst");
    uioUtdanning.setStudieform(StudieForm.CAMPUS);
    uioUtdanning.setOrganisasjonId("ORG-UIO-002");
    uioUtdanning.setAktiv(true);
    uioUtdanning.setOpprettet(LocalDateTime.now());
  }

  // ==================== SINGLE UTDANNING QUERY SIKKERHET ====================

  @Test
  @DisplayName("utdanning: Administrator skal kunne se utdanning fra alle organisasjoner")
  void utdanning_administratorSkalKunneSeUtdanningFraAlleOrganisasjoner() {
    // Given
    when(utdanningRepository.findById("UTD-UIO-789")).thenReturn(uioUtdanning);
    when(securityService.isAdministrator()).thenReturn(true);

    // When
    Utdanning result = queryResolver.utdanning("UTD-UIO-789");

    // Then
    assertNotNull(result);
    assertEquals("UTD-UIO-789", result.getId());
    assertEquals("ORG-UIO-002", result.getOrganisasjonId());
    verify(utdanningRepository).findById("UTD-UIO-789");
    verify(securityService).isAdministrator();
  }

  @Test
  @DisplayName("utdanning: OPPTAKSLEDER skal kun kunne se utdanninger fra egen organisasjon")
  void utdanning_opptakslederSkalKunKunneSeUtdanningerFraEgenOrganisasjon() {
    // Given
    when(utdanningRepository.findById("UTD-NTNU-123")).thenReturn(ntnu1Utdanning);
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001");

    // When
    Utdanning result = queryResolver.utdanning("UTD-NTNU-123");

    // Then
    assertNotNull(result);
    assertEquals("UTD-NTNU-123", result.getId());
    assertEquals("ORG-NTNU-001", result.getOrganisasjonId());
    verify(securityService).isAdministrator();
    verify(securityService).getCurrentUserOrganisasjonId();
  }

  @Test
  @DisplayName("utdanning: OPPTAKSLEDER skal IKKE kunne se utdanninger fra andre organisasjoner")
  void utdanning_opptakslederSkalIkkeKunneSeUtdanningerFraAndreOrganisasjoner() {
    // Given
    when(utdanningRepository.findById("UTD-UIO-789")).thenReturn(uioUtdanning);
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001"); // NTNU user trying to access UiO data

    // When
    Utdanning result = queryResolver.utdanning("UTD-UIO-789");

    // Then
    assertNull(result, "OPPTAKSLEDER skal ikke kunne se utdanninger fra andre organisasjoner");
    verify(securityService).isAdministrator();
    verify(securityService).getCurrentUserOrganisasjonId();
  }

  @Test
  @DisplayName("utdanning: Skal returnere null for ikke-eksisterende utdanning")
  void utdanning_skalReturnereNullForIkkeEksisterendeUtdanning() {
    // Given
    when(utdanningRepository.findById("UTD-FINNES-IKKE")).thenReturn(null);
    when(securityService.isAdministrator()).thenReturn(true);

    // When
    Utdanning result = queryResolver.utdanning("UTD-FINNES-IKKE");

    // Then
    assertNull(result);
    verify(utdanningRepository).findById("UTD-FINNES-IKKE");
  }

  // ==================== UTDANNINGER PAGE QUERY SIKKERHET ====================

  @Test
  @DisplayName("utdanninger: Administrator skal se alle utdanninger uansett organisasjon")
  void utdanninger_administratorSkalSeAlleUtdanningerUansettOrganisasjon() {
    // Given
    List<Utdanning> alleUtdanninger = Arrays.asList(ntnu1Utdanning, ntnu2Utdanning, uioUtdanning);
    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters(null, null, null, null, null, null, 20, 0))
        .thenReturn(alleUtdanninger);
    when(utdanningRepository.countWithFilters(null, null, null, null, null, null))
        .thenReturn(3L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, null);

    // Then
    assertNotNull(result);
    assertEquals(3, result.getContent().size());
    assertEquals(3L, result.getTotalElements());
    verify(securityService).isAdministrator();
    verify(securityService, never()).getCurrentUserOrganisasjonId(); // Administrator doesn't need org filtering
  }

  @Test
  @DisplayName("utdanninger: OPPTAKSLEDER skal automatisk få filtrert på egen organisasjon")
  void utdanninger_opptakslederSkalAutomatiskFaFiltretPaEgenOrganisasjon() {
    // Given
    List<Utdanning> ntnuUtdanninger = Arrays.asList(ntnu1Utdanning, ntnu2Utdanning);
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001");
    when(utdanningRepository.findWithFilters(null, null, null, "ORG-NTNU-001", null, null, 20, 0))
        .thenReturn(ntnuUtdanninger);
    when(utdanningRepository.countWithFilters(null, null, null, "ORG-NTNU-001", null, null))
        .thenReturn(2L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, null);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals(2L, result.getTotalElements());
    assertTrue(result.getContent().stream().allMatch(u -> u.getOrganisasjonId().equals("ORG-NTNU-001")));
    verify(securityService).isAdministrator();
    verify(securityService).getCurrentUserOrganisasjonId();
  }

  @Test
  @DisplayName("utdanninger: Skal respektere paginering og filtre")
  void utdanninger_skalRespekterePagineringOgFiltre() {
    // Given
    UtdanningFilter filter = new UtdanningFilter();
    filter.setNavn("Data");
    filter.setStudienivaa("Bachelor");

    PageInput page = new PageInput();
    page.setPage(1);  // Second page
    page.setSize(10);

    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters("Data", "Bachelor", null, null, null, null, 10, 10))
        .thenReturn(List.of(ntnu1Utdanning));
    when(utdanningRepository.countWithFilters("Data", "Bachelor", null, null, null, null))
        .thenReturn(15L);

    // When
    UtdanningPage result = queryResolver.utdanninger(filter, page);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals(15L, result.getTotalElements());
    assertEquals(1, result.getPage());
    assertEquals(10, result.getSize());
    verify(utdanningRepository).findWithFilters("Data", "Bachelor", null, null, null, null, 10, 10);
  }

  @Test
  @DisplayName("utdanninger: SØKNADSBEHANDLER skal få filtrert på egen organisasjon")
  void utdanninger_soknadsbehandlerSkalFaFiltreretPaEgenOrganisasjon() {
    // Given
    List<Utdanning> ntnuUtdanninger = Arrays.asList(ntnu1Utdanning);
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001");
    when(utdanningRepository.findWithFilters(null, null, null, "ORG-NTNU-001", null, null, 20, 0))
        .thenReturn(ntnuUtdanninger);
    when(utdanningRepository.countWithFilters(null, null, null, "ORG-NTNU-001", null, null))
        .thenReturn(1L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, null);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertTrue(result.getContent().stream().allMatch(u -> u.getOrganisasjonId().equals("ORG-NTNU-001")));
    verify(securityService).getCurrentUserOrganisasjonId();
  }

  // ==================== UTDANNINGER FOR ORGANISASJON SIKKERHET ====================

  @Test
  @DisplayName("utdanningerForOrganisasjon: Administrator skal kunne se utdanninger for alle organisasjoner")
  void utdanningerForOrganisasjon_administratorSkalKunneSeUtdanningerForAlleOrganisasjoner() {
    // Given
    String organisasjonId = "ORG-UIO-002";
    List<Utdanning> uioUtdanninger = Arrays.asList(uioUtdanning);
    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters(null, null, null, organisasjonId, null, null, 20, 0))
        .thenReturn(uioUtdanninger);
    when(utdanningRepository.countWithFilters(null, null, null, organisasjonId, null, null))
        .thenReturn(1L);

    // When
    UtdanningPage result = queryResolver.utdanningerForOrganisasjon(organisasjonId, null, null);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals("ORG-UIO-002", result.getContent().get(0).getOrganisasjonId());
    verify(securityService).isAdministrator();
    verify(securityService, never()).getCurrentUserOrganisasjonId();
  }

  @Test
  @DisplayName("utdanningerForOrganisasjon: OPPTAKSLEDER skal kunne se kun egen organisasjons utdanninger")
  void utdanningerForOrganisasjon_opptakslederSkalKunneSeKunEgenOrganisasjonsUtdanninger() {
    // Given
    String organisasjonId = "ORG-NTNU-001";  // User's own organization
    List<Utdanning> ntnuUtdanninger = Arrays.asList(ntnu1Utdanning, ntnu2Utdanning);
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001");
    when(utdanningRepository.findWithFilters(null, null, null, organisasjonId, null, null, 20, 0))
        .thenReturn(ntnuUtdanninger);
    when(utdanningRepository.countWithFilters(null, null, null, organisasjonId, null, null))
        .thenReturn(2L);

    // When
    UtdanningPage result = queryResolver.utdanningerForOrganisasjon(organisasjonId, null, null);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertTrue(result.getContent().stream().allMatch(u -> u.getOrganisasjonId().equals("ORG-NTNU-001")));
    verify(securityService).isAdministrator();
    verify(securityService).getCurrentUserOrganisasjonId();
  }

  @Test
  @DisplayName("utdanningerForOrganisasjon: OPPTAKSLEDER skal få tom side for andre organisasjoner")
  void utdanningerForOrganisasjon_opptakslederSkalFaTomSideForAndreOrganisasjoner() {
    // Given
    String annenOrganisasjonId = "ORG-UIO-002";  // Different organization
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn("ORG-NTNU-001"); // User belongs to NTNU

    // When
    UtdanningPage result = queryResolver.utdanningerForOrganisasjon(annenOrganisasjonId, null, null);

    // Then
    assertNotNull(result);
    assertEquals(0, result.getContent().size());
    assertEquals(0L, result.getTotalElements());
    assertEquals(0, result.getPage());
    assertEquals(20, result.getSize());
    verify(securityService).isAdministrator();
    verify(securityService).getCurrentUserOrganisasjonId();
    verify(utdanningRepository, never()).findWithFilters(anyString(), anyString(), anyString(), anyString(), any(), any(), anyInt(), anyInt());
  }

  @Test
  @DisplayName("utdanningerForOrganisasjon: Skal håndtere null organisasjonId for ikke-administrator")
  void utdanningerForOrganisasjon_skalHandtereNullOrganisasjonIdForIkkeAdministrator() {
    // Given
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn(null);

    // When
    UtdanningPage result = queryResolver.utdanningerForOrganisasjon("ORG-ANY-123", null, null);

    // Then
    assertNotNull(result);
    assertEquals(0, result.getContent().size());
    assertEquals(0L, result.getTotalElements());
  }

  // ==================== SCHEMA MAPPING SIKKERHET ====================

  @Test
  @DisplayName("organisasjon schema mapping: Skal hente organisasjon for utdanning")
  void organisasjon_skalHenteOrganisasjonForUtdanning() {
    // Given
    when(organisasjonRepository.findById("ORG-NTNU-001")).thenReturn(ntnu);

    // When
    Object result = queryResolver.organisasjon(ntnu1Utdanning);

    // Then
    assertNotNull(result);
    assertEquals(ntnu, result);
    verify(organisasjonRepository).findById("ORG-NTNU-001");
  }

  @Test
  @DisplayName("organisasjon schema mapping: Skal håndtere ikke-eksisterende organisasjon")
  void organisasjon_skalHandtereIkkeEksisterendeOrganisasjon() {
    // Given
    when(organisasjonRepository.findById("ORG-FINNES-IKKE")).thenReturn(null);
    ntnu1Utdanning.setOrganisasjonId("ORG-FINNES-IKKE");

    // When
    Object result = queryResolver.organisasjon(ntnu1Utdanning);

    // Then
    assertNull(result);
    verify(organisasjonRepository).findById("ORG-FINNES-IKKE");
  }

  // ==================== EDGE CASES OG BOUNDARY TESTING ====================

  @Test
  @DisplayName("utdanninger: Skal håndtere null filter og page input gracefully")
  void utdanninger_skalHandtereNullFilterOgPageInputGracefully() {
    // Given
    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters(null, null, null, null, null, null, 20, 0))
        .thenReturn(Arrays.asList(ntnu1Utdanning));
    when(utdanningRepository.countWithFilters(null, null, null, null, null, null))
        .thenReturn(1L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, null);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(utdanningRepository).findWithFilters(null, null, null, null, null, null, 20, 0);
  }

  @Test
  @DisplayName("utdanningerForOrganisasjon: Skal håndtere null filter og page input gracefully")
  void utdanningerForOrganisasjon_skalHandtereNullFilterOgPageInputGracefully() {
    // Given
    String organisasjonId = "ORG-NTNU-001";
    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters(null, null, null, organisasjonId, null, null, 20, 0))
        .thenReturn(Arrays.asList(ntnu1Utdanning));
    when(utdanningRepository.countWithFilters(null, null, null, organisasjonId, null, null))
        .thenReturn(1L);

    // When
    UtdanningPage result = queryResolver.utdanningerForOrganisasjon(organisasjonId, null, null);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(utdanningRepository).findWithFilters(null, null, null, organisasjonId, null, null, 20, 0);
  }

  @Test
  @DisplayName("utdanninger: Skal håndtere stor pagineringsstørrelse")
  void utdanninger_skalHandtereStorPagineringsstorrelse() {
    // Given
    PageInput largePage = new PageInput();
    largePage.setPage(0);
    largePage.setSize(1000); // Large page size

    when(securityService.isAdministrator()).thenReturn(true);
    when(utdanningRepository.findWithFilters(null, null, null, null, null, null, 1000, 0))
        .thenReturn(Arrays.asList(ntnu1Utdanning));
    when(utdanningRepository.countWithFilters(null, null, null, null, null, null))
        .thenReturn(1L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, largePage);

    // Then
    assertNotNull(result);
    assertEquals(1000, result.getSize());
    verify(utdanningRepository).findWithFilters(null, null, null, null, null, null, 1000, 0);
  }

  @Test
  @DisplayName("utdanninger: Skal håndtere negative pagineringsverdier")
  void utdanninger_skalHandtereNegativePagineringsverdier() {
    // Given
    PageInput negativePage = new PageInput();
    negativePage.setPage(-1); // Negative page
    negativePage.setSize(-5); // Negative size

    when(securityService.isAdministrator()).thenReturn(true);
    // The resolver should handle negative values gracefully, likely converting to 0
    when(utdanningRepository.findWithFilters(null, null, null, null, null, null, -5, -5))
        .thenReturn(Collections.emptyList());
    when(utdanningRepository.countWithFilters(null, null, null, null, null, null))
        .thenReturn(0L);

    // When
    UtdanningPage result = queryResolver.utdanninger(null, negativePage);

    // Then
    assertNotNull(result);
    assertEquals(-1, result.getPage());
    assertEquals(-5, result.getSize());
    verify(utdanningRepository).findWithFilters(null, null, null, null, null, null, -5, -5);
  }
}