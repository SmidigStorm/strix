package no.utdanning.opptak.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.utdanning.opptak.domain.Opptak;
import no.utdanning.opptak.domain.OpptaksStatus;
import no.utdanning.opptak.domain.OpptaksType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementasjon av OpptakCrudRepository for produksjonsklar Opptak CRUD operasjoner. */
@Repository
public class JdbcOpptakRepository implements OpptakCrudRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcOpptakRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Opptak> findAll() {
    String sql = "SELECT * FROM opptak ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper());
  }

  @Override
  public Opptak findById(String id) {
    String sql = "SELECT * FROM opptak WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new OpptakRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<Opptak> findByAktiv(boolean aktiv) {
    String sql = "SELECT * FROM opptak WHERE aktiv = ? ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), aktiv);
  }

  @Override
  public List<Opptak> findByAdministratorOrganisasjonId(String administratorOrganisasjonId) {
    String sql =
        "SELECT * FROM opptak WHERE administrator_organisasjon_id = ? ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), administratorOrganisasjonId);
  }

  @Override
  public List<Opptak> findByType(OpptaksType type) {
    String sql = "SELECT * FROM opptak WHERE type = ? ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), type.name());
  }

  @Override
  public List<Opptak> findByAar(Integer aar) {
    String sql = "SELECT * FROM opptak WHERE aar = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), aar);
  }

  @Override
  public List<Opptak> findByStatus(OpptaksStatus status) {
    String sql = "SELECT * FROM opptak WHERE status = ? ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), status.name());
  }

  @Override
  public List<Opptak> findBySamordnet(boolean samordnet) {
    String sql = "SELECT * FROM opptak WHERE samordnet = ? ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), samordnet);
  }

  @Override
  public List<Opptak> findByNavnContainingIgnoreCase(String navnSok) {
    String sql = "SELECT * FROM opptak WHERE UPPER(navn) LIKE UPPER(?) ORDER BY aar DESC, navn";
    String searchPattern = "%" + navnSok + "%";
    return jdbcTemplate.query(sql, new OpptakRowMapper(), searchPattern);
  }

  @Override
  public List<Opptak> findWithFilters(
      String navn,
      OpptaksType type,
      Integer aar,
      OpptaksStatus status,
      Boolean samordnet,
      String administratorOrganisasjonId,
      Boolean aktiv,
      Integer limit,
      Integer offset) {

    StringBuilder sql = new StringBuilder("SELECT * FROM opptak WHERE 1=1");
    List<Object> params = new ArrayList<>();

    // Add WHERE clauses dynamically
    if (navn != null && !navn.trim().isEmpty()) {
      sql.append(" AND UPPER(navn) LIKE UPPER(?)");
      params.add("%" + navn.trim() + "%");
    }
    if (type != null) {
      sql.append(" AND type = ?");
      params.add(type.name());
    }
    if (aar != null) {
      sql.append(" AND aar = ?");
      params.add(aar);
    }
    if (status != null) {
      sql.append(" AND status = ?");
      params.add(status.name());
    }
    if (samordnet != null) {
      sql.append(" AND samordnet = ?");
      params.add(samordnet);
    }
    if (administratorOrganisasjonId != null && !administratorOrganisasjonId.trim().isEmpty()) {
      sql.append(" AND administrator_organisasjon_id = ?");
      params.add(administratorOrganisasjonId.trim());
    }
    if (aktiv != null) {
      sql.append(" AND aktiv = ?");
      params.add(aktiv);
    }

    // Add ORDER BY
    sql.append(" ORDER BY aar DESC, navn");

    // Add pagination
    if (limit != null && limit > 0) {
      sql.append(" LIMIT ?");
      params.add(limit);

      if (offset != null && offset > 0) {
        sql.append(" OFFSET ?");
        params.add(offset);
      }
    }

    return jdbcTemplate.query(sql.toString(), new OpptakRowMapper(), params.toArray());
  }

  @Override
  public long countWithFilters(
      String navn,
      OpptaksType type,
      Integer aar,
      OpptaksStatus status,
      Boolean samordnet,
      String administratorOrganisasjonId,
      Boolean aktiv) {

    StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM opptak WHERE 1=1");
    List<Object> params = new ArrayList<>();

    // Same filter logic as findWithFilters
    if (navn != null && !navn.trim().isEmpty()) {
      sql.append(" AND UPPER(navn) LIKE UPPER(?)");
      params.add("%" + navn.trim() + "%");
    }
    if (type != null) {
      sql.append(" AND type = ?");
      params.add(type.name());
    }
    if (aar != null) {
      sql.append(" AND aar = ?");
      params.add(aar);
    }
    if (status != null) {
      sql.append(" AND status = ?");
      params.add(status.name());
    }
    if (samordnet != null) {
      sql.append(" AND samordnet = ?");
      params.add(samordnet);
    }
    if (administratorOrganisasjonId != null && !administratorOrganisasjonId.trim().isEmpty()) {
      sql.append(" AND administrator_organisasjon_id = ?");
      params.add(administratorOrganisasjonId.trim());
    }
    if (aktiv != null) {
      sql.append(" AND aktiv = ?");
      params.add(aktiv);
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    return count != null ? count : 0;
  }

  @Override
  public Opptak save(Opptak opptak) {
    if (opptak.getId() == null) {
      // Insert new
      opptak.setId(UUID.randomUUID().toString());
      opptak.setOpprettet(LocalDateTime.now());

      String sql =
          """
          INSERT INTO opptak (id, navn, type, aar, administrator_organisasjon_id, samordnet,
                             soknadsfrist, svarfrist, max_utdanninger_per_soknad, status,
                             opptaksomgang, beskrivelse, opprettet, aktiv)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
          """;

      jdbcTemplate.update(
          sql,
          opptak.getId(),
          opptak.getNavn(),
          opptak.getType() != null ? opptak.getType().name() : null,
          opptak.getAar(),
          opptak.getAdministratorOrganisasjonId(),
          opptak.getSamordnet() != null ? opptak.getSamordnet() : false,
          opptak.getSoknadsfrist(),
          opptak.getSvarfrist(),
          opptak.getMaxUtdanningerPerSoknad() != null ? opptak.getMaxUtdanningerPerSoknad() : 10,
          opptak.getStatus() != null ? opptak.getStatus().name() : OpptaksStatus.FREMTIDIG.name(),
          opptak.getOpptaksomgang(),
          opptak.getBeskrivelse(),
          opptak.getOpprettet(),
          opptak.getAktiv() != null ? opptak.getAktiv() : true);
    } else {
      // Update existing
      String sql =
          """
          UPDATE opptak SET navn = ?, type = ?, aar = ?, soknadsfrist = ?, svarfrist = ?,
                           max_utdanninger_per_soknad = ?, status = ?, opptaksomgang = ?,
                           beskrivelse = ?, aktiv = ?
          WHERE id = ?
          """;

      jdbcTemplate.update(
          sql,
          opptak.getNavn(),
          opptak.getType() != null ? opptak.getType().name() : null,
          opptak.getAar(),
          opptak.getSoknadsfrist(),
          opptak.getSvarfrist(),
          opptak.getMaxUtdanningerPerSoknad(),
          opptak.getStatus() != null ? opptak.getStatus().name() : null,
          opptak.getOpptaksomgang(),
          opptak.getBeskrivelse(),
          opptak.getAktiv(),
          opptak.getId());
    }

    return opptak;
  }

  @Override
  public boolean deleteById(String id) {
    String sql = "DELETE FROM opptak WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean deaktiverById(String id) {
    String sql = "UPDATE opptak SET aktiv = false WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean aktiverById(String id) {
    String sql = "UPDATE opptak SET aktiv = true WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean existsById(String id) {
    String sql = "SELECT COUNT(*) FROM opptak WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }

  @Override
  public boolean existsByNavn(String navn) {
    String sql = "SELECT COUNT(*) FROM opptak WHERE navn = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, navn);
    return count != null && count > 0;
  }

  @Override
  public boolean existsByNavnAndIdNot(String navn, String id) {
    String sql = "SELECT COUNT(*) FROM opptak WHERE navn = ? AND id != ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, navn, id);
    return count != null && count > 0;
  }

  /** RowMapper for å konvertere database-rad til Opptak objekt */
  private static class OpptakRowMapper implements RowMapper<Opptak> {
    @Override
    public Opptak mapRow(ResultSet rs, int rowNum) throws SQLException {
      Opptak opptak = new Opptak();
      opptak.setId(rs.getString("id"));
      opptak.setNavn(rs.getString("navn"));

      // Convert string to enum
      String typeStr = rs.getString("type");
      if (typeStr != null) {
        opptak.setType(OpptaksType.valueOf(typeStr));
      }

      opptak.setAar(rs.getInt("aar"));
      opptak.setAdministratorOrganisasjonId(rs.getString("administrator_organisasjon_id"));
      opptak.setSamordnet(rs.getBoolean("samordnet"));

      if (rs.getDate("soknadsfrist") != null) {
        opptak.setSoknadsfrist(rs.getDate("soknadsfrist").toLocalDate());
      }
      if (rs.getDate("svarfrist") != null) {
        opptak.setSvarfrist(rs.getDate("svarfrist").toLocalDate());
      }

      opptak.setMaxUtdanningerPerSoknad(rs.getInt("max_utdanninger_per_soknad"));

      String statusStr = rs.getString("status");
      if (statusStr != null) {
        opptak.setStatus(OpptaksStatus.valueOf(statusStr));
      }

      opptak.setOpptaksomgang(rs.getString("opptaksomgang"));
      opptak.setBeskrivelse(rs.getString("beskrivelse"));

      if (rs.getTimestamp("opprettet") != null) {
        opptak.setOpprettet(rs.getTimestamp("opprettet").toLocalDateTime());
      }

      opptak.setAktiv(rs.getBoolean("aktiv"));
      return opptak;
    }
  }
}
