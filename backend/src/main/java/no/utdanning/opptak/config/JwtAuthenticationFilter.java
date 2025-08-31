package no.utdanning.opptak.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import no.utdanning.opptak.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter that validates JWT tokens and sets up Spring Security authentication
 * context for authorized requests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractTokenFromRequest(request);
    logger.debug("JWT Filter - Token extracted: " + (token != null ? "present" : "missing"));

    if (token != null && jwtService.isTokenValid(token)) {
      try {
        String userId = jwtService.extractUserId(token);
        List<String> roles = jwtService.extractRoles(token);

        logger.debug("JWT Authentication - UserId: " + userId + ", Roles: " + roles);

        // Convert roles to Spring Security authorities with ROLE_ prefix
        List<SimpleGrantedAuthority> authorities =
            roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

        logger.debug("JWT Authentication - Authorities: " + authorities);

        // Create authentication token
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userId, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authToken);

        logger.debug("JWT Authentication - SecurityContext set for user: " + userId);

      } catch (SecurityException e) {
        // Log security exception but don't block request - Spring Security will handle unauthorized
        // access
        logger.warn("JWT token validation failed: " + e.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }

  /** Extract JWT token from Authorization header */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
