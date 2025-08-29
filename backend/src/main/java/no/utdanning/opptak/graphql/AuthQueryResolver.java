package no.utdanning.opptak.graphql;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.domain.Rolle;
import no.utdanning.opptak.graphql.dto.TestBruker;
import no.utdanning.opptak.repository.BrukerRepository;
import no.utdanning.opptak.service.AuthService;
import no.utdanning.opptak.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class AuthQueryResolver {

  @Autowired private AuthService authService;

  @Autowired private JwtService jwtService;

  @Autowired private BrukerRepository brukerRepository;

  @QueryMapping
  public Bruker meg() {
    String token = hentTokenFraRequest();

    if (token == null) {
      throw new SecurityException("Ingen gyldig JWT token funnet");
    }

    try {
      Claims claims = jwtService.validateToken(token);
      String email = jwtService.getEmail(claims);

      return authService.getBrukerByEmail(email);
    } catch (Exception e) {
      throw new SecurityException("Ugyldig token: " + e.getMessage());
    }
  }

  @QueryMapping
  public List<TestBruker> testBrukere() {
    return brukerRepository.findAll().stream()
        .map(
            bruker ->
                new TestBruker(
                    bruker.getEmail(),
                    bruker.getNavn(),
                    bruker.getRoller().stream()
                        .map(brukerRolle -> brukerRolle.getRolleId())
                        .collect(Collectors.toList()),
                    bruker.getOrganisasjonId(),
                    "test123"))
        .collect(Collectors.toList());
  }

  @SchemaMapping(typeName = "Bruker", field = "roller")
  public List<Rolle> brukerRoller(Bruker bruker) {
    return bruker.getRoller().stream()
        .map(brukerRolle -> brukerRolle.getRolle())
        .collect(Collectors.toList());
  }

  private String hentTokenFraRequest() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      return null;
    }

    HttpServletRequest request = attributes.getRequest();
    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }

    return null;
  }
}
