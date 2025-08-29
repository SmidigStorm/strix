package no.utdanning.opptak.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import no.utdanning.opptak.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "no.utdanning.opptak.repository")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OpptakRepositoryTest {

  @Autowired private OpptakRepository repository;

  @Test
  void skalFinneAlleOrganisasjoner() {
    List<Organisasjon> organisasjoner = repository.findAllOrganisasjoner();

    assertThat(organisasjoner).isNotEmpty();
    assertThat(organisasjoner).allMatch(org -> org.getAktiv());
    assertThat(organisasjoner)
        .isSortedAccordingTo((o1, o2) -> o1.getNavn().compareTo(o2.getNavn()));
  }

  @Test
  void skalFinneOrganisasjonById() {
    Optional<Organisasjon> organisasjon = repository.findOrganisasjonById("NTNU-001");

    assertThat(organisasjon).isPresent();
    assertThat(organisasjon.get().getNavn()).isEqualTo("NTNU");
    assertThat(organisasjon.get().getKortNavn()).isEqualTo("NTNU");
  }

  @Test
  void skalReturnereEmptyNaarOrganisasjonIkkeFinnes() {
    Optional<Organisasjon> organisasjon = repository.findOrganisasjonById("FINNES-IKKE");

    assertThat(organisasjon).isEmpty();
  }

  @Test
  void skalFinneAlleOpptak() {
    List<Opptak> opptak = repository.findAllOpptak();

    assertThat(opptak).isNotEmpty();
    assertThat(opptak).allMatch(o -> o.getAktiv());

    // Skal være sortert etter år (DESC) og navn
    assertThat(opptak.get(0).getAar()).isGreaterThanOrEqualTo(opptak.get(1).getAar());
  }

  @Test
  void skalFinneOpptakById() {
    Optional<Opptak> opptak = repository.findOpptakById("SO-H25-001");

    assertThat(opptak).isPresent();
    assertThat(opptak.get().getNavn()).isEqualTo("Samordnet opptak H25");
    assertThat(opptak.get().getType()).isEqualTo(OpptaksType.UHG);
    assertThat(opptak.get().getAar()).isEqualTo(2025);
  }

  @Test
  void skalFinneUtdanningerForOrganisasjon() {
    List<Utdanning> utdanninger = repository.findUtdanningerByOrganisasjonId("NTNU-001");

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

    Opptak lagretOpptak = repository.saveOpptak(nyttOpptak);

    assertThat(lagretOpptak.getId()).isNotNull();
    assertThat(lagretOpptak.getNavn()).isEqualTo("Test Opptak");
    assertThat(lagretOpptak.getOpprettet()).isNotNull();
    assertThat(lagretOpptak.getAktiv()).isTrue();

    // Verifiser at det kan hentes fra database
    Optional<Opptak> hentetOpptak = repository.findOpptakById(lagretOpptak.getId());
    assertThat(hentetOpptak).isPresent();
    assertThat(hentetOpptak.get().getNavn()).isEqualTo("Test Opptak");
  }
}
