package com.dsw02.empleados.repository;

import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EmpleadoId;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmpleadoRepository extends JpaRepository<Empleado, EmpleadoId> {

    Optional<Empleado> findByClave(String clave);

    Optional<Empleado> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Empleado> findAllByOrderByIdConsecutivoAsc(Pageable pageable);

    @Query("select coalesce(max(e.id.consecutivo), 0) from Empleado e")
    Long findMaxConsecutivo();

    boolean existsByDepartamentoId(Long departamentoId);
}
