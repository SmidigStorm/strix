package no.utdanning.opptak.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import no.utdanning.opptak.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OpptakRepository {

  private final JdbcTemplate jdbcTemplate;

  public OpptakRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Organisasjon> findAllOrganisasjoner() {
    String sql = "SELECT * FROM organisasjon WHERE aktiv = true ORDER BY navn";
    return jdbcTemplate.query(sql, new OrganisasjonRowMapper());
  }

  public Optional<Organisasjon> findOrganisasjonById(String id) {
    String sql = "SELECT * FROM organisasjon WHERE id = ? AND aktiv = true";
    List<Organisasjon> results = jdbcTemplate.query(sql, new OrganisasjonRowMapper(), id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  public List<Opptak> findAllOpptak() {
    String sql = "SELECT * FROM opptak WHERE aktiv = true ORDER BY aar DESC, navn";
    return jdbcTemplate.query(sql, new OpptakRowMapper());
  }

  public Optional<Opptak> findOpptakById(String id) {
    String sql = "SELECT * FROM opptak WHERE id = ? AND aktiv = true";
    List<Opptak> results = jdbcTemplate.query(sql, new OpptakRowMapper(), id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  public List<Utdanning> findUtdanningerByOrganisasjonId(String organisasjonId) {
    String sql = "SELECT * FROM utdanning WHERE organisasjon_id = ? AND aktiv = true ORDER BY navn";
    return jdbcTemplate.query(sql, new UtdanningRowMapper(), organisasjonId);
  }

  public List<UtdanningIOpptak> findUtdanningerByOpptakId(String opptakId) {
    String sql =
        """
            SELECT uio.*, u.navn as utdanning_navn, o.navn as opptak_navn
            FROM utdanning_i_opptak uio
            JOIN utdanning u ON uio.utdanning_id = u.id
            JOIN opptak o ON uio.opptak_id = o.id
            WHERE uio.opptak_id = ? AND uio.aktivt = true
            ORDER BY u.navn
        """;
    return jdbcTemplate.query(sql, new UtdanningIOpptakRowMapper(), opptakId);
  }

  public Opptak saveOpptak(Opptak opptak) {
    if (opptak.getId() == null) {
      opptak.setId(java.util.UUID.randomUUID().toString());
    }
    if (opptak.getOpprettet() == null) {
      opptak.setOpprettet(LocalDateTime.now());
    }
    if (opptak.getAktiv() == null) {
      opptak.setAktiv(true);
    }

    String sql =
        """
            INSERT INTO opptak (id, navn, type, aar, soknadsfrist, svarfrist, max_utdanninger_per_soknad,
                               status, opptaksomgang, beskrivelse, opprettet, aktiv)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    jdbcTemplate.update(
        sql,
        opptak.getId(),
        opptak.getNavn(),
        opptak.getType().name(),
        opptak.getAar(),
        opptak.getSoknadsfrist(),
        opptak.getSvarfrist(),
        opptak.getMaxUtdanningerPerSoknad(),
        opptak.getStatus().name(),
        opptak.getOpptaksomgang(),
        opptak.getBeskrivelse(),
        opptak.getOpprettet(),
        opptak.getAktiv());

    return opptak;
  }

  private static class OrganisasjonRowMapper implements RowMapper<Organisasjon> {
    @Override
    public Organisasjon mapRow(ResultSet rs, int rowNum) throws SQLException {
      Organisasjon org = new Organisasjon();
      org.setId(rs.getString("id"));
      org.setNavn(rs.getString("navn"));
      org.setKortNavn(rs.getString("kort_navn"));

      // Map string type to enum
      String typeStr = rs.getString("type");
      if (typeStr != null) {
        org.setType(no.utdanning.opptak.domain.OrganisasjonsType.valueOf(typeStr.toUpperCase()));
      }

      org.setOrganisasjonsnummer(rs.getString("organisasjonsnummer"));
      org.setAdresse(rs.getString("adresse"));
      org.setNettside(rs.getString("nettside"));
      org.setOpprettet(rs.getTimestamp("opprettet").toLocalDateTime());
      org.setAktiv(rs.getBoolean("aktiv"));
      return org;
    }
  }

  private static class OpptakRowMapper implements RowMapper<Opptak> {
    @Override
    public Opptak mapRow(ResultSet rs, int rowNum) throws SQLException {
      Opptak opptak = new Opptak();
      opptak.setId(rs.getString("id"));
      opptak.setNavn(rs.getString("navn"));
      opptak.setType(OpptaksType.valueOf(rs.getString("type")));
      opptak.setAar(rs.getInt("aar"));

      if (rs.getDate("soknadsfrist") != null) {
        opptak.setSoknadsfrist(rs.getDate("soknadsfrist").toLocalDate());
      }
      if (rs.getDate("svarfrist") != null) {
        opptak.setSvarfrist(rs.getDate("svarfrist").toLocalDate());
      }

      opptak.setMaxUtdanningerPerSoknad(rs.getInt("max_utdanninger_per_soknad"));
      opptak.setStatus(OpptaksStatus.valueOf(rs.getString("status")));
      opptak.setOpptaksomgang(rs.getString("opptaksomgang"));
      opptak.setBeskrivelse(rs.getString("beskrivelse"));
      opptak.setOpprettet(rs.getTimestamp("opprettet").toLocalDateTime());
      opptak.setAktiv(rs.getBoolean("aktiv"));

      return opptak;
    }
  }

  private static class UtdanningRowMapper implements RowMapper<Utdanning> {
    @Override
    public Utdanning mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Utdanning(
          rs.getString("id"),
          rs.getString("navn"),
          rs.getString("studienivaa"),
          rs.getInt("studiepoeng"),
          rs.getInt("varighet"),
          rs.getString("studiested"),
          rs.getString("undervisningssprak"),
          rs.getString("beskrivelse"),
          rs.getTimestamp("opprettet").toLocalDateTime(),
          rs.getBoolean("aktiv"),
          rs.getString("organisasjon_id"));
    }
  }

  private static class UtdanningIOpptakRowMapper implements RowMapper<UtdanningIOpptak> {
    @Override
    public UtdanningIOpptak mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new UtdanningIOpptak(
          rs.getString("id"),
          rs.getString("utdanning_id"),
          rs.getString("opptak_id"),
          rs.getInt("antall_plasser"),
          rs.getBoolean("aktivt"),
          rs.getTimestamp("opprettet").toLocalDateTime());
    }
  }
}
