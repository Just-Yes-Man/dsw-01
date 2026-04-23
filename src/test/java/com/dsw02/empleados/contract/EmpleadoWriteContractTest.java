package com.dsw02.empleados.contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.config.GlobalExceptionHandler;
import com.dsw02.empleados.config.SecurityConfig;
import com.dsw02.empleados.config.SecurityUsersConfig;
import com.dsw02.empleados.controller.EmpleadoController;
import com.dsw02.empleados.dto.DepartamentoEmbeddedResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.EmpleadoUpdateRequest;
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
class EmpleadoWriteContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

        @MockBean
        private EmpleadoUserDetailsService empleadoUserDetailsService;

        @MockBean
        private AuthLockoutService authLockoutService;

    @Test
    void shouldUpdateAndDelete() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("EMP-1");
        response.setNombre("Ana Maria");
        response.setDireccion("Calle 2");
        response.setTelefono("555-5678");
        response.setEmail("ana@example.com");
        DepartamentoEmbeddedResponse departamento = new DepartamentoEmbeddedResponse();
        departamento.setId(20L);
        departamento.setNombre("Finanzas");
        departamento.setEstado(com.dsw02.empleados.entity.EstadoAcceso.ACTIVO);
        response.setDepartamento(departamento);
        response.setEstadoAcceso(com.dsw02.empleados.entity.EstadoAcceso.ACTIVO);

        when(empleadoService.update(any(String.class), any(EmpleadoUpdateRequest.class))).thenReturn(response);
        doNothing().when(empleadoService).delete("EMP-1");

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":20}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("EMP-1"))
                .andExpect(jsonPath("$.departamento.id").value(20));

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn423WhenAccountIsTemporarilyLockedForWriteEndpoint() throws Exception {
        when(authLockoutService.isBlocked("locked@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(httpBasic("locked@example.com", "any")))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.code").value("LOCKED"));
    }

    @Test
    void shouldRejectInvalidEmailFormatOnUpdate() throws Exception {
        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"bad-email","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldRejectDuplicateEmailOnUpdate() throws Exception {
        when(empleadoService.update(any(String.class), any(EmpleadoUpdateRequest.class)))
                .thenThrow(new IllegalArgumentException("email ya está registrado"));

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("bootstrap_admin", "bootstrap123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"duplicado@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

        @Test
        void shouldReturn404WhenDepartamentoNotFoundOnUpdate() throws Exception {
                when(empleadoService.update(any(String.class), any(EmpleadoUpdateRequest.class)))
                                .thenThrow(new ResourceNotFoundException("Departamento no encontrado"));

                mockMvc.perform(put("/api/v1/empleados/EMP-1")
                                                .with(httpBasic("bootstrap_admin", "bootstrap123"))
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678","email":"ana@example.com","password":"ana456","estadoAcceso":"ACTIVO","departamentoId":999}
                                                                """))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
        }
}
