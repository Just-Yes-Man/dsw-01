# Rollback Plan: Feature 004 Schema Changes

## Scope

Aplica a cambios de esquema introducidos por Feature 004 en migraciones Flyway dentro de `src/main/resources/db/migration/` relacionados con:

- Tabla `departamentos`
- Columna de relación `empleados.departamento_id`

## Preconditions

- Ambiente en mantenimiento (sin escrituras concurrentes)
- Backup lógico reciente de base (`pg_dump`) disponible
- Confirmación de que rollback de aplicación y contrato API se ejecutará en el mismo release

## Step 1: Backup before rollback

```bash
pg_dump -h <host> -U <user> -d <db> -Fc -f before_feature_004_rollback.dump
```

## Step 2: SQL rollback sequence

Ejecutar en PostgreSQL en una transacción controlada:

```sql
BEGIN;

ALTER TABLE IF EXISTS empleados
  DROP CONSTRAINT IF EXISTS fk_empleados_departamento;

ALTER TABLE IF EXISTS empleados
  DROP COLUMN IF EXISTS departamento_id;

DROP TABLE IF EXISTS departamentos;

COMMIT;
```

## Step 3: Flyway metadata alignment

Si la reversión se hace manualmente, alinear `flyway_schema_history` para evitar drift:

1. Identificar versión aplicada de la feature 004.
2. Marcar reparación con `flyway repair` o revertir entrada bajo procedimiento DBA aprobado.

## Step 4: Post-rollback validation

Validar:

- Arranque de aplicación sin errores de migración
- Endpoints de departamentos no expuestos en build rollback
- Endpoints de empleados operativos sin `departamentoId`

Comandos sugeridos:

```bash
mvn -q -DskipTests spring-boot:run
mvn test
```

## Risk Notes

- Este rollback elimina información de departamentos y asociación con empleados.
- Si hay datos productivos, restaurar desde backup tras rollback o ejecutar plan de migración inversa con preservación de datos.
