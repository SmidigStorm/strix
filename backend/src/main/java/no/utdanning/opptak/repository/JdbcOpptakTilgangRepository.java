package no.utdanning.opptak.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import no.utdanning.opptak.domain.OpptakTilgang;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/** JDBC implementasjon av OpptakTilgangRepository for å håndtere samordnet opptak tilganger. */
@Repository
public class JdbcOpptakTilgangRepository implements OpptakTilgangRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcOpptakTilgangRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<OpptakTilgang> findAll() {
    String sql = "SELECT * FROM opptak_tilgang ORDER BY tildelt DESC";
    return jdbcTemplate.query(sql, new OpptakTilgangRowMapper());
  }

  @Override
  public OpptakTilgang findById(String id) {
    String sql = "SELECT * FROM opptak_tilgang WHERE id = ?";
    try {
      return jdbcTemplate.queryForObject(sql, new OpptakTilgangRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public List<OpptakTilgang> findByOpptakId(String opptakId) {
    String sql = "SELECT * FROM opptak_tilgang WHERE opptak_id = ? ORDER BY tildelt";
    return jdbcTemplate.query(sql, new OpptakTilgangRowMapper(), opptakId);
  }

  @Override
  public List<OpptakTilgang> findByOrganisasjonId(String organisasjonId) {
    String sql = "SELECT * FROM opptak_tilgang WHERE organisasjon_id = ? ORDER BY tildelt DESC";
    return jdbcTemplate.query(sql, new OpptakTilgangRowMapper(), organisasjonId);
  }

  @Override
  public boolean hasAccess(String opptakId, String organisasjonId) {
    String sql = "SELECT COUNT(*) FROM opptak_tilgang WHERE opptak_id = ? AND organisasjon_id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, opptakId, organisasjonId);
    return count != null && count > 0;
  }

  @Override
  public OpptakTilgang findByOpptakIdAndOrganisasjonId(String opptakId, String organisasjonId) {
    String sql = "SELECT * FROM opptak_tilgang WHERE opptak_id = ? AND organisasjon_id = ?";
    try {
      return jdbcTemplate.queryForObject(
          sql, new OpptakTilgangRowMapper(), opptakId, organisasjonId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public OpptakTilgang save(OpptakTilgang tilgang) {
    if (tilgang.getId() == null) {
      // Insert new
      tilgang.setId(UUID.randomUUID().toString());
      tilgang.setTildelt(LocalDateTime.now());

      String sql =
          """
          INSERT INTO opptak_tilgang (id, opptak_id, organisasjon_id, tildelt, tildelt_av)
          VALUES (?, ?, ?, ?, ?)
          """;

      jdbcTemplate.update(
          sql,
          tilgang.getId(),
          tilgang.getOpptakId(),
          tilgang.getOrganisasjonId(),
          tilgang.getTildelt(),
          tilgang.getTildeltAv());
    } else {
      // Update existing (though this is rarely needed for access grants)
      String sql =
          """
          UPDATE opptak_tilgang SET tildelt_av = ?
          WHERE id = ?
          """;

      jdbcTemplate.update(sql, tilgang.getTildeltAv(), tilgang.getId());
    }

    return tilgang;
  }

  @Override
  public boolean deleteById(String id) {
    String sql = "DELETE FROM opptak_tilgang WHERE id = ?";
    int affected = jdbcTemplate.update(sql, id);
    return affected > 0;
  }

  @Override
  public boolean existsById(String id) {
    String sql = "SELECT COUNT(*) FROM opptak_tilgang WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }

  /** RowMapper for å konvertere database-rad til OpptakTilgang objekt */
  private static class OpptakTilgangRowMapper implements RowMapper<OpptakTilgang> {
    @Override
    public OpptakTilgang mapRow(ResultSet rs, int rowNum) throws SQLException {
      OpptakTilgang tilgang = new OpptakTilgang();
      tilgang.setId(rs.getString("id"));
      tilgang.setOpptakId(rs.getString("opptak_id"));
      tilgang.setOrganisasjonId(rs.getString("organisasjon_id"));

      if (rs.getTimestamp("tildelt") != null) {
        tilgang.setTildelt(rs.getTimestamp("tildelt").toLocalDateTime());
      }

      tilgang.setTildeltAv(rs.getString("tildelt_av"));
      return tilgang;
    }
  }
}
