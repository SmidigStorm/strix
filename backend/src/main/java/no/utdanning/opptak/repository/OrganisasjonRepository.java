package no.utdanning.opptak.repository;

import java.util.List;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.OrganisasjonsType;

/** Repository for organisasjoner - håndterer alle database operasjoner */
public interface OrganisasjonRepository {

  /** Henter alle organisasjoner */
  List<Organisasjon> findAll();

  /** Henter organisasjon ved ID */
  Organisasjon findById(String id);

  /** Henter organisasjoner filtrert på aktiv status */
  List<Organisasjon> findByAktiv(boolean aktiv);

  /** Henter organisasjoner filtrert på type */
  List<Organisasjon> findByType(OrganisasjonsType type);

  /** Søker organisasjoner på navn (case insensitive) */
  List<Organisasjon> findByNavnContainingIgnoreCase(String navnSok);

  /** Henter organisasjon ved organisasjonsnummer */
  Organisasjon findByOrganisasjonsnummer(String organisasjonsnummer);

  /** Lagrer organisasjon (oppretter ny eller oppdaterer eksisterende) */
  Organisasjon save(Organisasjon organisasjon);

  /** Sjekker om organisasjonsnummer eksisterer allerede */
  boolean existsByOrganisasjonsnummer(String organisasjonsnummer);

  /**
   * Sjekker om organisasjonsnummer eksisterer for andre organisasjoner (unntatt den med gitt ID)
   */
  boolean existsByOrganisasjonsnummerAndIdNot(String organisasjonsnummer, String id);
}
