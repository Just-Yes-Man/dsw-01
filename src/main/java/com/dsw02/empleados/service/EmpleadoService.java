package com.dsw02.empleados.service;

import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.DepartamentoEmbeddedResponse;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.EmpleadoUpdateRequest;
import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.departamentos.repository.DepartamentoRepository;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.repository.EmpleadoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoService.class);
    private static final String PREFIJO = "EMP-";
    private static final String EMPLEADO_NO_ENCONTRADO = "Empleado no encontrado";
    private static final String DEPARTAMENTO_NO_ENCONTRADO = "Departamento no encontrado";
    private static final int PAGE_SIZE = 10;

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoService(EmpleadoRepository empleadoRepository,
                           DepartamentoRepository departamentoRepository,
                           PasswordEncoder passwordEncoder) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmpleadoResponse create(EmpleadoCreateRequest request) {
        if (empleadoRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("email ya está registrado");
        }

        validateDepartamentoActivo(request.getDepartamentoId());

        Long nextConsecutivo = empleadoRepository.findMaxConsecutivo() + 1;
        String clave = PREFIJO + nextConsecutivo;

        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId(PREFIJO, nextConsecutivo));
        empleado.setClave(clave);
        empleado.setNombre(request.getNombre());
        empleado.setDireccion(request.getDireccion());
        empleado.setTelefono(request.getTelefono());
        empleado.setEmail(request.getEmail());
        empleado.setPassword(passwordEncoder.encode(request.getPassword()));
        empleado.setEstadoAcceso(request.getEstadoAcceso() == null ? EstadoAcceso.ACTIVO : request.getEstadoAcceso());
        empleado.setDepartamentoId(request.getDepartamentoId());

        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("event=empleado_created clave={} prefijo={} consecutivo={}", saved.getClave(), PREFIJO, nextConsecutivo);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
        public EmpleadoPageResponse findAll(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id.consecutivo"));
        Page<Empleado> pageResult = empleadoRepository.findAllByOrderByIdConsecutivoAsc(pageable);
        List<EmpleadoResponse> items = pageResult.getContent().stream()
            .map(this::toResponse)
            .toList();

        EmpleadoPageResponse response = new EmpleadoPageResponse();
        response.setPage(page);
        response.setSize(PAGE_SIZE);
        response.setTotalElements(pageResult.getTotalElements());
        response.setItems(items);

        LOGGER.info("event=empleado_query_paged");

        return response;
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findByClave(String clave) {
        Empleado empleado = empleadoRepository.findByClave(clave)
            .orElseThrow(() -> new ResourceNotFoundException(EMPLEADO_NO_ENCONTRADO));
        return toResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse update(String clave, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findByClave(clave)
            .orElseThrow(() -> new ResourceNotFoundException(EMPLEADO_NO_ENCONTRADO));

        validateDepartamentoActivo(request.getDepartamentoId());

        empleadoRepository.findByEmail(request.getEmail())
            .filter(existing -> !existing.getClave().equals(clave))
            .ifPresent(existing -> {
                throw new IllegalArgumentException("email ya está registrado");
            });

        empleado.setNombre(request.getNombre());
        empleado.setDireccion(request.getDireccion());
        empleado.setTelefono(request.getTelefono());
        empleado.setEmail(request.getEmail());
        empleado.setPassword(passwordEncoder.encode(request.getPassword()));
        empleado.setEstadoAcceso(request.getEstadoAcceso());
        empleado.setDepartamentoId(request.getDepartamentoId());

        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("event=empleado_updated");

        return toResponse(saved);
    }

    @Transactional
    public void delete(String clave) {
        Empleado empleado = empleadoRepository.findByClave(clave)
            .orElseThrow(() -> new ResourceNotFoundException(EMPLEADO_NO_ENCONTRADO));
        empleadoRepository.delete(empleado);
        LOGGER.info("event=empleado_deleted");
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave(empleado.getClave());
        response.setNombre(empleado.getNombre());
        response.setDireccion(empleado.getDireccion());
        response.setTelefono(empleado.getTelefono());
        response.setEmail(empleado.getEmail());
        response.setDepartamento(toDepartamentoEmbedded(empleado.getDepartamentoId()));
        response.setEstadoAcceso(empleado.getEstadoAcceso());
        return response;
    }

    private void validateDepartamentoActivo(Long departamentoId) {
        if (departamentoId == null) {
            throw new IllegalArgumentException("departamentoId es obligatorio");
        }
        departamentoRepository.findByIdAndEstado(departamentoId, EstadoAcceso.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException(DEPARTAMENTO_NO_ENCONTRADO));
    }

    private DepartamentoEmbeddedResponse toDepartamentoEmbedded(Long departamentoId) {
        if (departamentoId == null) {
            return null;
        }

        Departamento departamento = departamentoRepository.findByIdAndEstado(departamentoId, EstadoAcceso.ACTIVO)
            .orElseThrow(() -> new ResourceNotFoundException(DEPARTAMENTO_NO_ENCONTRADO));

        DepartamentoEmbeddedResponse embedded = new DepartamentoEmbeddedResponse();
        embedded.setId(departamento.getId());
        embedded.setNombre(departamento.getNombre());
        embedded.setEstado(departamento.getEstado());
        return embedded;
    }
}
