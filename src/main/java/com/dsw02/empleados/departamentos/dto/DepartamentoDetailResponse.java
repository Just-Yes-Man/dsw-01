package com.dsw02.empleados.departamentos.dto;

import java.util.List;

public class DepartamentoDetailResponse extends DepartamentoResponse {

    private List<EmpleadoSummaryResponse> empleados;

    public List<EmpleadoSummaryResponse> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<EmpleadoSummaryResponse> empleados) {
        this.empleados = empleados;
    }
}
