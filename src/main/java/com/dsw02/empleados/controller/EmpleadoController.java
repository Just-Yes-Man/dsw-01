package com.dsw02.empleados.controller;

import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.EmpleadoUpdateRequest;
import com.dsw02.empleados.service.EmpleadoService;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse create(@Valid @RequestBody EmpleadoCreateRequest request) {
        return empleadoService.create(request);
    }

    @GetMapping
    public EmpleadoPageResponse findAll(@RequestParam(defaultValue = "0") @Min(0) int page) {
        return empleadoService.findAll(page);
    }

    @GetMapping("/{clave}")
    public EmpleadoResponse findByClave(@PathVariable String clave) {
        return empleadoService.findByClave(clave);
    }

    @PutMapping("/{clave}")
    public EmpleadoResponse update(@PathVariable String clave, @Valid @RequestBody EmpleadoUpdateRequest request) {
        return empleadoService.update(clave, request);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String clave) {
        empleadoService.delete(clave);
    }
}
