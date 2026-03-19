package com.dsw02.empleados.dto.auth;

import java.time.OffsetDateTime;

public class SessionResponse {

    private boolean authenticated;
    private String empleadoClave;
    private String email;
    private OffsetDateTime expiresAt;
    private Integer idleTimeoutMinutes;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getEmpleadoClave() {
        return empleadoClave;
    }

    public void setEmpleadoClave(String empleadoClave) {
        this.empleadoClave = empleadoClave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getIdleTimeoutMinutes() {
        return idleTimeoutMinutes;
    }

    public void setIdleTimeoutMinutes(Integer idleTimeoutMinutes) {
        this.idleTimeoutMinutes = idleTimeoutMinutes;
    }
}
