package no.utdanning.opptak.repository;

import no.utdanning.opptak.domain.Bruker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrukerRepository extends JpaRepository<Bruker, String> {
    Optional<Bruker> findByEmail(String email);
}