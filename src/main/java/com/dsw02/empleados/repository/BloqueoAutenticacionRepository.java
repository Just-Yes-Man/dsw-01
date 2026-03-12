package com.dsw02.empleados.repository;

import com.dsw02.empleados.entity.BloqueoAutenticacion;
import com.dsw02.empleados.entity.BloqueoAutenticacionId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueoAutenticacionRepository extends JpaRepository<BloqueoAutenticacion, BloqueoAutenticacionId> {

    Optional<BloqueoAutenticacion> findByIdEmail(String email);
}
