package com.dsw02.empleados.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private BloqueoAutenticacionRepository bloqueoAutenticacionRepository;

    @BeforeEach
    void setUp() {
        bloqueoAutenticacionRepository.deleteAll();
        empleadoRepository.deleteAll();
        empleadoRepository.save(buildEmpleado("EMP-1", "ana@example.com", "ana123", EstadoAcceso.ACTIVO));
    }

    @Test
    void shouldLoginSuccessfullyAndSetCookie() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"ana123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("JSESSIONID=")))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.empleadoClave").value("EMP-1"));
    }

    @Test
    void shouldReturn401OnInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    private Empleado buildEmpleado(String clave, String email, String password, EstadoAcceso estado) {
        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId("EMP", 1L));
        empleado.setClave(clave);
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle 1");
        empleado.setTelefono("555-1234");
        empleado.setEmail(email);
        empleado.setPassword(password);
        empleado.setEstadoAcceso(estado);
        empleado.setDepartamentoId(null);
        return empleado;
    }
}
