package com.dsw02.empleados.service;

import com.dsw02.empleados.dto.EmpleadoCreateRequest;
import com.dsw02.empleados.dto.EmpleadoPageResponse;
import com.dsw02.empleados.dto.EmpleadoResponse;
import com.dsw02.empleados.dto.EmpleadoUpdateRequest;
import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import com.dsw02.empleados.repository.EmpleadoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpleadoService.class);
    private static final String PREFIJO = "EMP-";
    private static final int PAGE_SIZE = 10;

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public EmpleadoResponse create(EmpleadoCreateRequest request) {
        Long nextConsecutivo = empleadoRepository.findMaxConsecutivo() + 1;
        String clave = PREFIJO + nextConsecutivo;

        Empleado empleado = new Empleado();
        empleado.setId(new EmpleadoId(PREFIJO, nextConsecutivo));
        empleado.setClave(clave);
        empleado.setNombre(request.getNombre());
        empleado.setDireccion(request.getDireccion());
        empleado.setTelefono(request.getTelefono());

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

        LOGGER.info(
            "event=empleado_query_paged page={} size={} totalElements={} itemsCount={}",
            response.getPage(),
            response.getSize(),
            response.getTotalElements(),
            response.getItems().size()
        );

        return response;
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse findByClave(String clave) {
        Empleado empleado = empleadoRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));
        return toResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse update(String clave, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        empleado.setNombre(request.getNombre());
        empleado.setDireccion(request.getDireccion());
        empleado.setTelefono(request.getTelefono());

        Empleado saved = empleadoRepository.save(empleado);
        LOGGER.info("event=empleado_updated clave={}", saved.getClave());

        return toResponse(saved);
    }

    @Transactional
    public void delete(String clave) {
        Empleado empleado = empleadoRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));
        empleadoRepository.delete(empleado);
        LOGGER.info("event=empleado_deleted clave={}", clave);
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave(empleado.getClave());
        response.setNombre(empleado.getNombre());
        response.setDireccion(empleado.getDireccion());
        response.setTelefono(empleado.getTelefono());
        return response;
    }
}
