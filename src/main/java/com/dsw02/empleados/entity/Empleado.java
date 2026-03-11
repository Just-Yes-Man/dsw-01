package com.dsw02.empleados.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleados")
public class Empleado {

    @EmbeddedId
    private EmpleadoId id;

    @Column(name = "clave", nullable = false, unique = true, length = 24)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 100)
    private String direccion;

    @Column(name = "telefono", nullable = false, length = 100)
    private String telefono;

    public EmpleadoId getId() {
        return id;
    }

    public void setId(EmpleadoId id) {
        this.id = id;
    }

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
