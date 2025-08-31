package no.utdanning.opptak.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.graphql.dto.EndreOpptaksStatusInput;
import no.utdanning.opptak.graphql.dto.OppdaterOpptakInput;
import no.utdanning.opptak.graphql.dto.OpprettOpptakInput;
import no.utdanning.opptak.service.OpptakService;
import no.utdanning.opptak.graphql.OpptakMutationResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class OpptakMutationResolverTest {

  @Mock private OpptakService opptakService;

  private OpptakMutationResolver mutationResolver;

  @BeforeEach
  void setUp() {
    mutationResolver = new OpptakMutationResolver(opptakService);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalOppretteNyttOpptak() {
    // Arrange
    OpprettOpptakInput input = new OpprettOpptakInput();
    input.setNavn("Nytt Opptak");
    input.setType(OpptaksType.LOKALT);
    input.setAar(2025);
    input.setAdministratorOrganisasjonId("ntnu");

    Opptak opptak = createOpptak("new-1");
    when(opptakService.opprettOpptak(input)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.opprettOpptak(input);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("new-1");
    verify(opptakService).opprettOpptak(input);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalOppdatereOpptak() {
    // Arrange
    OppdaterOpptakInput input = new OppdaterOpptakInput();
    input.setId("opptak-1");
    input.setNavn("Oppdatert Navn");
    input.setBeskrivelse("Ny beskrivelse");

    Opptak opptak = createOpptak("opptak-1");
    opptak.setNavn("Oppdatert Navn");
    opptak.setBeskrivelse("Ny beskrivelse");
    when(opptakService.oppdaterOpptak(input)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.oppdaterOpptak(input);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getNavn()).isEqualTo("Oppdatert Navn");
    assertThat(result.getBeskrivelse()).isEqualTo("Ny beskrivelse");
    verify(opptakService).oppdaterOpptak(input);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalEndreOpptaksStatus() {
    // Arrange
    EndreOpptaksStatusInput input = new EndreOpptaksStatusInput();
    input.setOpptakId("opptak-1");
    input.setNyStatus(OpptaksStatus.APENT);

    Opptak opptak = createOpptak("opptak-1");
    opptak.setStatus(OpptaksStatus.APENT);
    when(opptakService.endreStatus(input)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.endreOpptaksStatus(input);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(OpptaksStatus.APENT);
    verify(opptakService).endreStatus(input);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalDeaktivereOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setAktiv(false);
    when(opptakService.deaktiverOpptak(opptakId)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.deaktiverOpptak(opptakId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getAktiv()).isFalse();
    verify(opptakService).deaktiverOpptak(opptakId);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalReaktivereOpptak() {
    // Arrange
    String opptakId = "opptak-1";
    
    Opptak opptak = createOpptak(opptakId);
    opptak.setAktiv(true);
    when(opptakService.reaktiverOpptak(opptakId)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.reaktiverOpptak(opptakId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getAktiv()).isTrue();
    verify(opptakService).reaktiverOpptak(opptakId);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalGiOrganisasjonTilgang() {
    // Arrange
    String opptakId = "opptak-1";
    String organisasjonId = "uio";
    
    Opptak opptak = createOpptak(opptakId);
    when(opptakService.giOrganisasjonTilgang(opptakId, organisasjonId)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.giOrganisasjonOpptakTilgang(opptakId, organisasjonId);

    // Assert
    assertThat(result).isNotNull();
    verify(opptakService).giOrganisasjonTilgang(opptakId, organisasjonId);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalFjerneOrganisasjonTilgang() {
    // Arrange
    String opptakId = "opptak-1";
    String organisasjonId = "uio";
    
    Opptak opptak = createOpptak(opptakId);
    when(opptakService.fjernOrganisasjonTilgang(opptakId, organisasjonId)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.fjernOrganisasjonOpptakTilgang(opptakId, organisasjonId);

    // Assert
    assertThat(result).isNotNull();
    verify(opptakService).fjernOrganisasjonTilgang(opptakId, organisasjonId);
  }

  @Test
  void skalKalleOpptakServiceVedOpprettOpptak() {
    // Note: Security testing (@PreAuthorize) happens in integration tests
    // Arrange
    OpprettOpptakInput input = new OpprettOpptakInput();
    input.setNavn("Nytt Opptak");
    input.setType(OpptaksType.LOKALT);
    input.setAar(2025);
    input.setAdministratorOrganisasjonId("ntnu");
    
    Opptak expectedOpptak = createOpptak("opptak-1");
    when(opptakService.opprettOpptak(input)).thenReturn(expectedOpptak);

    // Act
    Opptak result = mutationResolver.opprettOpptak(input);

    // Assert
    assertThat(result).isNotNull();
    verify(opptakService).opprettOpptak(input);
  }

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void skalTillateAdministratorAaOppretteOpptak() {
    // Arrange
    OpprettOpptakInput input = new OpprettOpptakInput();
    input.setNavn("Admin Opptak");
    input.setType(OpptaksType.LOKALT);
    input.setAar(2025);
    input.setAdministratorOrganisasjonId("ntnu");

    Opptak opptak = createOpptak("admin-1");
    when(opptakService.opprettOpptak(input)).thenReturn(opptak);

    // Act
    Opptak result = mutationResolver.opprettOpptak(input);

    // Assert
    assertThat(result).isNotNull();
    verify(opptakService).opprettOpptak(input);
  }

  @Test
  void skalKalleOpptakServiceVedEndreOpptaksStatus() {
    // Note: Security testing (@PreAuthorize) happens in integration tests
    // Arrange
    EndreOpptaksStatusInput input = new EndreOpptaksStatusInput();
    input.setOpptakId("opptak-1");
    input.setNyStatus(OpptaksStatus.APENT);
    
    Opptak expectedOpptak = createOpptak("opptak-1");
    when(opptakService.endreStatus(input)).thenReturn(expectedOpptak);

    // Act
    Opptak result = mutationResolver.endreOpptaksStatus(input);

    // Assert
    assertThat(result).isNotNull();
    verify(opptakService).endreStatus(input);
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