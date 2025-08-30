package no.utdanning.opptak.repository;

import java.util.List;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.OpptaksType;

/** Repository interface for Opptak entity operations */
public interface NewOpptakRepository {

  /** Henter alle opptak */
  List<Opptak> findAll();

  /** Henter opptak basert på ID */
  Opptak findById(String id);

  /** Henter aktive opptak */
  List<Opptak> findByAktiv(boolean aktiv);

  /** Henter opptak som en organisasjon administrerer */
  List<Opptak> findByAdministratorOrganisasjonId(String organisasjonId);

  /** Henter opptak basert på år */
  List<Opptak> findByAar(Integer aar);

  /** Henter opptak basert på type */
  List<Opptak> findByType(OpptaksType type);

  /** Henter opptak basert på status */
  List<Opptak> findByStatus(OpptaksStatus status);

  /** Henter samordnede opptak */
  List<Opptak> findBySamordnet(boolean samordnet);

  /** Lagrer opptak (opprett eller oppdater) */
  Opptak save(Opptak opptak);

  /** Sletter opptak basert på ID */
  boolean deleteById(String id);

  /** Sjekker om opptak finnes */
  boolean existsById(String id);

  /** Henter opptak med filtrering og paginering */
  List<Opptak> findWithFilters(
      String navn,
      Integer aar,
      OpptaksType type,
      OpptaksStatus status,
      String administratorOrganisasjonId,
      Boolean samordnet,
      Boolean aktiv,
      Integer limit,
      Integer offset);

  /** Teller opptak med filtrering */
  long countWithFilters(
      String navn,
      Integer aar,
      OpptaksType type,
      OpptaksStatus status,
      String administratorOrganisasjonId,
      Boolean samordnet,
      Boolean aktiv);
}
