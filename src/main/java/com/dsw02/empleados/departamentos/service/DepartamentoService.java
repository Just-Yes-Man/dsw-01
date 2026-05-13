package com.dsw02.empleados.departamentos.service;

import com.dsw02.empleados.departamentos.dto.DepartamentoCreateRequest;
import com.dsw02.empleados.departamentos.dto.DepartamentoDetailResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoPageResponse;
import com.dsw02.empleados.departamentos.dto.EmpleadoSummaryResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoResponse;
import com.dsw02.empleados.departamentos.dto.DepartamentoUpdateRequest;
import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.exception.DepartamentoConflictException;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.repository.EmpleadoRepository;
import com.dsw02.empleados.service.ResourceNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartamentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartamentoService.class);
    private static final int PAGE_SIZE = 10;
    private static final String DEPARTAMENTO_NO_ENCONTRADO = "Departamento no encontrado";
    private static final String DEPARTAMENTO_NOMBRE_CONFLICTO = "Departamento con nombre ya existe";

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public DepartamentoResponse create(DepartamentoCreateRequest request) {
        if (departamentoRepository.existsByNombreAndEstado(request.getNombre(), EstadoAcceso.ACTIVO)) {
            LOGGER.warn("event=departamento_create_conflict");
            throw new DepartamentoConflictException(DEPARTAMENTO_NOMBRE_CONFLICTO);
        }

        Departamento departamento = new Departamento();
        departamento.setNombre(request.getNombre());
        departamento.setEstado(EstadoAcceso.ACTIVO);

        Departamento saved = departamentoRepository.save(departamento);
        LOGGER.info("event=departamento_created");
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DepartamentoPageResponse findAll(int page) {
        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
        Page<Departamento> pageResult = departamentoRepository.findAllByEstado(EstadoAcceso.ACTIVO, pageable);
        List<DepartamentoResponse> items = pageResult.getContent().stream()
                .map(this::toResponse)
                .toList();

        DepartamentoPageResponse response = new DepartamentoPageResponse();
        response.setPage(page);
        response.setSize(PAGE_SIZE);
        response.setTotalElements(pageResult.getTotalElements());
        response.setItems(items);

        LOGGER.info("event=departamento_query_paged");
        return response;
    }

    @Transactional(readOnly = true)
    public DepartamentoDetailResponse findById(Long id) {
        Departamento departamento = departamentoRepository.findByIdAndEstado(id, EstadoAcceso.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException(DEPARTAMENTO_NO_ENCONTRADO));

        List<EmpleadoSummaryResponse> empleados = empleadoRepository
                .findTop50ByDepartamentoIdOrderByIdConsecutivoAsc(id)
                .stream()
                .map(this::toEmpleadoSummary)
                .toList();

        LOGGER.info("event=departamento_read");
        return toDetailResponse(departamento, empleados);
    }

    @Transactional
    public DepartamentoResponse update(Long id, DepartamentoUpdateRequest request) {
        Departamento departamento = departamentoRepository.findByIdAndEstado(id, EstadoAcceso.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException(DEPARTAMENTO_NO_ENCONTRADO));

        if (departamentoRepository.existsByNombreAndEstadoAndIdNot(
                request.getNombre(), EstadoAcceso.ACTIVO, id)) {
            LOGGER.warn("event=departamento_update_conflict");
            throw new DepartamentoConflictException(DEPARTAMENTO_NOMBRE_CONFLICTO);
        }

        departamento.setNombre(request.getNombre());
        Departamento saved = departamentoRepository.save(departamento);
        LOGGER.info("event=departamento_updated");
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Departamento departamento = departamentoRepository.findByIdAndEstado(id, EstadoAcceso.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException(DEPARTAMENTO_NO_ENCONTRADO));

        if (empleadoRepository.existsByDepartamentoId(id)) {
            LOGGER.warn("event=departamento_delete_rejected reason=has_employees");
            throw new DepartamentoConflictException(
                    "No se puede eliminar el departamento porque tiene empleados asociados");
        }

        departamento.setEstado(EstadoAcceso.INACTIVO);
        departamentoRepository.save(departamento);
        LOGGER.info("event=departamento_deleted");
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId(departamento.getId());
        response.setNombre(departamento.getNombre());
        response.setEstado(departamento.getEstado());
        response.setCreadoEn(departamento.getCreadoEn());
        response.setActualizadoEn(departamento.getActualizadoEn());
        return response;
    }

    private DepartamentoDetailResponse toDetailResponse(Departamento departamento,
                                                        List<EmpleadoSummaryResponse> empleados) {
        DepartamentoDetailResponse response = new DepartamentoDetailResponse();
        response.setId(departamento.getId());
        response.setNombre(departamento.getNombre());
        response.setEstado(departamento.getEstado());
        response.setCreadoEn(departamento.getCreadoEn());
        response.setActualizadoEn(departamento.getActualizadoEn());
        response.setEmpleados(empleados);
        return response;
    }

    private EmpleadoSummaryResponse toEmpleadoSummary(Empleado empleado) {
        EmpleadoSummaryResponse response = new EmpleadoSummaryResponse();
        response.setClave(empleado.getClave());
        response.setNombre(empleado.getNombre());
        response.setEmail(empleado.getEmail());
        response.setEstadoAcceso(empleado.getEstadoAcceso());
        return response;
    }
}
