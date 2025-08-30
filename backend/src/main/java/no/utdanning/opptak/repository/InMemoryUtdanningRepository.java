package no.utdanning.opptak.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.utdanning.opptak.domain.Studieform;
import no.utdanning.opptak.domain.Utdanning;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementasjon av UtdanningRepository
 * Bruker HashMap for rask oppslag og enkel testing
 */
@Repository
public class InMemoryUtdanningRepository implements UtdanningRepository {

  private final Map<String, Utdanning> utdanninger = new HashMap<>();

  public InMemoryUtdanningRepository() {
    // Legg til test-utdanninger basert på database testdata
    initializeTestData();
  }

  private void initializeTestData() {
    LocalDateTime now = LocalDateTime.now();

    Utdanning ntnu_informatikk =
        new Utdanning(
            "ntnu-informatikk-h25",
            "Bachelor i informatikk H25",
            "bachelor",
            180,
            6,
            "Trondheim",
            "norsk",
            "Bachelor i informatikk med fokus på programmering og systemutvikling",
            now,
            true,
            "org-1", // NTNU ID fra OrganisasjonRepository
            "HØST_2025",
            Studieform.HELTID);

    Utdanning ntnu_bygg =
        new Utdanning(
            "ntnu-bygg-h25",
            "Bachelor i bygg- og miljøteknikk H25",
            "bachelor",
            180,
            6,
            "Trondheim",
            "norsk",
            "Utdanning innen bygg- og miljøteknikk",
            now,
            true,
            "org-1", // NTNU
            "HØST_2025",
            Studieform.HELTID);

    Utdanning uio_informatikk =
        new Utdanning(
            "uio-informatikk-h25",
            "Bachelor i informatikk H25",
            "bachelor",
            180,
            6,
            "Oslo",
            "norsk",
            "Informatikkutdanning ved UiO",
            now,
            true,
            "org-2", // UiO ID
            "HØST_2025",
            Studieform.HELTID);

    Utdanning hvl_sykepleie =
        new Utdanning(
            "hvl-sykepleie-h25",
            "Bachelor i sykepleie H25",
            "bachelor",
            180,
            6,
            "Bergen",
            "norsk",
            "Sykepleieutdanning ved HVL",
            now,
            true,
            "org-3", // HVL ID
            "HØST_2025",
            Studieform.HELTID);

    // Legg til et deltids-eksempel
    Utdanning hvl_sykepleie_deltid =
        new Utdanning(
            "hvl-sykepleie-deltid-h25",
            "Bachelor i sykepleie H25 (deltid)",
            "bachelor",
            180,
            10, // Lengre varighet for deltid
            "Bergen",
            "norsk",
            "Sykepleieutdanning ved HVL - deltidsstudium",
            now,
            true,
            "org-3", // HVL ID
            "HØST_2025",
            Studieform.DELTID);

    utdanninger.put(ntnu_informatikk.getId(), ntnu_informatikk);
    utdanninger.put(ntnu_bygg.getId(), ntnu_bygg);
    utdanninger.put(uio_informatikk.getId(), uio_informatikk);
    utdanninger.put(hvl_sykepleie.getId(), hvl_sykepleie);
    utdanninger.put(hvl_sykepleie_deltid.getId(), hvl_sykepleie_deltid);
  }

  @Override
  public List<Utdanning> findAll() {
    return new ArrayList<>(utdanninger.values());
  }

  @Override
  public Utdanning findById(String id) {
    return utdanninger.get(id);
  }

  @Override
  public List<Utdanning> findByAktiv(boolean aktiv) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByOrganisasjonId(String organisasjonId) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getOrganisasjonId().equals(organisasjonId))
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByOrganisasjonIdAndAktiv(String organisasjonId, boolean aktiv) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getOrganisasjonId().equals(organisasjonId) && utd.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByNavnContainingIgnoreCase(String navnSok) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getNavn().toLowerCase().contains(navnSok.toLowerCase()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByStudienivaa(String studienivaa) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getStudienivaa().equals(studienivaa))
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByStudiested(String studiested) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getStudiested().equals(studiested))
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByStudieform(Studieform studieform) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getStudieform() == studieform)
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findByStarttidspunkt(String starttidspunkt) {
    return utdanninger.values().stream()
        .filter(utd -> utd.getStarttidspunkt().equals(starttidspunkt))
        .collect(Collectors.toList());
  }

  @Override
  public List<Utdanning> findWithFilters(
      String navn,
      String studienivaa,
      String studiested,
      String organisasjonId,
      Studieform studieform,
      Boolean aktiv,
      Integer limit,
      Integer offset) {

    Stream<Utdanning> stream = utdanninger.values().stream();

    // Anvendt alle filtre
    if (navn != null && !navn.trim().isEmpty()) {
      stream = stream.filter(utd -> utd.getNavn().toLowerCase().contains(navn.toLowerCase()));
    }
    if (studienivaa != null) {
      stream = stream.filter(utd -> utd.getStudienivaa().equals(studienivaa));
    }
    if (studiested != null) {
      stream = stream.filter(utd -> utd.getStudiested().equals(studiested));
    }
    if (organisasjonId != null) {
      stream = stream.filter(utd -> utd.getOrganisasjonId().equals(organisasjonId));
    }
    if (studieform != null) {
      stream = stream.filter(utd -> utd.getStudieform() == studieform);
    }
    if (aktiv != null) {
      stream = stream.filter(utd -> utd.getAktiv() == aktiv);
    } else {
      // Default til kun aktive
      stream = stream.filter(utd -> utd.getAktiv());
    }

    // Sorter alfabetisk på navn for konsistent rekkefølge
    stream = stream.sorted((a, b) -> a.getNavn().compareToIgnoreCase(b.getNavn()));

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
  public long countWithFilters(
      String navn,
      String studienivaa,
      String studiested,
      String organisasjonId,
      Studieform studieform,
      Boolean aktiv) {

    Stream<Utdanning> stream = utdanninger.values().stream();

    // Samme filtre som findWithFilters, men uten paginering
    if (navn != null && !navn.trim().isEmpty()) {
      stream = stream.filter(utd -> utd.getNavn().toLowerCase().contains(navn.toLowerCase()));
    }
    if (studienivaa != null) {
      stream = stream.filter(utd -> utd.getStudienivaa().equals(studienivaa));
    }
    if (studiested != null) {
      stream = stream.filter(utd -> utd.getStudiested().equals(studiested));
    }
    if (organisasjonId != null) {
      stream = stream.filter(utd -> utd.getOrganisasjonId().equals(organisasjonId));
    }
    if (studieform != null) {
      stream = stream.filter(utd -> utd.getStudieform() == studieform);
    }
    if (aktiv != null) {
      stream = stream.filter(utd -> utd.getAktiv() == aktiv);
    } else {
      // Default til kun aktive
      stream = stream.filter(utd -> utd.getAktiv());
    }

    return stream.count();
  }

  @Override
  public Utdanning save(Utdanning utdanning) {
    if (utdanning.getId() == null || utdanning.getId().trim().isEmpty()) {
      // Ny utdanning - generer ID
      utdanning.setId("utd-" + UUID.randomUUID().toString().substring(0, 8));
      utdanning.setOpprettet(LocalDateTime.now());
    }

    utdanninger.put(utdanning.getId(), utdanning);
    return utdanning;
  }

  @Override
  public boolean deleteById(String id) {
    return utdanninger.remove(id) != null;
  }

  @Override
  public boolean existsById(String id) {
    return utdanninger.containsKey(id);
  }
}