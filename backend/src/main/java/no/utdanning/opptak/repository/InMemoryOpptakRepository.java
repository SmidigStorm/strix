package no.utdanning.opptak.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.OpptaksType;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementasjon av NewOpptakRepository
 * Bruker HashMap for rask oppslag og enkel testing
 */
@Repository
public class InMemoryOpptakRepository implements NewOpptakRepository {

  private final Map<String, Opptak> opptak = new HashMap<>();

  public InMemoryOpptakRepository() {
    initializeTestData();
  }

  private void initializeTestData() {
    LocalDateTime now = LocalDateTime.now();

    // Nasjonalt samordnet opptak for universiteter og høgskoler - Høst 2025
    Opptak uhgHost2025 = new Opptak(
        "uhg-host-2025",
        "Nasjonalt samordnet opptak UHG Høst 2025",
        OpptaksType.UHG,
        2025,
        LocalDate.of(2025, 4, 15), // Søknadsfrist
        LocalDate.of(2025, 7, 20), // Svarfrist
        12, // Max 12 utdanninger per søknad
        OpptaksStatus.FREMTIDIG,
        "HOVEDOPPTAK_HØST",
        "Nasjonalt samordnet opptak for universiteter og høgskoler høst 2025. Administrert av UNIT på vegne av alle UH-institusjoner.",
        now,
        true,
        true, // Samordnet
        "ntnu" // NTNU som testadministrator
    );

    // Nasjonalt samordnet opptak for fagskoler - Høst 2025
    Opptak fsuHost2025 = new Opptak(
        "fsu-host-2025",
        "Nasjonalt samordnet opptak FSU Høst 2025",
        OpptaksType.FSU,
        2025,
        LocalDate.of(2025, 3, 1), // Tidligere søknadsfrist for fagskoler
        LocalDate.of(2025, 6, 15),
        8, // Færre utdanninger per søknad
        OpptaksStatus.APENT,
        "HOVEDOPPTAK_HØST",
        "Nasjonalt samordnet opptak for fagskoler høst 2025. Samordnet system for fagskoleopptak.",
        now,
        true,
        true, // Samordnet
        "fagskolen-innlandet" // Fagskole som testadministrator
    );

    // Lokalt opptak for HVL - ikke samordnet
    Opptak hvlLokal2025 = new Opptak(
        "hvl-lokal-host-2025",
        "HVL Lokalt opptak Høst 2025",
        OpptaksType.LOKALT,
        2025,
        LocalDate.of(2025, 6, 1), // Senere søknadsfrist
        LocalDate.of(2025, 8, 15),
        5, // Færre utdanninger
        OpptaksStatus.FREMTIDIG,
        "HOVEDOPPTAK_HØST",
        "Lokalt opptak ved Høgskulen på Vestlandet for spesialiserte programmer som ikke inngår i det nasjonale samordnede opptaket.",
        now,
        true,
        false, // Ikke samordnet - kun HVL kan legge til utdanninger
        "hvl" // HVL administrerer selv
    );

    // Vår opptak 2025
    Opptak uhgVar2025 = new Opptak(
        "uhg-var-2025",
        "Nasjonalt samordnet opptak UHG Vår 2025",
        OpptaksType.UHG,
        2025,
        LocalDate.of(2024, 12, 1), // Søknadsfrist i desember
        LocalDate.of(2025, 2, 1), // Tidligere svarfrist
        10,
        OpptaksStatus.STENGT, // Allerede stengt
        "HOVEDOPPTAK_VÅR",
        "Nasjonalt samordnet opptak for universiteter og høgskoler vår 2025.",
        now.minusMonths(6), // Opprettet for 6 måneder siden
        true,
        true,
        "uio" // UiO som testadministrator
    );

    // Historisk opptak - avsluttet
    Opptak uhgHost2024 = new Opptak(
        "uhg-host-2024",
        "Nasjonalt samordnet opptak UHG Høst 2024",
        OpptaksType.UHG,
        2024,
        LocalDate.of(2024, 4, 15),
        LocalDate.of(2024, 7, 20),
        12,
        OpptaksStatus.AVSLUTTET, // Ferdig gjennomført
        "HOVEDOPPTAK_HØST",
        "Nasjonalt samordnet opptak for universiteter og høgskoler høst 2024. Opptak er gjennomført og avsluttet.",
        now.minusYears(1),
        true,
        true,
        "ntnu"
    );

    // Deaktivert test-opptak
    Opptak testOpptak = new Opptak(
        "test-opptak-2025",
        "Test opptak 2025 - Deaktivert",
        OpptaksType.LOKALT,
        2025,
        LocalDate.of(2025, 5, 1),
        LocalDate.of(2025, 7, 1),
        3,
        OpptaksStatus.FREMTIDIG,
        "TEST",
        "Test opptak som er deaktivert.",
        now,
        false, // Deaktivert
        false,
        "ntnu"
    );

    // Lagre test-data
    opptak.put(uhgHost2025.getId(), uhgHost2025);
    opptak.put(fsuHost2025.getId(), fsuHost2025);
    opptak.put(hvlLokal2025.getId(), hvlLokal2025);
    opptak.put(uhgVar2025.getId(), uhgVar2025);
    opptak.put(uhgHost2024.getId(), uhgHost2024);
    opptak.put(testOpptak.getId(), testOpptak);
  }

