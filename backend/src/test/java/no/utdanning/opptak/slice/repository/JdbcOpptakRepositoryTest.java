package no.utdanning.opptak.slice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.repository.JdbcOpptakRepository;
import no.utdanning.opptak.repository.JdbcOrganisasjonRepository;
import no.utdanning.opptak.repository.JdbcUtdanningRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@JdbcTest
@ActiveProfiles("dev")
@Import({JdbcOpptakRepository.class, JdbcOrganisasjonRepository.class, JdbcUtdanningRepository.class})
class JdbcOpptakRepositoryTest {

  @Autowired private JdbcOpptakRepository opptakRepository;
  @Autowired private JdbcOrganisasjonRepository organisasjonRepository;
  @Autowired private JdbcUtdanningRepository utdanningRepository;
  @Autowired private JdbcTemplate jdbcTemplate;
  
  private String testOrgId1 = "test-org-1";
  private String testOrgId2 = "test-org-2";
  private String testSoOrgId = "test-so-org";
  private String testOpptakId1 = "test-opptak-1";
  private String testOpptakId2 = "test-opptak-2";
  
  @BeforeEach
  void setUp() {
    // Create test organizations
    createTestOrganisasjon(testOrgId1, "NTNU Test", "NTNU");
    createTestOrganisasjon(testOrgId2, "UiO Test", "UiO");
    createTestOrganisasjon(testSoOrgId, "Samordnet opptak test", "SO");
    
    // Create test opptak
    createTestOpptak(testOpptakId1, "Samordnet opptak H25", OpptaksType.UHG, testSoOrgId, true);
    createTestOpptak(testOpptakId2, "NTNU Lokalt opptak", OpptaksType.LOKALT, testOrgId1, false);
    
    // Create test utdanninger
    createTestUtdanning("test-utd-1", "Bachelor i informatikk", testOrgId1);
    createTestUtdanning("test-utd-2", "Master i AI", testOrgId1);
  }
  
  @AfterEach
  void tearDown() {
    // Clean up in reverse order due to foreign keys - only test data
    jdbcTemplate.update("DELETE FROM utdanning_i_opptak WHERE utdanning_id LIKE 'test-%' OR opptak_id LIKE 'test-%'");
    jdbcTemplate.update("DELETE FROM utdanning WHERE id LIKE 'test-%'");
    jdbcTemplate.update("DELETE FROM opptak WHERE id LIKE 'test-%' OR administrator_organisasjon_id LIKE 'test-%'");
    jdbcTemplate.update("DELETE FROM organisasjon WHERE id LIKE 'test-%'");
  }
  
  private void createTestOrganisasjon(String id, String navn, String kortNavn) {
    // Use different organisasjonsnummer for each test org to avoid unique constraint violations
    String orgNummer = "99" + Math.abs(id.hashCode() % 1000000); // Generate unique number based on ID
    jdbcTemplate.update(
      "INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, adresse, nettside, opprettet, aktiv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
      id, navn, kortNavn, "UNIVERSITET", orgNummer, "Test adresse", "https://test.no", LocalDateTime.now(), true
    );
  }
  
  private void createTestOpptak(String id, String navn, OpptaksType type, String adminOrgId, boolean samordnet) {
    jdbcTemplate.update(
      "INSERT INTO opptak (id, navn, type, aar, administrator_organisasjon_id, samordnet, max_utdanninger_per_soknad, status, opprettet, aktiv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
      id, navn, type.name(), 2025, adminOrgId, samordnet, 10, OpptaksStatus.FREMTIDIG.name(), LocalDateTime.now(), true
    );
  }
  
  private void createTestUtdanning(String id, String navn, String orgId) {
    jdbcTemplate.update(
      "INSERT INTO utdanning (id, navn, studienivaa, studiepoeng, varighet, studiested, undervisningssprak, beskrivelse, opprettet, aktiv, organisasjon_id, starttidspunkt, studieform) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
      id, navn, "bachelor", 180, 3, "Test sted", "norsk", "Test beskrivelse", LocalDateTime.now(), true, orgId, "HØST_2025", "HELTID"
    );
  }

  @Test
  void skalFinneAlleOrganisasjoner() {
    List<Organisasjon> organisasjoner = organisasjonRepository.findAll();

    assertThat(organisasjoner).isNotEmpty();
    assertThat(organisasjoner).allMatch(org -> org.getAktiv());
    assertThat(organisasjoner)
        .isSortedAccordingTo((o1, o2) -> o1.getNavn().compareTo(o2.getNavn()));
  }

  @Test
  void skalFinneOrganisasjonById() {
    Organisasjon organisasjon = organisasjonRepository.findById(testOrgId1);

    assertThat(organisasjon).isNotNull();
    assertThat(organisasjon.getNavn()).isEqualTo("NTNU Test");
    assertThat(organisasjon.getKortNavn()).isEqualTo("NTNU");
  }

  @Test
  void skalReturnereNullNaarOrganisasjonIkkeFinnes() {
    Organisasjon organisasjon = organisasjonRepository.findById("FINNES-IKKE");

    assertThat(organisasjon).isNull();
  }

  @Test
  void skalFinneAlleOpptak() {
    List<Opptak> opptak = opptakRepository.findByAktiv(true);

    assertThat(opptak).isNotEmpty();
    assertThat(opptak).allMatch(o -> o.getAktiv());

    // Skal være sortert etter år (DESC) og navn
    assertThat(opptak.get(0).getAar()).isGreaterThanOrEqualTo(opptak.get(1).getAar());
  }

