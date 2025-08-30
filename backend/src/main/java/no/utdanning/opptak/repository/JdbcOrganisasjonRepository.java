package no.utdanning.opptak.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import no.utdanning.opptak.domain.Organisasjon;
import no.utdanning.opptak.domain.OrganisasjonsType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementasjon av OrganisasjonRepository.
 * Erstatter InMemoryOrganisasjonRepository for produksjonsklar persistens.
 */
@Repository
public class JdbcOrganisasjonRepository implements OrganisasjonRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcOrganisasjonRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Organisasjon> findAll() {
    String sql = "SELECT * FROM organisasjon ORDER BY navn";
    return jdbcTemplate.query(sql, new OrganisasjonRowMapper());
  }

  @Override
  public Organisasjon findById(String id) {
    String sql = "SELECT * FROM organisasjon WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new OrganisasjonRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<Organisasjon> findByAktiv(boolean aktiv) {
    String sql = "SELECT * FROM organisasjon WHERE aktiv = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new OrganisasjonRowMapper(), aktiv);
  }

  @Override
  public List<Organisasjon> findByType(OrganisasjonsType type) {
    String sql = "SELECT * FROM organisasjon WHERE type = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new OrganisasjonRowMapper(), type.name());
  }

  @Override
  public List<Organisasjon> findByNavnContainingIgnoreCase(String navnSok) {
    String sql = "SELECT * FROM organisasjon WHERE UPPER(navn) LIKE UPPER(?) ORDER BY navn";
    String searchPattern = "%" + navnSok + "%";
    return jdbcTemplate.query(sql, new OrganisasjonRowMapper(), searchPattern);
  }

  @Override
  public Organisasjon findByOrganisasjonsnummer(String organisasjonsnummer) {
    String sql = "SELECT * FROM organisasjon WHERE organisasjonsnummer = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new OrganisasjonRowMapper(), organisasjonsnummer);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public Organisasjon save(Organisasjon organisasjon) {
    if (organisasjon.getId() == null) {
      // Insert new
      organisasjon.setId(UUID.randomUUID().toString());
      organisasjon.setOpprettet(LocalDateTime.now());
      organisasjon.setOppdatert(LocalDateTime.now());
      
      String sql = """
          INSERT INTO organisasjon (id, navn, kort_navn, type, organisasjonsnummer, 
                                   adresse, nettside, opprettet, aktiv)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
          """;
      
      jdbcTemplate.update(sql,
          organisasjon.getId(),
          organisasjon.getNavn(),
          organisasjon.getKortNavn(),
          organisasjon.getType().name(),
          organisasjon.getOrganisasjonsnummer(),
          organisasjon.getAdresse(),
          organisasjon.getNettside(),
          organisasjon.getOpprettet(),
          organisasjon.getAktiv() != null ? organisasjon.getAktiv() : true);
    } else {
      // Update existing
      organisasjon.setOppdatert(LocalDateTime.now());
      
      String sql = """
          UPDATE organisasjon SET navn = ?, kort_navn = ?, type = ?, organisasjonsnummer = ?,
                                adresse = ?, nettside = ?, aktiv = ?
          WHERE id = ?
          """;
      
      jdbcTemplate.update(sql,
          organisasjon.getNavn(),
          organisasjon.getKortNavn(),
          organisasjon.getType().name(),
          organisasjon.getOrganisasjonsnummer(),
          organisasjon.getAdresse(),
          organisasjon.getNettside(),
          organisasjon.getAktiv(),
          organisasjon.getId());
    }
    
    return organisasjon;
  }

  @Override
  public boolean existsByOrganisasjonsnummer(String organisasjonsnummer) {
    String sql = "SELECT COUNT(*) FROM organisasjon WHERE organisasjonsnummer = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, organisasjonsnummer);
    return count != null && count > 0;
  }

  @Override
  public boolean existsByOrganisasjonsnummerAndIdNot(String organisasjonsnummer, String id) {
    String sql = "SELECT COUNT(*) FROM organisasjon WHERE organisasjonsnummer = ? AND id != ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, organisasjonsnummer, id);
    return count != null && count > 0;
  }

  /**
   * RowMapper for å konvertere database-rad til Organisasjon objekt
   */
  private static class OrganisasjonRowMapper implements RowMapper<Organisasjon> {
    @Override
    public Organisasjon mapRow(ResultSet rs, int rowNum) throws SQLException {
      Organisasjon org = new Organisasjon();
      org.setId(rs.getString("id"));
      org.setNavn(rs.getString("navn"));
      org.setKortNavn(rs.getString("kort_navn"));
      
      // Convert string to enum
      String typeStr = rs.getString("type");
      if (typeStr != null) {
        org.setType(OrganisasjonsType.valueOf(typeStr.toUpperCase()));
      }
      
      org.setOrganisasjonsnummer(rs.getString("organisasjonsnummer"));
      org.setAdresse(rs.getString("adresse"));
      org.setNettside(rs.getString("nettside"));
      
      if (rs.getTimestamp("opprettet") != null) {
        org.setOpprettet(rs.getTimestamp("opprettet").toLocalDateTime());
      }
      
      org.setAktiv(rs.getBoolean("aktiv"));
      return org;
    }
  }
}