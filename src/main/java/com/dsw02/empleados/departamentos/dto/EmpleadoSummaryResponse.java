package com.dsw02.empleados.departamentos.dto;

import com.dsw02.empleados.entity.EstadoAcceso;

public class EmpleadoSummaryResponse {

    private String clave;
    private String nombre;
    private String email;
    private EstadoAcceso estadoAcceso;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EstadoAcceso getEstadoAcceso() {
        return estadoAcceso;
    }

    public void setEstadoAcceso(EstadoAcceso estadoAcceso) {
        this.estadoAcceso = estadoAcceso;
    }
}
