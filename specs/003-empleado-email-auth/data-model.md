# Data Model: Autenticación de Empleados por Correo

## Entity: Empleado
- Description: Registro principal del catálogo de empleados con datos de negocio y credenciales de autenticación.

### Fields
- `clave`:
  - Type: `string`
  - Constraints: formato `EMP-<autonumérico>`, único, inmutable
- `nombre`:
  - Type: `string`
  - Constraints: longitud `1..100`
- `direccion`:
  - Type: `string`
  - Constraints: longitud `1..100`
- `telefono`:
  - Type: `string`
  - Constraints: longitud `1..100`
- `email`:
  - Type: `string`
  - Constraints: formato correo válido, único, requerido para autenticación
- `password`:
  - Type: `string`
  - Constraints: requerido, almacenado en texto plano (decisión de alcance)
- `estadoAcceso`:
  - Type: `enum`
  - Allowed values: `ACTIVO`, `INACTIVO`
  - Default: `ACTIVO`

## Entity: BloqueoAutenticacion
- Description: Estado operativo de control de intentos fallidos por correo.

### Fields
- `email`:
  - Type: `string`
  - Constraints: referencia lógica a credencial de empleado
- `failedAttempts`:
  - Type: `integer`
  - Constraints: `>= 0`
- `blockedUntil`:
  - Type: `datetime`
  - Constraints: nulo cuando no hay bloqueo activo

## Entity: EventoAutenticacion
- Description: Registro de trazabilidad de intentos de autenticación.

### Fields
- `timestamp`:
  - Type: `datetime`
- `email`:
  - Type: `string`
- `resultado`:
  - Type: `enum`
  - Allowed values: `SUCCESS`, `INVALID_CREDENTIALS`, `INACTIVE_ACCOUNT`, `TEMPORARY_LOCK`
- `path`:
  - Type: `string`
- `method`:
  - Type: `string`

## Relationships
- `Empleado.email` se relaciona 1:1 con datos de autenticación.
- `BloqueoAutenticacion.email` se relaciona 0..1 con `Empleado.email`.
- `EventoAutenticacion.email` se relaciona 0..N con `Empleado.email`.

## Validation Rules
- `email` MUST ser válido y único.
- `password` MUST estar presente en alta/actualización cuando se configure acceso.
- `estadoAcceso=INACTIVO` MUST bloquear autenticación aunque correo/contraseña sean correctos.
- `failedAttempts >= 5` MUST activar bloqueo temporal por 15 minutos.
- Una autenticación exitosa MUST reiniciar `failedAttempts` y limpiar `blockedUntil`.

## State Transitions
- `estadoAcceso`: `ACTIVO -> INACTIVO` (deshabilitar acceso), `INACTIVO -> ACTIVO` (rehabilitar acceso).
- `BloqueoAutenticacion`:
  - `NONE -> COUNTING` al primer fallo.
  - `COUNTING -> BLOCKED` al quinto fallo consecutivo.
  - `BLOCKED -> NONE` tras expiración de 15 min o autenticación exitosa posterior.
