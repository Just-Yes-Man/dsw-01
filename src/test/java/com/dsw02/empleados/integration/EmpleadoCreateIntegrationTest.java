package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.entity.EstadoAcceso;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class EmpleadoCreateIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldCreateEmpleado() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
            .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                    """.formatted(departamento.getId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value("EMP-1"))
            .andExpect(jsonPath("$.departamento.id").value(departamento.getId()));
    }

    @Test
    void shouldRejectTooLongFields() throws Exception {
        String longValue = "A".repeat(101);
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
            .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"nombre":"%s","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                    """.formatted(longValue, departamento.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectClientClaveInCreate() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
            .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"clave":"EMP-999","nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                    """.formatted(departamento.getId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateEmpleadoWithDepartamento() throws Exception {
        Departamento departamento = createDepartamento("Ventas", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana.dep@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":%d}
                                """.formatted(departamento.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("EMP-1"))
                        .andExpect(jsonPath("$.departamento.id").value(departamento.getId()));

        Long departamentoIdPersistido = empleadoRepository.findByClave("EMP-1")
                .orElseThrow()
                .getDepartamentoId();
        Assertions.assertEquals(departamento.getId(), departamentoIdPersistido);
    }

    @Test
    void shouldReturn404WhenDepartamentoNotFoundOnCreate() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .with(bootstrapAuth())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana.no-dep@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":999999}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

}
