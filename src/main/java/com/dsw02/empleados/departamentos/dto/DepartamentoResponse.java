package com.dsw02.empleados.departamentos.dto;

import com.dsw02.empleados.entity.EstadoAcceso;
import java.time.LocalDateTime;

public class DepartamentoResponse {

    private Long id;
    private String nombre;
    private EstadoAcceso estado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public EstadoAcceso getEstado() { return estado; }
    public void setEstado(EstadoAcceso estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
