package no.utdanning.opptak.graphql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureGraphQlTester
@DisplayName("Auth GraphQL Integration Tests")
class AuthGraphQLIntegrationTest {

  @Autowired private GraphQlTester graphQlTester;

  @Test
  @DisplayName("Should login successfully with valid credentials")
  void skalLoggeInnMedGyldigeBrukerdataene() {
    String mutation =
        """
            mutation Login($input: LoginInput!) {
                login(input: $input) {
                    token
                    bruker {
                        id
                        email
                        navn
                        aktiv
                    }
                }
            }
        """;

    graphQlTester
        .document(mutation)
        .variable(
            "input",
            Map.of(
                "email", "opptaksleder@ntnu.no",
                "passord", "test123"))
        .execute()
        .path("login.token")
        .entity(String.class)
        .satisfies(
            token -> {
              assertThat(token).isNotNull();
              assertThat(token).isNotEmpty();
              assertThat(token.split("\\.")).hasSize(3); // JWT format
            })
        .path("login.bruker.email")
        .entity(String.class)
        .isEqualTo("opptaksleder@ntnu.no")
        .path("login.bruker.navn")
        .entity(String.class)
        .isEqualTo("Kari Opptaksleder")
        .path("login.bruker.aktiv")
        .entity(Boolean.class)
        .isEqualTo(true);
  }

  @Test
  @DisplayName("Should return error for invalid credentials")
  void skalReturnereErrorForUgyldigeBrukerdataene() {
    String mutation =
        """
            mutation Login($input: LoginInput!) {
                login(input: $input) {
                    token
                    bruker {
                        id
                        email
                        navn
                    }
                }
            }
        """;

    graphQlTester
        .document(mutation)
        .variable(
            "input",
            Map.of(
                "email", "opptaksleder@ntnu.no",
                "passord", "feilpassord"))
        .execute()
        .errors()
        .satisfy(
            errors -> {
              assertThat(errors).hasSize(1);
              // GraphQL error handler shows actual error message
              assertThat(errors.get(0).getMessage()).isEqualTo("Ugyldig email eller passord");
            });
  }

  @Test
  @DisplayName("Should return error for non-existent user")
  void skalReturnereErrorForIkkeEksisterendeBruker() {
    String mutation =
        """
            mutation Login($input: LoginInput!) {
                login(input: $input) {
                    token
                    bruker {
                        id
                        email
                        navn
                    }
                }
            }
        """;

    graphQlTester
        .document(mutation)
        .variable(
            "input",
            Map.of(
                "email", "finnesikke@example.com",
                "passord", "test123"))
        .execute()
        .errors()
        .satisfy(
            errors -> {
              assertThat(errors).hasSize(1);
              // GraphQL error handler shows actual error message
              assertThat(errors.get(0).getMessage()).isEqualTo("Ugyldig email eller passord");
            });
  }
}
