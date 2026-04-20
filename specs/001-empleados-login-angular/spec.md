# Feature Specification: Login de Empleados en Frontend

**Feature Branch**: `001-empleados-login-angular`  
**Created**: 2026-03-17  
**Status**: Draft  
**Input**: User description: "añade un login para los empleados en el front end de angular, este debera se registrara con el correo y contraseña"

## Clarifications

### Session 2026-03-17

- Q: ¿Qué mecanismo de sesión debe usar el login frontend de empleados? → A: Opción B, sesión con cookie `HttpOnly` + `Secure` + `SameSite`.
- Q: ¿Cómo debe definirse la expiración de sesión para frontend? → A: Opción D, expiración configurable por entorno (default 8h por inactividad con renovación).
- Q: ¿Cuál debe ser la ruta de redirección tras login exitoso? → A: Opción B, redirigir a `/empleados`.
- Q: ¿Cómo debe mostrarse el error de autenticación al usuario? → A: Opción C, mensaje genérico único “Credenciales inválidas”.
- Q: ¿Debe incluirse registro/recuperación de contraseña en este alcance? → A: Opción A, no; sólo login/logout con cuentas existentes.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar sesión con correo y contraseña (Priority: P1)

Como empleado, quiero iniciar sesión en el frontend con mi correo y contraseña para acceder al sistema sin usar credenciales técnicas en la interfaz.

**Why this priority**: Es el acceso base al sistema; sin login funcional no existe flujo de uso para empleados.

**Independent Test**: Se puede validar de forma aislada al abrir la pantalla de login, enviar credenciales válidas y confirmar redirección a la pantalla principal autenticada.

**Acceptance Scenarios**:

1. **Given** que el empleado está en la pantalla de login y posee credenciales válidas, **When** envía correo y contraseña, **Then** el sistema autentica y muestra el área autenticada.
2. **Given** que el empleado no está autenticado, **When** intenta acceder a una ruta protegida del frontend, **Then** el sistema lo redirige a la pantalla de login.

---

### User Story 2 - Manejar errores de autenticación (Priority: P2)

Como empleado, quiero recibir mensajes claros cuando mis credenciales son inválidas para corregir el intento de acceso sin ambigüedad.

**Why this priority**: Reduce intentos fallidos repetitivos y soporte operativo por errores de acceso.

**Independent Test**: Se valida con credenciales inválidas y confirmando mensaje de error visible, sin autenticación ni navegación a áreas protegidas.

**Acceptance Scenarios**:

1. **Given** que el empleado ingresa correo o contraseña incorrectos, **When** envía el formulario, **Then** el sistema muestra error de autenticación y mantiene al usuario en login.
2. **Given** que el login falla, **When** el empleado corrige los datos y reintenta con credenciales válidas, **Then** el acceso se completa exitosamente.

---

### User Story 3 - Mantener sesión y cerrar sesión (Priority: P3)

Como empleado autenticado, quiero conservar mi sesión durante la navegación y tener opción de cerrar sesión para proteger mi cuenta.

**Why this priority**: Completa el ciclo de seguridad y uso diario posterior al acceso inicial.

**Independent Test**: Se valida iniciando sesión, navegando entre rutas protegidas sin reautenticación y ejecutando logout para volver a login y bloquear rutas protegidas.

**Acceptance Scenarios**:

1. **Given** que el empleado ya inició sesión, **When** navega entre vistas protegidas del frontend, **Then** no se le vuelve a solicitar autenticación durante la sesión activa.
2. **Given** que el empleado está autenticado, **When** selecciona cerrar sesión, **Then** la sesión termina y cualquier acceso a rutas protegidas lo redirige al login.

---

### Edge Cases

