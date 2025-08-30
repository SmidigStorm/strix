# 🚨 CRITICAL SECURITY AUDIT - Strix Opptakssystem

**Date**: 2025-08-30  
**Severity**: **CRITICAL - NO BACKEND AUTHORIZATION**  
**Status**: Immediate action required before ANY production use  

---

## ⚠️ EXECUTIVE SUMMARY

The Strix application currently has **ZERO backend authorization**. Despite having JWT authentication infrastructure, **no authorization checks are performed** on GraphQL resolvers. This means:

- ❌ **Any user can perform ANY operation** by bypassing the frontend
- ❌ **JWT tokens are generated but never validated** for business operations
- ❌ **All GraphQL endpoints are completely unprotected**
- ❌ **Frontend security is purely cosmetic** ("security theater")

**Bottom Line**: A Søker (applicant) can create universities, delete organizations, and modify any data by sending direct GraphQL requests.

---

## 🔓 CURRENT SECURITY STATE

### **What EXISTS (but unused)**
✅ JWT token generation (`JwtService.java`)  
✅ Password hashing with BCrypt (`AuthService.java`)  
✅ Login mutation that returns JWT tokens  
✅ Frontend sends Authorization headers  
✅ Database has roles and users  

### **What's MISSING (critical)**
❌ Spring Security configuration  
❌ JWT validation filter  
❌ Authorization checks in GraphQL resolvers  
❌ Method-level security annotations  
❌ Permission system implementation  
❌ Security context for current user  

---

## 🎯 PRIORITIZED SECURITY IMPROVEMENTS

### **🔴 Priority 1: IMMEDIATE (Block all unauthorized access)**

#### **1.1 Add Spring Security Dependency**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### **1.2 Create JWT Authentication Filter**
```java
// JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        String token = extractToken(request);
        
        if (token != null && jwtService.validateToken(token)) {
            String userId = jwtService.extractUserId(token);
            List<String> roles = jwtService.extractRoles(token);
            
            // Set authentication in SecurityContext
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(userId, null, 
                    roles.stream().map(SimpleGrantedAuthority::new).toList());
            
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### **1.3 Configure Spring Security**
```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/graphql").permitAll() // GraphQL endpoint
                .requestMatchers("/graphiql").permitAll() // GraphiQL UI
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

### **🟠 Priority 2: CRITICAL (Protect all GraphQL operations)**

#### **2.1 Add Authorization to Query Resolvers**
```java
// OrganisasjonQueryResolver.java
@Controller
@PreAuthorize("isAuthenticated()") // Require authentication for all methods
public class OrganisasjonQueryResolver {
    
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPPTAKSLEDER', 'SOKNADSBEHANDLER', 'SOKER')")
    public List<Organisasjon> organisasjoner(@Argument OrganisasjonFilter filter) {
        // Existing implementation
        // TODO: Filter based on user's organization for non-admin users
    }
}
```

#### **2.2 Protect Mutation Resolvers**
```java
// OrganisasjonMutationResolver.java
@Controller
@PreAuthorize("isAuthenticated()")
public class OrganisasjonMutationResolver {
    
    @MutationMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Organisasjon opprettOrganisasjon(@Argument OpprettOrganisasjonInput input) {
        // Only administrators can create organizations
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('ADMINISTRATOR') or " +
                  "(hasRole('OPPTAKSLEDER') and @authService.userBelongsToOrganization(#input.id))")
    public Organisasjon oppdaterOrganisasjon(@Argument OppdaterOrganisasjonInput input) {
        // Administrators can update any, Opptaksleder only their own
    }
    
    @MutationMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Organisasjon deaktiverOrganisasjon(@Argument String id) {
        // Only administrators can deactivate
    }
}
```

#### **2.3 Create Authorization Service**
```java
// AuthorizationService.java
@Service
public class AuthorizationService {
    
    @Autowired
    private BrukerRepository brukerRepository;
    
    public boolean userBelongsToOrganization(String organisasjonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        
        Bruker user = brukerRepository.findById(userId)
            .orElseThrow(() -> new SecurityException("User not found"));
            
        return organisasjonId.equals(user.getOrganisasjonId());
    }
    
    public boolean canViewOrganization(String organisasjonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Administrators and Søknadsbehandler can see all
        if (auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || 
                          a.getAuthority().equals("ROLE_SOKNADSBEHANDLER"))) {
            return true;
        }
        
        // Others can only see their own organization
        return userBelongsToOrganization(organisasjonId);
    }
}
```

---

### **🟡 Priority 3: IMPORTANT (Data filtering and row-level security)**

#### **3.1 Implement Data Filtering Based on Roles**
```java
@QueryMapping
public List<Organisasjon> organisasjoner(@Argument OrganisasjonFilter filter) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String role = auth.getAuthorities().iterator().next().getAuthority();
    
    switch(role) {
        case "ROLE_ADMINISTRATOR":
        case "ROLE_SOKNADSBEHANDLER":
            // Can see all organizations
            return organisasjonRepository.findAll();
            
        case "ROLE_OPPTAKSLEDER":
            // Can only see own organization
            String userId = auth.getName();
            Bruker user = brukerRepository.findById(userId).orElseThrow();
            return organisasjonRepository.findById(user.getOrganisasjonId())
                .map(List::of)
                .orElse(List.of());
                
        case "ROLE_SOKER":
            // Can see active organizations they can apply to
            return organisasjonRepository.findByAktiv(true);
            
        default:
            throw new SecurityException("Unknown role");
    }
}
```

