package com.dsw02.empleados.departamentos.contract;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsw02.empleados.departamentos.dto.DepartamentoDetailResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.EmpleadoSummaryResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import java.util.ArrayList;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import com.dsw02.empleados.EmpleadosApplication;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.service.ResourceNotFoundException;
import com.dsw02.empleados.testsupport.TestDataFactory;
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
        DepartamentoResponse item = TestDataFactory.departamentoResponse(1L, "Ventas", EstadoAcceso.ACTIVO);
        DepartamentoPageResponse page = TestDataFactory.departamentoPageResponse(0, 10, 1L, List.of(item));

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
        DepartamentoPageResponse page = TestDataFactory.departamentoPageResponse(0, 10, 0L, Collections.emptyList());

        when(departamentoService.findAll(0)).thenReturn(page);

        mockMvc.perform(get("/api/v1/departamentos")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void shouldReturnSingleDepartamentoById() throws Exception {
        DepartamentoDetailResponse item = TestDataFactory.departamentoDetailResponse(
            5L,
            "Finanzas",
            EstadoAcceso.ACTIVO,
            new ArrayList<>());

        when(departamentoService.findById(5L)).thenReturn(item);

        mockMvc.perform(get("/api/v1/departamentos/5")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Finanzas"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.empleados").isArray());
    }

    @Test
    void shouldReturnDepartamentoByIdWithEmbeddedEmpleados() throws Exception {
        EmpleadoSummaryResponse empleado = TestDataFactory.empleadoSummary(
            "EMP-1",
            "Ana",
            "ana@example.com",
            EstadoAcceso.ACTIVO);

        DepartamentoDetailResponse item = TestDataFactory.departamentoDetailResponse(
            10L,
            "Operaciones",
            EstadoAcceso.ACTIVO,
            List.of(empleado));

        when(departamentoService.findById(10L)).thenReturn(item);

        mockMvc.perform(get("/api/v1/departamentos/10")
                .with(httpBasic("bootstrap_admin", "bootstrap123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.empleados.length()").value(1))
                .andExpect(jsonPath("$.empleados[0].clave").value("EMP-1"))
                .andExpect(jsonPath("$.empleados[0].nombre").value("Ana"))
                .andExpect(jsonPath("$.empleados[0].email").value("ana@example.com"))
                .andExpect(jsonPath("$.empleados[0].estadoAcceso").value("ACTIVO"));
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
