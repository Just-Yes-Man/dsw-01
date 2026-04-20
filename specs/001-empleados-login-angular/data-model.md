# Data Model: Login de Empleados en Frontend

**Date**: 2026-03-17  
**Feature**: `001-empleados-login-angular`

## 1) Domain Entities

## EmpleadoAuthCredential

- **Description**: Payload de entrada para autenticación de empleado.

| Field | Type | Required | Rules |
|---|---|---:|---|
| email | String | Yes | formato email válido, max 255 |
| password | String | Yes | no vacío |

## EmpleadoSession

- **Description**: Estado de sesión autenticada del empleado para frontend.

| Field | Type | Required | Rules |
|---|---|---:|---|
| authenticated | Boolean | Yes | `true` si sesión válida |
| empleadoClave | String | Yes (si authenticated) | clave de empleado autenticado |
| email | String | Yes (si authenticated) | email del empleado |
| expiresAt | DateTime | Yes (si authenticated) | expiración efectiva de sesión |
| idleTimeoutMinutes | Integer | Yes | configurable por entorno (default equivalente a 8h) |

## AuthErrorState

- **Description**: Resultado de error de autenticación para UI.

| Field | Type | Required | Rules |
|---|---|---:|---|
| code | String | Yes | `UNAUTHORIZED`, `LOCKED`, `SERVER_ERROR` |
| message | String | Yes | en login inválido siempre `Credenciales inválidas` |

## 2) State Transitions

- `ANONIMO -> AUTENTICADO` al completar login exitoso.
- `AUTENTICADO -> ANONIMO` por logout explícito.
- `AUTENTICADO -> ANONIMO` por expiración de sesión/inactividad.
- `ANONIMO` se mantiene ante login fallido.

## 3) API Representation Contract (Auth)

## Login Request

```json
{
  "email": "ana@example.com",
  "password": "secret"
}
```

## Login Success Response

```json
{
  "authenticated": true,
  "empleadoClave": "EMP-1",
  "email": "ana@example.com",
  "expiresAt": "2026-03-17T20:00:00Z"
}
```

Cookie de sesión segura se emite por cabecera `Set-Cookie` con flags `HttpOnly`, `Secure`, `SameSite`.

## Login Failure Response

```json
{
  "code": "UNAUTHORIZED",
  "message": "Credenciales inválidas"
}
```

## 4) Validation Rules

- Email obligatorio y con formato válido.
- Password obligatoria y no vacía.
- Mensaje de error de autenticación inválida debe ser genérico y único.
- Frontend no debe persistir credenciales/tokens en `localStorage`/`sessionStorage`.

## 5) Security Constraints

- Sesión por cookie segura (`HttpOnly`, `Secure`, `SameSite`).
- Expiración por inactividad configurable por entorno (default 8h) con renovación por actividad.
- Compatibilidad de endpoints Basic existentes se mantiene fuera del flujo frontend de sesión.

## 6) Frontend Route Model

| Route | Auth required | Behavior |
|---|---:|---|
| /login | No | Muestra formulario de acceso |
| /empleados | Sí | Vista protegida post-login |

Acceso a rutas protegidas sin sesión válida redirige a `/login`.
