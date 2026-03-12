package com.dsw02.empleados.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.entity.EstadoAcceso;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmpleadoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldRejectNombreLongerThan100() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest();
        request.setNombre("A".repeat(101));
        request.setDireccion("Calle 1");
        request.setTelefono("555-1234");
        request.setEmail("ana@example.com");
        request.setPassword("ana123");
        request.setEstadoAcceso(EstadoAcceso.ACTIVO);

        Set<ConstraintViolation<EmpleadoCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("máximo 100")));
    }

    @Test
    void shouldRejectClaveProvidedByClient() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest();
        request.setClave("EMP-99");
        request.setNombre("Ana");
        request.setDireccion("Calle 1");
        request.setTelefono("555-1234");
        request.setEmail("ana@example.com");
        request.setPassword("ana123");
        request.setEstadoAcceso(EstadoAcceso.ACTIVO);

        Set<ConstraintViolation<EmpleadoCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("clave")));
    }

    @Test
    void shouldAcceptValidRequest() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle 1");
        request.setTelefono("555-1234");
        request.setEmail("ana@example.com");
        request.setPassword("ana123");
        request.setEstadoAcceso(EstadoAcceso.ACTIVO);

        Set<ConstraintViolation<EmpleadoCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.iterator().hasNext());
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        EmpleadoCreateRequest request = new EmpleadoCreateRequest();
        request.setNombre("Ana");
        request.setDireccion("Calle 1");
        request.setTelefono("555-1234");
        request.setEmail("not-an-email");
        request.setPassword("ana123");
        request.setEstadoAcceso(EstadoAcceso.ACTIVO);

        Set<ConstraintViolation<EmpleadoCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldBuildEmpleadoPageResponseWithMetadata() {
        EmpleadoResponse item = new EmpleadoResponse();
        item.setClave("EMP-1");
        item.setNombre("Ana");
        item.setDireccion("Calle 1");
        item.setTelefono("555-1234");

        EmpleadoPageResponse response = new EmpleadoPageResponse();
        response.setPage(0);
        response.setSize(10);
        response.setTotalElements(1);
        response.setItems(List.of(item));

        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getItems().size());
        assertEquals("EMP-1", response.getItems().get(0).getClave());
    }
}
