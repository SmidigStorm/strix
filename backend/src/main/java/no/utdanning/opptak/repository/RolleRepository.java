package no.utdanning.opptak.repository;

import java.util.List;
import java.util.Optional;
import no.utdanning.opptak.domain.Rolle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for rolle (role) entities */
@Repository
public interface RolleRepository extends JpaRepository<Rolle, String> {

  /** Find role by name */
  Optional<Rolle> findByNavn(String navn);

  /** Find all active roles (if we implement active/inactive in future) */
  List<Rolle> findAll();
}
