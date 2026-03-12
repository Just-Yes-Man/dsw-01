package com.dsw02.empleados.config;

import com.dsw02.empleados.dto.ErrorResponse;
import com.dsw02.empleados.service.AccountTemporarilyLockedException;
import com.dsw02.empleados.service.AuthLockoutService;
import com.dsw02.empleados.service.EmpleadoUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmpleadoUserDetailsService empleadoUserDetailsService;
    private final AuthLockoutService authLockoutService;

    @Value("${security.bootstrap.user:bootstrap_admin}")
    private String bootstrapUser;

    @Value("${security.bootstrap.password:bootstrap123}")
    private String bootstrapPassword;

    public SecurityConfig(EmpleadoUserDetailsService empleadoUserDetailsService,
                          AuthLockoutService authLockoutService) {
        this.empleadoUserDetailsService = empleadoUserDetailsService;
        this.authLockoutService = authLockoutService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = String.valueOf(authentication.getCredentials());

                if (bootstrapUser.equals(username) && bootstrapPassword.equals(password)) {
                    LOGGER.info("event=auth_result outcome=SUCCESS principal={} bootstrap=true", username);
                    return new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_BOOTSTRAP"))
                    );
                }

                if (authLockoutService.isBlocked(username)) {
                    LOGGER.warn("event=auth_result outcome=TEMPORARY_LOCK principal={}", username);
                    throw new AccountTemporarilyLockedException("Cuenta bloqueada temporalmente");
                }

                UserDetails userDetails;
                try {
                    userDetails = empleadoUserDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException ex) {
                    authLockoutService.registerFailure(username);
                    LOGGER.warn("event=auth_result outcome=INVALID_CREDENTIALS principal={}", username);
                    throw new BadCredentialsException("Credenciales inválidas");
                }

                if (!userDetails.isEnabled()) {
                    throw new DisabledException("Cuenta inactiva");
                }

                if (!userDetails.getPassword().equals(password)) {
                    boolean blocked = authLockoutService.registerFailure(username);
                    if (blocked) {
                        LOGGER.warn("event=auth_result outcome=TEMPORARY_LOCK principal={}", username);
                        throw new AccountTemporarilyLockedException("Cuenta bloqueada temporalmente");
                    }
                    LOGGER.warn("event=auth_result outcome=INVALID_CREDENTIALS principal={}", username);
                    throw new BadCredentialsException("Credenciales inválidas");
                }

                authLockoutService.registerSuccess(username);
                LOGGER.info("event=auth_result outcome=SUCCESS principal={} bootstrap=false", username);
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
            LOGGER.warn("event=auth_failed path={} method={} locked={}", request.getRequestURI(), request.getMethod(), locked);
            response.setStatus(locked ? HttpStatus.LOCKED.value() : HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), locked
                    ? new ErrorResponse("LOCKED", "Cuenta bloqueada temporalmente")
                    : new ErrorResponse("UNAUTHORIZED", "Credenciales inválidas o ausentes"));
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }
}
