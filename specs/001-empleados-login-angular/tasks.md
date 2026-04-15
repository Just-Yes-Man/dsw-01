# Tasks: Login de Empleados en Frontend

**Input**: Design documents from `/specs/001-empleados-login-angular/`  
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: Incluidos (requeridos por FR-012 y CA-008).  
**Organization**: Tareas agrupadas por historia de usuario para implementación y validación independiente.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Ejecutable en paralelo (archivos distintos, sin dependencia directa)
- **[Story]**: `US1`, `US2`, `US3` (solo fases de historias)
- Todas las tareas incluyen ruta exacta de archivo

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar estructura frontend y base de integración con backend

- [X] T001 Crear estructura inicial de frontend Angular 22 en `frontend/` (`frontend/package.json`, `frontend/angular.json`, `frontend/tsconfig.json`, `frontend/src/main.ts`)
- [X] T002 [P] Configurar dependencias de frontend y scripts (`start`, `build`, `test`, `cypress:open`, `cypress:run`) en `frontend/package.json`
- [X] T003 [P] Crear configuración de entornos con URL de API en `frontend/src/environments/environment.ts` y `frontend/src/environments/environment.prod.ts`
- [X] T004 [P] Crear configuración base de Cypress en `frontend/cypress.config.ts` y `frontend/cypress/support/e2e.ts`
- [X] T005 Ajustar `docker-compose.yml` para documentar/soportar ejecución concurrente backend + frontend (si aplica)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Fundaciones de autenticación y seguridad compartidas para todas las historias

**⚠️ CRITICAL**: No comenzar historias sin completar esta fase

- [X] T006 Crear DTOs de auth (`LoginRequest`, `SessionResponse`) en `src/main/java/com/dsw02/empleados/dto/auth/`
- [X] T007 [P] Crear controlador de auth con rutas versionadas en `src/main/java/com/dsw02/empleados/controller/AuthController.java`
- [X] T008 [P] Crear servicio de sesión de auth en `src/main/java/com/dsw02/empleados/service/AuthSessionService.java`
- [X] T009 Configurar política de cookie de sesión (`HttpOnly`, `Secure`, `SameSite`) y expiración configurable en `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T010 [P] Configurar CORS para frontend Angular en `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T011 [P] Ajustar `application.yml` para timeout de sesión configurable por entorno en `src/main/resources/application.yml`
- [X] T012 [P] Publicar contrato OpenAPI de auth en `specs/001-empleados-login-angular/contracts/openapi.yaml` y reflejarlo en configuración OpenAPI runtime (`src/main/java/com/dsw02/empleados/config/OpenApiConfig.java`)
- [X] T013 Crear módulo de autenticación frontend con rutas base en `frontend/src/app/auth/` y `frontend/src/app/app.routes.ts`
- [X] T014 [P] Crear `AuthService` base y modelos (`EmpleadoAuthCredential`, `EmpleadoSession`, `AuthErrorState`) en `frontend/src/app/auth/services/auth.service.ts` y `frontend/src/app/auth/models/*.ts`
- [X] T015 [P] Crear guardia de rutas protegidas en `frontend/src/app/auth/guards/auth.guard.ts`

**Checkpoint**: Infraestructura lista para implementar historias de forma independiente

---

## Phase 3: User Story 1 - Iniciar sesión con correo y contraseña (Priority: P1) 🎯 MVP

**Goal**: Login exitoso con correo/contraseña y redirección a `/empleados`

**Independent Test**: Con credenciales válidas, iniciar sesión y verificar acceso a `/empleados`; acceso directo a ruta protegida sin sesión redirige a `/login`

### Tests for User Story 1

