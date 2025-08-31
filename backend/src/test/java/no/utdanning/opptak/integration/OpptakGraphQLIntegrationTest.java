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
@DisplayName("Opptak GraphQL Integration Tests")
class OpptakGraphQLIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtService jwtService;
  @Autowired private ObjectMapper objectMapper;

  private String adminToken;
  private String opptakslederToken;
  private String sokerToken;

  @BeforeEach
  void setUp() {
    // Create JWT tokens using the realistic test users from the database
    adminToken = jwtService.generateToken(
        "BRUKER-ADMIN", "admin@strix.no", "Sara Administrator", 
        Arrays.asList("ADMINISTRATOR"), null);

    opptakslederToken = jwtService.generateToken(
        "BRUKER-OPPTAKSLEDER", "opptaksleder@ntnu.no", "Kari Opptaksleder", 
        Arrays.asList("OPPTAKSLEDER"), "ntnu");

    sokerToken = jwtService.generateToken(
        "BRUKER-SOKER", "soker@student.no", "Astrid Søker", 
        Arrays.asList("SOKER"), null);
  }

  @Test
  @DisplayName("Should get all opptak with admin token")
  void skalHenteAlleOpptakMedAdminToken() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { alleOpptak { id navn type aar status aktiv } }"
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
        .andExpect(jsonPath("$.data.alleOpptak").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();

    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    JsonNode opptakList = jsonNode.get("data").get("alleOpptak");
    
    assertThat(opptakList).isNotNull();
    assertThat(opptakList.size()).isGreaterThan(0);
  }

  @Test
  @DisplayName("Should get specific opptak by ID with admin token")
  void skalHenteSpesifiktOpptakMedAdminToken() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { opptak(id: \\"samordnet-uhg-h25\\") { id navn type aar status aktiv } }"
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
        .andExpect(jsonPath("$.data.opptak.id").value("samordnet-uhg-h25"))
        .andExpect(jsonPath("$.data.opptak.navn").value("Samordnet opptak høst 2025"))
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();
  }

  @Test
  @DisplayName("Should create new opptak with admin token")
  void skalOppretteNyttOpptakMedAdminToken() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettOpptak(input: { navn: \\"Integration Test Opptak\\", type: LOKALT, aar: 2026, administratorOrganisasjonId: \\"ntnu\\", beskrivelse: \\"Test opptak fra integration test\\" }) { id navn type aar status aktiv } }"
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
        .andExpect(jsonPath("$.data.opprettOpptak.navn").value("Integration Test Opptak"))
        .andExpect(jsonPath("$.data.opprettOpptak.type").value("LOKALT"))
        .andExpect(jsonPath("$.data.opprettOpptak.aar").value(2026))
        .andExpect(jsonPath("$.data.opprettOpptak.status").value("FREMTIDIG"))
        .andExpect(jsonPath("$.data.opprettOpptak.aktiv").value(true))
        .andExpect(jsonPath("$.errors").doesNotExist())
        .andReturn();
  }

  @Test
  @DisplayName("Should allow OPPTAKSLEDER to access opptak queries")
  void skalTillateOpptakslederATilgangeOpptakQueries() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { alleOpptak { id navn type } }"
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
        .andExpect(jsonPath("$.data.alleOpptak").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should reject opptak queries without authentication")
  void skalAvviseOpptakQueriesUtenAuthentication() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { alleOpptak { id navn } }"
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
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Should reject opptak mutations without proper role")
  void skalAvviseOpptakMutationsUtenRiktigRolle() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettOpptak(input: { navn: \\"Unauthorized Test\\", type: LOKALT, aar: 2026, administratorOrganisasjonId: \\"ntnu\\" }) { id navn } }"
        }
        """;

    // SOKER role should not be able to create opptak
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
  @DisplayName("Should handle invalid organisation ID in opptak creation")
  void skalHandtereUgyldigOrganisasjonIdVedOpptakOpprettelse() throws Exception {
    String graphqlMutation = """
        {
          "query": "mutation { opprettOpptak(input: { navn: \\"Invalid Org Test\\", type: LOKALT, aar: 2026, administratorOrganisasjonId: \\"non-existent-org\\" }) { id navn } }"
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
        .andExpect(jsonPath("$.errors[0].message").value(containsString("Administrator organisasjon ikke funnet")));
  }

  @Test
  @DisplayName("Should return null for non-existent opptak ID")
  void skalReturnereNullForIkkeEksisterendeOpptakId() throws Exception {
    String graphqlQuery = """
        {
          "query": "query { opptak(id: \\"non-existent-opptak\\") { id navn } }"
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
        .andExpect(jsonPath("$.data.opptak").doesNotExist())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }
}