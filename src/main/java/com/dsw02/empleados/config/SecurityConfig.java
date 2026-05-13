package com.dsw02.empleados.config;

import com.dsw02.empleados.dto.ErrorResponse;
import com.dsw02.empleados.service.AccountTemporarilyLockedException;
import com.dsw02.empleados.service.AuthLockoutService;
import com.dsw02.empleados.service.EmpleadoUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmpleadoUserDetailsService empleadoUserDetailsService;
    private final AuthLockoutService authLockoutService;
    private final SessionAuthenticationFilter sessionAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.bootstrap.user:}")
    private String bootstrapUser;

    @Value("${security.bootstrap.password:}")
    private String bootstrapPassword;

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Value("${security.csrf.ignored-paths:/api/v1/auth/login,/api/v1/auth/logout}")
    private String csrfIgnoredPaths;

    public SecurityConfig(EmpleadoUserDetailsService empleadoUserDetailsService,
                          AuthLockoutService authLockoutService,
                          SessionAuthenticationFilter sessionAuthenticationFilter,
                          PasswordEncoder passwordEncoder) {
        this.empleadoUserDetailsService = empleadoUserDetailsService;
        this.authLockoutService = authLockoutService;
        this.sessionAuthenticationFilter = sessionAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = String.valueOf(authentication.getCredentials());

                if (isBootstrapUser(username) && isBootstrapPassword(password)) {
                    LOGGER.info("event=auth_result outcome=SUCCESS bootstrap=true");
                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_BOOTSTRAP"))
                    );
                }

                if (authLockoutService.isBlocked(username)) {
                    LOGGER.warn("event=auth_result outcome=TEMPORARY_LOCK");
                    throw new AccountTemporarilyLockedException("Cuenta bloqueada temporalmente");
                }

                UserDetails userDetails;
                try {
                    userDetails = empleadoUserDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException ex) {
                    authLockoutService.registerFailure(username);
                    LOGGER.warn("event=auth_result outcome=INVALID_CREDENTIALS");
                    throw new BadCredentialsException("Credenciales inválidas");
                }

                if (!userDetails.isEnabled()) {
                    throw new DisabledException("Cuenta inactiva");
                }

                if (!matchesStoredPassword(password, userDetails.getPassword())) {
                    boolean blocked = authLockoutService.registerFailure(username);
                    if (blocked) {
                        LOGGER.warn("event=auth_result outcome=TEMPORARY_LOCK");
                        throw new AccountTemporarilyLockedException("Cuenta bloqueada temporalmente");
                    }
                    LOGGER.warn("event=auth_result outcome=INVALID_CREDENTIALS");
                    throw new BadCredentialsException("Credenciales inválidas");
                }

                authLockoutService.registerSuccess(username);
                LOGGER.info("event=auth_result outcome=SUCCESS bootstrap=false");
                return new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        userDetails.getAuthorities()
                );
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            boolean locked = isLocked(authException);
            LOGGER.warn("event=auth_failed locked={}", locked);
            response.setStatus(locked ? HttpStatus.LOCKED.value() : HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), locked
                    ? new ErrorResponse("LOCKED", "Cuenta bloqueada temporalmente")
                    : new ErrorResponse("UNAUTHORIZED", "Credenciales inválidas"));
        };
    }

    private boolean isLocked(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof AccountTemporarilyLockedException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean isBootstrapUser(String username) {
        return bootstrapUser != null && !bootstrapUser.isBlank() && bootstrapUser.equals(username);
    }

    private boolean isBootstrapPassword(String providedPassword) {
        if (bootstrapPassword == null || bootstrapPassword.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                bootstrapPassword.getBytes(StandardCharsets.UTF_8),
                providedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean matchesStoredPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        // Fallback for legacy plaintext records that were created before password hashing.
        return MessageDigest.isEqual(
                storedPassword.getBytes(StandardCharsets.UTF_8),
                rawPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isBcryptHash(String passwordValue) {
        return passwordValue.startsWith("$2a$")
                || passwordValue.startsWith("$2b$")
                || passwordValue.startsWith("$2y$");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(csrfIgnoredRequestMatchers()))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/actuator/health",
                            "/api/v1/auth/login",
                            "/api/v1/auth/session",
                            "/api/v1/auth/logout"
                    ).permitAll()
                        .anyRequest().authenticated())
                        .addFilterBefore(sessionAuthenticationFilter, BasicAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }

    private String[] csrfIgnoredRequestMatchers() {
        return Arrays.stream(csrfIgnoredPaths.split(","))
                .map(String::trim)
                .filter(path -> !path.isBlank())
                .toArray(String[]::new);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
