package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.repository.OpptakRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QueryResolver {
    
    private final OpptakRepository repository;
    
    public QueryResolver(OpptakRepository repository) {
        this.repository = repository;
    }
    
    @QueryMapping
    public List<Organisasjon> organisasjoner() {
        return repository.findAllOrganisasjoner();
    }
    
    @QueryMapping
    public Organisasjon organisasjon(@Argument String id) {
        return repository.findOrganisasjonById(id).orElse(null);
    }
    
    @QueryMapping
    public List<Opptak> alleOpptak() {
        return repository.findAllOpptak();
    }
    
    @QueryMapping
    public Opptak opptak(@Argument String id) {
        return repository.findOpptakById(id).orElse(null);
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