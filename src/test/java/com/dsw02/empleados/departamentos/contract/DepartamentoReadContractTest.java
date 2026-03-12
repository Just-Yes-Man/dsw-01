package com.dsw02.empleados.departamentos.contract;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import com.dsw02.empleados.EmpleadosApplication;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.service.ResourceNotFoundException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = EmpleadosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "security.bootstrap.user=bootstrap_admin",
    "security.bootstrap.password=bootstrap123"
})
class DepartamentoReadContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartamentoService departamentoService;

    @Test
    void shouldReturnPaginatedListWithSizeFixed10() throws Exception {
        DepartamentoResponse item = new DepartamentoResponse();
        item.setId(1L);
        item.setNombre("Ventas");
        item.setEstado(EstadoAcceso.ACTIVO);

        DepartamentoPageResponse page = new DepartamentoPageResponse();
        page.setPage(0);
        page.setSize(10);
        page.setTotalElements(1L);
        page.setItems(List.of(item));

        when(departamentoService.findAll(0)).thenReturn(page);

        mockMvc.perform(get("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.items[0].nombre").value("Ventas"))
                .andExpect(jsonPath("$.items[0].estado").value("ACTIVO"));
    }

    @Test
    void shouldReturnEmptyListWhenNoDepartamentos() throws Exception {
        DepartamentoPageResponse page = new DepartamentoPageResponse();
        page.setPage(0);
        page.setSize(10);
        page.setTotalElements(0L);
        page.setItems(Collections.emptyList());

        when(departamentoService.findAll(0)).thenReturn(page);

        mockMvc.perform(get("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void shouldReturnSingleDepartamentoById() throws Exception {
        DepartamentoResponse item = new DepartamentoResponse();
        item.setId(5L);
        item.setNombre("Finanzas");
        item.setEstado(EstadoAcceso.ACTIVO);

        when(departamentoService.findById(5L)).thenReturn(item);

        mockMvc.perform(get("/api/v1/departamentos/5")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Finanzas"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void shouldReturn404ForNonExistentId() throws Exception {
        when(departamentoService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Departamento no encontrado"));

        mockMvc.perform(get("/api/v1/departamentos/999")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticatedOnList() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenNotAuthenticatedOnFindById() throws Exception {
        mockMvc.perform(get("/api/v1/departamentos/1"))
                .andExpect(status().isUnauthorized());
    }
}
