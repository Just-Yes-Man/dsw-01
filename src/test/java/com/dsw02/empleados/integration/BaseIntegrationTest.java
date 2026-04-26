package com.dsw02.empleados.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.repository.BloqueoAutenticacionRepository;
import com.dsw02.empleados.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected static final String BOOTSTRAP_USER = "bootstrap_admin";
    protected static final String BOOTSTRAP_PASSWORD = "bootstrap123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EmpleadoRepository empleadoRepository;

    @Autowired
    protected DepartamentoRepository departamentoRepository;

    @Autowired
    protected BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

    @org.junit.jupiter.api.BeforeEach
    void resetData() {
        bloqueoAutenticacionRepository.deleteAll();
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();
        setupTestData();
    }

    protected void setupTestData() {
        // Override when a test class needs initial state after cleanup.
    }

    protected RequestPostProcessor bootstrapAuth() {
        return httpBasic(BOOTSTRAP_USER, BOOTSTRAP_PASSWORD);
    }

    protected Departamento createDepartamento(String nombre, EstadoAcceso estado) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setEstado(estado);
        return departamentoRepository.save(departamento);
    }

    protected Empleado createEmpleado(String clave, String email, String password, EstadoAcceso estado, Long departamentoId) {
        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId("EMP", 1L));
        empleado.setClave(clave);
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("555-1234");
        empleado.setEmail(email);
        empleado.setPassword(password);
        empleado.setEstadoAcceso(estado);
        empleado.setDepartamentoId(departamentoId);
        return empleadoRepository.save(empleado);
    }
}
