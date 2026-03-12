package com.dsw02.empleados.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BloqueoAutenticacionId implements Serializable {

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    public BloqueoAutenticacionId() {
    }

    public BloqueoAutenticacionId(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BloqueoAutenticacionId that)) {
            return false;
        }
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
