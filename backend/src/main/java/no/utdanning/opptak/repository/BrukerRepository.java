package no.utdanning.opptak.repository;

import java.util.List;
import java.util.Optional;
import no.utdanning.opptak.domain.Bruker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrukerRepository extends JpaRepository<Bruker, String> {
  Optional<Bruker> findByEmail(String email);

  /** Find all users with a specific role ID */
  @Query("SELECT b FROM Bruker b JOIN b.roller br WHERE br.rolleId = :rolleId")
  List<Bruker> findByRollerRolleId(@Param("rolleId") String rolleId);
}
