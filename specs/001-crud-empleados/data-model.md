# Data Model: CRUD de Empleados

## Entity: Empleado
- Description: Registro de empleado gestionado por el catálogo interno.

### Fields
- `prefijo`:
  - Type: `VARCHAR(4)`
  - Required: Sí
  - Constraints: valor fijo `EMP-`, parte de PK compuesta, inmutable
  - Source: Generado por el sistema
- `consecutivo`:
  - Type: `BIGINT`
  - Required: Sí
  - Constraints: autoincremental, parte de PK compuesta, inmutable
  - Source: Generado por el sistema (no asignable por cliente)
- `clave`:
  - Type: `VARCHAR(24)`
  - Required: Sí
  - Constraints: formato `EMP-<autonumérico>`, único para exposición API, derivada de `prefijo` + `consecutivo`
  - Source: Generado por el sistema (no asignable por cliente)
- `nombre`:
  - Type: `VARCHAR(100)`
  - Required: Sí
  - Constraints: longitud 1..100
- `direccion`:
  - Type: `VARCHAR(100)`
  - Required: Sí
  - Constraints: longitud 1..100
- `telefono`:
  - Type: `VARCHAR(100)`
  - Required: Sí
  - Constraints: longitud 1..100

## Relationships
- No aplica (entidad independiente en este alcance).

## Validation Rules
- En creación y actualización:
  - `nombre`, `direccion`, `telefono` MUST existir y tener longitud máxima de 100.
  - `clave`, `prefijo` y `consecutivo` MUST NO ser recibidos desde el cliente para creación.
  - Si `clave`, `prefijo` o `consecutivo` se envían en creación/actualización, la solicitud se rechaza con error 400.

## State Transitions
- `CREATED`: empleado dado de alta.
- `UPDATED`: empleado modificado (nombre, dirección o teléfono).
- `DELETED`: empleado eliminado del catálogo.

## Indexing
- Índice primario compuesto en (`prefijo`, `consecutivo`).
- Índice único en `clave` para búsqueda por identificador expuesto.
- No se requieren índices secundarios para el alcance actual.
