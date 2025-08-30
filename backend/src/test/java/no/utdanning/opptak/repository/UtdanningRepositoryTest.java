package no.utdanning.opptak.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import no.utdanning.opptak.domain.Studieform;
import no.utdanning.opptak.domain.Utdanning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UtdanningRepository - Repository layer testing")
class UtdanningRepositoryTest {

  @Autowired private UtdanningRepository utdanningRepository;

  // Testene bruker kun data fra InMemoryUtdanningRepository
  // Egne testdata lages inline når nødvendig

  // ==================== BASIC CRUD TESTING ====================

  @Test
  @DisplayName("save: Skal lagre ny utdanning med alle påkrevde felter")
  void save_skalLagreNyUtdanningMedAllePakrevdeFelter() {
    // Given
    Utdanning nyUtdanning = new Utdanning();
    nyUtdanning.setNavn("Ny Utdanning Test");
    nyUtdanning.setStudienivaa("PhD");
    nyUtdanning.setStudiepoeng(240);
    nyUtdanning.setVarighet(4);
    nyUtdanning.setStudiested("Bergen");
    nyUtdanning.setUndervisningssprak("Engelsk");
    nyUtdanning.setBeskrivelse("PhD program");
    nyUtdanning.setStarttidspunkt("Vår");
    nyUtdanning.setStudieform(Studieform.HELTID);
    nyUtdanning.setOrganisasjonId("874789652"); // UiB organisasjonsnummer
    nyUtdanning.setAktiv(true);
    nyUtdanning.setOpprettet(LocalDateTime.now());

    // When
    Utdanning lagret = utdanningRepository.save(nyUtdanning);

    // Then
    assertNotNull(lagret.getId(), "ID skal være generert");
    assertEquals("Ny Utdanning Test", lagret.getNavn());
    assertEquals("PhD", lagret.getStudienivaa());
    assertEquals(Integer.valueOf(240), lagret.getStudiepoeng());
    assertEquals(Integer.valueOf(4), lagret.getVarighet());
    assertEquals("Bergen", lagret.getStudiested());
    assertEquals("Engelsk", lagret.getUndervisningssprak());
    assertEquals("PhD program", lagret.getBeskrivelse());
    assertEquals("Vår", lagret.getStarttidspunkt());
    assertEquals(Studieform.HELTID, lagret.getStudieform());
    assertEquals("874789652", lagret.getOrganisasjonId());
    assertTrue(lagret.getAktiv());
    assertNotNull(lagret.getOpprettet());
  }

  @Test
  @DisplayName("findById: Skal finne utdanning med gyldig ID")
  void findById_skalFinneUtdanningMedGyldigId() {
    // When - bruker ID fra InMemoryRepository  
    Utdanning funnet = utdanningRepository.findById("ntnu-informatikk-h25");

    // Then
    assertNotNull(funnet);
    assertEquals("ntnu-informatikk-h25", funnet.getId());
    assertEquals("Bachelor i informatikk H25", funnet.getNavn());
    assertEquals("ntnu", funnet.getOrganisasjonId());
  }

  @Test
  @DisplayName("findById: Skal returnere null for ikke-eksisterende ID")
  void findById_skalReturneNullForIkkeEksisterendeId() {
    // When
    Utdanning funnet = utdanningRepository.findById("FINNES-IKKE");

    // Then
    assertNull(funnet);
  }

  @Test
  @DisplayName("existsById: Skal returnere true for eksisterende utdanning")
  void existsById_skalReturneTrueForEksisterendeUtdanning() {
    // When - bruker ID fra InMemoryRepository
    boolean exists = utdanningRepository.existsById("ntnu-informatikk-h25");

    // Then
    assertTrue(exists);
  }

  @Test
  @DisplayName("existsById: Skal returnere false for ikke-eksisterende utdanning")
  void existsById_skalReturneFalseForIkkeEksisterendeUtdanning() {
    // When
    boolean exists = utdanningRepository.existsById("FINNES-IKKE");

    // Then
    assertFalse(exists);
  }

  @Test
  @DisplayName("deaktiverById: Skal deaktivere eksisterende utdanning (soft delete)")
  void deaktiverById_skalDeaktivereEksisterendeUtdanning() {
    // Given - bruker ID fra InMemoryRepository
    String idToDeactivate = "hvl-sykepleie-h25";
    assertTrue(utdanningRepository.existsById(idToDeactivate), "Utdanning skal eksistere før deaktivering");
    assertTrue(utdanningRepository.findById(idToDeactivate).getAktiv(), "Utdanning skal være aktiv før deaktivering");

    // When
    boolean deaktivert = utdanningRepository.deaktiverById(idToDeactivate);

    // Then
    assertTrue(deaktivert, "Deaktivering skal returnere true");
    assertTrue(utdanningRepository.existsById(idToDeactivate), "Utdanning skal fortsatt eksistere etter deaktivering");
    assertFalse(utdanningRepository.findById(idToDeactivate).getAktiv(), "Utdanning skal være inaktiv etter deaktivering");
  }

