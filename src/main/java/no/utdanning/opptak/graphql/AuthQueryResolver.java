package no.utdanning.opptak.graphql;

import io.jsonwebtoken.Claims;
import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.graphql.dto.TestBruker;
import no.utdanning.opptak.service.AuthService;
import no.utdanning.opptak.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class AuthQueryResolver {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtService jwtService;
    
    @QueryMapping
    public Bruker meg() {
        String token = hentTokenFraRequest();
        
        if (token == null) {
            throw new SecurityException("Ingen gyldig JWT token funnet");
        }
        
        try {
            Claims claims = jwtService.validateToken(token);
            String email = jwtService.getEmail(claims);
            
            return authService.getBrukerByEmail(email);
        } catch (Exception e) {
            throw new SecurityException("Ugyldig token: " + e.getMessage());
        }
    }
    
    @QueryMapping
    public List<TestBruker> testBrukere() {
        return Arrays.asList(
            new TestBruker(
                "opptaksleder@ntnu.no",
                "Kari Opptaksleder",
                Arrays.asList("OPPTAKSLEDER"),
                "NTNU-TEST",
                "test123"
            ),
            new TestBruker(
                "behandler@uio.no", 
                "Per Behandler",
                Arrays.asList("SOKNADSBEHANDLER"),
                "UiO-TEST",
                "test123"
            ),
            new TestBruker(
                "soker@student.no",
                "Astrid Søker", 
                Arrays.asList("SOKER"),
                null,
                "test123"
            ),
            new TestBruker(
                "admin@samordnetopptak.no",
                "Bjørn SO-Administrator",
                Arrays.asList("OPPTAKSLEDER"),
                "SO-TEST",
                "test123"
            )
        );
    }
    
    private String hentTokenFraRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
}