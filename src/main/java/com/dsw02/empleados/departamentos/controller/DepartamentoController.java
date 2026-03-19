package com.dsw02.empleados.departamentos.controller;

import com.dsw02.empleados.departamentos.dto.DepartamentoCreateRequest;
import com.dsw02.empleados.departamentos.dto.DepartamentoDetailResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoUpdateRequest;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Validated
@RequestMapping("/api/v1/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> create(
            @Valid @RequestBody DepartamentoCreateRequest request) {
        DepartamentoResponse created = departamentoService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public DepartamentoPageResponse findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page) {
        return departamentoService.findAll(page);
    }

    @GetMapping("/{id}")
    public DepartamentoDetailResponse findById(@PathVariable Long id) {
        return departamentoService.findById(id);
    }

    @PatchMapping("/{id}")
    public DepartamentoResponse update(@PathVariable Long id,
                                       @Valid @RequestBody DepartamentoUpdateRequest request) {
        return departamentoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departamentoService.delete(id);
    }
}
