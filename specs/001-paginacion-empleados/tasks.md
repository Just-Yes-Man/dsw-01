# Tasks: Paginación de Consultas de Empleados

**Input**: Design documents from `/specs/001-paginacion-empleados/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Se incluyen tareas de pruebas porque la especificación exige evidencia de pruebas automatizadas (CA-007).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar estructura compartida para paginación y rutas versionadas

- [X] T001 Create paginated response DTO `EmpleadoPageResponse` in `src/main/java/com/dsw02/empleados/dto/EmpleadoPageResponse.java`
- [X] T002 [P] Add paginated query method signature in `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T003 [P] Add request-param validation handling for `page` in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T004 Add shared page-size constant and mapper scaffolding in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Base técnica obligatoria antes de implementar historias de usuario

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T005 Version GET endpoints to `/api/v1/empleados` in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T006 Add `page` query parameter with `@Min(0)` validation in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T007 Implement paged and sorted repository call (clave ASC, size 10) in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T008 Keep detail query behavior unchanged under versioned route in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T009 [P] Align security path expectations for versioned API in `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T010 [P] Prepare common integration test data setup utility in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Consultar primera página (Priority: P1) 🎯 MVP

**Goal**: Entregar listado paginado de empleados con tamaño fijo 10 y metadatos `{page,size,totalElements,items}`

**Independent Test**: Invocar `GET /api/v1/empleados` sin `page` y verificar status 200, `size=10`, `items<=10` y estructura de sobre paginado

### Tests for User Story 1

- [X] T011 [P] [US1] Update contract test for default paginated listing in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T012 [P] [US1] Add integration test for first page and response envelope in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 1

- [X] T013 [US1] Implement `findAll(int page)` returning `EmpleadoPageResponse` in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T014 [US1] Return paginated envelope from GET collection endpoint in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T015 [US1] Ensure default `page=0` handling in collection endpoint in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T016 [US1] Update paginated collection contract schema in `specs/001-paginacion-empleados/contracts/openapi.yaml`

**Checkpoint**: User Story 1 fully functional and independently testable

---

## Phase 4: User Story 2 - Navegar páginas (Priority: P2)

**Goal**: Permitir navegación de páginas consecutivas con orden estable por `clave` y manejo de página fuera de rango

**Independent Test**: Invocar `GET /api/v1/empleados?page=1` y `page=2` con dataset >20, verificando orden ascendente, no duplicados posicionales y out-of-range vacío

### Tests for User Story 2

- [X] T017 [P] [US2] Add contract assertions for `page` query parameter in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T018 [P] [US2] Add integration test for page navigation and non-overlap in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T019 [P] [US2] Add integration test for out-of-range page returning empty `items` in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 2

- [X] T020 [US2] Enforce deterministic sort by `clave` ascending in paged query in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T021 [US2] Return empty `items` while preserving metadata for out-of-range pages in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T022 [US2] Update quickstart with `page=1`, `page=2` and out-of-range examples in `specs/001-paginacion-empleados/quickstart.md`

**Checkpoint**: User Story 1 and User Story 2 both work independently

---

## Phase 5: User Story 3 - Compatibilidad de consultas puntuales (Priority: P3)

**Goal**: Mantener la consulta individual por `clave` como recurso único no paginado en ruta versionada

**Independent Test**: Invocar `GET /api/v1/empleados/{clave}` y validar respuesta singular `EmpleadoResponse` sin metadatos de paginación

### Tests for User Story 3

- [X] T023 [P] [US3] Update contract test for versioned detail endpoint in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T024 [P] [US3] Add integration regression test for non-paginated detail response in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 3

- [X] T025 [US3] Keep detail endpoint response type as `EmpleadoResponse` in `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T026 [US3] Preserve not-found behavior for detail endpoint in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T027 [US3] Confirm detail endpoint schema remains singular in `specs/001-paginacion-empleados/contracts/openapi.yaml`

**Checkpoint**: All user stories are independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de calidad, contrato y evidencia reproducible

- [X] T028 [P] Add/adjust unit test for `EmpleadoPageResponse` mapping and page metadata in `src/test/java/com/dsw02/empleados/unit/EmpleadoValidationTest.java`
- [X] T029 Validate error payload for negative `page` requests in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T030 [P] Sync final feature documentation in `specs/001-paginacion-empleados/spec.md` and `specs/001-paginacion-empleados/quickstart.md`
- [X] T031 Run full test suite and record execution command in `specs/001-paginacion-empleados/quickstart.md`
- [X] T032 Implement structured logging for paginated queries (`page`, `size`, `totalElements`, `itemsCount`) in `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T033 [P] Add integration test assertions for unauthorized access (`401`) on `GET /api/v1/empleados` and `GET /api/v1/empleados/{clave}` in `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T034 [P] Add contract test assertions for unauthorized access (`401`) on versioned query endpoints in `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T035 Add structured logging verification for authentication failures in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T036 Define and execute performance check for paginated endpoint (p95 < 2s) and document evidence in `specs/001-paginacion-empleados/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: Depend on Foundational completion; execute in priority order for MVP
- **Polish (Phase 6)**: Depends on target user stories completion

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2 and defines MVP for paginated collection
- **US2 (P2)**: Depends on US1 collection envelope and extends behavior to multi-page navigation
- **US3 (P3)**: Depends on versioned routes from Phase 2 and protects detail-query compatibility

### Parallel Opportunities

- **Setup**: T002 and T003 can run in parallel
- **Foundational**: T009 and T010 can run in parallel after T005/T006
- **US1**: T011 and T012 run in parallel before T013/T014
- **US2**: T017, T018 and T019 run in parallel
- **US3**: T023 and T024 run in parallel
- **Polish**: T028, T030, T033 and T034 run in parallel

---

## Parallel Example: User Story 1

```bash
Task: "T011 [US1] Update contract test for default paginated listing in src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java"
Task: "T012 [US1] Add integration test for first page and response envelope in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
```

## Parallel Example: User Story 2

```bash
Task: "T017 [US2] Add contract assertions for page query parameter in src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java"
Task: "T018 [US2] Add integration test for page navigation and non-overlap in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T019 [US2] Add integration test for out-of-range page returning empty items in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
```

## Parallel Example: User Story 3

```bash
Task: "T023 [US3] Update contract test for versioned detail endpoint in src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java"
Task: "T024 [US3] Add integration regression test for non-paginated detail response in src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate MVP with versioned route and paginated envelope

### Incremental Delivery

1. Deliver US1 (first paginated page)
2. Deliver US2 (multi-page navigation and out-of-range behavior)
3. Deliver US3 (detail compatibility)
4. Close with Phase 6 quality checks
