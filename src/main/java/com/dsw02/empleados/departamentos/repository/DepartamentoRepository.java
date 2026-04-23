package com.dsw02.empleados.departamentos.repository;

import com.dsw02.empleados.departamentos.entity.Departamento;
import com.dsw02.empleados.entity.EstadoAcceso;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    Optional<Departamento> findByIdAndEstado(Long id, EstadoAcceso estado);

    boolean existsByNombreAndEstado(String nombre, EstadoAcceso estado);

    boolean existsByNombreAndEstadoAndIdNot(String nombre, EstadoAcceso estado, Long id);

    Page<Departamento> findAllByEstado(EstadoAcceso estado, Pageable pageable);
}
