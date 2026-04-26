package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.entity.EstadoAcceso;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class EmpleadoWriteIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldUpdateAndDeleteEmpleado() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Maria"));

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(bootstrapAuth()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(bootstrapAuth()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectClientClaveInUpdate() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clave":"EMP-99","nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"duplicado@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Luis","direccion":"Calle 2","telefono":"555-5678","email":"duplicado@example.com","password":"luis123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldUpdateEmpleadoDepartamento() throws Exception {
        Departamento departamentoA = createDepartamento("Ventas", EstadoAcceso.ACTIVO);
        Departamento departamentoB = createDepartamento("Finanzas", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana-rel@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamentoA.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana-rel@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamentoB.getId())))
                .andExpect(status().isOk())
                        .andExpect(jsonPath("$.departamento.id").value(departamentoB.getId()));

        Long departamentoIdPersistido = empleadoRepository.findByClave("EMP-1")
                .orElseThrow()
                .getDepartamentoId();
        Assertions.assertEquals(departamentoB.getId(), departamentoIdPersistido);
    }

    @Test
    void shouldReturn404WhenUpdatingWithInactiveDepartamento() throws Exception {
        Departamento departamentoActivo = createDepartamento("Ventas", EstadoAcceso.ACTIVO);
        Departamento departamentoInactivo = createDepartamento("Compras", EstadoAcceso.INACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana-inactive@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamentoActivo.getId())))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana-inactive@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamentoInactivo.getId())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

}
