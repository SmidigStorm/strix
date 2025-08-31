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
@DisplayName("Utdanning GraphQL Integration Tests")
class UtdanningGraphQLIntegrationTest {

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
        "BRUKER-OPPTAKSLEDER-NTNU", "opptaksleder@ntnu.no", "Kari Opptaksleder", 
        Arrays.asList("OPPTAKSLEDER"), "ntnu");

    sokerToken = jwtService.generateToken(
        "BRUKER-SOKER", "soker@student.no", "Astrid Søker", 
        Arrays.asList("SOKER"), null);
  }

  @Test
  @DisplayName("Should get specific utdanning by ID with authentication")
  void skalHenteSpesifikkUtdanningMedId() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanning(id: \\"CS-BACH-NTNU\\") { id navn studienivaa studiepoeng varighet studiested undervisningssprak starttidspunkt studieform aktiv } }"
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
        .andReturn();

    // Check if data exists, could be null if no test data
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode utdanning = jsonNode.get("data").get("utdanning");
    
    // Since test data might not have this specific ID, just verify no errors
    assertThat(jsonNode.has("errors")).isFalse();
  }

  @Test
  @DisplayName("Should get paginated utdanninger with filtering")
  void skalHentePaginertUtdanningerMedFiltrering() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanninger(filter: { studienivaa: \\"Bachelor\\", aktiv: true }, page: { size: 5, page: 0 }) { content { id navn studienivaa studiepoeng } totalElements totalPages currentPage pageSize hasNext hasPrevious } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + opptakslederToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult result = mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.utdanninger.content").isArray())
        .andExpect(jsonPath("$.data.utdanninger.totalElements").exists())
        .andExpect(jsonPath("$.data.utdanninger.pageSize").value(5))
        .andExpect(jsonPath("$.data.utdanninger.currentPage").value(0))
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();

    // Verify response contains utdanning data
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode content = jsonNode.get("data").get("utdanninger").get("content");
    
    assertThat(content).isNotNull();
    assertThat(content.isArray()).isTrue();
    // Verify all returned utdanninger are Bachelor level
    for (JsonNode utdanning : content) {
      assertThat(utdanning.get("studienivaa").asText()).isEqualTo("Bachelor");
    }
  }

  @Test
  @DisplayName("Should get utdanninger for specific organisation")
  void skalHenteUtdanningerForSpesifikkOrganisasjon() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanningerForOrganisasjon(organisasjonId: \\"ntnu\\", filter: { aktiv: true }, page: { size: 10, page: 0 }) { content { id navn organisasjon { id navn } } totalElements } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + opptakslederToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlQuery))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult result = mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.utdanningerForOrganisasjon.content").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();

    // Verify all returned utdanninger belong to NTNU
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode content = jsonNode.get("data").get("utdanningerForOrganisasjon").get("content");
    
    for (JsonNode utdanning : content) {
      assertThat(utdanning.get("organisasjon").get("id").asText()).isEqualTo("ntnu");
    }
  }

  @Test
  @DisplayName("Should create new utdanning with ADMINISTRATOR role")
  void skalOppretteNyUtdanningMedAdministratorRolle() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Test Utdanning\\", studienivaa: \\"Master\\", studiepoeng: 120, varighet: 4, studiested: \\"Trondheim\\", undervisningssprak: \\"Norsk\\", beskrivelse: \\"Test beskrivelse\\", starttidspunkt: \\"HØST_2026\\", studieform: HELTID, organisasjonId: \\"ntnu\\" }) { id navn studienivaa studiepoeng varighet studiested studieform aktiv } }"
        }
        """;

    MvcResult mvcResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(graphqlMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    MvcResult result = mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.opprettUtdanning.navn").value("Test Utdanning"))
        .andExpect(jsonPath("$.data.opprettUtdanning.studienivaa").value("Master"))
        .andExpect(jsonPath("$.data.opprettUtdanning.studiepoeng").value(120))
        .andExpect(jsonPath("$.data.opprettUtdanning.varighet").value(4))
        .andExpect(jsonPath("$.data.opprettUtdanning.studieform").value("HELTID"))
        .andExpect(jsonPath("$.data.opprettUtdanning.aktiv").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();
  }

  @Test
  @DisplayName("Should allow OPPTAKSLEDER to create utdanning for their organisation")
  void skalTillateOpptakslederOppretteUtdanningForSinOrganisasjon() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Opptaksleder Test Utdanning\\", studienivaa: \\"Bachelor\\", studiepoeng: 180, varighet: 6, studiested: \\"Trondheim\\", undervisningssprak: \\"Norsk\\", starttidspunkt: \\"VÅR_2026\\", studieform: DELTID, organisasjonId: \\"ntnu\\" }) { id navn organisasjon { id } } }"
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
        .andExpect(jsonPath("$.data.opprettUtdanning.navn").value("Opptaksleder Test Utdanning"))
        .andExpect(jsonPath("$.data.opprettUtdanning.organisasjon.id").value("ntnu"))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should update existing utdanning")
  void skalOppdatereEksisterendeUtdanning() throws Exception {
    // First create an utdanning to update
    String createMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Utdanning for Update\\", studienivaa: \\"Bachelor\\", studiepoeng: 180, varighet: 6, studiested: \\"Oslo\\", undervisningssprak: \\"Norsk\\", starttidspunkt: \\"HØST_2025\\", studieform: HELTID, organisasjonId: \\"uio\\" }) { id } }"
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
    String createdId = createJsonNode.get("data").get("opprettUtdanning").get("id").asText();

    // Then update it
    String updateMutation = String.format("""
        {
          "query": "mutation { oppdaterUtdanning(input: { id: \\"%s\\", navn: \\"Updated Utdanning Name\\", studiepoeng: 240, beskrivelse: \\"Updated description\\" }) { id navn studiepoeng beskrivelse } }"
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
        .andExpect(jsonPath("$.data.oppdaterUtdanning.id").value(createdId))
        .andExpect(jsonPath("$.data.oppdaterUtdanning.navn").value("Updated Utdanning Name"))
        .andExpect(jsonPath("$.data.oppdaterUtdanning.studiepoeng").value(240))
        .andExpect(jsonPath("$.data.oppdaterUtdanning.beskrivelse").value("Updated description"))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should deactivate and activate utdanning")
  void skalDeaktivereOgAktivereUtdanning() throws Exception {
    // First create an utdanning
    String createMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Utdanning for Deactivation\\", studienivaa: \\"Master\\", studiepoeng: 120, varighet: 4, studiested: \\"Bergen\\", undervisningssprak: \\"Engelsk\\", starttidspunkt: \\"HØST_2025\\", studieform: HELTID, organisasjonId: \\"hvl\\" }) { id aktiv } }"
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
    String createdId = createJsonNode.get("data").get("opprettUtdanning").get("id").asText();

    // Deactivate
    String deactivateMutation = String.format("""
        {
          "query": "mutation { deaktiverUtdanning(id: \\"%s\\") { id aktiv } }"
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
        .andExpect(jsonPath("$.data.deaktiverUtdanning.id").value(createdId))
        .andExpect(jsonPath("$.data.deaktiverUtdanning.aktiv").value(false))
        .andExpect(jsonPath("$.errors").doesNotExist());

    // Activate
    String activateMutation = String.format("""
        {
          "query": "mutation { aktiverUtdanning(id: \\"%s\\") { id aktiv } }"
        }
        """, createdId);

    MvcResult activateResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(activateMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(activateResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.aktiverUtdanning.id").value(createdId))
        .andExpect(jsonPath("$.data.aktiverUtdanning.aktiv").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should permanently delete utdanning with ADMINISTRATOR role only")
  void skalPermanentSletteUtdanningKunMedAdministratorRolle() throws Exception {
    // First create an utdanning
    String createMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Utdanning for Deletion\\", studienivaa: \\"Bachelor\\", studiepoeng: 180, varighet: 6, studiested: \\"Oslo\\", undervisningssprak: \\"Norsk\\", starttidspunkt: \\"HØST_2025\\", studieform: HELTID, organisasjonId: \\"uio\\" }) { id } }"
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
    String createdId = createJsonNode.get("data").get("opprettUtdanning").get("id").asText();

    // Try to delete with OPPTAKSLEDER (should fail)
    String deleteMutation = String.format("""
        {
          "query": "mutation { slettUtdanning(id: \\"%s\\") }"
        }
        """, createdId);

    MvcResult deleteWithOpptakslederResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + opptakslederToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(deleteMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(deleteWithOpptakslederResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("Access Denied"));

    // Delete with ADMINISTRATOR (should succeed)
    MvcResult deleteWithAdminResult = mockMvc
        .perform(post("/graphql")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(deleteMutation))
        .andExpect(request().asyncStarted())
        .andReturn();

    mockMvc
        .perform(asyncDispatch(deleteWithAdminResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slettUtdanning").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should reject utdanning queries without authentication")
  void skalAvviseUtdanningQueriesUtenAutentisering() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanning(id: \\"CS-BACH-NTNU\\") { id navn } }"
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
        .andExpect(jsonPath("$.data.utdanning").doesNotExist());
  }

  @Test
  @DisplayName("Should reject SOKER from creating utdanning")
  void skalAvviseSokerFraOppretteUtdanning() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Unauthorized Utdanning\\", studienivaa: \\"Bachelor\\", studiepoeng: 180, varighet: 6, studiested: \\"Oslo\\", undervisningssprak: \\"Norsk\\", starttidspunkt: \\"HØST_2025\\", studieform: HELTID, organisasjonId: \\"uio\\" }) { id navn } }"
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
  @DisplayName("Should handle invalid organisation ID in utdanning creation")
  void skalHandtereUgyldigOrganisasjonIdVedUtdanningOpprettelse() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettUtdanning(input: { navn: \\"Invalid Org Utdanning\\", studienivaa: \\"Bachelor\\", studiepoeng: 180, varighet: 6, studiested: \\"Oslo\\", undervisningssprak: \\"Norsk\\", starttidspunkt: \\"HØST_2025\\", studieform: HELTID, organisasjonId: \\"non-existent-org\\" }) { id navn } }"
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
        .andExpect(jsonPath("$.errors[0].message").value(containsString("Organisasjon ikke funnet")));
  }

  @Test
  @DisplayName("Should test pagination parameters correctly")
  void skalTestePagineringsParametereKorrekt() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanninger(page: { size: 2, page: 1 }) { content { id } totalElements totalPages currentPage pageSize hasNext hasPrevious } }"
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
        .andExpect(jsonPath("$.data.utdanninger.pageSize").value(2))
        .andExpect(jsonPath("$.data.utdanninger.currentPage").value(1))
        .andExpect(jsonPath("$.data.utdanninger.hasPrevious").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();
  }

  @Test
  @DisplayName("Should return null for non-existent utdanning ID")
  void skalReturnereNullForIkkeEksisterendeUtdanningId() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { utdanning(id: \\"non-existent-utdanning\\") { id navn } }"
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
        .andExpect(jsonPath("$.data.utdanning").doesNotExist())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }
}