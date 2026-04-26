package com.dsw02.empleados.departamentos.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

class DepartamentoReadIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldReturnEmptyListWhenNoDepartamentos() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos")
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void shouldReturnFirstPageOf10From15Records() throws Exception {
        for (int i = 1; i <= 15; i++) {
            Departamento dep = new Departamento();
            dep.setNombre("Departamento " + i);
            dep.setEstado(EstadoAcceso.ACTIVO);
            departamentoRepository.save(dep);
        }

        mockMvc.perform(get("/api/v1/departamentos?page=0")
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.items.length()").value(10));

        mockMvc.perform(get("/api/v1/departamentos?page=1")
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(5));
    }

    @Test
    void shouldReturnDepartamentoById() throws Exception {
        Departamento dep = new Departamento();
        dep.setNombre("Recursos Humanos");
        dep.setEstado(EstadoAcceso.ACTIVO);
        Departamento saved = departamentoRepository.save(dep);

        mockMvc.perform(get("/api/v1/departamentos/" + saved.getId())
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.nombre").value("Recursos Humanos"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.empleados").isArray());
    }

    @Test
    void shouldEmbedAssociatedEmpleadosInDepartamentoDetail() throws Exception {
        Departamento dep = new Departamento();
        dep.setNombre("Operaciones");
        dep.setEstado(EstadoAcceso.ACTIVO);
        Departamento saved = departamentoRepository.save(dep);

        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId("EMP-", 1L));
        empleado.setClave("EMP-1");
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("555-1234");
        empleado.setEmail("ana.dep.read@example.com");
        empleado.setPassword("secret");
        empleado.setEstadoAcceso(EstadoAcceso.ACTIVO);
        empleado.setDepartamentoId(saved.getId());
        empleadoRepository.save(empleado);

        mockMvc.perform(get("/api/v1/departamentos/" + saved.getId())
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empleados.length()").value(1))
                .andExpect(jsonPath("$.empleados[0].clave").value("EMP-1"))
                .andExpect(jsonPath("$.empleados[0].nombre").value("Ana"));
    }

    @Test
    void shouldLimitEmbeddedEmpleadosTo50InDepartamentoDetail() throws Exception {
        Departamento dep = new Departamento();
        dep.setNombre("Tecnologia");
        dep.setEstado(EstadoAcceso.ACTIVO);
        Departamento saved = departamentoRepository.save(dep);

        for (int i = 1; i <= 60; i++) {
            Empleado empleado = new Empleado();
            empleado.setId(new EmpleadoId("EMP-", (long) i));
            empleado.setClave("EMP-" + i);
            empleado.setNombre("Empleado " + i);
            empleado.setDireccion("Calle " + i);
            empleado.setTelefono("555-" + String.format("%04d", i));
            empleado.setEmail("dep-limit-" + i + "@example.com");
            empleado.setPassword("secret");
            empleado.setEstadoAcceso(EstadoAcceso.ACTIVO);
            empleado.setDepartamentoId(saved.getId());
            empleadoRepository.save(empleado);
        }

        mockMvc.perform(get("/api/v1/departamentos/" + saved.getId())
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empleados.length()").value(50));
    }

    @Test
    void shouldReturn404ForNonExistentId() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos/99999")
            .with(bootstrapAuth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void shouldNotReturnInactiveDepartamentos() throws Exception {
        Departamento active = new Departamento();
        active.setNombre("Activo");
        active.setEstado(EstadoAcceso.ACTIVO);
        departamentoRepository.save(active);

        Departamento inactive = new Departamento();
        inactive.setNombre("Inactivo");
        inactive.setEstado(EstadoAcceso.INACTIVO);
        departamentoRepository.save(inactive);

        mockMvc.perform(get("/api/v1/departamentos")
            .with(bootstrapAuth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].nombre").value("Activo"));
    }

    @Test
    void shouldReturn404ForInactiveDepartamentoById() throws Exception {
        Departamento inactive = new Departamento();
        inactive.setNombre("Inactivo");
        inactive.setEstado(EstadoAcceso.INACTIVO);
        Departamento saved = departamentoRepository.save(inactive);

        mockMvc.perform(get("/api/v1/departamentos/" + saved.getId())
            .with(bootstrapAuth()))
                .andExpect(status().isNotFound());
    }
}
