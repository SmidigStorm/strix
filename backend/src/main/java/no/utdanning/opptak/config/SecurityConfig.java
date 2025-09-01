package no.utdanning.opptak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security configuration that enables JWT-based authentication and method-level
 * authorization for GraphQL endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize annotations
public class SecurityConfig {

  @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT authentication
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
        .sessionManagement(
            session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)) // Stateless sessions
        .authorizeHttpRequests(
            auth ->
                auth
                    // Allow login mutation (needed for authentication)
                    .requestMatchers("/graphql")
                    .permitAll() // GraphQL endpoint (authorization handled at method level)
                    .requestMatchers("/graphiql", "/graphiql/**", "/api-tester", "/custom-graphiql.html")
                    .permitAll() // GraphiQL development tools
                    .requestMatchers("/", "/index.html", "/assets/**", "/owl-logo.png", "/vite.svg", "/graphiql.html")
                    .permitAll() // Static frontend resources and GraphiQL
                    .requestMatchers(
                        "/opptak", "/opptak/**", 
                        "/organisasjoner", "/organisasjoner/**", 
                        "/utdanninger", "/utdanninger/**")
                    .permitAll() // Frontend SPA routes
                    .anyRequest()
                    .authenticated()) // All other requests require authentication
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class) // Add JWT filter before Spring's default
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setStatus(401);
                          response.setContentType("application/json");
                          response
                              .getWriter()
                              .write(
                                  "{\"error\":\"Unauthorized\",\"message\":\"Valid JWT token required\"}");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setStatus(403);
                          response.setContentType("application/json");
                          response
                              .getWriter()
                              .write(
                                  "{\"error\":\"Forbidden\",\"message\":\"Insufficient permissions\"}");
                        }))
        .build();
  }

  /** CORS configuration to allow frontend requests from development and production */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow requests from development and production origins
    configuration.addAllowedOrigin("http://localhost:3000"); // React dev
    configuration.addAllowedOrigin("http://localhost:5173"); // Vite dev
    configuration.addAllowedOrigin("http://localhost:4200"); // Angular dev
    configuration.addAllowedOrigin("https://opptaksapp.smidigakademiet.no"); // Production
    configuration.addAllowedOrigin("https://www.smidigakademiet.no"); // Production alternative

    configuration.addAllowedMethod("*"); // Allow all HTTP methods
    configuration.addAllowedHeader("*"); // Allow all headers
    configuration.setAllowCredentials(true); // Allow credentials (cookies, auth headers)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
