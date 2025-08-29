package no.utdanning.opptak.graphql;

import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.OppdaterOrganisasjonInput;
import no.utdanning.opptak.graphql.dto.OpprettOrganisasjonInput;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/** GraphQL resolver for organisasjon mutations */
@Controller
public class OrganisasjonMutationResolver {

  private final OrganisasjonRepository organisasjonRepository;

  public OrganisasjonMutationResolver(OrganisasjonRepository organisasjonRepository) {
    this.organisasjonRepository = organisasjonRepository;
  }

  @MutationMapping
  public Organisasjon opprettOrganisasjon(@Argument OpprettOrganisasjonInput input) {
    // Valider at organisasjonsnummer ikke finnes fra før
    if (organisasjonRepository.existsByOrganisasjonsnummer(input.getOrganisasjonsnummer())) {
      throw new RuntimeException(
          "Organisasjonsnummer er allerede registrert: " + input.getOrganisasjonsnummer());
    }

    // Valider organisasjonsnummer format (9 siffer)
    if (!input.getOrganisasjonsnummer().matches("\\d{9}")) {
      throw new RuntimeException("Ugyldig organisasjonsnummer. Må være 9 siffer.");
    }

    // Opprett ny organisasjon
    Organisasjon organisasjon = new Organisasjon();
    organisasjon.setNavn(input.getNavn());
    organisasjon.setOrganisasjonsnummer(input.getOrganisasjonsnummer());
    organisasjon.setType(input.getOrganisasjonstype());
    organisasjon.setEpost(input.getEpost());
    organisasjon.setTelefon(input.getTelefon());
    organisasjon.setAdresse(input.getAdresse());
    organisasjon.setPoststed(input.getPoststed());
    organisasjon.setPostnummer(input.getPostnummer());
    organisasjon.setAktiv(true); // Nye organisasjoner er aktive som standard

    return organisasjonRepository.save(organisasjon);
  }

  @MutationMapping
  public Organisasjon oppdaterOrganisasjon(@Argument OppdaterOrganisasjonInput input) {
    Organisasjon eksisterende = organisasjonRepository.findById(input.getId());
    if (eksisterende == null) {
      throw new RuntimeException("Organisasjon ikke funnet: " + input.getId());
    }

    // Oppdater kun felter som er spesifisert
    if (input.getNavn() != null) {
      eksisterende.setNavn(input.getNavn());
    }
    if (input.getOrganisasjonstype() != null) {
      eksisterende.setType(input.getOrganisasjonstype());
    }
    if (input.getEpost() != null) {
      eksisterende.setEpost(input.getEpost());
    }
    if (input.getTelefon() != null) {
      eksisterende.setTelefon(input.getTelefon());
    }
    if (input.getAdresse() != null) {
      eksisterende.setAdresse(input.getAdresse());
    }
    if (input.getPoststed() != null) {
      eksisterende.setPoststed(input.getPoststed());
    }
    if (input.getPostnummer() != null) {
      eksisterende.setPostnummer(input.getPostnummer());
    }

    return organisasjonRepository.save(eksisterende);
  }

  @MutationMapping
  public Organisasjon deaktiverOrganisasjon(@Argument String id) {
    Organisasjon organisasjon = organisasjonRepository.findById(id);
    if (organisasjon == null) {
      throw new RuntimeException("Organisasjon ikke funnet: " + id);
    }

    organisasjon.setAktiv(false);
    return organisasjonRepository.save(organisasjon);
  }

  @MutationMapping
  public Organisasjon reaktiverOrganisasjon(@Argument String id) {
    Organisasjon organisasjon = organisasjonRepository.findById(id);
    if (organisasjon == null) {
      throw new RuntimeException("Organisasjon ikke funnet: " + id);
    }

    organisasjon.setAktiv(true);
    return organisasjonRepository.save(organisasjon);
  }
}
