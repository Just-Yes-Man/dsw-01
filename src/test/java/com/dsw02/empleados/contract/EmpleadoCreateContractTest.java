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
import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.service.EmpleadoService;
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
        "security.basic.user=admin",
        "security.basic.password=admin123"
})
class EmpleadoCreateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldCreateEmpleado() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("EMP-1");
        response.setNombre("Ana");
        response.setDireccion("Calle 1");
        response.setTelefono("555-1234");

        when(empleadoService.create(any(EmpleadoCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/empleados")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clave").value("EMP-1"));
    }

    @Test
    void shouldRejectWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234"}
                                """))
                .andExpect(status().isUnauthorized());
    }
}