  @Test
  void skalFinneOpptakById() {
    Opptak opptak = opptakRepository.findById(testOpptakId1);

    assertThat(opptak).isNotNull();
    assertThat(opptak.getNavn()).isEqualTo("Samordnet opptak H25");
    assertThat(opptak.getType()).isEqualTo(OpptaksType.UHG);
    assertThat(opptak.getAar()).isEqualTo(2025);
  }

  @Test
  void skalFinneUtdanningerForOrganisasjon() {
    List<Utdanning> utdanninger = utdanningRepository.findByOrganisasjonId(testOrgId1);

    assertThat(utdanninger).isNotEmpty();
    assertThat(utdanninger).allMatch(u -> u.getOrganisasjonId().equals(testOrgId1));
    assertThat(utdanninger).allMatch(u -> u.getAktiv());
  }

  @Test
  void skalLagreNyttOpptak() {
    Opptak nyttOpptak = new Opptak();
    nyttOpptak.setNavn("Test Opptak");
    nyttOpptak.setType(OpptaksType.LOKALT);
    nyttOpptak.setAar(2025);
    nyttOpptak.setMaxUtdanningerPerSoknad(5);
    nyttOpptak.setStatus(OpptaksStatus.FREMTIDIG);
    nyttOpptak.setSoknadsfrist(LocalDate.of(2025, 3, 1));
    nyttOpptak.setAdministratorOrganisasjonId(testOrgId1);
    nyttOpptak.setSamordnet(false);
    nyttOpptak.setAktiv(true);

    Opptak lagretOpptak = opptakRepository.save(nyttOpptak);

    assertThat(lagretOpptak.getId()).isNotNull();
    assertThat(lagretOpptak.getNavn()).isEqualTo("Test Opptak");
    assertThat(lagretOpptak.getOpprettet()).isNotNull();
    assertThat(lagretOpptak.getAktiv()).isTrue();

    // Verifiser at det kan hentes fra database
    Opptak hentetOpptak = opptakRepository.findById(lagretOpptak.getId());
    assertThat(hentetOpptak).isNotNull();
    assertThat(hentetOpptak.getNavn()).isEqualTo("Test Opptak");
  }

  @Test
  void skalLagreSamordnetOpptak() {
    Opptak samordnetOpptak = new Opptak();
    samordnetOpptak.setNavn("Test Samordnet Opptak");
    samordnetOpptak.setType(OpptaksType.UHG);
    samordnetOpptak.setAar(2025);
    samordnetOpptak.setMaxUtdanningerPerSoknad(12);
    samordnetOpptak.setStatus(OpptaksStatus.FREMTIDIG);
    samordnetOpptak.setSoknadsfrist(LocalDate.of(2025, 4, 15));
    samordnetOpptak.setSvarfrist(LocalDate.of(2025, 7, 20));
    samordnetOpptak.setAdministratorOrganisasjonId(testSoOrgId);
    samordnetOpptak.setSamordnet(true);
    samordnetOpptak.setOpptaksomgang("Hovedopptak");
    samordnetOpptak.setBeskrivelse("Test beskrivelse for samordnet opptak");

    Opptak lagretOpptak = opptakRepository.save(samordnetOpptak);

    assertThat(lagretOpptak.getId()).isNotNull();
    assertThat(lagretOpptak.getSamordnet()).isTrue();
    assertThat(lagretOpptak.getAdministratorOrganisasjonId()).isEqualTo(testSoOrgId);
    assertThat(lagretOpptak.getOpptaksomgang()).isEqualTo("Hovedopptak");
    assertThat(lagretOpptak.getBeskrivelse()).isEqualTo("Test beskrivelse for samordnet opptak");
  }

  @Test
  void skalOppdatereEksisterendeOpptak() {
    Opptak opptak = opptakRepository.findById(testOpptakId1);
    assertThat(opptak).isNotNull();

    opptak.setNavn("Oppdatert navn");
    opptak.setBeskrivelse("Oppdatert beskrivelse");
    opptak.setMaxUtdanningerPerSoknad(15);

    Opptak oppdatertOpptak = opptakRepository.save(opptak);

    assertThat(oppdatertOpptak.getNavn()).isEqualTo("Oppdatert navn");
    assertThat(oppdatertOpptak.getBeskrivelse()).isEqualTo("Oppdatert beskrivelse");
    assertThat(oppdatertOpptak.getMaxUtdanningerPerSoknad()).isEqualTo(15);
  }

  @Test
  void skalFinneOpptakByType() {
    List<Opptak> uhgOpptak = opptakRepository.findByType(OpptaksType.UHG);
    List<Opptak> fsuOpptak = opptakRepository.findByType(OpptaksType.FSU);

    assertThat(uhgOpptak).isNotEmpty();
    assertThat(uhgOpptak).allMatch(o -> o.getType() == OpptaksType.UHG);

    assertThat(fsuOpptak).isNotEmpty();
    assertThat(fsuOpptak).allMatch(o -> o.getType() == OpptaksType.FSU);
  }

  @Test
  void skalFinneOpptakBySamordnet() {
    List<Opptak> samordnede = opptakRepository.findBySamordnet(true);
    List<Opptak> lokale = opptakRepository.findBySamordnet(false);

    assertThat(samordnede).isNotEmpty();
    assertThat(samordnede).allMatch(o -> o.getSamordnet());

    assertThat(lokale).isNotEmpty();
    assertThat(lokale).allMatch(o -> !o.getSamordnet());
  }

  @Test
  void skalSjekkeOmNavnEksisterer() {
    boolean eksisterer = opptakRepository.existsByNavn("Samordnet opptak H25");
    boolean eksistererIkke = opptakRepository.existsByNavn("Finnes ikke");

    assertThat(eksisterer).isTrue();
    assertThat(eksistererIkke).isFalse();
  }
}