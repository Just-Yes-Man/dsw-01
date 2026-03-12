package com.dsw02.empleados.service;

import com.dsw02.empleados.entity.BloqueoAutenticacion;
import com.dsw02.empleados.entity.BloqueoAutenticacionId;
import com.dsw02.empleados.repository.BloqueoAutenticacionRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthLockoutService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final BloqueoAutenticacionRepository bloqueoAutenticacionRepository;
    private final Clock clock;

    public AuthLockoutService(BloqueoAutenticacionRepository bloqueoAutenticacionRepository, Clock clock) {
        this.bloqueoAutenticacionRepository = bloqueoAutenticacionRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(String email) {
        return bloqueoAutenticacionRepository.findByIdEmail(email)
                .map(this::isCurrentlyBlocked)
                .orElse(false);
    }

    @Transactional
    public boolean registerFailure(String email) {
        BloqueoAutenticacion bloqueo = bloqueoAutenticacionRepository.findByIdEmail(email)
                .orElseGet(() -> {
                    BloqueoAutenticacion created = new BloqueoAutenticacion();
                    created.setId(new BloqueoAutenticacionId(email));
                    created.setFailedAttempts(0);
                    return created;
                });

        LocalDateTime now = LocalDateTime.now(clock);
        if (isCurrentlyBlocked(bloqueo)) {
            return true;
        }

        int failedAttempts = bloqueo.getFailedAttempts() + 1;
        bloqueo.setFailedAttempts(failedAttempts);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            bloqueo.setBlockedUntil(now.plusMinutes(LOCK_MINUTES));
        }

        bloqueoAutenticacionRepository.save(bloqueo);
        return isCurrentlyBlocked(bloqueo);
    }

    @Transactional
    public void registerSuccess(String email) {
        bloqueoAutenticacionRepository.findByIdEmail(email).ifPresent(bloqueo -> {
            bloqueo.setFailedAttempts(0);
            bloqueo.setBlockedUntil(null);
            bloqueoAutenticacionRepository.save(bloqueo);
        });
    }

    private boolean isCurrentlyBlocked(BloqueoAutenticacion bloqueo) {
        if (bloqueo.getBlockedUntil() == null) {
            return false;
        }
        return bloqueo.getBlockedUntil().isAfter(LocalDateTime.now(clock));
    }
}
