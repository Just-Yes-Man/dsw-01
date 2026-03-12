package com.dsw02.empleados.service;

import com.dsw02.empleados.entity.Empleado;
import com.dsw02.empleados.entity.EstadoAcceso;
import com.dsw02.empleados.repository.EmpleadoRepository;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoUserDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado"));

        boolean enabled = empleado.getEstadoAcceso() == EstadoAcceso.ACTIVO;
        return User.withUsername(empleado.getEmail())
                .password(empleado.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                .disabled(!enabled)
                .build();
    }
}
