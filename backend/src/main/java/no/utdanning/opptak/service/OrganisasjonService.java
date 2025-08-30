package no.utdanning.opptak.service;

import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.graphql.dto.OppdaterOrganisasjonInput;
import no.utdanning.opptak.graphql.dto.OpprettOrganisasjonInput;
import no.utdanning.opptak.graphql.dto.OrganisasjonFilter;
import no.utdanning.opptak.repository.OrganisasjonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service-lag for organisasjon-operasjoner.
 * Inneholder all forretningslogikk relatert til organisasjoner.
 */
@Service
@Transactional
public class OrganisasjonService {

  private final OrganisasjonRepository organisasjonRepository;

  public OrganisasjonService(OrganisasjonRepository organisasjonRepository) {
    this.organisasjonRepository = organisasjonRepository;
  }

  /**
   * Henter alle organisasjoner med valgfri filtrering.
   */
  public List<Organisasjon> findAll(OrganisasjonFilter filter) {
    if (filter == null) {
      return organisasjonRepository.findAll();
    }

    // Start med alle organisasjoner
    List<Organisasjon> result = organisasjonRepository.findAll();

    // Filtrer på aktiv status hvis spesifisert
    if (filter.getAktiv() != null) {
      result = organisasjonRepository.findByAktiv(filter.getAktiv());
    }

    // Filtrer på type hvis spesifisert
    if (filter.getOrganisasjonstype() != null) {
      result =
          result.stream().filter(org -> org.getType() == filter.getOrganisasjonstype()).toList();
    }

    // Søk i navn hvis spesifisert
    if (filter.getNavnSok() != null && !filter.getNavnSok().trim().isEmpty()) {
      result =
          result.stream()
              .filter(
                  org -> org.getNavn().toLowerCase().contains(filter.getNavnSok().toLowerCase()))
              .toList();
    }

    return result;
  }

  /**
   * Henter en spesifikk organisasjon basert på ID.
   */
  public Organisasjon findById(String id) {
    return organisasjonRepository.findById(id);
  }

  /**
   * Oppretter en ny organisasjon.
   */
  public Organisasjon opprettOrganisasjon(OpprettOrganisasjonInput input) {
    // Valider at organisasjonsnummer ikke finnes fra før
    if (organisasjonRepository.existsByOrganisasjonsnummer(input.getOrganisasjonsnummer())) {
      throw new IllegalArgumentException(
          "Organisasjonsnummer er allerede registrert: " + input.getOrganisasjonsnummer());
    }

    // Valider organisasjonsnummer format (9 siffer)
    if (!input.getOrganisasjonsnummer().matches("\\d{9}")) {
      throw new IllegalArgumentException("Ugyldig organisasjonsnummer. Må være 9 siffer.");
    }

    // Opprett ny organisasjon
    Organisasjon organisasjon = new Organisasjon();
    organisasjon.setNavn(input.getNavn());
    organisasjon.setKortNavn(input.getKortNavn());
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

  /**
   * Oppdaterer en eksisterende organisasjon.
   */
  public Organisasjon oppdaterOrganisasjon(OppdaterOrganisasjonInput input) {
    Organisasjon eksisterende = organisasjonRepository.findById(input.getId());
    if (eksisterende == null) {
      throw new IllegalArgumentException("Organisasjon ikke funnet: " + input.getId());
    }

    // Oppdater kun felter som er spesifisert
    if (input.getNavn() != null) {
      eksisterende.setNavn(input.getNavn());
    }
    if (input.getKortNavn() != null) {
      eksisterende.setKortNavn(input.getKortNavn());
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

  /**
   * Deaktiverer en organisasjon (soft delete).
   */
  public Organisasjon deaktiverOrganisasjon(String id) {
    Organisasjon organisasjon = organisasjonRepository.findById(id);
    if (organisasjon == null) {
      throw new IllegalArgumentException("Organisasjon ikke funnet: " + id);
    }

    organisasjon.setAktiv(false);
    return organisasjonRepository.save(organisasjon);
  }

  /**
   * Reaktiverer en deaktivert organisasjon.
   */
  public Organisasjon reaktiverOrganisasjon(String id) {
    Organisasjon organisasjon = organisasjonRepository.findById(id);
    if (organisasjon == null) {
      throw new IllegalArgumentException("Organisasjon ikke funnet: " + id);
    }

    organisasjon.setAktiv(true);
    return organisasjonRepository.save(organisasjon);
  }

  /**
   * Sjekker om en organisasjon eksisterer.
   */
  public boolean existsById(String id) {
    return organisasjonRepository.findById(id) != null;
  }

  /**
   * Sjekker om et organisasjonsnummer er registrert.
   */
  public boolean existsByOrganisasjonsnummer(String organisasjonsnummer) {
    return organisasjonRepository.existsByOrganisasjonsnummer(organisasjonsnummer);
  }
}