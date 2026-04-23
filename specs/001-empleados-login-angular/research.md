# Research: Login de Empleados en Frontend

**Date**: 2026-03-17  
**Feature**: `001-empleados-login-angular`

## Decision 1: Modelo de autenticación para frontend

- **Decision**: Implementar autenticación basada en sesión con cookie `HttpOnly` + `Secure` + `SameSite` para frontend web.
- **Rationale**: Evita exposición de credenciales/tokens en almacenamiento JS y alinea con el requerimiento aclarado en spec.
- **Alternatives considered**:
  - Basic Auth en cada request desde frontend (rechazada por exposición repetida de credenciales).
  - JWT en `localStorage` (rechazada por mayor riesgo XSS).

## Decision 2: Compatibilidad con Basic Auth existente

- **Decision**: Mantener Basic Auth para endpoints legado actuales y habilitar flujo de sesión para endpoints de autenticación orientados a frontend.
- **Rationale**: Minimiza ruptura de clientes existentes y permite migración incremental.
- **Alternatives considered**:
  - Migración completa inmediata de todo backend a sesión (rechazada por alto impacto en feature acotada).

## Decision 3: Contrato de autenticación versionado

- **Decision**: Definir endpoints versionados bajo `/api/v1/auth/login`, `/api/v1/auth/logout` y `/api/v1/auth/session`.
- **Rationale**: Cumple gobernanza de rutas versionadas y separa claramente autenticación del dominio de negocio.
- **Alternatives considered**:
  - Reusar endpoints de dominio `empleados` para login (rechazada por mezcla de responsabilidades).

## Decision 4: Expiración y renovación de sesión

- **Decision**: Expiración configurable por entorno, default 8h por inactividad, con renovación por actividad.
- **Rationale**: Balancea seguridad y experiencia, y fue definición explícita de clarificación.
- **Alternatives considered**:
  - TTL fijo sin renovación (rechazada por rigidez operativa).

## Decision 5: Mensajería de error en login

- **Decision**: Mostrar siempre mensaje genérico único `Credenciales inválidas` ante fallo de autenticación.
- **Rationale**: Evita enumeración de usuarios y fuga de información sensible.
- **Alternatives considered**:
  - Mensajes diferenciados por causa (`correo no existe`/`contraseña incorrecta`) rechazados por seguridad.

## Decision 6: Diseño de frontend Angular

- **Decision**: Crear módulo de autenticación con `LoginComponent`, `AuthService`, `AuthGuard` e interceptor HTTP para estado de sesión.
- **Rationale**: Arquitectura estándar de Angular, testeable y de baja fricción para crecimiento posterior.
- **Alternatives considered**:
  - Lógica de auth distribuida por pantallas (rechazada por acoplamiento y baja mantenibilidad).

## Decision 7: Estrategia de pruebas

- **Decision**: Cobertura mínima obligatoria con Cypress E2E para login exitoso, login fallido, acceso protegido sin sesión y logout.
- **Rationale**: Cumple constitución y valida recorridos reales de usuario final.
- **Alternatives considered**:
  - Sólo pruebas unitarias frontend (rechazada por cobertura insuficiente de flujo extremo a extremo).

## Decision 8: CORS y CSRF en sesión por cookie

- **Decision**: Configurar CORS explícito para origen frontend y estrategia CSRF compatible con sesión (token o exclusión controlada en endpoints auth, según política de seguridad acordada en implementación).
- **Rationale**: Necesario para operación segura con cookies cross-origin en desarrollo local.
- **Alternatives considered**:
  - Mantener configuración actual sin CORS explícito (rechazada por bloqueo de navegador y riesgo de configuración ad hoc).
