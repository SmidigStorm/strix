package no.utdanning.opptak.repository;

import java.util.Optional;
import no.utdanning.opptak.domain.Bruker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrukerRepository extends JpaRepository<Bruker, String> {
  Optional<Bruker> findByEmail(String email);
}