- ¿Qué ocurre si el usuario envía correo vacío, correo con formato inválido o contraseña vacía?
- ¿Qué ocurre si la API de autenticación no responde temporalmente o devuelve error de servidor?
- ¿Qué ocurre si un usuario autenticado abre una nueva pestaña o recarga la página?
- ¿Qué ocurre si el token/sesión expira mientras navega por una ruta protegida?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST mostrar una pantalla de login para empleados con campos de correo y contraseña.
- **FR-002**: El sistema MUST validar en frontend que correo y contraseña sean obligatorios antes de enviar la solicitud de autenticación.
- **FR-003**: El sistema MUST validar formato de correo antes de permitir envío del formulario.
- **FR-004**: El sistema MUST autenticar al empleado usando correo + contraseña contra un endpoint de login público que establezca sesión en cookie `HttpOnly` + `Secure` + `SameSite`.
- **FR-005**: El sistema MUST redirigir al empleado autenticado a `/empleados` tras login exitoso.
- **FR-006**: El sistema MUST impedir acceso a rutas protegidas cuando no exista sesión válida y redirigir al login.
- **FR-007**: El sistema MUST mostrar siempre un mensaje genérico único “Credenciales inválidas” ante fallos de autenticación (sin distinguir si falló correo o contraseña).
- **FR-008**: El sistema MUST mostrar mensaje genérico recuperable cuando falle la autenticación por error técnico temporal.
- **FR-009**: El sistema MUST mantener la sesión del empleado durante navegación entre rutas protegidas mientras sea válida, con expiración configurable por entorno (default 8 horas por inactividad) y renovación por actividad.
- **FR-010**: El sistema MUST permitir cerrar sesión explícitamente y limpiar estado de autenticación del frontend.
- **FR-011**: El sistema MUST registrar eventos funcionales de login exitoso, login fallido y logout en los logs del backend sin exponer contraseñas.
- **FR-012**: El sistema MUST incluir pruebas E2E que cubran login exitoso, login fallido, protección de rutas y logout.
- **FR-013**: El frontend MUST no persistir contraseñas ni tokens de autenticación en `localStorage` o `sessionStorage`.
- **FR-014**: El sistema MUST mantener compatibilidad con autenticación Basic del backend actual para los endpoints protegidos existentes no migrados.
- **FR-016**: El sistema MUST declarar explícitamente que `/api/v1/auth/login`, `/api/v1/auth/session` y `/api/v1/auth/logout` son endpoints públicos de sesión para frontend, y que todo endpoint de negocio protegido conserva requisito de Basic Authentication conforme a la constitución.
- **FR-015**: El alcance de esta feature MUST excluir registro de nuevas cuentas y recuperación de contraseña; sólo login/logout de cuentas existentes.

### Constitution Alignment *(mandatory)*

- **CA-001**: The feature MUST run on Spring Boot 3 with Java 17, or document an approved exception.
- **CA-002**: The feature MUST define Basic Authentication behavior for protected endpoints y distinguir explícitamente endpoints públicos de sesión (`/api/v1/auth/login`, `/api/v1/auth/session`, `/api/v1/auth/logout`) que no sustituyen Basic en endpoints protegidos de negocio.
- **CA-003**: The feature MUST define PostgreSQL persistence impact and Docker/Docker Compose needs.
- **CA-004**: The feature MUST define Swagger/OpenAPI contract updates for every endpoint change with routes versioned as `/api/v{major}/...`.
- **CA-005**: The feature MUST define pagination for collection queries with 10 instances per response.
- **CA-006**: The feature MUST define required automated tests and logging/security evidence.
- **CA-007**: If the feature includes web frontend scope, it MUST use Angular 22 LTS (or document approved exception).
- **CA-008**: If the feature includes web frontend scope, it MUST define Cypress E2E coverage for critical flows.

### Key Entities *(include if feature involves data)*

- **EmpleadoAuthCredential**: Representa el conjunto de entrada de autenticación (`email`, `password`) usado en el formulario de login.
- **EmpleadoSession**: Representa el estado de sesión autenticada en frontend respaldada por cookie de sesión `HttpOnly`/`Secure`/`SameSite`, con expiración por inactividad configurable por entorno y renovación por actividad.
- **AuthErrorState**: Representa resultado de error de autenticación para feedback al usuario (tipo de error, mensaje visible, timestamp de intento).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 95% de empleados con credenciales válidas completa login en menos de 30 segundos desde que abre la pantalla.
- **SC-002**: El 100% de accesos a rutas protegidas sin sesión válida redirige al login.
- **SC-003**: El 100% de intentos con credenciales inválidas muestra mensaje de error visible sin navegar a zona protegida.
- **SC-004**: La suite Cypress de autenticación crítica pasa al 100% en ejecución reproducible local/CI.

## Assumptions

- La API backend de autenticación para empleados con correo y contraseña ya existe y está disponible para integración.
- El alcance de esta feature es login/logout y protección de rutas; no incluye recuperación de contraseña ni registro de nuevos empleados.
- El frontend web se implementará con Angular 22 LTS conforme a la constitución.
- Las credenciales nunca se almacenan en texto plano en cliente ni logs.
