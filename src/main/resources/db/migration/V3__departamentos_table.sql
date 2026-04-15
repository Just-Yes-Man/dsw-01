-- V3: Create departamentos table and add optional FK from empleados
-- Constitution Gate 6: see specs/004-crud-departamentos/rollback-v3.md for rollback procedure

CREATE TABLE departamentos (
    id            BIGSERIAL    PRIMARY KEY,
    nombre        VARCHAR(255) NOT NULL,
    estado        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    creado_en     TIMESTAMP    NOT NULL DEFAULT NOW(),
    actualizado_en TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_departamentos_nombre    UNIQUE (nombre),
    CONSTRAINT chk_departamentos_estado   CHECK  (estado IN ('ACTIVO', 'INACTIVO'))
);

CREATE INDEX idx_departamentos_estado ON departamentos(estado);

-- Add optional association from empleados to departamentos (nullable FK)
ALTER TABLE empleados ADD COLUMN departamento_id BIGINT;
ALTER TABLE empleados ADD CONSTRAINT fk_empleados_departamento
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id);
