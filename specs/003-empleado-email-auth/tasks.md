# Tasks: Autenticación de Empleados por Correo

**Input**: Design documents from `/specs/003-empleado-email-auth/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Se incluyen tareas de pruebas porque la especificación exige evidencia de pruebas automatizadas (CA-006).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar base de datos, DTOs y estructura compartida para autenticación por correo

- [X] T001 Create migration for `email`, `password`, `estado_acceso` and lockout table in `src/main/resources/db/migration/V2__empleado_email_auth.sql`
- [X] T002 [P] Add `EstadoAcceso` enum in `src/main/java/com/dsw02/empleados/entity/EstadoAcceso.java`
- [X] T003 [P] Add lockout entity model in `src/main/java/com/dsw02/empleados/entity/BloqueoAutenticacion.java`
- [X] T004 [P] Add lockout composite key model in `src/main/java/com/dsw02/empleados/entity/BloqueoAutenticacionId.java`
- [X] T005 Create lockout repository in `src/main/java/com/dsw02/empleados/repository/BloqueoAutenticacionRepository.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura de seguridad y persistencia que bloquea todas las historias

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T006 Extend employee entity with `email`, `password`, `estadoAcceso` in `src/main/java/com/dsw02/empleados/entity/Empleado.java`
- [X] T007 [P] Add `findByEmail` and `existsByEmail` repository methods in `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T008 [P] Implement DB-backed UserDetails lookup by email in `src/main/java/com/dsw02/empleados/service/EmpleadoUserDetailsService.java`
- [X] T009 Implement lockout policy service (5 fails, 15 min) in `src/main/java/com/dsw02/empleados/service/AuthLockoutService.java`
- [X] T010 Integrate employee auth provider and lockout pre-check in `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T011 [P] Replace static user config with employee-backed authentication wiring in `src/main/java/com/dsw02/empleados/config/SecurityUsersConfig.java`
- [X] T012 Add `423 LOCKED` error mapping in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T013 [P] Add lockout-related custom exception in `src/main/java/com/dsw02/empleados/service/AccountTemporarilyLockedException.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Iniciar sesión con correo y contraseña (Priority: P1) 🎯 MVP

**Goal**: Permitir autenticación Basic con correo/contraseña de empleado activo

**Independent Test**: Crear empleado activo con correo/contraseña y verificar que `GET /api/v1/empleados?page=0` responde 200 con esas credenciales y 401 con contraseña inválida

### Tests for User Story 1

- [X] T014 [P] [US1] Update read contract tests for employee credentials in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T015 [P] [US1] Add integration test for successful employee-authenticated access in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T016 [P] [US1] Add integration test for invalid employee password rejection in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 1

- [X] T017 [US1] Add `email`, `password`, `estadoAcceso` fields to create request DTO in `src/main/java/com/dsw02/empleados/dto/EmpleadoCreateRequest.java`
- [X] T018 [US1] Add `email`, `password`, `estadoAcceso` fields to update request DTO in `src/main/java/com/dsw02/empleados/dto/EmpleadoUpdateRequest.java`
- [X] T019 [US1] Expose `email` and `estadoAcceso` in employee response DTO in `src/main/java/com/dsw02/empleados/dto/EmpleadoResponse.java`
- [X] T020 [US1] Persist employee credentials and default access status in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T021 [US1] Keep versioned CRUD endpoints compatible with auth payload changes in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T022 [US1] Update employee auth schema and examples in `specs/003-empleado-email-auth/contracts/openapi.yaml`

**Checkpoint**: User Story 1 fully functional and independently testable

---

## Phase 4: User Story 2 - Proteger operaciones según autenticación (Priority: P2)

**Goal**: Aplicar rechazo sin credenciales válidas y bloqueo temporal por intentos fallidos

**Independent Test**: Ejecutar 5 intentos fallidos consecutivos por correo y verificar bloqueo temporal de 15 minutos (`423`), luego validar acceso exitoso tras expiración/reinicio de contador

### Tests for User Story 2

- [X] T023 [P] [US2] Add contract assertions for `423 LOCKED` responses in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T024 [P] [US2] Add integration test for lockout after 5 failed attempts in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T025 [P] [US2] Add integration test for lockout reset after successful authentication in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T026 [P] [US2] Add integration test for lockout expiration after 15 minutes using controllable time source in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T027 [P] [US2] Add contract assertions for `423 LOCKED` on protected write endpoints in `src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java`

### Implementation for User Story 2

- [X] T028 [US2] Persist failed-attempt counters and block timestamp in `src/main/java/com/dsw02/empleados/service/AuthLockoutService.java`
- [X] T029 [US2] Introduce controllable time source for lockout window validation in `src/main/java/com/dsw02/empleados/service/AuthLockoutService.java`
- [X] T030 [US2] Enforce lockout decision in authentication flow in `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T031 [US2] Map lockout exception to JSON `LOCKED` payload in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T032 [US2] Add structured logs for `SUCCESS`, `INVALID_CREDENTIALS` and `TEMPORARY_LOCK` in `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T033 [US2] Document `423` responses across all protected endpoints in `specs/003-empleado-email-auth/contracts/openapi.yaml`

**Checkpoint**: User Story 1 and User Story 2 both work independently

---

## Phase 5: User Story 3 - Gestionar credenciales de forma consistente (Priority: P3)

**Goal**: Validar formato/duplicidad de correo y estado de acceso para garantizar coherencia de credenciales

**Independent Test**: Crear/actualizar empleados con email inválido y duplicado (400), y autenticar empleado inactivo (401)

### Tests for User Story 3

- [X] T034 [P] [US3] Add contract tests for email format and uniqueness validation in `src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java`
- [X] T035 [P] [US3] Add integration test for duplicate email rejection in `src/test/java/com/dsw02/empleados/integration/EmpleadoWriteIntegrationTest.java`
- [X] T036 [P] [US3] Add integration test for inactive account authentication rejection in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 3

- [X] T037 [US3] Add Bean Validation rules for email/password/access status in `src/main/java/com/dsw02/empleados/dto/EmpleadoCreateRequest.java`
- [X] T038 [US3] Mirror validation rules for credential updates in `src/main/java/com/dsw02/empleados/dto/EmpleadoUpdateRequest.java`
- [X] T039 [US3] Enforce unique email and inactive-account behavior in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T040 [US3] Add repository checks supporting credential consistency rules in `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T041 [US3] Sync credential validation and `estadoAcceso` contract details in `specs/003-empleado-email-auth/contracts/openapi.yaml`

