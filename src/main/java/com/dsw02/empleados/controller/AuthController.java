package com.dsw02.empleados.controller;

import com.dsw02.empleados.dto.ErrorResponse;
import com.dsw02.empleados.dto.auth.LoginRequest;
import com.dsw02.empleados.dto.auth.SessionResponse;
import com.dsw02.empleados.service.AccountTemporarilyLockedException;
import com.dsw02.empleados.service.AuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthSessionService authSessionService;

    public AuthController(AuthSessionService authSessionService) {
        this.authSessionService = authSessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse) {
        try {
            HttpSession session = httpServletRequest.getSession(true);
            SessionResponse sessionResponse = authSessionService.login(request, session);
            LOGGER.info("event=login_success principal={} authType=session-cookie", request.getEmail());
            return ResponseEntity.ok(sessionResponse);
        } catch (AccountTemporarilyLockedException ex) {
            LOGGER.warn("event=login_failed principal={} reason=LOCKED", request.getEmail());
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new ErrorResponse("LOCKED", "Cuenta bloqueada temporalmente"));
        } catch (BadCredentialsException | AuthenticationCredentialsNotFoundException ex) {
            LOGGER.warn("event=login_failed principal={} reason=UNAUTHORIZED", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Credenciales inválidas"));
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.warn("event=session_check outcome=UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Sesión no válida"));
        }

        try {
            LOGGER.info("event=session_check outcome=SUCCESS");
            return ResponseEntity.ok(authSessionService.getSession(session));
        } catch (AuthenticationCredentialsNotFoundException ex) {
            LOGGER.warn("event=session_check outcome=UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Sesión no válida"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            authSessionService.logout(session);
            LOGGER.info("event=logout_success");
        } else {
            LOGGER.info("event=logout_success session=none");
        }

        ResponseCookie.ResponseCookieBuilder clearCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .sameSite("Lax");

        if (request.isSecure() || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"))) {
            clearCookie.secure(true);
        }

        response.addHeader("Set-Cookie", clearCookie.build().toString());
        return ResponseEntity.noContent().build();
    }
}
