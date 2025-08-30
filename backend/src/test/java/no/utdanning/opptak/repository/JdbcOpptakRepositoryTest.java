package no.utdanning.opptak.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import no.utdanning.opptak.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@ActiveProfiles("test")
@Import({JdbcOpptakRepository.class, JdbcOrganisasjonRepository.class, JdbcUtdanningRepository.class})
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class JdbcOpptakRepositoryTest {

  @Autowired private JdbcOpptakRepository opptakRepository;
  @Autowired private JdbcOrganisasjonRepository organisasjonRepository;
  @Autowired private JdbcUtdanningRepository utdanningRepository;

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
    Organisasjon organisasjon = organisasjonRepository.findById("NTNU-001");

    assertThat(organisasjon).isNotNull();
    assertThat(organisasjon.getNavn()).isEqualTo("NTNU");
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
    Opptak opptak = opptakRepository.findById("SO-H25-001");

    assertThat(opptak).isNotNull();
    assertThat(opptak.getNavn()).isEqualTo("Samordnet opptak H25");
    assertThat(opptak.getType()).isEqualTo(OpptaksType.UHG);
    assertThat(opptak.getAar()).isEqualTo(2025);
  }

  @Test
  void skalFinneUtdanningerForOrganisasjon() {
    List<Utdanning> utdanninger = utdanningRepository.findByOrganisasjonId("NTNU-001");

    assertThat(utdanninger).isNotEmpty();
    assertThat(utdanninger).allMatch(u -> u.getOrganisasjonId().equals("NTNU-001"));
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
    nyttOpptak.setAdministratorOrganisasjonId("NTNU-001");
    nyttOpptak.setSamordnet(false);

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
    samordnetOpptak.setAdministratorOrganisasjonId("SO-001");
    samordnetOpptak.setSamordnet(true);
    samordnetOpptak.setOpptaksomgang("Hovedopptak");
    samordnetOpptak.setBeskrivelse("Test beskrivelse for samordnet opptak");

    Opptak lagretOpptak = opptakRepository.save(samordnetOpptak);

    assertThat(lagretOpptak.getId()).isNotNull();
    assertThat(lagretOpptak.getSamordnet()).isTrue();
    assertThat(lagretOpptak.getAdministratorOrganisasjonId()).isEqualTo("SO-001");
    assertThat(lagretOpptak.getOpptaksomgang()).isEqualTo("Hovedopptak");
    assertThat(lagretOpptak.getBeskrivelse()).isEqualTo("Test beskrivelse for samordnet opptak");
  }

  @Test
  void skalOppdatereEksisterendeOpptak() {
    Opptak opptak = opptakRepository.findById("SO-H25-001");
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