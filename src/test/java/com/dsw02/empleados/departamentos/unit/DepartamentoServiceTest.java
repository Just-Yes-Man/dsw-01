package com.dsw02.empleados.departamentos.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dsw02.empleados.departamentos.dto.DepartamentoCreateRequest;
import com.dsw02.empleados.departamentos.dto.DepartamentoDetailResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoUpdateRequest;
import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.exception.DepartamentoConflictException;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.departamentos.service.DepartamentoService;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.repository.EmpleadoRepository;
import com.dsw02.empleados.service.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private DepartamentoService departamentoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- create() ---

    @Test
    void createShouldPersistAndReturnResponseWithEstadoActivo() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("Ventas");

        when(departamentoRepository.existsByNombreAndEstado("Ventas", EstadoAcceso.ACTIVO)).thenReturn(false);

        Departamento saved = new Departamento();
        saved.setId(1L);
        saved.setNombre("Ventas");
        saved.setEstado(EstadoAcceso.ACTIVO);
        when(departamentoRepository.save(any(Departamento.class))).thenReturn(saved);

        DepartamentoResponse response = departamentoService.create(request);

        assertEquals(1L, response.getId());
        assertEquals("Ventas", response.getNombre());
        assertEquals(EstadoAcceso.ACTIVO, response.getEstado());
    }

    @Test
    void createShouldThrowConflictWhenNameAlreadyExists() {
        DepartamentoCreateRequest request = new DepartamentoCreateRequest();
        request.setNombre("Ventas");

        when(departamentoRepository.existsByNombreAndEstado("Ventas", EstadoAcceso.ACTIVO)).thenReturn(true);

        assertThrows(DepartamentoConflictException.class, () -> departamentoService.create(request));
    }

    // --- findAll() ---

    @Test
    void findAllShouldReturnPaginatedResponseWithSize10() {
        Departamento dep = new Departamento();
        dep.setId(1L);
        dep.setNombre("TI");
        dep.setEstado(EstadoAcceso.ACTIVO);

        PageImpl<Departamento> page = new PageImpl<>(List.of(dep), PageRequest.of(0, 10), 1);
        when(departamentoRepository.findAllByEstado(eq(EstadoAcceso.ACTIVO), any())).thenReturn(page);

        DepartamentoPageResponse response = departamentoService.findAll(0);

        assertEquals(10, response.getSize());
        assertEquals(1L, response.getTotalElements());
        assertEquals(1, response.getItems().size());
    }

    // --- findById() ---

    @Test
    void findByIdShouldReturnDepartamentoWhenActive() {
        Departamento dep = new Departamento();
        dep.setId(5L);
        dep.setNombre("Finanzas");
        dep.setEstado(EstadoAcceso.ACTIVO);

        when(departamentoRepository.findByIdAndEstado(5L, EstadoAcceso.ACTIVO)).thenReturn(Optional.of(dep));
        when(empleadoRepository.findTop50ByDepartamentoIdOrderByIdConsecutivoAsc(5L)).thenReturn(List.of());

        DepartamentoDetailResponse response = departamentoService.findById(5L);
        assertEquals("Finanzas", response.getNombre());
        assertNotNull(response.getEmpleados());
        assertTrue(response.getEmpleados().isEmpty());
    }

    @Test
    void findByIdShouldThrowNotFoundWhenNotActive() {
        when(departamentoRepository.findByIdAndEstado(99L, EstadoAcceso.ACTIVO)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departamentoService.findById(99L));
    }

    // --- update() ---

    @Test
    void updateShouldPersistNewNameWhenNoDuplicate() {
        Departamento existing = new Departamento();
        existing.setId(1L);
        existing.setNombre("Ventas");
        existing.setEstado(EstadoAcceso.ACTIVO);

        DepartamentoUpdateRequest request = new DepartamentoUpdateRequest();
        request.setNombre("Ventas Internacional");

        when(departamentoRepository.findByIdAndEstado(1L, EstadoAcceso.ACTIVO)).thenReturn(Optional.of(existing));
        when(departamentoRepository.existsByNombreAndEstadoAndIdNot("Ventas Internacional", EstadoAcceso.ACTIVO, 1L)).thenReturn(false);

        Departamento saved = new Departamento();
        saved.setId(1L);
        saved.setNombre("Ventas Internacional");
        saved.setEstado(EstadoAcceso.ACTIVO);
        when(departamentoRepository.save(any())).thenReturn(saved);

        DepartamentoResponse response = departamentoService.update(1L, request);
        assertEquals("Ventas Internacional", response.getNombre());
    }

    @Test
    void updateShouldThrowConflictWhenNewNameDuplicated() {
        Departamento existing = new Departamento();
        existing.setId(1L);
        existing.setNombre("Ventas");
        existing.setEstado(EstadoAcceso.ACTIVO);

        DepartamentoUpdateRequest request = new DepartamentoUpdateRequest();
        request.setNombre("Finanzas");

        when(departamentoRepository.findByIdAndEstado(1L, EstadoAcceso.ACTIVO)).thenReturn(Optional.of(existing));
        when(departamentoRepository.existsByNombreAndEstadoAndIdNot("Finanzas", EstadoAcceso.ACTIVO, 1L)).thenReturn(true);

        assertThrows(DepartamentoConflictException.class, () -> departamentoService.update(1L, request));
    }

    // --- delete() ---

    @Test
    void deleteShouldMarkAsInactivoWhenNoEmployees() {
        Departamento existing = new Departamento();
        existing.setId(1L);
        existing.setNombre("TI");
        existing.setEstado(EstadoAcceso.ACTIVO);

        when(departamentoRepository.findByIdAndEstado(1L, EstadoAcceso.ACTIVO)).thenReturn(Optional.of(existing));
        when(empleadoRepository.existsByDepartamentoId(1L)).thenReturn(false);
        when(departamentoRepository.save(any())).thenReturn(existing);

        departamentoService.delete(1L);

        assertEquals(EstadoAcceso.INACTIVO, existing.getEstado());
        verify(departamentoRepository).save(existing);
    }

    @Test
    void deleteShouldThrowConflictWhenEmployeesAssigned() {
        Departamento existing = new Departamento();
        existing.setId(1L);
        existing.setNombre("TI");
        existing.setEstado(EstadoAcceso.ACTIVO);

        when(departamentoRepository.findByIdAndEstado(1L, EstadoAcceso.ACTIVO)).thenReturn(Optional.of(existing));
        when(empleadoRepository.existsByDepartamentoId(1L)).thenReturn(true);

        assertThrows(DepartamentoConflictException.class, () -> departamentoService.delete(1L));
    }
}
