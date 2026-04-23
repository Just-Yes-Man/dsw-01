package com.dsw02.empleados.departamentos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.EmpleadosApplication;
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

@SpringBootTest(classes = EmpleadosApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DepartamentoCreateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();
        bloqueoAutenticacionRepository.deleteAll();
    }

    @Test
    void shouldCreateDepartamentoSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Ventas"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.creadoEn").isNotEmpty())
                .andExpect(jsonPath("$.actualizadoEn").isNotEmpty());
    }

    @Test
    void shouldReturn409WhenCreatingDuplicateName() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Finanzas"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Finanzas"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn400WhenNombreIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn400WhenNombreIsNull() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNombreExceeds255Chars() throws Exception {
        String longName = "A".repeat(256);
        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"" + longName + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/departamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas"}
                        """))
                .andExpect(status().isUnauthorized());
    }
}
