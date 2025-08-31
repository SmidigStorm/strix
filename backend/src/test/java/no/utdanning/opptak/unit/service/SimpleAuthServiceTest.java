package no.utdanning.opptak.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import no.utdanning.opptak.domain.Bruker;
import no.utdanning.opptak.domain.BrukerRolle;
import no.utdanning.opptak.repository.BrukerRepository;
import no.utdanning.opptak.service.JwtService;
import no.utdanning.opptak.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Simple AuthService Tests")
class SimpleAuthServiceTest {

  @Mock private BrukerRepository brukerRepository;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthService authService;

  private Bruker testBruker;

  @BeforeEach
  void setUp() {
    testBruker = new Bruker();
    testBruker.setId("BRUKER-123");
    testBruker.setEmail("test@example.com");
    testBruker.setNavn("Test Bruker");
    testBruker.setOrganisasjonId("ORG-123");
    testBruker.setAktiv(true);
    testBruker.setPassordHash("$2a$10$hashedPassword");

    Set<BrukerRolle> roller = new HashSet<>();
    BrukerRolle rolle = new BrukerRolle();
    rolle.setRolleId("OPPTAKSLEDER");
    roller.add(rolle);
    testBruker.setRoller(roller);
  }

  @Test
  @DisplayName("Should successfully login with valid credentials")
  void testLoginSuccess() {
    // Arrange
    String email = "test@example.com";
    String password = "Test123!";
    String expectedToken = "jwt.token.here";

    // Set up password that will match
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword = encoder.encode(password);
    testBruker.setPassordHash(hashedPassword);

    when(brukerRepository.findByEmail(email)).thenReturn(Optional.of(testBruker));
    when(brukerRepository.save(any(Bruker.class))).thenReturn(testBruker);
    when(jwtService.generateToken(anyString(), anyString(), anyString(), any(), anyString()))
        .thenReturn(expectedToken);

    // Act
    String token = authService.login(email, password);

    // Assert
    assertEquals(expectedToken, token);
    verify(brukerRepository).findByEmail(email);
    verify(brukerRepository).save(any(Bruker.class));
  }

  @Test
  @DisplayName("Should throw exception for non-existent user")
  void testLoginUserNotFound() {
    // Arrange
    String email = "notfound@example.com";
    String password = "Test123!";

    when(brukerRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    SecurityException exception =
        assertThrows(SecurityException.class, () -> authService.login(email, password));

    assertEquals("Ugyldig email eller passord", exception.getMessage());
    verify(brukerRepository).findByEmail(email);
    verify(brukerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception for inactive user")
  void testLoginInactiveUser() {
    // Arrange
    String email = "test@example.com";
    String password = "Test123!";
    testBruker.setAktiv(false);

    when(brukerRepository.findByEmail(email)).thenReturn(Optional.of(testBruker));

    // Act & Assert
    SecurityException exception =
        assertThrows(SecurityException.class, () -> authService.login(email, password));

    assertEquals("Brukeren er deaktivert", exception.getMessage());
    verify(brukerRepository).findByEmail(email);
    verify(brukerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception for wrong password")
  void testLoginWrongPassword() {
    // Arrange
    String email = "test@example.com";
    String wrongPassword = "WrongPassword";

    // Use a real encoder to create a hash that won't match
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword = encoder.encode("CorrectPassword");
    testBruker.setPassordHash(hashedPassword);

    when(brukerRepository.findByEmail(email)).thenReturn(Optional.of(testBruker));

    // Act & Assert
    SecurityException exception =
        assertThrows(SecurityException.class, () -> authService.login(email, wrongPassword));

    assertEquals("Ugyldig email eller passord", exception.getMessage());
    verify(brukerRepository).findByEmail(email);
    verify(brukerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should hash password correctly")
  void testHashPassord() {
    // Arrange
    String password = "Test123!";

    // Act
    String hashedPassword = authService.hashPassord(password);

    // Assert
    assertNotNull(hashedPassword);
    assertTrue(hashedPassword.startsWith("$2a$"));
    assertTrue(hashedPassword.length() > 50);
  }

  @Test
  @DisplayName("Should validate password correctly")
  void testValiderPassord() {
    // Arrange
    String password = "Test123!";
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hash = encoder.encode(password);

    // Act
    boolean isValid = authService.validerPassord(password, hash);
    boolean isInvalid = authService.validerPassord("WrongPassword", hash);

    // Assert
    assertTrue(isValid);
    assertFalse(isInvalid);
  }

  @Test
  @DisplayName("Should get user by email")
  void testGetBrukerByEmail() {
    // Arrange
    String email = "test@example.com";
    when(brukerRepository.findByEmail(email)).thenReturn(Optional.of(testBruker));

    // Act
    Bruker result = authService.getBrukerByEmail(email);

    // Assert
    assertNotNull(result);
    assertEquals(testBruker.getId(), result.getId());
    assertEquals(testBruker.getEmail(), result.getEmail());
    verify(brukerRepository).findByEmail(email);
  }

  @Test
  @DisplayName("Should throw exception when user not found by email")
  void testGetBrukerByEmailNotFound() {
    // Arrange
    String email = "notfound@example.com";
    when(brukerRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    SecurityException exception =
        assertThrows(SecurityException.class, () -> authService.getBrukerByEmail(email));

    assertEquals("Bruker ikke funnet", exception.getMessage());
    verify(brukerRepository).findByEmail(email);
  }

  @Test
  @DisplayName("Should update last login time on successful login")
  void testLoginUpdatesLastLoginTime() {
    // Arrange
    String email = "test@example.com";
    String password = "Test123!";

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword = encoder.encode(password);
    testBruker.setPassordHash(hashedPassword);

    LocalDateTime beforeLogin = LocalDateTime.now();

    when(brukerRepository.findByEmail(email)).thenReturn(Optional.of(testBruker));
    when(brukerRepository.save(any(Bruker.class)))
        .thenAnswer(
            invocation -> {
              Bruker savedUser = invocation.getArgument(0);
              assertNotNull(savedUser.getSistInnlogget());
              assertTrue(savedUser.getSistInnlogget().isAfter(beforeLogin));
              return savedUser;
            });
    when(jwtService.generateToken(anyString(), anyString(), anyString(), any(), anyString()))
        .thenReturn("token");

    // Act
    authService.login(email, password);

    // Assert
    verify(brukerRepository).save(any(Bruker.class));
  }
}
