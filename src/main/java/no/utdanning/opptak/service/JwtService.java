package no.utdanning.opptak.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    
    private static final String SECRET = "mock-jwt-secret-key-for-development-only-please-change-in-production";
    private static final int EXPIRATION_HOURS = 24;
    
    private final SecretKey key;
    
    public JwtService() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    
    public String generateToken(String brukerId, String email, String navn, List<String> roller, String organisasjonId) {
        LocalDateTime expiration = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
        
        JwtBuilder builder = Jwts.builder()
                .subject(brukerId)
                .claim("email", email)
                .claim("navn", navn)
                .claim("roller", roller)
                .issuedAt(new Date())
                .expiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key);
        
        if (organisasjonId != null) {
            builder.claim("organisasjonId", organisasjonId);
        }
        
        return builder.compact();
    }
    
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new SecurityException("Ugyldig JWT token: " + e.getMessage());
        }
    }
    
    public String getBrukerId(Claims claims) {
        return claims.getSubject();
    }
    
    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }
    
    public String getNavn(Claims claims) {
        return claims.get("navn", String.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getRoller(Claims claims) {
        return claims.get("roller", List.class);
    }
    
    public String getOrganisasjonId(Claims claims) {
        return claims.get("organisasjonId", String.class);
    }
    
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}