- [X] T016 [P] [US1] Crear prueba de contrato backend para `POST /api/v1/auth/login` en `src/test/java/com/dsw02/empleados/contract/AuthLoginContractTest.java`
- [X] T017 [P] [US1] Crear prueba de integración backend de login exitoso con cookie en `src/test/java/com/dsw02/empleados/integration/AuthLoginIntegrationTest.java`
- [X] T018 [P] [US1] Crear prueba unitaria frontend de `AuthService.login` en `frontend/src/app/auth/services/auth.service.spec.ts`
- [X] T019 [P] [US1] Crear prueba Cypress de login exitoso y redirección a `/empleados` en `frontend/cypress/e2e/auth/login-success.cy.ts`
- [X] T020 [P] [US1] Crear prueba Cypress de protección de ruta sin sesión en `frontend/cypress/e2e/auth/protected-route.cy.ts`

### Implementation for User Story 1

- [X] T021 [US1] Implementar endpoint `POST /api/v1/auth/login` en `src/main/java/com/dsw02/empleados/controller/AuthController.java`
- [X] T022 [US1] Implementar validación de credenciales y creación de sesión en `src/main/java/com/dsw02/empleados/service/AuthSessionService.java`
- [X] T023 [US1] Implementar emisión de cookie segura y payload de sesión en `src/main/java/com/dsw02/empleados/controller/AuthController.java`
- [X] T024 [US1] Implementar pantalla `LoginComponent` en `frontend/src/app/auth/pages/login/login.component.ts` y `frontend/src/app/auth/pages/login/login.component.html`
- [X] T025 [US1] Implementar redirección post-login a `/empleados` en `frontend/src/app/auth/pages/login/login.component.ts`
- [X] T026 [US1] Implementar guardia de ruta para `/empleados` en `frontend/src/app/auth/guards/auth.guard.ts` y `frontend/src/app/app.routes.ts`
- [X] T027 [US1] Crear página protegida mínima de empleados en `frontend/src/app/empleados/pages/empleados-home.component.ts`

**Checkpoint**: US1 funcional de extremo a extremo

---

## Phase 4: User Story 2 - Manejar errores de autenticación (Priority: P2)

**Goal**: Mostrar mensaje genérico único `Credenciales inválidas` en fallos de login

**Independent Test**: Con credenciales inválidas, permanecer en `/login` con mensaje genérico y sin acceso a rutas protegidas

### Tests for User Story 2

- [X] T028 [P] [US2] Crear prueba de contrato backend para `401 UNAUTHORIZED` con mensaje genérico en `src/test/java/com/dsw02/empleados/contract/AuthLoginContractTest.java`
- [X] T029 [P] [US2] Crear prueba de integración backend para credenciales inválidas en `src/test/java/com/dsw02/empleados/integration/AuthLoginIntegrationTest.java`
- [X] T030 [P] [US2] Crear prueba unitaria frontend de mapeo de error en `frontend/src/app/auth/pages/login/login.component.spec.ts`
- [X] T031 [P] [US2] Crear prueba Cypress de login fallido con mensaje genérico en `frontend/cypress/e2e/auth/login-failure.cy.ts`

### Implementation for User Story 2

- [X] T032 [US2] Ajustar respuesta de errores de autenticación en `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T033 [US2] Alinear `GlobalExceptionHandler` para errores de auth en `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T034 [US2] Implementar manejo de error genérico en `frontend/src/app/auth/pages/login/login.component.ts`
- [X] T035 [US2] Implementar mensajes de error UX en `frontend/src/app/auth/pages/login/login.component.html`

**Checkpoint**: US2 funcional e independiente

---

## Phase 5: User Story 3 - Mantener sesión y cerrar sesión (Priority: P3)

**Goal**: Persistir sesión válida entre navegación y permitir logout con invalidación

**Independent Test**: Login válido, navegar por rutas protegidas, ejecutar logout y confirmar redirección a `/login` + bloqueo posterior de rutas

### Tests for User Story 3

