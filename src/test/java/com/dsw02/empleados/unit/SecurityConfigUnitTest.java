package com.dsw02.empleados.unit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dsw02.empleados.config.SecurityConfig;
import com.dsw02.empleados.config.SessionAuthenticationFilter;
import com.dsw02.empleados.service.AccountTemporarilyLockedException;
import com.dsw02.empleados.service.AuthLockoutService;
import com.dsw02.empleados.service.EmpleadoUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class SecurityConfigUnitTest {

    private EmpleadoUserDetailsService empleadoUserDetailsService;
    private AuthLockoutService authLockoutService;
    private PasswordEncoder passwordEncoder;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        empleadoUserDetailsService = mock(EmpleadoUserDetailsService.class);
        authLockoutService = mock(AuthLockoutService.class);
        passwordEncoder = mock(PasswordEncoder.class);

        securityConfig = new SecurityConfig(
                empleadoUserDetailsService,
                authLockoutService,
                mock(SessionAuthenticationFilter.class),
                passwordEncoder
        );
    }

    @Test
    void shouldSupportUsernamePasswordAuthenticationToken() {
        var provider = securityConfig.authenticationProvider();
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldAuthenticateWithBcrypt2yHash() {
        when(authLockoutService.isBlocked("ana@example.com")).thenReturn(false);
        when(empleadoUserDetailsService.loadUserByUsername("ana@example.com"))
                .thenReturn(new User(
                        "ana@example.com",
                        "$2y$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmno",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ));
        when(passwordEncoder.matches("ana123", "$2y$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmno"))
                .thenReturn(true);

        Authentication result = securityConfig.authenticationProvider().authenticate(
                new UsernamePasswordAuthenticationToken("ana@example.com", "ana123")
        );

        assertEquals("ana@example.com", result.getName());
        verify(passwordEncoder).matches("ana123", "$2y$10$abcdefghijklmnopqrstuv1234567890abcdefghijklmno");
    }

    @Test
    void shouldDetectLockedCauseInExceptionChain() {
        RuntimeException wrapper = new RuntimeException(new AccountTemporarilyLockedException("locked"));
        boolean locked = (boolean) ReflectionTestUtils.invokeMethod(securityConfig, "isLocked", wrapper);
        assertTrue(locked);
    }

    @Test
    void shouldParseAndFilterIgnoredCsrfPaths() {
        ReflectionTestUtils.setField(securityConfig, "csrfIgnoredPaths", "/api/v1/auth/login,  ,/api/v1/auth/logout");

        String[] paths = ReflectionTestUtils.invokeMethod(securityConfig, "csrfIgnoredRequestMatchers");

        assertArrayEquals(new String[]{"/api/v1/auth/login", "/api/v1/auth/logout"}, paths);
    }

    @Test
    void shouldRegisterFailureWhenUserIsNotFound() {
        when(authLockoutService.isBlocked("missing@example.com")).thenReturn(false);
        when(empleadoUserDetailsService.loadUserByUsername("missing@example.com"))
                .thenThrow(new UsernameNotFoundException("not found"));

        var provider = securityConfig.authenticationProvider();
        var authToken = new UsernamePasswordAuthenticationToken("missing@example.com", "pwd");
        assertThrows(BadCredentialsException.class, () -> provider.authenticate(authToken));

        verify(authLockoutService).registerFailure("missing@example.com");
    }

    @Test
    void shouldReturnFalseWhenBootstrapPasswordIsBlank() {
        ReflectionTestUtils.setField(securityConfig, "bootstrapPassword", " ");

        boolean matches = (boolean) ReflectionTestUtils.invokeMethod(securityConfig, "isBootstrapPassword", "any");

        assertFalse(matches);
    }

    @Test
    void shouldReturnFalseWhenStoredPasswordIsBlank() {
        boolean matches = (boolean) ReflectionTestUtils.invokeMethod(
                securityConfig,
                "matchesStoredPassword",
                "raw",
                " "
        );

        assertFalse(matches);
    }
}