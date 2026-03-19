# Data Model: CRUD de Departamentos en Frontend

**Date**: 2026-03-19  
**Feature**: `005-crud-departamentos`

## 1) Domain/UI Entities

### DepartamentoView

- **Description**: Representación de departamento mostrada en tabla y usada como base de edición.

| Field | Type | Required | Rules |
|---|---|---:|---|
| id | number | Sí | entero positivo, solo lectura en UI |
| nombre | string | Sí | obligatorio, no vacío, máximo 255 |
| estado | `ACTIVO \| INACTIVO` | Sí | provisto por backend |
| creadoEn | string (ISO datetime) | Sí | solo lectura |
| actualizadoEn | string (ISO datetime) | Sí | solo lectura |

### DepartamentoListState

- **Description**: Estado de la vista de listado paginado.

| Field | Type | Required | Rules |
|---|---|---:|---|
| page | number | Sí | `>= 0` |
| size | number | Sí | fijo en `10` |
| totalElements | number | Sí | `>= 0` |
| items | `DepartamentoView[]` | Sí | puede ser vacío |
| loading | boolean | Sí | `true` durante consulta |
| error | string \| null | Sí | mensaje para fallo de carga |

### DepartamentoFormState

- **Description**: Estado de formulario para alta y edición.

| Field | Type | Required | Rules |
|---|---|---:|---|
| mode | `create \| edit` | Sí | define comportamiento de UI |
| values.nombre | string | Sí | obligatorio |
| fieldErrors | `Record<string,string>` | Sí | errores de validación de cliente |
| backendError | string \| null | Sí | error técnico o conflicto backend |
| submitting | boolean | Sí | bloquea doble envío |

### DeleteDepartamentoState

- **Description**: Estado de confirmación y ejecución de eliminación.

| Field | Type | Required | Rules |
|---|---|---:|---|
| targetId | number \| null | Sí | departamento seleccionado |
| open | boolean | Sí | controla visibilidad del diálogo |
| processing | boolean | Sí | bloquea doble confirmación |
| backendError | string \| null | Sí | error de conflicto o técnico |

## 2) Backend Contract Entities Consumed

### DepartamentoPageResponse

| Field | Type | Notes |
|---|---|---|
| page | number | página solicitada |
| size | number | esperado `10` |
| totalElements | number | total de activos |
| items | `DepartamentoResponse[]` | resultados de la página |

### DepartamentoResponse

| Field | Type | Notes |
|---|---|---|
| id | number | identificador del departamento |
| nombre | string | único en estado activo |
| estado | `ACTIVO \| INACTIVO` | estado persistido |
| creadoEn | string | timestamp ISO |
| actualizadoEn | string | timestamp ISO |

### ErrorResponse

| Field | Type | Notes |
|---|---|---|
| code | string | p.ej. `CONFLICT`, `VALIDATION_ERROR`, `UNAUTHORIZED` |
| message | string | mensaje de error mostrable/normalizable en UI |

## 3) State Transitions

- `LIST_IDLE -> LIST_LOADING -> LIST_READY` al consultar página con éxito.
- `LIST_LOADING -> LIST_EMPTY` cuando la respuesta trae `items.length = 0`.
- `LIST_LOADING -> LIST_ERROR` cuando falla la consulta.
- `FORM_IDLE -> FORM_SUBMITTING -> FORM_SUCCESS` en alta/edición válida.
- `FORM_SUBMITTING -> FORM_ERROR_CONFLICT` cuando backend responde conflicto (`409`).
- `FORM_SUBMITTING -> FORM_ERROR_VALIDATION` cuando backend responde validación (`400`).
- `DELETE_IDLE -> DELETE_CONFIRMING -> DELETE_PROCESSING -> DELETE_SUCCESS` cuando eliminación es aceptada.
- `DELETE_PROCESSING -> DELETE_ERROR_CONFLICT` cuando hay empleados asociados (`409`).
- Cualquier estado de operación -> `SESSION_INVALID` con respuesta `401`, seguido de redirección a `/login`.

## 4) Validation Rules

- Frontend valida `nombre` obligatorio y no vacío antes de enviar create/update.
- Frontend bloquea botones de guardar/eliminar mientras `submitting` o `processing` estén activos.
- Frontend muestra mensaje de conflicto si backend reporta nombre duplicado o eliminación rechazada por asociación.
- Paginación siempre solicita página por query `page` y no altera `size` definido por backend.
