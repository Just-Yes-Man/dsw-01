package com.dsw02.empleados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public class EmpleadoUpdateRequest {

    @Null(message = "clave no debe enviarse en la solicitud")
    private String clave;

    @Null(message = "prefijo no debe enviarse en la solicitud")
    private String prefijo;

    @Null(message = "consecutivo no debe enviarse en la solicitud")
    private Long consecutivo;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre debe tener máximo 100 caracteres")
    private String nombre;

    @NotBlank(message = "direccion es obligatoria")
    @Size(max = 100, message = "direccion debe tener máximo 100 caracteres")
    private String direccion;

    @NotBlank(message = "telefono es obligatorio")
    @Size(max = 100, message = "telefono debe tener máximo 100 caracteres")
    private String telefono;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(Long consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
