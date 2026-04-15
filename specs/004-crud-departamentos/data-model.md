# Data Model: CRUD Departamentos + Relación Empleados

**Date**: 12 de marzo de 2026  
**Status**: Phase 1 Design Complete

## 1) Domain Entities

## Departamento

- **Description**: Unidad organizacional con ciclo de vida activo/inactivo.
- **Primary key**: `id` (BIGSERIAL / Long)

### Fields

| Field | Type | Required | Rules |
|---|---|---:|---|
| id | Long | Yes | PK autogenerada |
| nombre | String | Yes | 1..255, único |
| estado | Enum | Yes | `ACTIVO` / `INACTIVO`, default `ACTIVO` |
| creadoEn | LocalDateTime | Yes | auto timestamp |
| actualizadoEn | LocalDateTime | Yes | auto timestamp |

### State transitions

- `ACTIVO -> INACTIVO` vía DELETE lógico.
- `INACTIVO` no se retorna en lista ni lectura estándar.

## Empleado (impactado por la feature)

- **Description**: Persona operativa que debe pertenecer a un departamento válido.
- **Clave de negocio**: `clave` (composite id existente en sistema)

### Fields relevantes para esta feature

| Field | Type | Required | Rules |
|---|---|---:|---|
| clave | String | Yes | generado por sistema |
| nombre | String | Yes | validaciones existentes |
| email | String | Yes | único, validaciones existentes |
| estadoAcceso | Enum | Yes | `ACTIVO` / `INACTIVO` |
| departamentoId | Long | Yes | FK lógica obligatoria a `Departamento` ACTIVO |

## 2) Relationship Model

- **Cardinality**: `Departamento (1) -> (N) Empleado`
- **Rules**:
  - Un empleado pertenece a un solo departamento.
  - Un departamento puede tener cero o muchos empleados.
  - `departamentoId` en empleado es obligatorio en create/update.
  - Si `departamentoId` no existe o referencia departamento INACTIVO -> HTTP 404.

## 3) Persistence and Integrity

## DB constraints

- `departamentos.nombre` UNIQUE.
- `departamentos.estado` constrained to `ACTIVO|INACTIVO`.
- `empleados.departamento_id` presente como FK lógica usada por servicio.

## Delete behavior (departamento)

1. Buscar departamento ACTIVO por id.
2. Verificar empleados asociados (`existsByDepartamentoId(id)`).
3. Si existen -> HTTP 409 CONFLICT.
4. Si no existen -> marcar `INACTIVO` (soft-delete).

## 4) API Representation Contracts

## DepartamentoResponse (list/create/update)

```json
{
  "id": 10,
  "nombre": "Finanzas",
  "estado": "ACTIVO",
  "creadoEn": "2026-03-12T10:30:00",
  "actualizadoEn": "2026-03-12T10:30:00"
}
```

## DepartamentoDetailResponse (GET by id)

Incluye empleados embebidos con tope fijo de 50.

```json
{
  "id": 10,
  "nombre": "Finanzas",
  "estado": "ACTIVO",
  "creadoEn": "2026-03-12T10:30:00",
  "actualizadoEn": "2026-03-12T10:30:00",
  "empleados": [
    {
      "clave": "EMP-1",
      "nombre": "Ana",
      "email": "ana@example.com",
      "estadoAcceso": "ACTIVO"
    }
  ]
}
```

## EmpleadoResponse

Debe incluir `departamento` embebido (no sólo id).

```json
{
  "clave": "EMP-1",
  "nombre": "Ana",
  "direccion": "Calle 1",
  "telefono": "555-1234",
  "email": "ana@example.com",
  "estadoAcceso": "ACTIVO",
  "departamento": {
    "id": 10,
    "nombre": "Finanzas",
    "estado": "ACTIVO"
  }
}
```

## 5) Validation Rules

## Departamento

- `nombre` obligatorio, no vacío, max 255.
- duplicado de nombre -> HTTP 409.

## Empleado (relación)

- `departamentoId` obligatorio en create/update -> null => HTTP 400.
- `departamentoId` inválido (no existe o INACTIVO) -> HTTP 404.

## 6) Query Rules

- Listas de colección principales (`/api/v1/departamentos`, `/api/v1/empleados`) paginadas con size fijo 10.
- Embebido de `empleados` en `GET /departamentos/{id}` limitado a 50 (sin paginación embebida).

## 7) Non-functional considerations

- Mantener p95 < 500ms para operaciones CRUD bajo carga esperada.
- Evitar N+1 al armar embebidos; utilizar consulta eficiente acotada a 50.
- Logging estructurado en creación, actualización, delete bloqueado y errores de validación.
