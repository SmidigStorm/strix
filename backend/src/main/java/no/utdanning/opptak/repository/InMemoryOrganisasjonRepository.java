package no.utdanning.opptak.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.OrganisasjonsType;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementasjon av OrganisasjonRepository Bruker HashMap for rask oppslag og enkel
 * testing
 */
@Repository
public class InMemoryOrganisasjonRepository implements OrganisasjonRepository {

  private final Map<String, Organisasjon> organisasjoner = new HashMap<>();

  public InMemoryOrganisasjonRepository() {
    // Legg til noen test-organisasjoner
    initializeTestData();
  }

  private void initializeTestData() {
    LocalDateTime now = LocalDateTime.now();

    Organisasjon ntnu =
        new Organisasjon(
            "ntnu",
            "Norges teknisk-naturvitenskapelige universitet",
            "NTNU",
            OrganisasjonsType.UNIVERSITET,
            "974767880",
            "opptak@ntnu.no",
            "73595000",
            "Høgskoleringen 1",
            "Trondheim",
            "7491",
            "https://www.ntnu.no",
            now,
            now,
            true);

    Organisasjon uio =
        new Organisasjon(
            "uio",
            "Universitetet i Oslo",
            "UiO",
            OrganisasjonsType.UNIVERSITET,
            "971035854",
            "opptak@uio.no",
            "22855050",
            "Problemveien 7",
            "Oslo",
            "0315",
            "https://www.uio.no",
            now,
            now,
            true);

    Organisasjon hvl =
        new Organisasjon(
            "hvl",
            "Høgskulen på Vestlandet",
            "HVL",
            OrganisasjonsType.HOGSKOLE,
            "991825827",
            "post@hvl.no",
            "55587000",
            "Inndalsveien 28",
            "Bergen",
            "5020",
            "https://www.hvl.no",
            now,
            now,
            true);

    Organisasjon fagskole =
        new Organisasjon(
            "fagskolen-innlandet",
            "Fagskolen Innlandet",
            "FSI",
            OrganisasjonsType.FAGSKOLE,
            "123456789",
            "opptak@fagskolen-innlandet.no",
            "62430800",
            "Storgata 1",
            "Elverum",
            "2400",
            "https://www.fagskolen-innlandet.no",
            now,
            now,
            true);

    organisasjoner.put(ntnu.getId(), ntnu);
    organisasjoner.put(uio.getId(), uio);
    organisasjoner.put(hvl.getId(), hvl);
    organisasjoner.put(fagskole.getId(), fagskole);
  }

  @Override
  public List<Organisasjon> findAll() {
    return new ArrayList<>(organisasjoner.values());
  }

  @Override
  public Organisasjon findById(String id) {
    return organisasjoner.get(id);
  }

  @Override
  public List<Organisasjon> findByAktiv(boolean aktiv) {
    return organisasjoner.values().stream()
        .filter(org -> org.getAktiv() == aktiv)
        .collect(Collectors.toList());
  }

  @Override
  public List<Organisasjon> findByType(OrganisasjonsType type) {
    return organisasjoner.values().stream()
        .filter(org -> org.getType() == type)
        .collect(Collectors.toList());
  }

  @Override
  public List<Organisasjon> findByNavnContainingIgnoreCase(String navnSok) {
    return organisasjoner.values().stream()
        .filter(org -> org.getNavn().toLowerCase().contains(navnSok.toLowerCase()))
        .collect(Collectors.toList());
  }

  @Override
  public Organisasjon findByOrganisasjonsnummer(String organisasjonsnummer) {
    return organisasjoner.values().stream()
        .filter(org -> org.getOrganisasjonsnummer().equals(organisasjonsnummer))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Organisasjon save(Organisasjon organisasjon) {
    if (organisasjon.getId() == null) {
      // Ny organisasjon - generer ID
      organisasjon.setId("org-" + UUID.randomUUID().toString().substring(0, 8));
      organisasjon.setOpprettet(LocalDateTime.now());
    }
    organisasjon.setOppdatert(LocalDateTime.now());

    organisasjoner.put(organisasjon.getId(), organisasjon);
    return organisasjon;
  }

  @Override
  public boolean existsByOrganisasjonsnummer(String organisasjonsnummer) {
    return organisasjoner.values().stream()
        .anyMatch(org -> org.getOrganisasjonsnummer().equals(organisasjonsnummer));
  }

  @Override
  public boolean existsByOrganisasjonsnummerAndIdNot(String organisasjonsnummer, String id) {
    return organisasjoner.values().stream()
        .anyMatch(
            org ->
                org.getOrganisasjonsnummer().equals(organisasjonsnummer)
                    && !org.getId().equals(id));
  }
}
