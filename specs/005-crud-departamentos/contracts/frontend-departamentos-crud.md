# Frontend Integration Contract: Departamentos CRUD

**Feature**: `005-crud-departamentos`  
**Scope**: Consumo frontend hacia API protegida existente `/api/v1/departamentos`

## 1) Endpoints Consumidos

### Listar departamentos (paginado)

- **Method/Path**: `GET /api/v1/departamentos?page={n}`
- **Auth**: endpoint protegido, consumo con `withCredentials: true`
- **Success**: `200 OK` con `DepartamentoPageResponse`
- **Frontend behavior**:
  - renderiza tabla paginada
  - muestra estados de carga, vacío y error
  - mantiene navegación por página

### Obtener departamento por id

- **Method/Path**: `GET /api/v1/departamentos/{id}`
- **Auth**: endpoint protegido con sesión activa
- **Success**: `200 OK` con `DepartamentoDetailResponse`
- **Frontend behavior**: prellenar formulario de edición

### Crear departamento

- **Method/Path**: `POST /api/v1/departamentos`
- **Auth**: endpoint protegido con sesión activa
- **Request**: `DepartamentoCreateRequest { nombre }`
- **Success**: `201 Created` con `DepartamentoResponse`
- **Frontend behavior**: notificar éxito y refrescar listado

### Editar departamento

- **Method/Path**: `PATCH /api/v1/departamentos/{id}`
- **Auth**: endpoint protegido con sesión activa
- **Request**: `DepartamentoUpdateRequest { nombre }`
- **Success**: `200 OK` con `DepartamentoResponse`
- **Frontend behavior**: notificar éxito y reflejar cambios en la vista

### Eliminar departamento

- **Method/Path**: `DELETE /api/v1/departamentos/{id}`
- **Auth**: endpoint protegido con sesión activa
- **Success**: `204 No Content`
- **Frontend behavior**:
  - requiere confirmación explícita
  - elimina del listado visible después de éxito
  - si backend rechaza por asociación, mantiene elemento y muestra mensaje de conflicto

## 2) Error Handling Contract

### Error de sesión

- **Status**: `401 Unauthorized`
- **Frontend behavior**: limpiar estado de autenticación y redirigir a `/login`

### Error de validación

- **Status**: `400 Bad Request`
- **Body**: `ErrorResponse { code, message }`
- **Frontend behavior**: mostrar mensaje por campo o banner según contexto

### Conflicto de negocio

- **Status**: `409 Conflict`
- **Body**: `ErrorResponse { code: "CONFLICT", message }`
- **Casos esperados**:
  - nombre de departamento duplicado en create/update
  - eliminación rechazada por empleados asociados
- **Frontend behavior**: mostrar mensaje específico y permitir corrección/reintento

### Error técnico

- **Status**: `500` u otro no controlado
- **Frontend behavior**: mensaje genérico no sensible + opción de reintento

## 3) Frontend Route Contract

- `GET /login`: pantalla pública de autenticación.
- `GET /departamentos`: pantalla protegida de listado.
- `GET /departamentos/nuevo`: pantalla protegida de alta.
- `GET /departamentos/:id/editar`: pantalla protegida de edición.
- Acceso sin sesión a rutas protegidas -> redirección a `/login`.

## 4) Non-Goals

- No se cambia la versión de API (`/api/v1/...`) ni el esquema de autenticación del backend.
- No se agrega reactivación de departamentos en esta iteración.
- No se incorporan permisos por rol para operaciones CRUD en esta fase.

## 5) Trazabilidad de contrato (CA-004)

- Este feature **no introduce** endpoints nuevos ni cambios de versión; mantiene rutas versionadas existentes bajo `/api/v1/departamentos`.
- La actualización de contrato se limita a documentar consumo frontend y manejo de errores sobre el OpenAPI/Swagger vigente.
- Cualquier cambio futuro de request/response o códigos de estado deberá reflejarse en Swagger/OpenAPI en el mismo incremento.
