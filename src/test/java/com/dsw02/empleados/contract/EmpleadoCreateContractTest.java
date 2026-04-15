package com.dsw02.empleados.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.config.GlobalExceptionHandler;
import com.dsw02.empleados.config.SecurityConfig;
import com.dsw02.empleados.config.SecurityUsersConfig;
import com.dsw02.empleados.controller.EmpleadoController;
import com.dsw02.empleados.dto.DepartamentoEmbeddedResponse;
import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.service.AuthLockoutService;
import com.dsw02.empleados.service.EmpleadoService;
import com.dsw02.empleados.service.EmpleadoUserDetailsService;
import com.dsw02.empleados.service.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmpleadoController.class)
@Import({SecurityConfig.class, SecurityUsersConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
    "security.bootstrap.user=bootstrap_admin",
    "security.bootstrap.password=bootstrap123"
})
class EmpleadoCreateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @MockBean
    private EmpleadoUserDetailsService empleadoUserDetailsService;

    @MockBean
    private AuthLockoutService authLockoutService;

    @Test
    void shouldCreateEmpleado() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("EMP-1");
        response.setNombre("Ana");
        response.setDireccion("Calle 1");
        response.setTelefono("555-1234");
        response.setEmail("ana@example.com");
        DepartamentoEmbeddedResponse departamento = new DepartamentoEmbeddedResponse();
        departamento.setId(10L);
        departamento.setNombre("Ventas");
        departamento.setEstado(com.dsw02.empleados.entity.EstadoAcceso.ACTIVO);
        response.setDepartamento(departamento);
        response.setEstadoAcceso(com.dsw02.empleados.entity.EstadoAcceso.ACTIVO);

        when(empleadoService.create(any(EmpleadoCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("EMP-1"))
                        .andExpect(jsonPath("$.departamento.id").value(10));
    }

    @Test
    void shouldReturn404WhenDepartamentoNotFoundOnCreate() throws Exception {
        when(empleadoService.create(any(EmpleadoCreateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Departamento no encontrado"));

        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":999}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void shouldRejectWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234","email":"ana@example.com","password":"ana123","estadoAcceso":"ACTIVO","departamentoId":1}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
