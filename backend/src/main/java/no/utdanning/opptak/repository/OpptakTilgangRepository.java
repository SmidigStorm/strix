package no.utdanning.opptak.repository;

import java.util.List;
import no.utdanning.opptak.domain.OpptakTilgang;

/**
 * Repository interface for OpptakTilgang entity operations
 */
public interface OpptakTilgangRepository {
  
  /**
   * Henter alle tilganger
   */
  List<OpptakTilgang> findAll();
  
  /**
   * Henter tilgang basert på ID
   */
  OpptakTilgang findById(String id);
  
  /**
   * Henter tilganger for et opptak
   */
  List<OpptakTilgang> findByOpptakId(String opptakId);
  
  /**
   * Henter tilganger for en organisasjon
   */
  List<OpptakTilgang> findByOrganisasjonId(String organisasjonId);
  
  /**
   * Henter aktive tilganger for et opptak
   */
  List<OpptakTilgang> findByOpptakIdAndAktiv(String opptakId, boolean aktiv);
  
  /**
   * Henter aktive tilganger for en organisasjon
   */
  List<OpptakTilgang> findByOrganisasjonIdAndAktiv(String organisasjonId, boolean aktiv);
  
  /**
   * Henter spesifikk tilgang mellom opptak og organisasjon
   */
  OpptakTilgang findByOpptakIdAndOrganisasjonId(String opptakId, String organisasjonId);
  
  /**
   * Lagrer tilgang (opprett eller oppdater)
   */
  OpptakTilgang save(OpptakTilgang tilgang);
  
  /**
   * Sletter tilgang basert på ID
   */
  boolean deleteById(String id);
  
  /**
   * Sjekker om tilgang finnes
   */
  boolean existsById(String id);
  
  /**
   * Sjekker om organisasjon har tilgang til opptak
   */
  boolean hasAccess(String opptakId, String organisasjonId);
}