package no.utdanning.opptak.repository;

import java.util.List;
import no.utdanning.opptak.domain.Studieform;
import no.utdanning.opptak.domain.Utdanning;

/** Repository for utdanninger - håndterer alle database operasjoner */
public interface UtdanningRepository {

  /** Henter alle utdanninger */
  List<Utdanning> findAll();

  /** Henter utdanning ved ID */
  Utdanning findById(String id);

  /** Henter utdanninger filtrert på aktiv status */
  List<Utdanning> findByAktiv(boolean aktiv);

  /** Henter utdanninger for en spesifikk organisasjon */
  List<Utdanning> findByOrganisasjonId(String organisasjonId);

  /** Henter aktive utdanninger for en organisasjon */
  List<Utdanning> findByOrganisasjonIdAndAktiv(String organisasjonId, boolean aktiv);

  /** Søker utdanninger på navn (case insensitive) */
  List<Utdanning> findByNavnContainingIgnoreCase(String navnSok);

  /** Henter utdanninger filtrert på studienivå */
  List<Utdanning> findByStudienivaa(String studienivaa);

  /** Henter utdanninger filtrert på studiested */
  List<Utdanning> findByStudiested(String studiested);

  /** Henter utdanninger filtrert på studieform */
  List<Utdanning> findByStudieform(Studieform studieform);

  /** Henter utdanninger filtrert på starttidspunkt */
  List<Utdanning> findByStarttidspunkt(String starttidspunkt);

  /**
   * Søker utdanninger med kombinerte filtre
   *
   * @param navn søketekst i navn (case insensitive), nullable
   * @param studienivaa filtrer på studienivå, nullable
   * @param studiested filtrer på studiested, nullable
   * @param organisasjonId filtrer på organisasjon, nullable
   * @param studieform filtrer på studieform, nullable
   * @param aktiv filtrer på aktiv status, default true
   * @param limit maksimalt antall resultater (for paginering)
   * @param offset start offset (for paginering)
   * @return filtrerte utdanninger
   */
  List<Utdanning> findWithFilters(
      String navn,
      String studienivaa,
      String studiested,
      String organisasjonId,
      Studieform studieform,
      Boolean aktiv,
      Integer limit,
      Integer offset);

  /**
   * Teller utdanninger med samme filtre som findWithFilters
   *
   * @param navn søketekst i navn (case insensitive), nullable
   * @param studienivaa filtrer på studienivå, nullable
   * @param studiested filtrer på studiested, nullable
   * @param organisasjonId filtrer på organisasjon, nullable
   * @param studieform filtrer på studieform, nullable
   * @param aktiv filtrer på aktiv status, default true
   * @return antall utdanninger som matcher filtrene
   */
  long countWithFilters(
      String navn,
      String studienivaa,
      String studiested,
      String organisasjonId,
      Studieform studieform,
      Boolean aktiv);

  /** Lagrer utdanning (oppretter ny eller oppdaterer eksisterende) */
  Utdanning save(Utdanning utdanning);

  /** Sletter utdanning permanent */
  boolean deleteById(String id);

  /** Deaktiverer utdanning (soft delete) */
  boolean deaktiverById(String id);

  /** Aktiverer utdanning */
  boolean aktiverById(String id);

  /** Sjekker om utdanning med gitt ID eksisterer */
  boolean existsById(String id);
}
