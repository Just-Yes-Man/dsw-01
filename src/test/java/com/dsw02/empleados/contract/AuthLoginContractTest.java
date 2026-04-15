package com.dsw02.empleados.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.config.GlobalExceptionHandler;
import com.dsw02.empleados.config.SecurityConfig;
import com.dsw02.empleados.config.SecurityUsersConfig;
import com.dsw02.empleados.controller.AuthController;
import com.dsw02.empleados.dto.auth.SessionResponse;
import com.dsw02.empleados.service.AuthLockoutService;
import com.dsw02.empleados.service.AuthSessionService;
import com.dsw02.empleados.service.EmpleadoUserDetailsService;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, SecurityUsersConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "security.bootstrap.user=bootstrap_admin",
        "security.bootstrap.password=bootstrap123"
})
class AuthLoginContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthSessionService authSessionService;

    @MockBean
    private EmpleadoUserDetailsService empleadoUserDetailsService;

    @MockBean
    private AuthLockoutService authLockoutService;

    @Test
    void shouldLoginWithSessionCookie() throws Exception {
        SessionResponse response = new SessionResponse();
        response.setAuthenticated(true);
        response.setEmpleadoClave("EMP-1");
        response.setEmail("ana@example.com");
        response.setExpiresAt(OffsetDateTime.now().plusHours(8));

        when(authSessionService.login(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"ana123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.empleadoClave").value("EMP-1"));
    }

    @Test
    void shouldReturn401WithGenericMessageWhenCredentialsAreInvalid() throws Exception {
        when(authSessionService.login(any(), any())).thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"bad@example.com","password":"bad"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }
}
