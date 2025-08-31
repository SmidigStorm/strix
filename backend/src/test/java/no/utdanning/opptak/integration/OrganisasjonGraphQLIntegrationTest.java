package no.utdanning.opptak.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DisplayName("Organisasjon GraphQL Integration Tests")
class OrganisasjonGraphQLIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtService jwtService;
  @Autowired private ObjectMapper objectMapper;

  private String adminToken;
  private String opptakslederToken;
  private String sokerToken;

  @BeforeEach
  void setUp() {
    // Create JWT tokens using realistic test users from the database
    adminToken = jwtService.generateToken(
        "BRUKER-ADMIN", "admin@strix.no", "Sara Administrator", 
        Arrays.asList("ADMINISTRATOR"), null);

    opptakslederToken = jwtService.generateToken(
        "BRUKER-OPPTAKSLEDER-UIO", "opptaksleder@uio.no", "Per Opptaksleder", 
        Arrays.asList("OPPTAKSLEDER"), "uio");

    sokerToken = jwtService.generateToken(
        "BRUKER-SOKER", "soker@student.no", "Astrid Søker", 
        Arrays.asList("SOKER"), null);
  }

  @Test
  @DisplayName("Should get all organisasjoner with filtering")
  void skalHenteAlleOrganisasjonerMedFiltrering() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { organisasjoner(filter: { organisasjonstype: UNIVERSITET, aktiv: true }) { id navn kortNavn type organisasjonsnummer aktiv } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult result = mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.organisasjoner").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();

    // Verify response contains expected data
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode organisasjoner = jsonNode.get("data").get("organisasjoner");
    
    assertThat(organisasjoner).isNotNull();
    assertThat(organisasjoner.isArray()).isTrue();
    
    // Verify all returned organisations are universities
    for (JsonNode org : organisasjoner) {
      assertThat(org.get("type").asText()).isEqualTo("UNIVERSITET");
      assertThat(org.get("aktiv").asBoolean()).isTrue();
    }
  }

  @Test
  @DisplayName("Should get specific organisasjon by ID")
  void skalHenteSpesifikkOrganisasjonMedId() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { organisasjon(id: \\"ntnu\\") { id navn kortNavn type organisasjonsnummer epost telefon adresse poststed postnummer nettside opprettet aktiv utdanninger { id navn } } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + opptakslederToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.organisasjon.id").value("ntnu"))
        .andExpect(jsonPath("$.data.organisasjon.navn").value("Norges teknisk-naturvitenskapelige universitet"))
        .andExpect(jsonPath("$.data.organisasjon.kortNavn").value("NTNU"))
        .andExpect(jsonPath("$.data.organisasjon.type").value("UNIVERSITET"))
        .andExpect(jsonPath("$.data.organisasjon.organisasjonsnummer").value("974767880"))
        .andExpect(jsonPath("$.data.organisasjon.utdanninger").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should create new organisasjon with ADMINISTRATOR role")
  void skalOppretteNyOrganisasjonMedAdministratorRolle() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettOrganisasjon(input: { navn: \\"Test Høgskole\\", kortNavn: \\"TESTH\\", organisasjonsnummer: \\"999888777\\", organisasjonstype: HOGSKOLE, epost: \\"post@testhogskole.no\\", telefon: \\"12345678\\", adresse: \\"Testveien 1\\", poststed: \\"Testby\\", postnummer: \\"1234\\" }) { id navn kortNavn type organisasjonsnummer epost aktiv } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.navn").value("Test Høgskole"))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.kortNavn").value("TESTH"))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.type").value("HOGSKOLE"))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.organisasjonsnummer").value("999888777"))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.epost").value("post@testhogskole.no"))
        .andExpect(jsonPath("$.data.opprettOrganisasjon.aktiv").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should update existing organisasjon")
  void skalOppdatereEksisterendeOrganisasjon() throws Exception {
    // First create an organisation to update
    String createMutation = """
        {
          "query": "mutation { opprettOrganisasjon(input: { navn: \\"Organisasjon for Update\\", kortNavn: \\"OFU\\", organisasjonsnummer: \\"888777666\\", organisasjonstype: PRIVAT, epost: \\"old@test.no\\" }) { id } }"
        }
        """;

    MvcResult createResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult createResponse = mockMvc
        .perform(asyncDispatch(createResult))
        .andExpect(status().isOk())
        .andReturn();

    String createResponseJson = createResponse.getResponse().getContentAsString();
    JsonNode createJsonNode = objectMapper.readTree(createResponseJson);
    String createdId = createJsonNode.get("data").get("opprettOrganisasjon").get("id").asText();

    // Then update it
    String updateMutation = String.format("""
        {
          "query": "mutation { oppdaterOrganisasjon(input: { id: \\"%s\\", navn: \\"Updated Organisation Name\\", epost: \\"new@test.no\\", telefon: \\"99887766\\" }) { id navn epost telefon } }"
        }
        """, createdId);

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.oppdaterOrganisasjon.id").value(createdId))
        .andExpect(jsonPath("$.data.oppdaterOrganisasjon.navn").value("Updated Organisation Name"))
        .andExpect(jsonPath("$.data.oppdaterOrganisasjon.epost").value("new@test.no"))
        .andExpect(jsonPath("$.data.oppdaterOrganisasjon.telefon").value("99887766"))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should deactivate and reactivate organisasjon")
  void skalDeaktivereOgReaktivereOrganisasjon() throws Exception {
    // First create an organisation
    String createMutation = """
        {
          "query": "mutation { opprettOrganisasjon(input: { navn: \\"Organisasjon for Deactivation\\", kortNavn: \\"OFD\\", organisasjonsnummer: \\"777666555\\", organisasjonstype: FAGSKOLE, epost: \\"test@fagskole.no\\" }) { id aktiv } }"
        }
        """;

    MvcResult createResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(createMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult createResponse = mockMvc
        .perform(asyncDispatch(createResult))
        .andExpect(status().isOk())
        .andReturn();

    String createResponseJson = createResponse.getResponse().getContentAsString();
    JsonNode createJsonNode = objectMapper.readTree(createResponseJson);
    String createdId = createJsonNode.get("data").get("opprettOrganisasjon").get("id").asText();

    // Deactivate
    String deactivateMutation = String.format("""
        {
          "query": "mutation { deaktiverOrganisasjon(id: \\"%s\\") { id aktiv } }"
        }
        """, createdId);

    MvcResult deactivateResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(deactivateMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(deactivateResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.deaktiverOrganisasjon.id").value(createdId))
        .andExpect(jsonPath("$.data.deaktiverOrganisasjon.aktiv").value(false))
        .andExpect(jsonPath("$.errors").doesNotExist());

    // Reactivate
    String reactivateMutation = String.format("""
        {
          "query": "mutation { reaktiverOrganisasjon(id: \\"%s\\") { id aktiv } }"
        }
        """, createdId);

    MvcResult reactivateResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(reactivateMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(reactivateResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.reaktiverOrganisasjon.id").value(createdId))
        .andExpect(jsonPath("$.data.reaktiverOrganisasjon.aktiv").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should reject OPPTAKSLEDER from creating organisation")
  void skalAvviseOpptakslederFraOppretteOrganisasjon() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettOrganisasjon(input: { navn: \\"Unauthorized Org\\", kortNavn: \\"UNAUTH\\", organisasjonsnummer: \\"666555444\\", organisasjonstype: PRIVAT, epost: \\"unauth@test.no\\" }) { id navn } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + opptakslederToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("Access Denied"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Should reject SOKER from accessing organisation mutations")
  void skalAvviseSokerFraTilgangeOrganisasjonMutations() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { deaktiverOrganisasjon(id: \\"ntnu\\") { id aktiv } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + sokerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("Access Denied"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Should reject organisation queries without authentication")
  void skalAvviseOrganisasjonQueriesUtenAutentisering() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { organisasjon(id: \\"ntnu\\") { id navn } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("Access Denied"))
        .andExpect(jsonPath("$.data.organisasjon").doesNotExist());
  }

  @Test
  @DisplayName("Should handle duplicate organisation number")
  void skalHandtereDuplikatOrganisasjonsnummer() throws Exception {
    // Try to create an organisation with existing org number (NTNU's)
    String graphqlMutation = """
        {
          "query": "mutation { opprettOrganisasjon(input: { navn: \\"Duplicate Org\\", kortNavn: \\"DUP\\", organisasjonsnummer: \\"974767880\\", organisasjonstype: UNIVERSITET, epost: \\"dup@test.no\\" }) { id navn } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value(containsString("allerede registrert")));
  }

  @Test
  @DisplayName("Should return null for non-existent organisation ID")
  void skalReturnereNullForIkkeEksisterendeOrganisasjonId() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { organisasjon(id: \\"non-existent-org\\") { id navn } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.organisasjon").doesNotExist())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should filter organisations by name search")
  void skalFiltrereOrganisasjonerMedNavnSok() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { organisasjoner(filter: { navnSok: \\"universitet\\" }) { id navn } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult result = mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.organisasjoner").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();

    // Verify all returned organisations contain "universitet" in their name
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode organisasjoner = jsonNode.get("data").get("organisasjoner");
    
    for (JsonNode org : organisasjoner) {
      String navn = org.get("navn").asText().toLowerCase();
      assertThat(navn).contains("universitet");
    }
  }
}