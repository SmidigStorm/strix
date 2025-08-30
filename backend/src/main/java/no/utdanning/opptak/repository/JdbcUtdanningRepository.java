package no.utdanning.opptak.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import no.utdanning.opptak.domain.Studieform;
import no.utdanning.opptak.domain.Utdanning;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementasjon av UtdanningRepository. Erstatter InMemoryUtdanningRepository for
 * produksjonsklar persistens.
 */
@Repository
public class JdbcUtdanningRepository implements UtdanningRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcUtdanningRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Utdanning> findAll() {
    String sql = "SELECT * FROM utdanning ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper());
  }

  @Override
  public Utdanning findById(String id) {
    String sql = "SELECT * FROM utdanning WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new UtdanningRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<Utdanning> findByAktiv(boolean aktiv) {
    String sql = "SELECT * FROM utdanning WHERE aktiv = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), aktiv);
  }

  @Override
  public List<Utdanning> findByOrganisasjonId(String organisasjonId) {
    String sql = "SELECT * FROM utdanning WHERE organisasjon_id = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), organisasjonId);
  }

  @Override
  public List<Utdanning> findByOrganisasjonIdAndAktiv(String organisasjonId, boolean aktiv) {
    String sql = "SELECT * FROM utdanning WHERE organisasjon_id = ? AND aktiv = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), organisasjonId, aktiv);
  }

  @Override
  public List<Utdanning> findByNavnContainingIgnoreCase(String navnSok) {
    String sql = "SELECT * FROM utdanning WHERE UPPER(navn) LIKE UPPER(?) ORDER BY navn";
    String searchPattern = "%" + navnSok + "%";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), searchPattern);
  }

  @Override
  public List<Utdanning> findByStudienivaa(String studienivaa) {
    String sql = "SELECT * FROM utdanning WHERE studienivaa = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), studienivaa);
  }

  @Override
  public List<Utdanning> findByStudiested(String studiested) {
    String sql = "SELECT * FROM utdanning WHERE studiested = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), studiested);
  }

  @Override
  public List<Utdanning> findByStudieform(Studieform studieform) {
    String sql = "SELECT * FROM utdanning WHERE studieform = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), studieform.name());
  }

  @Override
  public List<Utdanning> findByStarttidspunkt(String starttidspunkt) {
    String sql = "SELECT * FROM utdanning WHERE starttidspunkt = ? ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), starttidspunkt);
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

    StringBuilder sql = new StringBuilder("SELECT * FROM utdanning WHERE 1=1");
    List<Object> params = new ArrayList<>();

    // Add WHERE clauses dynamically
    if (navn != null && !navn.trim().isEmpty()) {
      sql.append(" AND UPPER(navn) LIKE UPPER(?)");
      params.add("%" + navn.trim() + "%");
    }
    if (studienivaa != null && !studienivaa.trim().isEmpty()) {
      sql.append(" AND studienivaa = ?");
      params.add(studienivaa.trim());
    }
    if (studiested != null && !studiested.trim().isEmpty()) {
      sql.append(" AND studiested = ?");
      params.add(studiested.trim());
    }
    if (organisasjonId != null && !organisasjonId.trim().isEmpty()) {
      sql.append(" AND organisasjon_id = ?");
      params.add(organisasjonId.trim());
    }
    if (studieform != null) {
      sql.append(" AND studieform = ?");
      params.add(studieform.name());
    }
    if (aktiv != null) {
      sql.append(" AND aktiv = ?");
      params.add(aktiv);
    }

    // Add ORDER BY
    sql.append(" ORDER BY navn");

    // Add pagination
    if (limit != null && limit > 0) {
      sql.append(" LIMIT ?");
      params.add(limit);

      if (offset != null && offset > 0) {
        sql.append(" OFFSET ?");
        params.add(offset);
      }
    }

    return jdbcTemplate.query(sql.toString(), new UtdanningRowMapper(), params.toArray());
  }

  @Override
  public long countWithFilters(
      String navn,
      String studienivaa,
      String studiested,
      String organisasjonId,
      Studieform studieform,
      Boolean aktiv) {

    StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM utdanning WHERE 1=1");
    List<Object> params = new ArrayList<>();

    // Same filter logic as findWithFilters
    if (navn != null && !navn.trim().isEmpty()) {
      sql.append(" AND UPPER(navn) LIKE UPPER(?)");
      params.add("%" + navn.trim() + "%");
    }
    if (studienivaa != null && !studienivaa.trim().isEmpty()) {
      sql.append(" AND studienivaa = ?");
      params.add(studienivaa.trim());
    }
    if (studiested != null && !studiested.trim().isEmpty()) {
      sql.append(" AND studiested = ?");
      params.add(studiested.trim());
    }
    if (organisasjonId != null && !organisasjonId.trim().isEmpty()) {
      sql.append(" AND organisasjon_id = ?");
      params.add(organisasjonId.trim());
    }
    if (studieform != null) {
      sql.append(" AND studieform = ?");
      params.add(studieform.name());
    }
    if (aktiv != null) {
      sql.append(" AND aktiv = ?");
      params.add(aktiv);
    }

    Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    return count != null ? count : 0;
  }

  @Override
  public Utdanning save(Utdanning utdanning) {
    if (utdanning.getId() == null) {
      // Insert new
      utdanning.setId(UUID.randomUUID().toString());
      utdanning.setOpprettet(LocalDateTime.now());

      String sql =
          """
          INSERT INTO utdanning (id, navn, studienivaa, studiepoeng, varighet, studiested,
                                undervisningssprak, beskrivelse, opprettet, aktiv, organisasjon_id,
                                starttidspunkt, studieform)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
          """;

      jdbcTemplate.update(
          sql,
          utdanning.getId(),
          utdanning.getNavn(),
          utdanning.getStudienivaa(),
          utdanning.getStudiepoeng(),
          utdanning.getVarighet(),
          utdanning.getStudiested(),
          utdanning.getUndervisningssprak(),
          utdanning.getBeskrivelse(),
          utdanning.getOpprettet(),
          utdanning.getAktiv() != null ? utdanning.getAktiv() : true,
          utdanning.getOrganisasjonId(),
          utdanning.getStarttidspunkt(),
          utdanning.getStudieform() != null ? utdanning.getStudieform().name() : null);
    } else {
      // Update existing
      String sql =
          """
          UPDATE utdanning SET navn = ?, studienivaa = ?, studiepoeng = ?, varighet = ?,
                              studiested = ?, undervisningssprak = ?, beskrivelse = ?,
                              aktiv = ?, starttidspunkt = ?, studieform = ?
          WHERE id = ?
          """;

      jdbcTemplate.update(
          sql,
          utdanning.getNavn(),
          utdanning.getStudienivaa(),
          utdanning.getStudiepoeng(),
          utdanning.getVarighet(),
          utdanning.getStudiested(),
          utdanning.getUndervisningssprak(),
          utdanning.getBeskrivelse(),
          utdanning.getAktiv(),
          utdanning.getStarttidspunkt(),
          utdanning.getStudieform() != null ? utdanning.getStudieform().name() : null,
          utdanning.getId());
    }

    return utdanning;
  }

  @Override
  public boolean deleteById(String id) {
    String sql = "DELETE FROM utdanning WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean deaktiverById(String id) {
    String sql = "UPDATE utdanning SET aktiv = false WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean aktiverById(String id) {
    String sql = "UPDATE utdanning SET aktiv = true WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean existsById(String id) {
    String sql = "SELECT COUNT(*) FROM utdanning WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }

  /** RowMapper for å konvertere database-rad til Utdanning objekt */
  private static class UtdanningRowMapper implements RowMapper<Utdanning> {
    @Override
    public Utdanning mapRow(ResultSet rs, int rowNum) throws SQLException {
      Utdanning utdanning = new Utdanning();
      utdanning.setId(rs.getString("id"));
      utdanning.setNavn(rs.getString("navn"));
      utdanning.setStudienivaa(rs.getString("studienivaa"));
      utdanning.setStudiepoeng(rs.getInt("studiepoeng"));
      utdanning.setVarighet(rs.getInt("varighet"));
      utdanning.setStudiested(rs.getString("studiested"));
      utdanning.setUndervisningssprak(rs.getString("undervisningssprak"));
      utdanning.setBeskrivelse(rs.getString("beskrivelse"));

      if (rs.getTimestamp("opprettet") != null) {
        utdanning.setOpprettet(rs.getTimestamp("opprettet").toLocalDateTime());
      }

      utdanning.setAktiv(rs.getBoolean("aktiv"));
      utdanning.setOrganisasjonId(rs.getString("organisasjon_id"));
      utdanning.setStarttidspunkt(rs.getString("starttidspunkt"));

      // Convert string to enum
      String studieformStr = rs.getString("studieform");
      if (studieformStr != null) {
        utdanning.setStudieform(Studieform.valueOf(studieformStr));
      }

      return utdanning;
    }
  }
}
