package com.dsw02.empleados.dto;

import com.dsw02.empleados.entity.EstadoAcceso;

public class DepartamentoEmbeddedResponse {

    private Long id;
    private String nombre;
    private EstadoAcceso estado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public EstadoAcceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoAcceso estado) {
        this.estado = estado;
    }
}
