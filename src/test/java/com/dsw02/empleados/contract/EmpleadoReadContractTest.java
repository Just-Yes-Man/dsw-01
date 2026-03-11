package com.dsw02.empleados.contract;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.config.GlobalExceptionHandler;
import com.dsw02.empleados.config.SecurityConfig;
import com.dsw02.empleados.config.SecurityUsersConfig;
import com.dsw02.empleados.controller.EmpleadoController;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.service.EmpleadoService;
import com.dsw02.empleados.service.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmpleadoController.class)
@Import({SecurityConfig.class, SecurityUsersConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "security.basic.user=admin",
        "security.basic.password=admin123"
})
class EmpleadoReadContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldListAndGetByClave() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("EMP-1");
        response.setNombre("Ana");
        response.setDireccion("Calle 1");
        response.setTelefono("555-1234");

        EmpleadoPageResponse pageResponse = new EmpleadoPageResponse();
        pageResponse.setPage(0);
        pageResponse.setSize(10);
        pageResponse.setTotalElements(1);
        pageResponse.setItems(List.of(response));

        when(empleadoService.findAll(0)).thenReturn(pageResponse);
        when(empleadoService.findByClave("EMP-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/empleados").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].clave").value("EMP-1"));

        mockMvc.perform(get("/api/v1/empleados/EMP-1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clave").value("EMP-1"));
    }

    @Test
    void shouldReturn404ForMissingClave() throws Exception {
        when(empleadoService.findByClave("EMP-99")).thenThrow(new ResourceNotFoundException("Empleado no encontrado"));

        mockMvc.perform(get("/api/v1/empleados/EMP-99").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectNegativePage() throws Exception {
        mockMvc.perform(get("/api/v1/empleados?page=-1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWithoutAuthInQueryEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/empleados"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/empleados/EMP-1"))
                .andExpect(status().isUnauthorized());
    }
}
