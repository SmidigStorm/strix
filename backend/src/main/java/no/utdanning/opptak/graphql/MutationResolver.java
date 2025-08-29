package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.*;
import no.utdanning.opptak.repository.OpptakRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MutationResolver {

  private final OpptakRepository repository;

  public MutationResolver(OpptakRepository repository) {
    this.repository = repository;
  }

  @MutationMapping
  public Opptak opprettOpptak(@Argument OpprettOpptakInput input) {
    Opptak opptak = new Opptak();
    opptak.setNavn(input.getNavn());
    opptak.setType(input.getType());
    opptak.setAar(input.getAar());
    opptak.setSoknadsfrist(input.getSoknadsfrist());
    opptak.setSvarfrist(input.getSvarfrist());
    opptak.setMaxUtdanningerPerSoknad(
        input.getMaxUtdanningerPerSoknad() != null ? input.getMaxUtdanningerPerSoknad() : 10);
    opptak.setBeskrivelse(input.getBeskrivelse());
    opptak.setStatus(OpptaksStatus.FREMTIDIG);

    return repository.saveOpptak(opptak);
  }

  public static class OpprettOpptakInput {
    private String navn;
    private OpptaksType type;
    private Integer aar;
    private java.time.LocalDate soknadsfrist;
    private java.time.LocalDate svarfrist;
    private Integer maxUtdanningerPerSoknad;
    private String beskrivelse;

    public String getNavn() {
      return navn;
    }

    public void setNavn(String navn) {
      this.navn = navn;
    }

    public OpptaksType getType() {
      return type;
    }

    public void setType(OpptaksType type) {
      this.type = type;
    }

    public Integer getAar() {
      return aar;
    }

    public void setAar(Integer aar) {
      this.aar = aar;
    }

    public java.time.LocalDate getSoknadsfrist() {
      return soknadsfrist;
    }

    public void setSoknadsfrist(java.time.LocalDate soknadsfrist) {
      this.soknadsfrist = soknadsfrist;
    }

    public java.time.LocalDate getSvarfrist() {
      return svarfrist;
    }

    public void setSvarfrist(java.time.LocalDate svarfrist) {
      this.svarfrist = svarfrist;
    }

    public Integer getMaxUtdanningerPerSoknad() {
      return maxUtdanningerPerSoknad;
    }

    public void setMaxUtdanningerPerSoknad(Integer maxUtdanningerPerSoknad) {
      this.maxUtdanningerPerSoknad = maxUtdanningerPerSoknad;
    }

    public String getBeskrivelse() {
      return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
      this.beskrivelse = beskrivelse;
    }
  }
}
