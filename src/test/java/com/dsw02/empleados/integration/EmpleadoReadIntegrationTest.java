package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dsw02.empleados.repository.BloqueoAutenticacionRepository;
import com.dsw02.empleados.repository.EmpleadoRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpleadoReadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

        @Autowired
        private BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

        @MockBean
        private Clock clock;

        private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-01-01T00:00:00Z"));

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
                bloqueoAutenticacionRepository.deleteAll();
                now.set(Instant.parse("2026-01-01T00:00:00Z"));
                when(clock.getZone()).thenReturn(ZoneOffset.UTC);
                when(clock.withZone(ZoneId.of("UTC"))).thenReturn(clock);
                when(clock.instant()).thenAnswer(invocation -> now.get());
        }

        private void advanceMinutes(long minutes) {
                now.set(now.get().plusSeconds(minutes * 60));
    }

    @Test
    void shouldListAndReadByClave() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/empleados").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].clave").value("EMP-1"));

        mockMvc.perform(get("/api/v1/empleados/EMP-1").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("EMP-1"));
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
                mockMvc.perform(get("/api/v1/empleados/EMP-999").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNavigatePagesAndKeepOrder() throws Exception {
        for (int index = 0; index < 25; index++) {
            mockMvc.perform(post("/api/v1/empleados")
                            .with(httpBasic("bootstrap_admin", "bootstrap123"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre":"Empleado %d","direccion":"Calle %d","telefono":"555-%04d","email":"empleado%d@example.com","password":"pwd%d","estadoAcceso":"ACTIVO"}
                                    """.formatted(index, index, index, index, index)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/empleados?page=1").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.items.length()").value(10))
                .andExpect(jsonPath("$.items[0].clave").value("EMP-11"));

        mockMvc.perform(get("/api/v1/empleados?page=2").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.items.length()").value(5))
                .andExpect(jsonPath("$.items[0].clave").value("EMP-21"));
    }

    @Test
    void shouldReturnEmptyItemsForOutOfRangePage() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/empleados?page=9999").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void shouldRejectNegativePage() throws Exception {
                mockMvc.perform(get("/api/v1/empleados?page=-1").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isBadRequest());
    }

        @Test
        void shouldRejectNonNumericPage() throws Exception {
                mockMvc.perform(get("/api/v1/empleados?page=abc").with(httpBasic("bootstrap_admin", "bootstrap123")))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

    @Test
    void shouldRejectInvalidEmployeePassword() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLockAfterFiveFailedAttempts() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        for (int index = 0; index < 4; index++) {
            mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.code").value("LOCKED"));

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isLocked());
    }

    @Test
    void shouldResetFailureCounterAfterSuccessfulAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        for (int index = 0; index < 4; index++) {
            mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAuthenticationAfterLockoutExpiration() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated());

        for (int index = 0; index < 5; index++) {
            mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "wrong")))
                    .andExpect(index == 4 ? status().isLocked() : status().isUnauthorized());
        }

        advanceMinutes(16);

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInactiveEmployeeAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"INACTIVO"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("ana@example.com", "ana123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnauthorizedQueryRequests() throws Exception {
        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/empleados/EMP-1"))
                .andExpect(status().isUnauthorized());
    }

        @Test
        void shouldKeepPagedQueryP95UnderTwoSeconds() throws Exception {
                mockMvc.perform(post("/api/v1/empleados")
                                                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                                                """))
                                .andExpect(status().isCreated());

                long[] latenciesMs = new long[30];
                for (int index = 0; index < latenciesMs.length; index++) {
                        long start = System.nanoTime();
                        mockMvc.perform(get("/api/v1/empleados?page=0").with(httpBasic("bootstrap_admin", "bootstrap123")))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.size").value(10));
                        latenciesMs[index] = (System.nanoTime() - start) / 1_000_000;
                }

                Arrays.sort(latenciesMs);
                int p95Index = (int) Math.ceil(0.95 * latenciesMs.length) - 1;
                long p95 = latenciesMs[p95Index];
                assertTrue(p95 < 2_000, "p95 esperado < 2000ms, actual=" + p95 + "ms");
        }
}
