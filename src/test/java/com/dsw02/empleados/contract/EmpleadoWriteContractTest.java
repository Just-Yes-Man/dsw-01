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
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.EmpleadoUpdateRequest;
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
class EmpleadoWriteContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldUpdateAndDelete() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("EMP-1");
        response.setNombre("Ana Maria");
        response.setDireccion("Calle 2");
        response.setTelefono("555-5678");

        when(empleadoService.update(any(String.class), any(EmpleadoUpdateRequest.class))).thenReturn(response);
        doNothing().when(empleadoService).delete("EMP-1");

        mockMvc.perform(put("/api/v1/empleados/EMP-1")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("EMP-1"));

        mockMvc.perform(delete("/api/v1/empleados/EMP-1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
    }
}
