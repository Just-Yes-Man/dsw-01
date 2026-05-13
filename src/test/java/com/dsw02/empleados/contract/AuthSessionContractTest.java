package com.dsw02.empleados.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.dsw02.empleados.testsupport.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, SecurityUsersConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "security.bootstrap.user=bootstrap_admin",
        "security.bootstrap.password=bootstrap123"
})
class AuthSessionContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthSessionService authSessionService;

    @MockBean
    private EmpleadoUserDetailsService empleadoUserDetailsService;

    @MockBean
    private AuthLockoutService authLockoutService;

    @Test
    void shouldReturnSessionStateWhenSessionIsValid() throws Exception {
        SessionResponse response = TestDataFactory.authenticatedSession("EMP-1", "ana@example.com");

        when(authSessionService.getSession(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/session").session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.empleadoClave").value("EMP-1"));
    }

    @Test
    void shouldLogoutAndClearSessionCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout").session(new MockHttpSession()))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    void shouldReturn401WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/auth/session"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturn401WhenSessionServiceRejectsSession() throws Exception {
        when(authSessionService.getSession(any()))
                .thenThrow(new AuthenticationCredentialsNotFoundException("Sesión no válida"));

        mockMvc.perform(get("/api/v1/auth/session").session(new MockHttpSession()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void shouldClearSecureCookieOnSecureLogout() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("X-Forwarded-Proto", "https")
                        .session(new MockHttpSession()))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Secure")));
    }

        @Test
        void shouldLogoutWithoutSessionAndStillClearCookie() throws Exception {
                mockMvc.perform(post("/api/v1/auth/logout"))
                                .andExpect(status().isNoContent())
                                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
        }
}
