package com.dsw02.empleados.departamentos.dto;

import java.util.List;

public class DepartamentoPageResponse {

    private int page;
    private int size;
    private long totalElements;
    private List<DepartamentoResponse> items;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public List<DepartamentoResponse> getItems() { return items; }
    public void setItems(List<DepartamentoResponse> items) { this.items = items; }
}
