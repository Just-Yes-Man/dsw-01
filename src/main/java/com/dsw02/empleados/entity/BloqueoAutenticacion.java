package com.dsw02.empleados.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bloqueos_autenticacion")
public class BloqueoAutenticacion {

    @EmbeddedId
    private BloqueoAutenticacionId id;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    public BloqueoAutenticacionId getId() {
        return id;
    }

    public void setId(BloqueoAutenticacionId id) {
        this.id = id;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }
}
