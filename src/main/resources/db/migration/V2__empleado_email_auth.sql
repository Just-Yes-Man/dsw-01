ALTER TABLE empleados
    ADD COLUMN email VARCHAR(255),
    ADD COLUMN password VARCHAR(255),
    ADD COLUMN estado_acceso VARCHAR(20) NOT NULL DEFAULT 'ACTIVO';

UPDATE empleados
SET email = lower(replace(clave, 'EMP-', 'empleado')) || '@example.com',
    password = 'change_me',
    estado_acceso = 'ACTIVO'
WHERE email IS NULL OR password IS NULL;

ALTER TABLE empleados
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE empleados
    ADD CONSTRAINT uk_empleados_email UNIQUE (email),
    ADD CONSTRAINT ck_empleados_estado_acceso CHECK (estado_acceso IN ('ACTIVO', 'INACTIVO'));

CREATE TABLE bloqueos_autenticacion (
    email VARCHAR(255) NOT NULL,
    failed_attempts INT NOT NULL DEFAULT 0,
    blocked_until TIMESTAMP NULL,
    PRIMARY KEY (email)
);