#### **3.2 Add Audit Logging**
```java
@Component
@Aspect
public class SecurityAuditAspect {
    
    @AfterReturning("@annotation(PreAuthorize)")
    public void auditSecureMethodAccess(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("User {} accessed {} with roles {}", 
            auth.getName(), 
            joinPoint.getSignature().getName(),
            auth.getAuthorities());
    }
    
    @AfterThrowing(pointcut = "@annotation(PreAuthorize)", throwing = "ex")
    public void auditSecurityViolation(JoinPoint joinPoint, Exception ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.error("SECURITY VIOLATION: User {} denied access to {} - {}", 
            auth != null ? auth.getName() : "anonymous",
            joinPoint.getSignature().getName(),
            ex.getMessage());
    }
}
```

---

### **🟢 Priority 4: GOOD PRACTICE (Defense in depth)**

#### **4.1 Add Rate Limiting**
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String clientId = getClientIdentifier(request);
        RateLimiter limiter = limiters.computeIfAbsent(clientId, 
            k -> RateLimiter.create(100.0)); // 100 requests per second
        
        if (!limiter.tryAcquire()) {
            response.setStatus(429); // Too Many Requests
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### **4.2 Add GraphQL Query Depth Limiting**
```java
@Bean
public GraphQlWebMvcConfigurer graphQlConfigurer() {
    return GraphQlWebMvcConfigurer.create()
        .interceptor(new MaxQueryDepthInstrumentation(10)) // Max depth 10
        .interceptor(new MaxQueryComplexityInstrumentation(1000)); // Max complexity 1000
}
```

#### **4.3 Implement Field-Level Security**
```java
@SchemaMapping(typeName = "Bruker", field = "passordHash")
@PreAuthorize("hasRole('ADMINISTRATOR')") // Only admin can see password hashes
public String brukerPassordHash(Bruker bruker) {
    return bruker.getPassordHash();
}

@SchemaMapping(typeName = "Organisasjon", field = "finansiellInfo")
@PreAuthorize("hasRole('ADMINISTRATOR') or " +
              "(hasRole('OPPTAKSLEDER') and @authService.userBelongsToOrganization(#organisasjon.id))")
public FinansiellInfo organisasjonFinansiellInfo(Organisasjon organisasjon) {
    return finansiellInfoRepository.findByOrganisasjonId(organisasjon.getId());
}
```

---

## 🧪 SECURITY TESTING REQUIREMENTS

### **Integration Tests for Each Role**
```java
@Test
@WithMockUser(roles = "SOKER")
public void sokerCannotCreateOrganization() {
    // Should throw AccessDeniedException
}

@Test
@WithMockUser(roles = "ADMINISTRATOR")
public void administratorCanCreateOrganization() {
    // Should succeed
}

@Test
@WithAnonymousUser
public void anonymousUserCannotAccessOrganizations() {
    // Should return 401 Unauthorized
}
```

### **Penetration Testing Checklist**
- [ ] Test direct GraphQL requests without JWT token
- [ ] Test with expired JWT tokens
- [ ] Test with modified JWT tokens (tampered signature)
- [ ] Test role escalation attempts
- [ ] Test SQL injection in GraphQL variables
- [ ] Test query depth attacks
- [ ] Test rate limiting effectiveness
- [ ] Test cross-organization data access

---

## 📊 SECURITY MATURITY LEVELS

### **Current State: Level 0 - No Security**
- ❌ No backend authorization
- ❌ All endpoints unprotected
- ❌ Frontend-only security

### **Target State: Level 3 - Robust Security**
- ✅ JWT authentication with validation
- ✅ Role-based authorization on all endpoints
- ✅ Row-level security for data filtering
- ✅ Audit logging of all access
- ✅ Rate limiting and query complexity limits
- ✅ Field-level security for sensitive data
- ✅ Comprehensive security testing

### **Implementation Timeline**
- **Week 1**: Priority 1 & 2 (Block unauthorized access)
- **Week 2**: Priority 3 (Data filtering)
- **Week 3**: Priority 4 (Defense in depth)
- **Week 4**: Security testing and penetration testing

---

## ⚠️ CRITICAL WARNING

**DO NOT DEPLOY TO PRODUCTION** until at least Priority 1 and 2 are fully implemented and tested. The current state represents a complete security failure that would result in:

1. **Data Breach**: All organization data exposed
2. **Data Manipulation**: Unauthorized creation/modification/deletion
3. **Compliance Violations**: GDPR and Norwegian privacy law violations
4. **Reputation Damage**: Complete loss of trust

---

## 📝 CHECKLIST FOR IMMEDIATE ACTION

- [ ] Add Spring Security dependency to pom.xml
- [ ] Create and configure JWT authentication filter
- [ ] Add @PreAuthorize to ALL GraphQL resolvers
- [ ] Create AuthorizationService for complex rules
- [ ] Implement data filtering based on user roles
- [ ] Add comprehensive security tests
- [ ] Perform penetration testing
- [ ] Document security model for operations team
- [ ] Train developers on security best practices
- [ ] Implement security code review process

---

**Remember**: Security is not optional. Every unprotected endpoint is a potential data breach.