package com.dsw02.empleados.departamentos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class DepartamentoUpdateIntegrationTest extends BaseIntegrationTest {

    private Departamento saveDepartamento(String nombre) {
        Departamento dep = new Departamento();
        dep.setNombre(nombre);
        dep.setEstado(EstadoAcceso.ACTIVO);
        return departamentoRepository.save(dep);
    }

    @Test
    void shouldUpdateDepartamentoNameSuccessfully() throws Exception {
        Departamento saved = saveDepartamento("Ventas");

        mockMvc.perform(patch("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Ventas Internacional"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas Internacional"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void shouldReturn400WhenPatchNombreIsEmpty() throws Exception {
        Departamento saved = saveDepartamento("Ventas");

        mockMvc.perform(patch("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldReturn409WhenPatchWithDuplicateName() throws Exception {
        saveDepartamento("Finanzas");
        Departamento ventas = saveDepartamento("Ventas");

        mockMvc.perform(patch("/api/v1/departamentos/" + ventas.getId())
                .with(bootstrapAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Finanzas"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn404WhenPatchingNonExistentId() throws Exception {
        mockMvc.perform(patch("/api/v1/departamentos/99999")
                .with(bootstrapAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"Cualquiera"}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSoftDeleteDepartamentoAndReturn204() throws Exception {
        Departamento saved = saveDepartamento("TI");

        mockMvc.perform(delete("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth()))
                .andExpect(status().isNoContent());

        // Verify soft-deleted: 404 on subsequent GET
        mockMvc.perform(patch("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"TI Nuevo"}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenDeletingDepartamentoWithEmployees() throws Exception {
        Departamento saved = saveDepartamento("TI");

        createEmpleado("EMP-1", "ana@example.com", "pass", EstadoAcceso.ACTIVO, saved.getId());

        mockMvc.perform(delete("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void shouldReturn404WhenDeletingInactiveDepartamento() throws Exception {
        Departamento inactive = new Departamento();
        inactive.setNombre("Inactivo");
        inactive.setEstado(EstadoAcceso.INACTIVO);
        Departamento saved = departamentoRepository.save(inactive);

        mockMvc.perform(delete("/api/v1/departamentos/" + saved.getId())
                .with(bootstrapAuth()))
                .andExpect(status().isNotFound());
    }
}
