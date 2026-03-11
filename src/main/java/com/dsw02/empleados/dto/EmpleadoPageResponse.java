package com.dsw02.empleados.dto;

import java.util.List;

public class EmpleadoPageResponse {

    private int page;
    private int size;
    private long totalElements;
    private List<EmpleadoResponse> items;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<EmpleadoResponse> getItems() {
        return items;
    }

    public void setItems(List<EmpleadoResponse> items) {
        this.items = items;
    }
}