  @Test
  @DisplayName("aktiverById: Skal aktivere deaktivert utdanning")
  void aktiverById_skalAktivereDeaktivertUtdanning() {
    // Given - deaktiver først en utdanning
    String idToActivate = "uio-informatikk-h25";
    utdanningRepository.deaktiverById(idToActivate);
    assertFalse(utdanningRepository.findById(idToActivate).getAktiv(), "Utdanning skal være deaktivert først");

    // When
    boolean aktivert = utdanningRepository.aktiverById(idToActivate);

    // Then
    assertTrue(aktivert, "Aktivering skal returnere true");
    assertTrue(utdanningRepository.findById(idToActivate).getAktiv(), "Utdanning skal være aktiv etter aktivering");
  }

  // ==================== FILTERING AND SEARCH TESTING ====================

  @Test
  @DisplayName("findWithFilters: Skal finne alle utdanninger uten filtre")
  void findWithFilters_skalFinneAlleUtdanningerUtenFiltre() {
    // When
    List<Utdanning> result = utdanningRepository.findWithFilters(
        null, null, null, null, null, null, 100, 0);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).allMatch(utdanning -> utdanning.getNavn() != null);
    assertThat(result).allMatch(utdanning -> utdanning.getStudienivaa() != null);
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på navn (partial match)")
  void findWithFilters_skalFiltrePaNavn() {
    // When - bruker delnavn fra InMemoryRepository
    List<Utdanning> result = utdanningRepository.findWithFilters(
        "informatikk", null, null, null, null, null, 100, 0);

    // Then - 2 utdanninger har "informatikk" i navnet (NTNU og UiO)
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Utdanning::getNavn)
        .allMatch(navn -> navn.toLowerCase().contains("informatikk"));
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på studienivå")
  void findWithFilters_skalFiltrePaStudienivaa() {
    // When - bruker "bachelor" som matcher InMemoryRepository data
    List<Utdanning> result = utdanningRepository.findWithFilters(
        null, "bachelor", null, null, null, null, 100, 0);

    // Then
    assertThat(result).isNotEmpty();
    assertThat(result).extracting(Utdanning::getStudienivaa)
        .allMatch(nivaa -> nivaa != null && nivaa.equals("bachelor"));
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på studiested")
  void findWithFilters_skalFiltrePaStudiested() {
    // When - bruker "Trondheim" som har 2 NTNU utdanninger i InMemoryRepository
    List<Utdanning> result = utdanningRepository.findWithFilters(
        null, null, "Trondheim", null, null, null, 100, 0);

    // Then - 2 NTNU utdanninger i Trondheim (fra InMemoryRepository)
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Utdanning::getStudiested)
        .allMatch(sted -> sted.equals("Trondheim"));
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på organisasjonId")
  void findWithFilters_skalFiltrePaOrganisasjonId() {
    // When - bruker "ntnu" som har 2 utdanninger i InMemoryRepository
    List<Utdanning> result = utdanningRepository.findWithFilters(
        null, null, null, "ntnu", null, null, 100, 0);

    // Then - 2 NTNU utdanninger fra InMemoryRepository
    assertThat(result).hasSize(2);
    assertThat(result).extracting(Utdanning::getOrganisasjonId)
        .allMatch(orgId -> orgId.equals("ntnu"));
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på studieform")
  void findWithFilters_skalFiltrePaStudieform() {
    // When
    List<Utdanning> result = utdanningRepository.findWithFilters(
        null, null, null, null, Studieform.HELTID, null, 100, 0);

    // Then - minst 4 utdanninger har HELTID i InMemoryRepository
    assertThat(result).hasSizeGreaterThanOrEqualTo(4);
    assertThat(result).extracting(Utdanning::getStudieform)
        .allMatch(form -> form.equals(Studieform.HELTID));
  }

  @Test
  @DisplayName("findWithFilters: Skal filtrere på aktiv status")
  void findWithFilters_skalFiltrePaAktivStatus() {
    // When - only active
    List<Utdanning> activeResult = utdanningRepository.findWithFilters(
        null, null, null, null, null, true, 100, 0);

    // Then - alle utdanninger i baseline InMemoryRepository er aktive (minst 5)
    assertThat(activeResult).hasSizeGreaterThanOrEqualTo(5);
    assertThat(activeResult).extracting(Utdanning::getAktiv)
        .allMatch(aktiv -> aktiv.equals(true));

    // When - only inactive
    List<Utdanning> inactiveResult = utdanningRepository.findWithFilters(
        null, null, null, null, null, false, 100, 0);

    // Then - kan være inaktive utdanninger fra andre tester
    // Bare sjekk at alle er inaktive hvis noen finnes
    if (!inactiveResult.isEmpty()) {
      assertThat(inactiveResult).extracting(Utdanning::getAktiv)
          .allMatch(aktiv -> aktiv.equals(false));
    }
  }

  @Test
  @DisplayName("findWithFilters: Skal kombinere flere filtre")
  void findWithFilters_skalKombinereFlereFiltre() {
    // When - bruk kombinasjon som matcher data i InMemoryRepository
    List<Utdanning> result = utdanningRepository.findWithFilters(
        "informatikk", "bachelor", "Trondheim", "ntnu", Studieform.HELTID, true, 100, 0);

    // Then - skal finne NTNU informatikk-utdanning i Trondheim
    assertThat(result).hasSize(1);
    Utdanning funnet = result.get(0);
    assertThat(funnet.getNavn().toLowerCase()).contains("informatikk");
    assertThat(funnet.getStudienivaa()).isEqualTo("bachelor");
    assertThat(funnet.getStudiested()).isEqualTo("Trondheim");
    assertThat(funnet.getOrganisasjonId()).isEqualTo("ntnu");
    assertThat(funnet.getStudieform()).isEqualTo(Studieform.HELTID);
    assertThat(funnet.getAktiv()).isTrue();
  }

  @Test
  @DisplayName("findWithFilters: Skal respektere paginering (size og offset)")
  void findWithFilters_skalRespekterePaginering() {
    // When - first page, size 1
    List<Utdanning> page1 = utdanningRepository.findWithFilters(
        null, null, null, null, null, null, 1, 0);

    // When - second page, size 1
    List<Utdanning> page2 = utdanningRepository.findWithFilters(
        null, null, null, null, null, null, 1, 1);

    // Then
    assertThat(page1).hasSize(1);
    assertThat(page2).hasSize(1);
    assertThat(page1.get(0).getId()).isNotEqualTo(page2.get(0).getId());
  }

  @Test
  @DisplayName("countWithFilters: Skal telle alle utdanninger uten filtre")
  void countWithFilters_skalTelleAlleUtdanningerUtenFiltre() {
    // When
    long count = utdanningRepository.countWithFilters(
        null, null, null, null, null, null);

    // Then - sjekk at vi får et positivt antall
    assertThat(count).isGreaterThan(0);
  }

  @Test
  @DisplayName("countWithFilters: Skal telle filtrerte resultater korrekt")
  void countWithFilters_skalTelleFiltrerteResultaterKorrekt() {
    // When - bruker "bachelor" som matcher InMemoryRepository data
    long count = utdanningRepository.countWithFilters(
        null, "bachelor", null, null, null, null);

    // Then - sjekk at vi får et positivt antall (ikke hardkodet verdi)
    assertThat(count).isGreaterThan(0);
  }

  // ==================== BASIC VALIDATION ====================

  @Test
  @DisplayName("save: Skal validere påkrevde felter")
  void save_skalValiderePakrevdeFelter() {
    // Given - utdanning with missing required field
    Utdanning invalidUtdanning = new Utdanning();
    invalidUtdanning.setNavn(null); // Required field is null
    invalidUtdanning.setStudienivaa("Bachelor");
    invalidUtdanning.setStudiepoeng(180);
    invalidUtdanning.setVarighet(3);
    invalidUtdanning.setStudiested("Test");
    invalidUtdanning.setUndervisningssprak("Norsk");
    invalidUtdanning.setStarttidspunkt("Høst");
    invalidUtdanning.setStudieform(Studieform.HELTID);
    invalidUtdanning.setOrganisasjonId("123456789"); // Test organisasjonsnummer
    invalidUtdanning.setAktiv(true);
    invalidUtdanning.setOpprettet(LocalDateTime.now());

    // When & Then - should fail due to validation
    assertThrows(Exception.class, () -> {
      utdanningRepository.save(invalidUtdanning);
    }, "Lagring med null påkrevd felt skal feile");
  }

  @Test
  @DisplayName("findWithFilters: Case-insensitive søk skal fungere")
  void findWithFilters_caseInsensitiveSokSkalFungere() {
    // When - search with different case for "BACHELOR" som finnes i alle utdanningsnavnene
    List<Utdanning> result1 = utdanningRepository.findWithFilters(
        "BACHELOR", null, null, null, null, null, 100, 0);
    List<Utdanning> result2 = utdanningRepository.findWithFilters(
        "bachelor", null, null, null, null, null, 100, 0);

    // Then - should find the same results regardless of case
    assertThat(result1).hasSameSizeAs(result2);
    assertThat(result1).hasSizeGreaterThan(0);
    // Verify they contain the same utdanninger
    assertThat(result1).extracting(Utdanning::getId)
        .containsExactlyInAnyOrderElementsOf(
            result2.stream().map(Utdanning::getId).toList());
  }

}