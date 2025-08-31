package no.utdanning.opptak.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.graphql.dto.EndreOpptaksStatusInput;
import no.utdanning.opptak.graphql.dto.OppdaterOpptakInput;
import no.utdanning.opptak.graphql.dto.OpprettOpptakInput;
import no.utdanning.opptak.repository.JdbcOpptakRepository;
import no.utdanning.opptak.repository.JdbcOpptakTilgangRepository;
import no.utdanning.opptak.repository.JdbcOrganisasjonRepository;
import no.utdanning.opptak.service.OpptakService;
import no.utdanning.opptak.service.security.OpptakSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpptakServiceTest {

  @Mock private JdbcOpptakRepository opptakRepository;
  @Mock private JdbcOpptakTilgangRepository tilgangRepository;
  @Mock private JdbcOrganisasjonRepository organisasjonRepository;
  @Mock private OpptakSecurityService securityService;

  private OpptakService opptakService;

  @BeforeEach
  void setUp() {
    opptakService =
        new OpptakService(opptakRepository, tilgangRepository, organisasjonRepository, securityService);
  }

  @Test
  void skalFinneAlleOpptakForAdministrator() {
    // Arrange
    List<Opptak> alleOpptak = Arrays.asList(createOpptak("1"), createOpptak("2"));
    when(securityService.isAdministrator()).thenReturn(true);
    when(opptakRepository.findByAktiv(true)).thenReturn(alleOpptak);

    // Act
    List<Opptak> result = opptakService.findAll();

    // Assert
    assertThat(result).hasSize(2);
    verify(opptakRepository).findByAktiv(true);
  }

  @Test
  void skalFinneOpptakForBrukerMedOrganisasjon() {
    // Arrange
    String orgId = "ntnu";
    List<Opptak> adminOpptak = Arrays.asList(createOpptak("1"));
    when(securityService.isAdministrator()).thenReturn(false);
    when(securityService.getCurrentUserOrganisasjonId()).thenReturn(orgId);
    when(opptakRepository.findByAdministratorOrganisasjonId(orgId)).thenReturn(adminOpptak);
    when(tilgangRepository.findByOrganisasjonId(orgId)).thenReturn(Arrays.asList());

    // Act
    List<Opptak> result = opptakService.findAll();

    // Assert
    assertThat(result).hasSize(1);
  }

  @Test
  void skalOppretteNyttOpptak() {
    // Arrange
    OpprettOpptakInput input = new OpprettOpptakInput();
    input.setNavn("Test Opptak");
    input.setType(OpptaksType.LOKALT);
    input.setAar(2025);
    input.setAdministratorOrganisasjonId("ntnu");
    input.setSamordnet(false);
    input.setMaxUtdanningerPerSoknad(10);

    Organisasjon org = new Organisasjon();
    org.setId("ntnu");
    org.setNavn("NTNU");

    Opptak savedOpptak = createOpptak("new-id");
    savedOpptak.setNavn("Test Opptak");

    when(securityService.canManageOrganisasjon("ntnu")).thenReturn(true);
    when(organisasjonRepository.findById("ntnu")).thenReturn(org);
    when(opptakRepository.existsByNavn("Test Opptak")).thenReturn(false);
    when(opptakRepository.save(any(Opptak.class))).thenReturn(savedOpptak);

    // Act
    Opptak result = opptakService.opprettOpptak(input);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getNavn()).isEqualTo("Test Opptak");
    verify(opptakRepository).save(any(Opptak.class));
  }

  @Test
  void skalKasteExceptionVedOpprettingAvOpptakMedDuplikatNavn() {
    // Arrange
    OpprettOpptakInput input = new OpprettOpptakInput();
    input.setNavn("Eksisterende Opptak");
    input.setType(OpptaksType.LOKALT);
    input.setAar(2025);
    input.setAdministratorOrganisasjonId("ntnu");

    when(securityService.canManageOrganisasjon("ntnu")).thenReturn(true);
    when(organisasjonRepository.findById("ntnu")).thenReturn(new Organisasjon());
    when(opptakRepository.existsByNavn("Eksisterende Opptak")).thenReturn(true);

    // Act & Assert
    assertThatThrownBy(() -> opptakService.opprettOpptak(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("eksisterer allerede");
  }

  @Test
  void skalOppdatereEksisterendeOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    OppdaterOpptakInput input = new OppdaterOpptakInput();
    input.setId(opptakId);
    input.setNavn("Oppdatert Navn");
    input.setBeskrivelse("Ny beskrivelse");
    input.setMaxUtdanningerPerSoknad(15);

    Opptak eksisterende = createOpptak(opptakId);
    eksisterende.setNavn("Gammelt Navn");

    when(opptakRepository.findById(opptakId)).thenReturn(eksisterende);
    when(securityService.canManageOpptak(opptakId)).thenReturn(true);
    when(opptakRepository.existsByNavnAndIdNot("Oppdatert Navn", opptakId)).thenReturn(false);
    when(opptakRepository.save(any(Opptak.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Opptak result = opptakService.oppdaterOpptak(input);

    // Assert
    assertThat(result.getNavn()).isEqualTo("Oppdatert Navn");
    assertThat(result.getBeskrivelse()).isEqualTo("Ny beskrivelse");
    assertThat(result.getMaxUtdanningerPerSoknad()).isEqualTo(15);
  }

  @Test
  void skalEndreStatusPaaOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    EndreOpptaksStatusInput input = new EndreOpptaksStatusInput();
    input.setOpptakId(opptakId);
    input.setNyStatus(OpptaksStatus.APENT);

    Opptak opptak = createOpptak(opptakId);
    opptak.setStatus(OpptaksStatus.FREMTIDIG);

    when(opptakRepository.findById(opptakId)).thenReturn(opptak);
    when(securityService.canManageOpptak(opptakId)).thenReturn(true);
    when(opptakRepository.save(any(Opptak.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Opptak result = opptakService.endreStatus(input);

    // Assert
    assertThat(result.getStatus()).isEqualTo(OpptaksStatus.APENT);
  }

  @Test
  void skalKasteSecurityExceptionVedManglendeTilgang() {
    // Arrange
    String opptakId = "opptak-1";
    EndreOpptaksStatusInput input = new EndreOpptaksStatusInput();
    input.setOpptakId(opptakId);
    input.setNyStatus(OpptaksStatus.APENT);

    Opptak opptak = createOpptak(opptakId);
    when(opptakRepository.findById(opptakId)).thenReturn(opptak);
    when(securityService.canManageOpptak(opptakId)).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> opptakService.endreStatus(input))
        .isInstanceOf(SecurityException.class)
        .hasMessageContaining("Ingen tilgang");
  }

  @Test
  void skalDeaktivereOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    Opptak opptak = createOpptak(opptakId);
    opptak.setAktiv(true);

    when(opptakRepository.findById(opptakId)).thenReturn(opptak);
    when(securityService.canManageOpptak(opptakId)).thenReturn(true);
    when(opptakRepository.save(any(Opptak.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Opptak result = opptakService.deaktiverOpptak(opptakId);

    // Assert
    assertThat(result.getAktiv()).isFalse();
  }

  @Test
  void skalGiOrganisasjonTilgangTilSamordnetOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    String organisasjonId = "uio";
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setSamordnet(true);
    
    Organisasjon org = new Organisasjon();
    org.setId(organisasjonId);
    org.setNavn("UiO");

    when(opptakRepository.findById(opptakId)).thenReturn(opptak);
    when(securityService.canManageOpptak(opptakId)).thenReturn(true);
    when(organisasjonRepository.findById(organisasjonId)).thenReturn(org);
    when(tilgangRepository.hasAccess(opptakId, organisasjonId)).thenReturn(false);
    when(securityService.getCurrentUserId()).thenReturn("user-1");

    // Act
    Opptak result = opptakService.giOrganisasjonTilgang(opptakId, organisasjonId);

    // Assert
    assertThat(result).isNotNull();
    verify(tilgangRepository).save(any(OpptakTilgang.class));
  }

  @Test
  void skalKasteExceptionVedTilgangTilIkkeSamordnetOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    String organisasjonId = "uio";
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setSamordnet(false);

    when(opptakRepository.findById(opptakId)).thenReturn(opptak);

    // Act & Assert
    assertThatThrownBy(() -> opptakService.giOrganisasjonTilgang(opptakId, organisasjonId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("samordnede opptak");
  }

  private Opptak createOpptak(String id) {
    Opptak opptak = new Opptak();
    opptak.setId(id);
    opptak.setNavn("Test Opptak " + id);
    opptak.setType(OpptaksType.LOKALT);
    opptak.setAar(2025);
    opptak.setStatus(OpptaksStatus.FREMTIDIG);
    opptak.setAktiv(true);
    opptak.setSamordnet(false);
    opptak.setMaxUtdanningerPerSoknad(10);
    opptak.setAdministratorOrganisasjonId("ntnu");
    opptak.setOpprettet(java.time.LocalDateTime.now());
    return opptak;
  }
}