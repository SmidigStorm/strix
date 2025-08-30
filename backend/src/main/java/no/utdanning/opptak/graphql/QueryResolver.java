package no.utdanning.opptak.graphql;

import java.util.List;
import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.repository.OpptakRepository;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QueryResolver {

  private final OpptakRepository repository;

  public QueryResolver(OpptakRepository repository) {
    this.repository = repository;
  }

  @SchemaMapping(typeName = "Organisasjon", field = "utdanninger")
  public List<Utdanning> organisasjonUtdanninger(Organisasjon organisasjon) {
    return repository.findUtdanningerByOrganisasjonId(organisasjon.getId());
  }

  @SchemaMapping(typeName = "Opptak", field = "utdanninger")
  public List<UtdanningIOpptak> opptakUtdanninger(Opptak opptak) {
    return repository.findUtdanningerByOpptakId(opptak.getId());
  }
}
