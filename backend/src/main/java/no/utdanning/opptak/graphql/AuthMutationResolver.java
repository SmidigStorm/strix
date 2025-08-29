package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.graphql.dto.LoginInput;
import no.utdanning.opptak.graphql.dto.LoginResult;
import no.utdanning.opptak.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthMutationResolver {

  @Autowired private AuthService authService;

  @MutationMapping
  public LoginResult login(@Argument LoginInput input) {
    try {
      String token = authService.login(input.getEmail(), input.getPassord());

      // Hent brukerinfo for response
      Bruker bruker = authService.getBrukerByEmail(input.getEmail());

      return new LoginResult(token, bruker);
    } catch (SecurityException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