**Checkpoint**: All user stories are independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de calidad, documentación y validación integral

- [ ] T042 [P] Add unit tests for lockout counters and email validators in `src/test/java/com/dsw02/empleados/unit/EmpleadoValidationTest.java`
- [X] T043 [P] Add integration regression assertions for collection pagination invariants in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T044 Update quickstart with final auth/lockout test evidence in `specs/003-empleado-email-auth/quickstart.md`
- [X] T045 Run full test suite and record execution evidence in `specs/003-empleado-email-auth/quickstart.md`
- [ ] T046 Define and document HTTPS enforcement for non-local environments in `specs/003-empleado-email-auth/quickstart.md` and `src/main/resources/application.yml`
- [X] T047 [P] Add bootstrap credential strategy documentation (env-based, deshabilitable) in `specs/003-empleado-email-auth/quickstart.md` and `src/main/resources/application.yml`
- [X] T048 Define and execute explicit performance check for auth p95 < 2s and document evidence in `specs/003-empleado-email-auth/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: Depend on Foundational completion; execute in priority order for MVP
- **Polish (Phase 6)**: Depends on target user stories completion

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2 and delivers MVP authentication with employee credentials
- **US2 (P2)**: Depends on US1 authentication flow and extends behavior with lockout control
- **US3 (P3)**: Depends on US1 credential fields and hardens consistency/validation rules

### Parallel Opportunities

- **Setup**: T002, T003 y T004 can run in parallel
- **Foundational**: T007, T008, T011 y T013 can run in parallel after T006
- **US1**: T014, T015 y T016 can run in parallel before T017-T022
- **US2**: T023, T024, T025, T026 y T027 can run in parallel
- **US3**: T034, T035 y T036 can run in parallel
- **Polish**: T042, T043 y T047 can run in parallel

---

## Parallel Example: User Story 1

```bash
Task: "T014 [US1] Update read contract tests for employee credentials in src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java"
Task: "T015 [US1] Add integration test for successful employee-authenticated access in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T016 [US1] Add integration test for invalid employee password rejection in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
```

## Parallel Example: User Story 2

```bash
Task: "T023 [US2] Add contract assertions for 423 LOCKED responses in src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java"
Task: "T024 [US2] Add integration test for lockout after 5 failed attempts in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T025 [US2] Add integration test for lockout reset after successful authentication in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T026 [US2] Add integration test for lockout expiration after 15 minutes using controllable time source in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T027 [US2] Add contract assertions for 423 LOCKED on protected write endpoints in src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java"
```

## Parallel Example: User Story 3

```bash
Task: "T034 [US3] Add contract tests for email format and uniqueness validation in src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java"
Task: "T035 [US3] Add integration test for duplicate email rejection in src/test/java/com/dsw02/empleados/integration/EmpleadoWriteIntegrationTest.java"
Task: "T036 [US3] Add integration test for inactive account authentication rejection in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate employee-authenticated access with versioned routes and paginated collection response

### Incremental Delivery

1. Deliver US1 (employee login with Basic Auth)
2. Deliver US2 (lockout and security hardening)
3. Deliver US3 (credential consistency and inactive-account behavior)
4. Close with Phase 6 quality checks and evidence
