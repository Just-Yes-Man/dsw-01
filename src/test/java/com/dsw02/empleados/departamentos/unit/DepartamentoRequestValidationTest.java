package com.dsw02.empleados.departamentos.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dsw02.empleados.departamentos.dto.DepartamentoCreateRequest;
import com.dsw02.empleados.departamentos.dto.DepartamentoUpdateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartamentoRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // --- DepartamentoCreateRequest ---

    @Test
    void createRequestShouldPassWithValidNombre() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("Ventas");

        Set<ConstraintViolation<DepartamentoCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createRequestShouldRejectEmptyNombre() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("");

        Set<ConstraintViolation<DepartamentoCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createRequestShouldRejectNullNombre() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre(null);

        Set<ConstraintViolation<DepartamentoCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createRequestShouldRejectNombreExceeding255Chars() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("A".repeat(256));

        Set<ConstraintViolation<DepartamentoCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("255")));
    }

    @Test
    void createRequestShouldAcceptNombreOf255Chars() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("A".repeat(255));

        Set<ConstraintViolation<DepartamentoCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    // --- DepartamentoUpdateRequest ---

    @Test
    void updateRequestShouldPassWithValidNombre() {
        DepartamentoUpdateRequest request = new DepartamentoUpdateRequest();
        request.setNombre("Finanzas");

        Set<ConstraintViolation<DepartamentoUpdateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void updateRequestShouldRejectEmptyNombre() {
        DepartamentoUpdateRequest request = new DepartamentoUpdateRequest();
        request.setNombre("");

        Set<ConstraintViolation<DepartamentoUpdateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void updateRequestShouldRejectNombreExceeding255Chars() {
        DepartamentoUpdateRequest request = new DepartamentoUpdateRequest();
        request.setNombre("B".repeat(256));

        Set<ConstraintViolation<DepartamentoUpdateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}
