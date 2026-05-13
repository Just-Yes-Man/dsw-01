package com.dsw02.empleados.departamentos.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoUpdateRequest;
import com.dsw02.empleados.departamentos.exception.DepartamentoConflictException;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import com.dsw02.empleados.EmpleadosApplication;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.service.ResourceNotFoundException;
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
class DepartamentoWriteContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartamentoService departamentoService;

    @Test
    void shouldUpdateDepartamentoAndReturnUpdatedResponse() throws Exception {
        DepartamentoResponse response = TestDataFactory.departamentoResponse(
                1L,
                "Ventas Actualizado",
                EstadoAcceso.ACTIVO);

        when(departamentoService.update(eq(1L), any(DepartamentoUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas Actualizado"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Actualizado"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void shouldReturn400WhenPatchNombreIsEmpty() throws Exception {
        mockMvc.perform(patch("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn400WhenPatchNombreExceeds255Chars() throws Exception {
        String longName = "A".repeat(256);
        mockMvc.perform(patch("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"" + longName + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenPatchNombreDuplicated() throws Exception {
        when(departamentoService.update(eq(1L), any(DepartamentoUpdateRequest.class)))
                .thenThrow(new DepartamentoConflictException("Departamento con nombre 'Finanzas' ya existe"));

        mockMvc.perform(patch("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Finanzas"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn404WhenPatchingNonExistentDepartamento() throws Exception {
        when(departamentoService.update(eq(999L), any(DepartamentoUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Departamento no encontrado"));

        mockMvc.perform(patch("/api/v1/departamentos/999")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Cualquiera"}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteDepartamentoAndReturn204() throws Exception {
        doNothing().when(departamentoService).delete(1L);

        mockMvc.perform(delete("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenDeleteDepartamentoWithEmployees() throws Exception {
        doThrow(new DepartamentoConflictException(
                "No se puede eliminar el departamento porque tiene empleados asociados"))
                .when(departamentoService).delete(1L);

        mockMvc.perform(delete("/api/v1/departamentos/1")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentDepartamento() throws Exception {
        doThrow(new ResourceNotFoundException("Departamento no encontrado"))
                .when(departamentoService).delete(999L);

        mockMvc.perform(delete("/api/v1/departamentos/999")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn401WhenNotAuthenticatedOnPatch() throws Exception {
        mockMvc.perform(patch("/api/v1/departamentos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Test"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenNotAuthenticatedOnDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/departamentos/1"))
                .andExpect(status().isUnauthorized());
    }
}
