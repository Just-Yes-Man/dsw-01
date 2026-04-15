# Data Model: CRUD de Empleados en Frontend

**Date**: 2026-03-18  
**Feature**: `006-crud-empleados-frontend`

## 1) Domain/UI Entities

### EmpleadoView

- **Description**: Representación de empleado para tabla y formularios en frontend.

| Field | Type | Required | Rules |
|---|---|---:|---|
| clave | string | Sí | solo lectura en frontend, formato esperado `EMP-{n}` |
| nombre | string | Sí | no vacío, max 100 |
| direccion | string | Sí | no vacío, max 100 |
| telefono | string | Sí | no vacío, max 100 |
| email | string | Sí | formato email, max 255 |
| password | string | Sí (create/update) | no vacío, max 255 |
| estadoAcceso | `ACTIVO \| INACTIVO` | Sí | por defecto `ACTIVO` en creación |
| departamento.id | number | Sí | entero positivo |
| departamento.nombre | string | Sí | solo lectura para despliegue |

### EmpleadoListState

- **Description**: Estado de la vista de listado paginado.

| Field | Type | Required | Rules |
|---|---|---:|---|
| page | number | Sí | `>= 0` |
| size | number | Sí | fijo `10` (contrato backend) |
| totalElements | number | Sí | `>= 0` |
| items | `EmpleadoView[]` | Sí | puede ser vacío |
| showInactive | boolean | Sí | `false` por defecto |
| loading | boolean | Sí | `true` durante consulta |
| error | string \| null | Sí | mensaje cuando falla la carga |

### EmpleadoFormState

- **Description**: Estado del formulario de alta/edición.

| Field | Type | Required | Rules |
|---|---|---:|---|
| mode | `create \| edit` | Sí | define comportamiento y títulos |
| values | object | Sí | contiene campos editables de empleado |
| fieldErrors | `Record<string,string>` | Sí | errores por validación de cliente |
| backendError | string \| null | Sí | error técnico o de conflicto |
| submitting | boolean | Sí | bloquea doble envío |

### DeleteConfirmationState

- **Description**: Estado de confirmación para desactivación lógica.

| Field | Type | Required | Rules |
|---|---|---:|---|
| targetClave | string | Sí | empleado afectado |
| open | boolean | Sí | controla visibilidad de confirmación |
| processing | boolean | Sí | evita doble acción |

## 2) Backend Contract Entities Consumed

### EmpleadoPageResponse

| Field | Type | Notes |
|---|---|---|
| page | number | página solicitada |
| size | number | esperado 10 |
| totalElements | number | total en backend |
| items | `EmpleadoResponse[]` | lista de empleados |

### EmpleadoResponse

| Field | Type | Notes |
|---|---|---|
| clave | string | identificador legible |
| nombre | string |  |
| direccion | string |  |
| telefono | string |  |
| email | string | único en backend |
| estadoAcceso | `ACTIVO \| INACTIVO` | usado para filtro UI |
| departamento | object | embebido `{id,nombre,estado}` |

### ErrorResponse

| Field | Type | Notes |
|---|---|---|
| code | string | `VALIDATION_ERROR`, `UNAUTHORIZED`, etc. |
| message | string | texto mostrado/normalizado en UI |

## 3) State Transitions

- `LIST_LOADING -> LIST_READY` al obtener página exitosamente.
- `LIST_READY -> EMPTY_STATE` cuando `items.length = 0`.
- `FORM_IDLE -> FORM_SUBMITTING -> FORM_SUCCESS` en create/update válidos.
- `FORM_SUBMITTING -> FORM_ERROR_VALIDATION` ante validación cliente.
- `FORM_SUBMITTING -> FORM_ERROR_CONFLICT` ante conflicto de unicidad reportado por backend.
- `ACTIVE -> INACTIVE` al desactivar (eliminación lógica) vía edición/acción de desactivación.
- `INACTIVE -> ACTIVE` al reactivar desde edición.
- Cualquier estado de operación -> `SESSION_INVALID` cuando backend responde `401`, seguido de redirección a `/login`.

## 4) Validation Rules

- Frontend valida requeridos y formato email antes de enviar.
- Frontend evita doble submit mientras `submitting = true`.
- Backend conserva validación de unicidad de email y frontend muestra mensaje accionable cuando ocurra conflicto.
- Filtro de inactivos no altera contrato backend; se aplica sobre respuesta recibida en cliente en esta fase.
