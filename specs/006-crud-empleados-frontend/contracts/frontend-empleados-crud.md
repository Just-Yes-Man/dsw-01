# Frontend Integration Contract: Empleados CRUD

**Feature**: `006-crud-empleados-frontend`  
**Scope**: Contrato de consumo frontend hacia API protegida existente `/api/v1/empleados`

## 1) Endpoints Consumidos

### Listar empleados (paginado)

- **Method/Path**: `GET /api/v1/empleados?page={n}`
- **Auth**: endpoint protegido (`withCredentials: true` con autenticación vigente)
- **Success**: `200 OK` con `EmpleadoPageResponse`
- **Frontend behavior**:
  - renderiza tabla de empleados
  - paginación con `size = 10`
  - por defecto muestra activos; opcionalmente incluye inactivos con toggle

### Obtener empleado por clave

- **Method/Path**: `GET /api/v1/empleados/{clave}`
- **Auth**: endpoint protegido con autenticación vigente
- **Success**: `200 OK` con `EmpleadoResponse`
- **Frontend behavior**: prellenado de formulario de edición

### Crear empleado

- **Method/Path**: `POST /api/v1/empleados`
- **Auth**: endpoint protegido con autenticación vigente
- **Request**: `EmpleadoCreateRequest`
- **Success**: `201 Created` con `EmpleadoResponse`
- **Frontend behavior**: notifica éxito y refresca listado

### Actualizar empleado (incluye reactivación/desactivación lógica)

- **Method/Path**: `PUT /api/v1/empleados/{clave}`
- **Auth**: endpoint protegido con autenticación vigente
- **Request**: `EmpleadoUpdateRequest` con `estadoAcceso` (`ACTIVO`/`INACTIVO`)
- **Success**: `200 OK` con `EmpleadoResponse`
- **Frontend behavior**:
  - edición de datos generales
  - desactivación lógica: enviar `estadoAcceso=INACTIVO`
  - reactivación: enviar `estadoAcceso=ACTIVO`

### Eliminación física (no usada por este feature)

- **Method/Path**: `DELETE /api/v1/empleados/{clave}`
- **Policy in this feature**: no invocar desde UI para cumplir regla de eliminación lógica

## 2) Error Handling Contract

## 2.1) Security Model Alignment

- Los endpoints de negocio de empleados se mantienen protegidos por la configuración de seguridad vigente del backend.
- El frontend consume dichos endpoints protegidos sin credenciales hardcodeadas y con `withCredentials: true`.
- No se exponen secretos en cliente ni se persisten credenciales sensibles en almacenamiento local del navegador.

### Error de sesión

- **Status**: `401 Unauthorized`
- **Frontend behavior**: limpiar estado de autenticación y redirigir a `/login`

### Error de validación

- **Status**: `400 Bad Request`
- **Body**: `ErrorResponse { code, message }`
- **Frontend behavior**: mostrar mensaje por campo o banner según contexto

### Conflicto de unicidad de correo (email ya registrado)

- **Status**: `400 Bad Request` (según implementación actual)
- **Body message esperado**: incluye texto de conflicto (`email ya está registrado`)
- **Frontend behavior**: mostrar mensaje específico accionable y mantener formulario editable

### Error técnico

- **Status**: `500` u otro no controlado
- **Frontend behavior**: mensaje genérico no sensible + opción de reintento

## 3) Frontend Route Contract

- `GET /login`: pantalla pública de autenticación.
- `GET /empleados`: pantalla protegida con guard de sesión.
- Intento de acceso sin sesión a `/empleados` -> redirección a `/login`.

## 4) Non-Goals

- No se cambia la versión de API (`/api/v1/...`) ni se relaja la protección de endpoints de negocio.
- No se expone en UI la eliminación física vía `DELETE`.
