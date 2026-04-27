package com.dsw02.empleados.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dsw02.empleados.config.GlobalExceptionHandler;
import com.dsw02.empleados.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleMalformedBody() {
        ResponseEntity<ErrorResponse> response = handler.handleMalformedBody(
                new HttpMessageNotReadableException("invalid request", Mockito.mock(HttpInputMessage.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().getCode());
        assertEquals("Cuerpo de solicitud inválido", response.getBody().getMessage());
    }

    @Test
    void shouldHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class);
        Mockito.when(ex.getName()).thenReturn("page");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("Parámetro inválido: page", response.getBody().getMessage());
    }

    @Test
    void shouldHandleForbiddenUnauthorizedAndLocked() {
        ResponseEntity<ErrorResponse> forbidden = handler.handleForbidden();
        assertEquals(HttpStatus.FORBIDDEN, forbidden.getStatusCode());
        assertEquals("FORBIDDEN", forbidden.getBody().getCode());

        ResponseEntity<ErrorResponse> unauthorized = handler.handleUnauthorized(
                new AuthenticationCredentialsNotFoundException("missing"));
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatusCode());
        assertEquals("UNAUTHORIZED", unauthorized.getBody().getCode());

        ResponseEntity<ErrorResponse> locked = handler.handleLocked(new LockedException("locked"));
        assertEquals(HttpStatus.LOCKED, locked.getStatusCode());
        assertEquals("LOCKED", locked.getBody().getCode());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_ERROR", response.getBody().getCode());
        assertEquals("Error interno del servidor", response.getBody().getMessage());
    }

    @Test
    void shouldUseVerboseWarningBranchWhenIncludeMessageIsTrue() {
        ResponseEntity<ErrorResponse> response = ReflectionTestUtils.invokeMethod(
                handler,
                "warnAndRespond",
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "detalle",
                "event_name",
                true
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals("detalle", response.getBody().getMessage());
    }
}