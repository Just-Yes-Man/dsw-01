package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class EmpleadoWriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldUpdateAndDeleteEmpleado() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Maria"));

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectClientClaveInUpdate() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clave":"EMP-99","nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
