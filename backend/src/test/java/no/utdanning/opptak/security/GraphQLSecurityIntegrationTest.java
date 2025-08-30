package no.utdanning.opptak.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import no.utdanning.opptak.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("GraphQL Security Integration Tests - Async Handling")
class GraphQLSecurityIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtService jwtService;
  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should block unauthorized GraphQL request with proper error")
  void skalBlokkerereUnauthorizedGraphQLRequest() throws Exception {
    // Given
    String graphqlQuery =
        """
        {
          "query": "{ organisasjoner { id navn } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(post("/graphql").contentType(MediaType.APPLICATION_JSON).content(graphqlQuery))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("En intern feil oppstod"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Should allow authorized GraphQL request with valid JWT")
  void skalTillateAuthorizedGraphQLRequestMedGyldigJWT() throws Exception {
    // Given
    String userId = "BRUKER-TEST";
    String email = "test@strix.no";
    String navn = "Test Administrator";
    String token =
        jwtService.generateToken(userId, email, navn, Arrays.asList("ADMINISTRATOR"), null);

    String graphqlQuery =
        """
        {
          "query": "{ organisasjoner { id navn } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/graphql")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(graphqlQuery))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.organisasjoner").isArray())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should block role-restricted mutation for insufficient role")
  void skalBlokkerereRolleBegrensetMutationForUtilstrekkeligRolle() throws Exception {
    // Given - SOKNADSBEHANDLER should not be able to create organizations
    String userId = "BRUKER-BEHANDLER";
    String email = "behandler@test.no";
    String navn = "Test Behandler";
    String token =
        jwtService.generateToken(userId, email, navn, Arrays.asList("SOKNADSBEHANDLER"), "ORG-001");

    String graphqlMutation =
        """
        {
          "query": "mutation { opprettOrganisasjon(input: {navn: \\"Test Org\\", kortNavn: \\"TEST\\", organisasjonsnummer: \\"123456789\\", organisasjonstype: UNIVERSITET, epost: \\"test@test.no\\"}) { id navn } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/graphql")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(graphqlMutation))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("En intern feil oppstod"))
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  @DisplayName("Should allow login without authentication")
  void skalTillateLoginUtenAuthentication() throws Exception {
    // Given
    String loginMutation =
        """
        {
          "query": "mutation { login(input: {email: \\"admin@strix.no\\", passord: \\"test123\\"}) { token bruker { id navn } } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/graphql").contentType(MediaType.APPLICATION_JSON).content(loginMutation))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    MvcResult result =
        mockMvc
            .perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.data.login.token").exists())
            .andExpect(jsonPath("$.data.login.bruker.navn").value("Sara Administrator"))
            .andExpect(jsonPath("$.errors").doesNotExist())
            .andReturn();

    // Verify token is valid
    String responseJson = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseJson);
    String token = jsonNode.get("data").get("login").get("token").asText();

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(jwtService.isTokenValid(token)).isTrue();
  }

  @Test
  @DisplayName("Should allow testBrukere query without authentication")
  void skalTillateTestBrukereQueryUtenAuthentication() throws Exception {
    // Given
    String testUsersQuery =
        """
        {
          "query": "{ testBrukere { email navn roller } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/graphql").contentType(MediaType.APPLICATION_JSON).content(testUsersQuery))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.data.testBrukere").isArray())
        .andExpect(jsonPath("$.data.testBrukere[0].email").exists())
        .andExpect(jsonPath("$.errors").doesNotExist());
  }

  @Test
  @DisplayName("Should handle malformed JWT token gracefully")
  void skalHandtereMalformedJwtTokenGracefully() throws Exception {
    // Given
    String malformedToken = "this.is.not.a.valid.jwt.token";
    String graphqlQuery =
        """
        {
          "query": "{ organisasjoner { id navn } }"
        }
        """;

    // When - start async request
    MvcResult mvcResult =
        mockMvc
            .perform(
                post("/graphql")
                    .header("Authorization", "Bearer " + malformedToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(graphqlQuery))
            .andExpect(request().asyncStarted())
            .andReturn();

    // Then - dispatch async and verify response
    mockMvc
        .perform(asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.errors").isArray())
        .andExpect(jsonPath("$.errors[0].message").value("En intern feil oppstod"))
        .andExpect(jsonPath("$.data").isEmpty());
  }
}
