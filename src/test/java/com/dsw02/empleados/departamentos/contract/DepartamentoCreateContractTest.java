package com.dsw02.empleados.departamentos.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.dto.DepartamentoCreateRequest;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.exception.DepartamentoConflictException;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import com.dsw02.empleados.EmpleadosApplication;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.testsupport.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = EmpleadosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "security.bootstrap.user=bootstrap_admin",
    "security.bootstrap.password=bootstrap123"
})
class DepartamentoCreateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartamentoService departamentoService;

    @Test
    void shouldCreateDepartamentoAndReturnEstadoActivo() throws Exception {
                DepartamentoResponse response = TestDataFactory.departamentoResponse(1L, "Ventas", EstadoAcceso.ACTIVO);

        when(departamentoService.create(any(DepartamentoCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ventas"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
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
    void shouldReturn409WhenNombreDuplicated() throws Exception {
        when(departamentoService.create(any(DepartamentoCreateRequest.class)))
                .thenThrow(new DepartamentoConflictException("Departamento con nombre 'Ventas' ya existe"));

        mockMvc.perform(post("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
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
