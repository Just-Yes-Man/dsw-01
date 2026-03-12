package com.dsw02.empleados.service;

import org.springframework.security.core.AuthenticationException;

public class AccountTemporarilyLockedException extends AuthenticationException {

    public AccountTemporarilyLockedException(String message) {
        super(message);
    }
}