  @Override
  public List<Opptak> findAll() {
    return new ArrayList<>(opptak.values());
  }

  @Override
  public Opptak findById(String id) {
    return opptak.get(id);
  }

  @Override
  public List<Opptak> findByAktiv(boolean aktiv) {
    return opptak.values().stream()
        .filter(o -> o.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findByAdministratorOrganisasjonId(String organisasjonId) {
    return opptak.values().stream()
        .filter(o -> organisasjonId.equals(o.getAdministratorOrganisasjonId()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findByAar(Integer aar) {
    return opptak.values().stream()
        .filter(o -> aar.equals(o.getAar()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findByType(OpptaksType type) {
    return opptak.values().stream()
        .filter(o -> o.getType() == type)
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findByStatus(OpptaksStatus status) {
    return opptak.values().stream()
        .filter(o -> o.getStatus() == status)
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findBySamordnet(boolean samordnet) {
    return opptak.values().stream()
        .filter(o -> o.getSamordnet() == samordnet)
        .collect(Collectors.toList());
  }

  @Override
  public List<Opptak> findWithFilters(String navn, Integer aar, OpptaksType type, OpptaksStatus status,
      String administratorOrganisasjonId, Boolean samordnet, Boolean aktiv, Integer limit, Integer offset) {
    
    Stream<Opptak> stream = opptak.values().stream();

    // Anvend filtre
    if (navn != null && !navn.trim().isEmpty()) {
      stream = stream.filter(o -> o.getNavn() != null && 
          o.getNavn().toLowerCase().contains(navn.toLowerCase()));
    }
    if (aar != null) {
      stream = stream.filter(o -> aar.equals(o.getAar()));
    }
    if (type != null) {
      stream = stream.filter(o -> o.getType() == type);
    }
    if (status != null) {
      stream = stream.filter(o -> o.getStatus() == status);
    }
    if (administratorOrganisasjonId != null) {
      stream = stream.filter(o -> administratorOrganisasjonId.equals(o.getAdministratorOrganisasjonId()));
    }
    if (samordnet != null) {
      stream = stream.filter(o -> o.getSamordnet() != null && o.getSamordnet() == samordnet);
    }
    if (aktiv != null) {
      stream = stream.filter(o -> o.getAktiv() != null && o.getAktiv() == aktiv);
    } else {
      // Default til kun aktive
      stream = stream.filter(o -> o.getAktiv() != null && o.getAktiv());
    }

    // Sorter på år (nyeste først) og deretter navn
    stream = stream.sorted((a, b) -> {
      int aarCompare = Integer.compare(b.getAar() != null ? b.getAar() : 0, 
                                      a.getAar() != null ? a.getAar() : 0);
      if (aarCompare != 0) return aarCompare;
      
      String navnA = a.getNavn() != null ? a.getNavn() : "";
      String navnB = b.getNavn() != null ? b.getNavn() : "";
      return navnA.compareToIgnoreCase(navnB);
    });

    // Anvend paginering
    if (offset != null && offset > 0) {
      stream = stream.skip(offset);
    }
    if (limit != null && limit > 0) {
      stream = stream.limit(limit);
    }

    return stream.collect(Collectors.toList());
  }

  @Override
  public long countWithFilters(String navn, Integer aar, OpptaksType type, OpptaksStatus status,
      String administratorOrganisasjonId, Boolean samordnet, Boolean aktiv) {
    
    Stream<Opptak> stream = opptak.values().stream();

    // Samme filtre som findWithFilters, men uten paginering
    if (navn != null && !navn.trim().isEmpty()) {
      stream = stream.filter(o -> o.getNavn() != null && 
          o.getNavn().toLowerCase().contains(navn.toLowerCase()));
    }
    if (aar != null) {
      stream = stream.filter(o -> aar.equals(o.getAar()));
    }
    if (type != null) {
      stream = stream.filter(o -> o.getType() == type);
    }
    if (status != null) {
      stream = stream.filter(o -> o.getStatus() == status);
    }
    if (administratorOrganisasjonId != null) {
      stream = stream.filter(o -> administratorOrganisasjonId.equals(o.getAdministratorOrganisasjonId()));
    }
    if (samordnet != null) {
      stream = stream.filter(o -> o.getSamordnet() != null && o.getSamordnet() == samordnet);
    }
    if (aktiv != null) {
      stream = stream.filter(o -> o.getAktiv() != null && o.getAktiv() == aktiv);
    } else {
      // Default til kun aktive
      stream = stream.filter(o -> o.getAktiv() != null && o.getAktiv());
    }

    return stream.count();
  }

  @Override
  public Opptak save(Opptak opptakEntity) {
    // Valider påkrevde felter
    if (opptakEntity.getNavn() == null || opptakEntity.getNavn().trim().isEmpty()) {
      throw new IllegalArgumentException("Opptaksnavn er påkrevd");
    }
    if (opptakEntity.getType() == null) {
      throw new IllegalArgumentException("Opptakstype er påkrevd");
    }
    if (opptakEntity.getAar() == null || opptakEntity.getAar() < 2020 || opptakEntity.getAar() > 2030) {
      throw new IllegalArgumentException("År må være mellom 2020 og 2030");
    }
    if (opptakEntity.getMaxUtdanningerPerSoknad() == null || opptakEntity.getMaxUtdanningerPerSoknad() <= 0) {
      throw new IllegalArgumentException("Max utdanninger per søknad må være et positivt tall");
    }
    if (opptakEntity.getAdministratorOrganisasjonId() == null || 
        opptakEntity.getAdministratorOrganisasjonId().trim().isEmpty()) {
      throw new IllegalArgumentException("Administrator organisasjon ID er påkrevd");
    }

    if (opptakEntity.getId() == null || opptakEntity.getId().trim().isEmpty()) {
      // Ny opptak - generer ID
      opptakEntity.setId("opptak-" + UUID.randomUUID().toString().substring(0, 8));
      opptakEntity.setOpprettet(LocalDateTime.now());
    }

    // Sett standardverdier hvis ikke angitt
    if (opptakEntity.getStatus() == null) {
      opptakEntity.setStatus(OpptaksStatus.FREMTIDIG);
    }
    if (opptakEntity.getAktiv() == null) {
      opptakEntity.setAktiv(true);
    }
    if (opptakEntity.getSamordnet() == null) {
      opptakEntity.setSamordnet(false);
    }

    opptak.put(opptakEntity.getId(), opptakEntity);
    return opptakEntity;
  }

  @Override
  public boolean deleteById(String id) {
    return opptak.remove(id) != null;
  }

  @Override
  public boolean existsById(String id) {
    return opptak.containsKey(id);
  }
}