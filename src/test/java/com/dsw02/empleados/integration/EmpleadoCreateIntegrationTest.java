package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.repository.BloqueoAutenticacionRepository;
import com.dsw02.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpleadoCreateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

    @BeforeEach
    void setUp() {
        bloqueoAutenticacionRepository.deleteAll();
        empleadoRepository.deleteAll();
    }

    @Test
    void shouldCreateEmpleado() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("EMP-1"));
    }

    @Test
    void shouldRejectTooLongFields() throws Exception {
        String longValue = "A".repeat(101);

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"%s","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """.formatted(longValue)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectClientClaveInCreate() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"clave":"EMP-999","nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
