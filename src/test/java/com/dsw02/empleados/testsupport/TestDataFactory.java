package com.dsw02.empleados.testsupport;

import com.dsw02.empleados.departamentos.dto.DepartamentoDetailResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.dto.EmpleadoSummaryResponse;
import com.dsw02.empleados.dto.DepartamentoEmbeddedResponse;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.auth.SessionResponse;
import com.dsw02.empleados.entity.EstadoAcceso;
import java.time.OffsetDateTime;
import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static EmpleadoResponse empleadoResponse(
            String clave,
            String nombre,
            String direccion,
            String telefono,
            String email,
            Long departamentoId,
            String departamentoNombre,
            EstadoAcceso estado) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave(clave);
        response.setNombre(nombre);
        response.setDireccion(direccion);
        response.setTelefono(telefono);
        response.setEmail(email);
        response.setDepartamento(departamentoEmbedded(departamentoId, departamentoNombre, estado));
        response.setEstadoAcceso(estado);
        return response;
    }

    public static EmpleadoPageResponse empleadoPageResponse(int page, int size, long totalElements, List<EmpleadoResponse> items) {
        EmpleadoPageResponse response = new EmpleadoPageResponse();
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setItems(items);
        return response;
    }

    public static DepartamentoEmbeddedResponse departamentoEmbedded(Long id, String nombre, EstadoAcceso estado) {
        DepartamentoEmbeddedResponse embedded = new DepartamentoEmbeddedResponse();
        embedded.setId(id);
        embedded.setNombre(nombre);
        embedded.setEstado(estado);
        return embedded;
    }

    public static DepartamentoResponse departamentoResponse(Long id, String nombre, EstadoAcceso estado) {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId(id);
        response.setNombre(nombre);
        response.setEstado(estado);
        return response;
    }

    public static DepartamentoPageResponse departamentoPageResponse(int page, int size, long totalElements, List<DepartamentoResponse> items) {
        DepartamentoPageResponse response = new DepartamentoPageResponse();
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setItems(items);
        return response;
    }

    public static EmpleadoSummaryResponse empleadoSummary(
            String clave,
            String nombre,
            String email,
            EstadoAcceso estadoAcceso) {
        EmpleadoSummaryResponse response = new EmpleadoSummaryResponse();
        response.setClave(clave);
        response.setNombre(nombre);
        response.setEmail(email);
        response.setEstadoAcceso(estadoAcceso);
        return response;
    }

    public static DepartamentoDetailResponse departamentoDetailResponse(
            Long id,
            String nombre,
            EstadoAcceso estado,
            List<EmpleadoSummaryResponse> empleados) {
        DepartamentoDetailResponse response = new DepartamentoDetailResponse();
        response.setId(id);
        response.setNombre(nombre);
        response.setEstado(estado);
        response.setEmpleados(empleados);
        return response;
    }

    public static SessionResponse authenticatedSession(String empleadoClave, String email) {
        SessionResponse response = new SessionResponse();
        response.setAuthenticated(true);
        response.setEmpleadoClave(empleadoClave);
        response.setEmail(email);
        response.setExpiresAt(OffsetDateTime.now().plusHours(8));
        return response;
    }
}
