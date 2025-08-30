package no.utdanning.opptak.graphql;

import java.time.LocalDateTime;
import no.utdanning.opptak.domain.Utdanning;
import no.utdanning.opptak.graphql.dto.OppdaterUtdanningInput;
import no.utdanning.opptak.graphql.dto.OpprettUtdanningInput;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import no.utdanning.opptak.repository.UtdanningRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL resolver for utdanning mutations
 */
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class UtdanningMutationResolver {

  private final UtdanningRepository utdanningRepository;
  private final OrganisasjonRepository organisasjonRepository;

  public UtdanningMutationResolver(
      UtdanningRepository utdanningRepository,
      OrganisasjonRepository organisasjonRepository) {
    this.utdanningRepository = utdanningRepository;
    this.organisasjonRepository = organisasjonRepository;
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning opprettUtdanning(@Argument OpprettUtdanningInput input) {
    // Valider at organisasjon eksisterer
    if (organisasjonRepository.findById(input.getOrganisasjonId()) == null) {
      throw new RuntimeException("Organisasjon ikke funnet: " + input.getOrganisasjonId());
    }

    // Valider påkrevde felter
    if (input.getNavn() == null || input.getNavn().trim().isEmpty()) {
      throw new RuntimeException("Utdanningsnavn er påkrevd");
    }
    if (input.getStudienivaa() == null || input.getStudienivaa().trim().isEmpty()) {
      throw new RuntimeException("Studienivå er påkrevd");
    }
    if (input.getStudiepoeng() == null || input.getStudiepoeng() <= 0) {
      throw new RuntimeException("Studiepoeng må være et positivt tall");
    }
    if (input.getVarighet() == null || input.getVarighet() <= 0) {
      throw new RuntimeException("Varighet må være et positivt tall");
    }
    if (input.getStudiested() == null || input.getStudiested().trim().isEmpty()) {
      throw new RuntimeException("Studiested er påkrevd");
    }
    if (input.getUndervisningssprak() == null || input.getUndervisningssprak().trim().isEmpty()) {
      throw new RuntimeException("Undervisningsspråk er påkrevd");
    }
    if (input.getStarttidspunkt() == null || input.getStarttidspunkt().trim().isEmpty()) {
      throw new RuntimeException("Starttidspunkt er påkrevd");
    }
    if (input.getStudieform() == null) {
      throw new RuntimeException("Studieform er påkrevd");
    }

    // Opprett ny utdanning
    Utdanning utdanning = new Utdanning();
    utdanning.setNavn(input.getNavn().trim());
    utdanning.setStudienivaa(input.getStudienivaa().trim());
    utdanning.setStudiepoeng(input.getStudiepoeng());
    utdanning.setVarighet(input.getVarighet());
    utdanning.setStudiested(input.getStudiested().trim());
    utdanning.setUndervisningssprak(input.getUndervisningssprak().trim());
    utdanning.setBeskrivelse(input.getBeskrivelse());
    utdanning.setStarttidspunkt(input.getStarttidspunkt().trim());
    utdanning.setStudieform(input.getStudieform());
    utdanning.setOrganisasjonId(input.getOrganisasjonId());
    utdanning.setAktiv(true); // Nye utdanninger er aktive som standard
    utdanning.setOpprettet(LocalDateTime.now());

    return utdanningRepository.save(utdanning);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning oppdaterUtdanning(@Argument OppdaterUtdanningInput input) {
    Utdanning eksisterende = utdanningRepository.findById(input.getId());
    if (eksisterende == null) {
      throw new RuntimeException("Utdanning ikke funnet: " + input.getId());
    }

    // Oppdater kun felter som er spesifisert
    if (input.getNavn() != null && !input.getNavn().trim().isEmpty()) {
      eksisterende.setNavn(input.getNavn().trim());
    }
    if (input.getStudienivaa() != null && !input.getStudienivaa().trim().isEmpty()) {
      eksisterende.setStudienivaa(input.getStudienivaa().trim());
    }
    if (input.getStudiepoeng() != null && input.getStudiepoeng() > 0) {
      eksisterende.setStudiepoeng(input.getStudiepoeng());
    }
    if (input.getVarighet() != null && input.getVarighet() > 0) {
      eksisterende.setVarighet(input.getVarighet());
    }
    if (input.getStudiested() != null && !input.getStudiested().trim().isEmpty()) {
      eksisterende.setStudiested(input.getStudiested().trim());
    }
    if (input.getUndervisningssprak() != null && !input.getUndervisningssprak().trim().isEmpty()) {
      eksisterende.setUndervisningssprak(input.getUndervisningssprak().trim());
    }
    if (input.getBeskrivelse() != null) {
      eksisterende.setBeskrivelse(input.getBeskrivelse());
    }
    if (input.getStarttidspunkt() != null && !input.getStarttidspunkt().trim().isEmpty()) {
      eksisterende.setStarttidspunkt(input.getStarttidspunkt().trim());
    }
    if (input.getStudieform() != null) {
      eksisterende.setStudieform(input.getStudieform());
    }
    if (input.getAktiv() != null) {
      eksisterende.setAktiv(input.getAktiv());
    }

    return utdanningRepository.save(eksisterende);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning deaktiverUtdanning(@Argument String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    if (utdanning == null) {
      throw new RuntimeException("Utdanning ikke funnet: " + id);
    }

    utdanning.setAktiv(false);
    return utdanningRepository.save(utdanning);
  }

  @MutationMapping
  @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER')")
  public Utdanning aktiverUtdanning(@Argument String id) {
    Utdanning utdanning = utdanningRepository.findById(id);
    if (utdanning == null) {
      throw new RuntimeException("Utdanning ikke funnet: " + id);
    }

    utdanning.setAktiv(true);
    return utdanningRepository.save(utdanning);
  }

  @MutationMapping
  @PreAuthorize("hasRole('ADMINISTRATOR')")
  public Boolean slettUtdanning(@Argument String id) {
    if (!utdanningRepository.existsById(id)) {
      throw new RuntimeException("Utdanning ikke funnet: " + id);
    }

    return utdanningRepository.deleteById(id);
  }
}