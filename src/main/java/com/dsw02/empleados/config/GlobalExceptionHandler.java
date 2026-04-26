package com.dsw02.empleados.config;

import com.dsw02.empleados.departamentos.exception.DepartamentoConflictException;
import com.dsw02.empleados.dto.ErrorResponse;
import com.dsw02.empleados.service.ResourceNotFoundException;
import java.util.stream.Collectors;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    private ResponseEntity<ErrorResponse> warnAndRespond(HttpStatus status, String code, String message, String event) {
        LOGGER.warn("event={} message=\"{}\"", event, message);
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message));
    }

    private ResponseEntity<ErrorResponse> warnAndRespond(HttpStatus status, String code, String message, String event,
                                                         boolean includeMessage) {
        if (includeMessage) {
            return warnAndRespond(status, code, message, event);
        }
        LOGGER.warn("event={}", event);
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return warnAndRespond(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), "resource_not_found");
    }

    @ExceptionHandler(DepartamentoConflictException.class)
    public ResponseEntity<ErrorResponse> handleDepartamentoConflict(DepartamentoConflictException ex) {
        return warnAndRespond(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), "departamento_conflict");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return warnAndRespond(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, message, "request_validation_failed");
    }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
            .map(jakarta.validation.ConstraintViolation::getMessage)
            .collect(Collectors.joining("; "));

        return warnAndRespond(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, message, "query_validation_failed");
        }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedBody(HttpMessageNotReadableException ex) {
        return warnAndRespond(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST",
            "Cuerpo de solicitud inválido",
            "malformed_body",
            false);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Parámetro inválido: " + ex.getName();
        return warnAndRespond(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, message, "query_type_mismatch");
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden() {
        return warnAndRespond(HttpStatus.FORBIDDEN, "FORBIDDEN", "Acceso denegado", "auth_forbidden", false);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(AuthenticationCredentialsNotFoundException ex) {
        return warnAndRespond(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Credenciales inválidas", "auth_unauthorized");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(LockedException ex) {
        return warnAndRespond(HttpStatus.LOCKED, "LOCKED", "Cuenta bloqueada temporalmente", "auth_locked");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return warnAndRespond(HttpStatus.BAD_REQUEST, VALIDATION_ERROR, ex.getMessage(), "bad_request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        LOGGER.error("event=internal_error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
    }
}