- [X] T036 [P] [US3] Crear prueba de contrato para `GET /api/v1/auth/session` y `POST /api/v1/auth/logout` en `src/test/java/com/dsw02/empleados/contract/AuthSessionContractTest.java`
- [X] T037 [P] [US3] Crear prueba de integración backend para expiración/estado de sesión en `src/test/java/com/dsw02/empleados/integration/AuthSessionIntegrationTest.java`
- [X] T038 [P] [US3] Crear prueba unitaria frontend de logout y estado de sesión en `frontend/src/app/auth/services/auth.service.spec.ts`
- [X] T039 [P] [US3] Crear prueba Cypress de logout y bloqueo de rutas en `frontend/cypress/e2e/auth/logout.cy.ts`

### Implementation for User Story 3

- [X] T040 [US3] Implementar endpoint `GET /api/v1/auth/session` en `src/main/java/com/dsw02/empleados/controller/AuthController.java`
- [X] T041 [US3] Implementar endpoint `POST /api/v1/auth/logout` con invalidación de sesión en `src/main/java/com/dsw02/empleados/controller/AuthController.java`
- [X] T042 [US3] Implementar renovación por actividad + expiración configurable en `src/main/java/com/dsw02/empleados/service/AuthSessionService.java`
- [X] T043 [US3] Implementar estado de sesión en frontend (`isAuthenticated`, `checkSession`) en `frontend/src/app/auth/services/auth.service.ts`
- [X] T044 [US3] Implementar acción de logout en UI protegida en `frontend/src/app/empleados/pages/empleados-home.component.ts`

**Checkpoint**: US3 funcional e independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Endurecimiento final, documentación y validación global

- [X] T045 [P] Actualizar documentación de uso en `specs/001-empleados-login-angular/quickstart.md`
- [X] T046 [P] Verificar sincronización de contrato OpenAPI y comportamiento real de endpoints auth en `specs/001-empleados-login-angular/contracts/openapi.yaml`
- [X] T047 [P] Ejecutar suite backend focalizada de auth con Maven
- [X] T048 [P] Ejecutar pruebas unitarias frontend Angular
- [X] T049 [P] Ejecutar suite Cypress completa de auth
- [X] T050 [P] Revisar logs estructurados de login/fallo/logout sin fuga de secretos en `src/main/java/com/dsw02/empleados/config/`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: Sin dependencias
- **Phase 2 (Foundational)**: Depende de Phase 1 y bloquea historias
- **Phase 3 (US1)**: Depende de Phase 2
- **Phase 4 (US2)**: Depende de Phase 2 (y reutiliza flujo de US1)
- **Phase 5 (US3)**: Depende de Phase 2 (y reutiliza base de US1)
- **Phase 6 (Polish)**: Depende de historias completadas

### User Story Dependencies

- **US1 (P1)**: Primer incremento MVP
- **US2 (P2)**: Depende funcionalmente de endpoint/login base de US1
- **US3 (P3)**: Depende de US1 para sesión activa y añade ciclo completo de logout/estado

### Within Each User Story

- Tests primero (deben fallar antes de implementación)
- Backend auth endpoints y servicio
- Frontend servicios/guardias/componentes
- Validación E2E de historia

---

## Parallel Execution Examples

### User Story 1

- Ejecutar en paralelo: T016, T017, T018, T019, T020
- Ejecutar en paralelo: T024 y T027 (componentes frontend distintos)

### User Story 2

- Ejecutar en paralelo: T028, T029, T030, T031
- Ejecutar en paralelo: T034 y T035

### User Story 3

- Ejecutar en paralelo: T036, T037, T038, T039
- Ejecutar en paralelo: T040 y T043

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Setup + Foundational
2. Entregar US1 completa (login + guard + redirección)
3. Validar con pruebas backend + Cypress de US1

### Incremental Delivery

1. US1: login funcional
2. US2: manejo de errores seguro
3. US3: sesión completa + logout
4. Polish: hardening y ejecución global de pruebas

### Team Parallelization

1. Equipo A: backend auth (`AuthController`, `AuthSessionService`, seguridad)
2. Equipo B: frontend auth (`AuthService`, `AuthGuard`, `LoginComponent`)
3. Equipo C: pruebas (`MockMvc`, unitarias Angular, Cypress)
