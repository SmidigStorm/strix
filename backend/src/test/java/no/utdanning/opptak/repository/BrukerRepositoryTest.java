package no.utdanning.opptak.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;
import no.utdanning.opptak.domain.Bruker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BrukerRepository Tests")
class BrukerRepositoryTest {

  @Autowired private BrukerRepository brukerRepository;

  @Test
  @DisplayName("Should find user by email")
  void skalFinneBrukerPaEmail() {
    Optional<Bruker> bruker = brukerRepository.findByEmail("opptaksleder@ntnu.no");

    assertThat(bruker).isPresent();
    assertThat(bruker.get().getNavn()).isEqualTo("Kari Opptaksleder");
    assertThat(bruker.get().getOrganisasjonId()).isEqualTo("ntnu");
    assertThat(bruker.get().getAktiv()).isTrue();
  }

  @Test
  @DisplayName("Should return empty when user not found by email")
  void skalReturnereEmptyNaarBrukerIkkeFinnes() {
    Optional<Bruker> bruker = brukerRepository.findByEmail("finnes.ikke@example.com");

    assertThat(bruker).isEmpty();
  }

  @Test
  @DisplayName("Should save new user successfully")
  void skalLagreNyBruker() {
    Bruker nyBruker = new Bruker();
    nyBruker.setId("TEST-BRUKER-001");
    nyBruker.setEmail("test@example.com");
    nyBruker.setNavn("Test Bruker");
    nyBruker.setOrganisasjonId("ntnu");
    nyBruker.setPassordHash("$2a$10$hashedPassword");
    nyBruker.setAktiv(true);

    Bruker lagretBruker = brukerRepository.save(nyBruker);

    assertThat(lagretBruker.getId()).isEqualTo("TEST-BRUKER-001");
    assertThat(lagretBruker.getEmail()).isEqualTo("test@example.com");
    assertThat(lagretBruker.getNavn()).isEqualTo("Test Bruker");
    assertThat(lagretBruker.getOpprettet()).isNotNull();
    assertThat(lagretBruker.getAktiv()).isTrue();

    // Verify it can be retrieved from database
    Optional<Bruker> hentetBruker = brukerRepository.findByEmail("test@example.com");
    assertThat(hentetBruker).isPresent();
    assertThat(hentetBruker.get().getNavn()).isEqualTo("Test Bruker");
  }

  @Test
  @DisplayName("Should update user last login time")
  void skalOppdatereSistInnlogget() {
    Optional<Bruker> bruker = brukerRepository.findByEmail("opptaksleder@ntnu.no");
    assertThat(bruker).isPresent();

    Bruker eksisterendeBruker = bruker.get();
    LocalDateTime beforeUpdate = LocalDateTime.now();
    eksisterendeBruker.setSistInnlogget(beforeUpdate);

    Bruker oppdatertBruker = brukerRepository.save(eksisterendeBruker);

    assertThat(oppdatertBruker.getSistInnlogget()).isNotNull();
    assertThat(oppdatertBruker.getSistInnlogget()).isAfterOrEqualTo(beforeUpdate);
  }
}
