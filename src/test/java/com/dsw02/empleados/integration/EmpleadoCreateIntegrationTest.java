package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.repository.BloqueoAutenticacionRepository;
import com.dsw02.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.Assertions;
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
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

    @BeforeEach
    void setUp() {
        bloqueoAutenticacionRepository.deleteAll();
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();
    }

    @Test
    void shouldCreateEmpleado() throws Exception {
        Departamento departamento = createDepartamento("General", EstadoAcceso.ACTIVO);

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
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
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana.no-dep@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":999999}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    private Departamento createDepartamento(String nombre, EstadoAcceso estado) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setEstado(estado);
        return departamentoRepository.save(departamento);
    }
}
