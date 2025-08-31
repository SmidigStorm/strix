package no.utdanning.opptak.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.service.OpptakService;
import no.utdanning.opptak.graphql.OpptakQueryResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class OpptakQueryResolverTest {

  @Mock private OpptakService opptakService;

  private OpptakQueryResolver queryResolver;

  @BeforeEach
  void setUp() {
    queryResolver = new OpptakQueryResolver(opptakService);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalHenteAlleOpptakMedGyldigRolle() {
    // Arrange
    List<Opptak> opptak = Arrays.asList(createOpptak("1"), createOpptak("2"));
    when(opptakService.findAll()).thenReturn(opptak);

    // Act
    List<Opptak> result = queryResolver.alleOpptak();

    // Assert
    assertThat(result).hasSize(2);
    verify(opptakService).findAll();
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalHenteOpptakById() {
    // Arrange
    String opptakId = "opptak-1";
    Opptak opptak = createOpptak(opptakId);
    when(opptakService.findById(opptakId)).thenReturn(opptak);

    // Act
    Opptak result = queryResolver.opptak(opptakId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(opptakId);
    verify(opptakService).findById(opptakId);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalReturnereNullNaarOpptakIkkeFinnes() {
    // Arrange
    String opptakId = "ikke-eksisterende";
    when(opptakService.findById(opptakId)).thenReturn(null);

    // Act
    Opptak result = queryResolver.opptak(opptakId);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalHenteOpptakForAdministratorOrganisasjon() {
    // Arrange
    String organisasjonId = "ntnu";
    List<Opptak> opptak = Arrays.asList(createOpptak("1"));
    when(opptakService.findByAdministratorOrganisasjon(organisasjonId)).thenReturn(opptak);

    // Act
    List<Opptak> result = queryResolver.opptakForAdministratorOrganisasjon(organisasjonId);

    // Assert
    assertThat(result).hasSize(1);
    verify(opptakService).findByAdministratorOrganisasjon(organisasjonId);
  }

  @Test
  @WithMockUser(roles = "OPPTAKSLEDER")
  void skalHenteTilgjengeligeOpptakForOrganisasjon() {
    // Arrange
    String organisasjonId = "ntnu";
    List<Opptak> opptak = Arrays.asList(createOpptak("1"), createOpptak("2"));
    when(opptakService.findTilgjengeligeForOrganisasjon(organisasjonId)).thenReturn(opptak);

    // Act
    List<Opptak> result = queryResolver.tilgjengeligeOpptakForOrganisasjon(organisasjonId);

    // Assert
    assertThat(result).hasSize(2);
    verify(opptakService).findTilgjengeligeForOrganisasjon(organisasjonId);
  }

  @Test
  void skalHenteAdministratorOrganisasjonForOpptak() {
    // Arrange
    Opptak opptak = createOpptak("1");
    Organisasjon org = new Organisasjon();
    org.setId("ntnu");
    org.setNavn("NTNU");
    when(opptakService.getAdministratorOrganisasjon(opptak)).thenReturn(org);

    // Act
    Organisasjon result = queryResolver.administrator(opptak);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("ntnu");
    verify(opptakService).getAdministratorOrganisasjon(opptak);
  }

  @Test
  void skalHenteTillateTilgangsorganisasjonerForOpptak() {
    // Arrange
    Opptak opptak = createOpptak("1");
    Organisasjon org1 = new Organisasjon();
    org1.setId("uio");
    Organisasjon org2 = new Organisasjon();
    org2.setId("hvl");
    List<Organisasjon> orgs = Arrays.asList(org1, org2);
    when(opptakService.getTillateTilgangsorganisasjoner(opptak)).thenReturn(orgs);

    // Act
    List<Organisasjon> result = queryResolver.tillatteTilgangsorganisasjoner(opptak);

    // Assert
    assertThat(result).hasSize(2);
    verify(opptakService).getTillateTilgangsorganisasjoner(opptak);
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