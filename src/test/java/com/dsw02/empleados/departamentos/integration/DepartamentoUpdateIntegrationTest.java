package com.dsw02.empleados.departamentos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.EmpleadosApplication;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import com.dsw02.empleados.entity.EstadoAcceso;
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
class DepartamentoUpdateIntegrationTest {

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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNoContent());

        // Verify soft-deleted: 404 on subsequent GET
        mockMvc.perform(patch("/api/v1/departamentos/" + saved.getId())
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nombre":"TI Nuevo"}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenDeletingDepartamentoWithEmployees() throws Exception {
        Departamento saved = saveDepartamento("TI");

        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId("EMP-", 1L));
        empleado.setClave("EMP-1");
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("555-1234");
        empleado.setEmail("ana@example.com");
        empleado.setPassword("pass");
        empleado.setEstadoAcceso(EstadoAcceso.ACTIVO);
        empleado.setDepartamentoId(saved.getId());
        empleadoRepository.save(empleado);

        mockMvc.perform(delete("/api/v1/departamentos/" + saved.getId())
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNotFound());
    }
}
