package no.utdanning.opptak.repository;

import java.util.List;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.OpptaksType;

/**
 * Repository interface for Opptak CRUD operations. Følger samme mønster som andre repositories i
 * systemet.
 */
public interface OpptakCrudRepository {

  /** Henter alle opptak */
  List<Opptak> findAll();

  /** Henter opptak ved ID */
  Opptak findById(String id);

  /** Henter opptak filtrert på aktiv status */
  List<Opptak> findByAktiv(boolean aktiv);

  /** Henter opptak for en spesifikk administrator organisasjon */
  List<Opptak> findByAdministratorOrganisasjonId(String administratorOrganisasjonId);

  /** Henter opptak filtrert på type */
  List<Opptak> findByType(OpptaksType type);

  /** Henter opptak filtrert på år */
  List<Opptak> findByAar(Integer aar);

  /** Henter opptak filtrert på status */
  List<Opptak> findByStatus(OpptaksStatus status);

  /** Henter opptak filtrert på samordnet flag */
  List<Opptak> findBySamordnet(boolean samordnet);

  /** Søker opptak på navn (case insensitive) */
  List<Opptak> findByNavnContainingIgnoreCase(String navnSok);

  /**
   * Søker opptak med kombinerte filtre
   *
   * @param navn søketekst i navn (case insensitive), nullable
   * @param type filtrer på opptakstype, nullable
   * @param aar filtrer på år, nullable
   * @param status filtrer på status, nullable
   * @param samordnet filtrer på samordnet flag, nullable
   * @param administratorOrganisasjonId filtrer på administrator org, nullable
   * @param aktiv filtrer på aktiv status, default true
   * @param limit maksimalt antall resultater (for paginering)
   * @param offset start offset (for paginering)
   * @return filtrerte opptak
   */
  List<Opptak> findWithFilters(
      String navn,
      OpptaksType type,
      Integer aar,
      OpptaksStatus status,
      Boolean samordnet,
      String administratorOrganisasjonId,
      Boolean aktiv,
      Integer limit,
      Integer offset);

  /** Teller opptak med samme filtre som findWithFilters */
  long countWithFilters(
      String navn,
      OpptaksType type,
      Integer aar,
      OpptaksStatus status,
      Boolean samordnet,
      String administratorOrganisasjonId,
      Boolean aktiv);

  /** Lagrer opptak (oppretter ny eller oppdaterer eksisterende) */
  Opptak save(Opptak opptak);

  /** Sletter opptak permanent */
  boolean deleteById(String id);

  /** Deaktiverer opptak (soft delete) */
  boolean deaktiverById(String id);

  /** Aktiverer opptak */
  boolean aktiverById(String id);

  /** Sjekker om opptak med gitt ID eksisterer */
  boolean existsById(String id);

  /** Sjekker om opptak navn er tatt (for å hindre duplikater) */
  boolean existsByNavn(String navn);

  /** Sjekker om opptak navn er tatt av andre opptak (unntatt gitt ID) */
  boolean existsByNavnAndIdNot(String navn, String id);
}
