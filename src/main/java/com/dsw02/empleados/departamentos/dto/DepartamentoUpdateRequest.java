package com.dsw02.empleados.departamentos.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class DepartamentoUpdateRequest {

    @NotEmpty(message = "Nombre es requerido")
    @Size(min = 1, max = 255, message = "Nombre debe tener entre 1 y 255 caracteres")
    private String nombre;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
