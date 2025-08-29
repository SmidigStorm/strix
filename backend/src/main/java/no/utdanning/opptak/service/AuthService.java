package no.utdanning.opptak.service;

import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.repository.BrukerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private BrukerRepository brukerRepository;
    
    @Autowired
    private JwtService jwtService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public String login(String email, String passord) {
        Optional<Bruker> brukerOpt = brukerRepository.findByEmail(email);
        
        if (brukerOpt.isEmpty()) {
            throw new SecurityException("Ugyldig email eller passord");
        }
        
        Bruker bruker = brukerOpt.get();
        
        if (!bruker.getAktiv()) {
            throw new SecurityException("Brukeren er deaktivert");
        }
        
        if (!passwordEncoder.matches(passord, bruker.getPassordHash())) {
            throw new SecurityException("Ugyldig email eller passord");
        }
        
        // Oppdater sist innlogget
        bruker.setSistInnlogget(LocalDateTime.now());
        brukerRepository.save(bruker);
        
        // Hent roller
        List<String> rolleNavn = bruker.getRoller().stream()
                .map(brukerRolle -> brukerRolle.getRolleId())
                .toList();
        
        // Generer JWT token
        return jwtService.generateToken(
                bruker.getId(),
                bruker.getEmail(),
                bruker.getNavn(),
                rolleNavn,
                bruker.getOrganisasjonId()
        );
    }
    
    public String hashPassord(String passord) {
        return passwordEncoder.encode(passord);
    }
    
    public boolean validerPassord(String passord, String hash) {
        return passwordEncoder.matches(passord, hash);
    }
    
    public Bruker getBrukerByEmail(String email) {
        return brukerRepository.findByEmail(email)
                .orElseThrow(() -> new SecurityException("Bruker ikke funnet"));
    }
}