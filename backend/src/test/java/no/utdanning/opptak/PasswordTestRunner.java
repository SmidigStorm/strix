package no.utdanning.opptak;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTestRunner {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "test123";
        String expectedHash = "$2b$12$gODFdOmRlTz1kbn28ginSOdXwlLOz2W1GQ1242SKcimfnNs.Jw62a";
        
        System.out.println("Testing BCrypt password validation:");
        System.out.println("Password: " + password);
        System.out.println("Expected hash: " + expectedHash);
        System.out.println("Matches: " + encoder.matches(password, expectedHash));
        
        // Generate a fresh hash for comparison
        String freshHash = encoder.encode(password);
        System.out.println("Fresh hash: " + freshHash);
        System.out.println("Fresh matches: " + encoder.matches(password, freshHash));
    }
}