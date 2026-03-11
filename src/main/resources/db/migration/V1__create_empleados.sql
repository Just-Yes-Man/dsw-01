CREATE TABLE empleados (
    prefijo VARCHAR(4) NOT NULL DEFAULT 'EMP-',
    consecutivo BIGSERIAL NOT NULL,
    clave VARCHAR(24) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    telefono VARCHAR(100) NOT NULL,
    PRIMARY KEY (prefijo, consecutivo),
    CONSTRAINT uk_empleados_clave UNIQUE (clave),
    CONSTRAINT ck_empleados_prefijo CHECK (prefijo = 'EMP-'),
    CONSTRAINT ck_empleados_clave_formato CHECK (clave ~ '^EMP-[0-9]+$')
);
