package com.dsw02.empleados.service;

import com.dsw02.empleados.dto.auth.LoginRequest;
import com.dsw02.empleados.dto.auth.SessionResponse;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.repository.EmpleadoRepository;
import jakarta.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthSessionService {

    private static final String SESSION_EMAIL = "auth.email";
    private static final String SESSION_CLAVE = "auth.clave";

    private final AuthenticationProvider authenticationProvider;
    private final EmpleadoRepository empleadoRepository;

    @Value("${security.bootstrap.user:}")
    private String bootstrapUser;

    @Value("${auth.session.idle-timeout-minutes:480}")
    private int idleTimeoutMinutes;

    public AuthSessionService(AuthenticationProvider authenticationProvider,
                              EmpleadoRepository empleadoRepository) {
        this.authenticationProvider = authenticationProvider;
        this.empleadoRepository = empleadoRepository;
    }

    public SessionResponse login(LoginRequest request, HttpSession session) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (isBootstrapUser(authentication.getName())) {
            session.setMaxInactiveInterval(idleTimeoutMinutes * 60);
            session.setAttribute(SESSION_EMAIL, authentication.getName());
            session.setAttribute(SESSION_CLAVE, "BOOTSTRAP");
            return buildSessionResponse("BOOTSTRAP", authentication.getName());
        }

        Empleado empleado = empleadoRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Credenciales inválidas"));

        session.setMaxInactiveInterval(idleTimeoutMinutes * 60);
        session.setAttribute(SESSION_EMAIL, empleado.getEmail());
        session.setAttribute(SESSION_CLAVE, empleado.getClave());

        return buildSessionResponse(empleado.getClave(), empleado.getEmail());
    }

    public SessionResponse getSession(HttpSession session) {
        String email = (String) session.getAttribute(SESSION_EMAIL);
        String clave = (String) session.getAttribute(SESSION_CLAVE);

        if (email == null || clave == null) {
            throw new AuthenticationCredentialsNotFoundException("Sesión no válida");
        }

        session.setMaxInactiveInterval(idleTimeoutMinutes * 60);
        return buildSessionResponse(clave, email);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    private boolean isBootstrapUser(String username) {
        return bootstrapUser != null && !bootstrapUser.isBlank() && bootstrapUser.equals(username);
    }

    private SessionResponse buildSessionResponse(String clave, String email) {
        SessionResponse response = new SessionResponse();
        response.setAuthenticated(true);
        response.setEmpleadoClave(clave);
        response.setEmail(email);
        response.setIdleTimeoutMinutes(idleTimeoutMinutes);
        response.setExpiresAt(OffsetDateTime.now().plusMinutes(idleTimeoutMinutes));
        return response;
    }
}
