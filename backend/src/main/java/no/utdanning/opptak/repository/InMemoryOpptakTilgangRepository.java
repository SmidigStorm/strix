package no.utdanning.opptak.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import no.utdanning.opptak.domain.OpptakTilgang;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementasjon av OpptakTilgangRepository
 * Håndterer tilgangskontroll for samordnede opptak
 */
@Repository
public class InMemoryOpptakTilgangRepository implements OpptakTilgangRepository {

  private final Map<String, OpptakTilgang> tilganger = new HashMap<>();

  public InMemoryOpptakTilgangRepository() {
    initializeTestData();
  }

  private void initializeTestData() {
    LocalDateTime now = LocalDateTime.now();

    // Tilganger for UHG Høst 2025 - samordnet opptak administrert av NTNU
    // NTNU har automatisk tilgang som administrator (ikke lagres eksplisitt)
    // Gi andre universiteter tilgang
    OpptakTilgang uhgHost2025Uio = new OpptakTilgang(
        "tilgang-uhg-2025-uio",
        "uhg-host-2025", // opptak ID
        "uio", // UiO får tilgang
        true,
        now,
        "system"
    );

    OpptakTilgang uhgHost2025Hvl = new OpptakTilgang(
        "tilgang-uhg-2025-hvl",
        "uhg-host-2025",
        "hvl", // HVL får tilgang
        true,
        now,
        "system"
    );

    // Tilgang for FSU Høst 2025 - administrert av fagskolen-innlandet
    // Gi andre fagskoler tilgang (hvis vi hadde flere)
    // For nå kun administrator-fagskoler

    // Tilgang for UHG Vår 2025 - administrert av UiO
    OpptakTilgang uhgVar2025Ntnu = new OpptakTilgang(
        "tilgang-uhg-var-2025-ntnu",
        "uhg-var-2025",
        "ntnu", // NTNU får tilgang
        true,
        now.minusMonths(6),
        "system"
    );

    OpptakTilgang uhgVar2025Hvl = new OpptakTilgang(
        "tilgang-uhg-var-2025-hvl",
        "uhg-var-2025",
        "hvl", // HVL får tilgang
        true,
        now.minusMonths(6),
        "system"
    );

    // Historisk opptak 2024
    OpptakTilgang uhgHost2024Uio = new OpptakTilgang(
        "tilgang-uhg-2024-uio",
        "uhg-host-2024",
        "uio",
        true,
        now.minusYears(1),
        "system"
    );

    OpptakTilgang uhgHost2024Hvl = new OpptakTilgang(
        "tilgang-uhg-2024-hvl",
        "uhg-host-2024",
        "hvl",
        true,
        now.minusYears(1),
        "system"
    );

    // En deaktivert tilgang for testing
    OpptakTilgang deaktivertTilgang = new OpptakTilgang(
        "tilgang-deaktivert-test",
        "uhg-host-2025",
        "fagskolen-innlandet", // Fagskole som ikke skal ha tilgang til UHG
        false, // Deaktivert tilgang
        now.minusDays(30),
        "admin"
    );

    // Lagre test-data
    tilganger.put(uhgHost2025Uio.getId(), uhgHost2025Uio);
    tilganger.put(uhgHost2025Hvl.getId(), uhgHost2025Hvl);
    tilganger.put(uhgVar2025Ntnu.getId(), uhgVar2025Ntnu);
    tilganger.put(uhgVar2025Hvl.getId(), uhgVar2025Hvl);
    tilganger.put(uhgHost2024Uio.getId(), uhgHost2024Uio);
    tilganger.put(uhgHost2024Hvl.getId(), uhgHost2024Hvl);
    tilganger.put(deaktivertTilgang.getId(), deaktivertTilgang);
  }

  @Override
  public List<OpptakTilgang> findAll() {
    return new ArrayList<>(tilganger.values());
  }

  @Override
  public OpptakTilgang findById(String id) {
    return tilganger.get(id);
  }

  @Override
  public List<OpptakTilgang> findByOpptakId(String opptakId) {
    return tilganger.values().stream()
        .filter(t -> opptakId.equals(t.getOpptakId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<OpptakTilgang> findByOrganisasjonId(String organisasjonId) {
    return tilganger.values().stream()
        .filter(t -> organisasjonId.equals(t.getOrganisasjonId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<OpptakTilgang> findByOpptakIdAndAktiv(String opptakId, boolean aktiv) {
    return tilganger.values().stream()
        .filter(t -> opptakId.equals(t.getOpptakId()) && t.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public List<OpptakTilgang> findByOrganisasjonIdAndAktiv(String organisasjonId, boolean aktiv) {
    return tilganger.values().stream()
        .filter(t -> organisasjonId.equals(t.getOrganisasjonId()) && t.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public OpptakTilgang findByOpptakIdAndOrganisasjonId(String opptakId, String organisasjonId) {
    return tilganger.values().stream()
        .filter(t -> opptakId.equals(t.getOpptakId()) && organisasjonId.equals(t.getOrganisasjonId()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public OpptakTilgang save(OpptakTilgang tilgang) {
    // Valider påkrevde felter
    if (tilgang.getOpptakId() == null || tilgang.getOpptakId().trim().isEmpty()) {
      throw new IllegalArgumentException("Opptak ID er påkrevd");
    }
    if (tilgang.getOrganisasjonId() == null || tilgang.getOrganisasjonId().trim().isEmpty()) {
      throw new IllegalArgumentException("Organisasjon ID er påkrevd");
    }

    if (tilgang.getId() == null || tilgang.getId().trim().isEmpty()) {
      // Ny tilgang - generer ID
      tilgang.setId("tilgang-" + UUID.randomUUID().toString().substring(0, 8));
      tilgang.setOpprettet(LocalDateTime.now());
    } else {
      // Oppdatering - sett endret timestamp
      tilgang.setEndret(LocalDateTime.now());
    }

    // Sett standardverdier
    if (tilgang.getAktiv() == null) {
      tilgang.setAktiv(true);
    }

    tilganger.put(tilgang.getId(), tilgang);
    return tilgang;
  }

  @Override
  public boolean deleteById(String id) {
    return tilganger.remove(id) != null;
  }

  @Override
  public boolean existsById(String id) {
    return tilganger.containsKey(id);
  }

  @Override
  public boolean hasAccess(String opptakId, String organisasjonId) {
    return tilganger.values().stream()
        .anyMatch(t -> opptakId.equals(t.getOpptakId()) && 
                      organisasjonId.equals(t.getOrganisasjonId()) && 
                      t.getAktiv() == Boolean.TRUE);
  }
}