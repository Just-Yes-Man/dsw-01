# Tasks: CRUD Departamentos + Relación 1:N con Empleados

**Input**: Design documents from `/specs/004-crud-departamentos/`  
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: Incluidos porque la especificación exige pruebas automatizadas (CA-006).  
**Organization**: Tareas agrupadas por historia de usuario para implementación y validación independiente.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Ejecutable en paralelo (archivos distintos, sin dependencia directa)
- **[Story]**: US1, US2, US3 (solo fases de historias)
- Cada tarea incluye ruta exacta de archivo

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Alinear base técnica y contrato de la feature

- [X] T001 Verificar estructura de paquetes objetivo en `src/main/java/com/dsw02/empleados/departamentos/`
- [X] T002 Verificar estructura de pruebas en `src/test/java/com/dsw02/empleados/departamentos/`
- [X] T003 [P] Validar migraciones Flyway activas para departamentos/empleados en `src/main/resources/db/migration/` y documentar rollback en `specs/004-crud-departamentos/rollback.md`
- [X] T004 [P] Validar configuración de autenticación básica para rutas `/api/v1/**` en `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T005 [P] Confirmar contrato base del feature en `specs/004-crud-departamentos/contracts/openapi.yaml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura compartida obligatoria antes de implementar historias

**⚠️ CRITICAL**: Ninguna historia inicia sin completar esta fase

- [X] T006 Consolidar DTOs base de departamentos en `src/main/java/com/dsw02/empleados/departamentos/dto/`
- [X] T007 [P] Consolidar entidad y repositorio de departamentos en `src/main/java/com/dsw02/empleados/departamentos/entity/Departamento.java` y `src/main/java/com/dsw02/empleados/departamentos/repository/DepartamentoRepository.java`
- [X] T008 [P] Agregar consultas compartidas de empleados por departamento en `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T009 Implementar/ajustar manejo global de errores 400/401/404/409 en `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T010 [P] Alinear tipos de respuesta paginada de departamentos en `src/main/java/com/dsw02/empleados/departamentos/dto/`
- [X] T011 [P] Alinear mapeadores compartidos de empleados/departamentos en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java` y `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T012 Sincronizar contrato OpenAPI de componentes comunes (`ErrorResponse`, schemas compartidos) en `specs/004-crud-departamentos/contracts/openapi.yaml`

**Checkpoint**: Base lista; historias pueden avanzar en paralelo por prioridad

---

## Phase 3: User Story 1 - Crear Nuevo Departamento (Priority: P1) 🎯 MVP

**Goal**: Crear departamentos activos con validación estricta y unicidad.

**Independent Test**: `POST /api/v1/departamentos` devuelve 201 para payload válido, 400 para inválido y 409 para nombre duplicado.

### Tests for User Story 1

- [X] T013 [P] [US1] Crear contrato de creación exitosa en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoCreateContractTest.java`
- [X] T014 [P] [US1] Crear contrato de validación 400 (vacío/null/>255) en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoCreateContractTest.java`
- [X] T015 [P] [US1] Crear contrato de conflicto 409 por nombre duplicado en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoCreateContractTest.java`
- [X] T016 [P] [US1] Crear integración de persistencia de alta de departamento en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoCreateIntegrationTest.java`

### Implementation for User Story 1

- [X] T017 [US1] Implementar validación y unicidad de create en `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T018 [US1] Implementar endpoint `POST /api/v1/departamentos` en `src/main/java/com/dsw02/empleados/departamentos/controller/DepartamentoController.java`
- [X] T019 [US1] Ajustar respuesta create (`estado=ACTIVO`, timestamps) en `src/main/java/com/dsw02/empleados/departamentos/dto/DepartamentoResponse.java`
- [X] T020 [US1] Actualizar contrato OpenAPI de creación de departamento en `specs/004-crud-departamentos/contracts/openapi.yaml`

**Checkpoint**: US1 funcional e independiente

---

## Phase 4: User Story 2 - Listar Departamentos y Ver Detalle con Empleados (Priority: P2)

**Goal**: Listar departamentos activos (10 por página) y obtener detalle por id con empleados embebidos (máximo 50).

**Independent Test**: `GET /api/v1/departamentos?page=0` respeta size fijo 10 y `GET /api/v1/departamentos/{id}` incluye `empleados[]` acotado a 50.

### Tests for User Story 2

- [X] T021 [P] [US2] Crear contrato de listado paginado fijo en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoReadContractTest.java`
- [X] T022 [P] [US2] Crear contrato de detalle por id con empleados embebidos en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoReadContractTest.java`
- [X] T023 [P] [US2] Crear contrato 404 para departamento inexistente/inactivo en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoReadContractTest.java`
- [X] T024 [P] [US2] Crear integración de paginación (10 por página) en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [X] T025 [P] [US2] Crear integración de embebido de empleados con límite 50 en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoReadIntegrationTest.java`

### Implementation for User Story 2

- [X] T026 [US2] Implementar listado de departamentos activos con page-size fijo 10 en `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T027 [US2] Implementar query eficiente de empleados por departamento (evitar N+1) en `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T028 [US2] Implementar detalle de departamento con `empleados[]` (cap 50) en `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T029 [US2] Implementar endpoints `GET /api/v1/departamentos` y `GET /api/v1/departamentos/{id}` en `src/main/java/com/dsw02/empleados/departamentos/controller/DepartamentoController.java`
- [X] T030 [US2] Definir DTO de detalle con empleados embebidos en `src/main/java/com/dsw02/empleados/departamentos/dto/DepartamentoDetailResponse.java`
- [X] T031 [US2] Actualizar contrato OpenAPI de listado/detalle de departamentos en `specs/004-crud-departamentos/contracts/openapi.yaml`

**Checkpoint**: US2 funcional e independiente

---

## Phase 5: User Story 3 - Actualizar/Eliminar Departamento y Validar Relación en Empleados (Priority: P3)

**Goal**: Actualizar y soft-delete de departamentos con regla 409 si existen empleados asociados, y exigir `departamentoId` válido en create/update de empleado con respuesta embebida.

**Independent Test**: PATCH/DELETE de departamentos cumple 200/204/404/409 y create/update de empleado exige `departamentoId`, devuelve 404 para referencia inválida e incluye `departamento` embebido.

### Tests for User Story 3

- [X] T032 [P] [US3] Crear contrato PATCH departamento (200/400/404/409) en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoWriteContractTest.java`
- [X] T033 [P] [US3] Crear contrato DELETE departamento (204/404/409) en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoWriteContractTest.java`
- [X] T034 [P] [US3] Crear integración de soft-delete y exclusión de INACTIVO en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoWriteIntegrationTest.java`
- [X] T035 [P] [US3] Crear integración de bloqueo 409 al borrar departamento con empleados en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoWriteIntegrationTest.java`
- [X] T036 [P] [US3] Crear contrato de empleado create/update con `departamentoId` obligatorio en `src/test/java/com/dsw02/empleados/contract/EmpleadoCreateContractTest.java` y `src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java`
- [X] T037 [P] [US3] Crear integración de empleado con departamento válido/inválido/inactivo en `src/test/java/com/dsw02/empleados/integration/EmpleadoCreateIntegrationTest.java` y `src/test/java/com/dsw02/empleados/integration/EmpleadoWriteIntegrationTest.java`
- [X] T038 [P] [US3] Crear pruebas de lectura de empleado con objeto `departamento` embebido en `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java` y `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 3

- [X] T039 [US3] Implementar update de departamento (unicidad, validaciones, 404 para INACTIVO/no existente) en `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T040 [US3] Implementar soft-delete con verificación `existsByDepartamentoId` y conflicto 409 en `src/main/java/com/dsw02/empleados/departamentos/service/DepartamentoService.java`
- [X] T041 [US3] Implementar endpoints `PATCH` y `DELETE` de departamentos en `src/main/java/com/dsw02/empleados/departamentos/controller/DepartamentoController.java`
- [X] T042 [US3] Exigir `departamentoId` no nulo y positivo en `src/main/java/com/dsw02/empleados/dto/EmpleadoCreateRequest.java` y `src/main/java/com/dsw02/empleados/dto/EmpleadoUpdateRequest.java`
- [X] T043 [US3] Validar departamento ACTIVO en create/update de empleado en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T044 [US3] Reemplazar `departamentoId` en respuesta por objeto `departamento` embebido en `src/main/java/com/dsw02/empleados/dto/EmpleadoResponse.java` y `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T045 [US3] Actualizar contrato OpenAPI para respuestas de empleado y flujos write de departamento en `specs/004-crud-departamentos/contracts/openapi.yaml`

**Checkpoint**: US3 funcional e independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Cierre de calidad, documentación y validación E2E

- [X] T046 [P] Ejecutar suite completa de pruebas en repositorio con `mvn test`
- [X] T047 [P] Verificar quickstart E2E (Docker + curl) en `specs/004-crud-departamentos/quickstart.md`
- [X] T048 [P] Verificar performance objetivo p95 < 500ms en pruebas de integración de lectura/escritura en `src/test/java/com/dsw02/empleados/departamentos/integration/`
- [X] T049 [P] Revisar y alinear manejo de errores estándar (`code`, `message`) en `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T050 [P] Ajustar documentación final de feature en `specs/004-crud-departamentos/spec.md`, `specs/004-crud-departamentos/plan.md` y `specs/004-crud-departamentos/quickstart.md`
- [X] T051 [P] Agregar pruebas contract de respuesta 401 sin credenciales en `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoCreateContractTest.java`, `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoReadContractTest.java` y `src/test/java/com/dsw02/empleados/departamentos/contract/DepartamentoWriteContractTest.java`
- [X] T052 [P] Agregar pruebas integration de respuesta 401 sin credenciales en `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoCreateIntegrationTest.java`, `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoReadIntegrationTest.java` y `src/test/java/com/dsw02/empleados/departamentos/integration/DepartamentoWriteIntegrationTest.java`
- [X] T053 [P] Agregar pruebas 401 para endpoints de empleados impactados en `src/test/java/com/dsw02/empleados/contract/EmpleadoCreateContractTest.java`, `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java` y `src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: sin dependencias
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea historias
- **Phase 3/4/5 (US1/US2/US3)**: dependen de Phase 2
- **Phase 6 (Polish)**: depende de completar historias objetivo

### User Story Dependencies

- **US1 (P1)**: inicia al terminar Foundational; no depende de otras historias
- **US2 (P2)**: inicia al terminar Foundational; usa base de departamentos de US1 pero es testeable de forma independiente
- **US3 (P3)**: inicia al terminar Foundational; integra reglas de departamentos y empleados

### Within Each User Story

- Tests primero (deben fallar antes de implementar)
- DTOs/Modelos antes de servicios
- Servicios antes de controladores
- Contrato OpenAPI actualizado al cerrar cada historia

---

## Parallel Execution Examples

### User Story 1

- Ejecutar en paralelo T013, T014, T015, T016 (todos sobre tests de create)
- Ejecutar T017 y T019 en paralelo parcial; T018 depende de T017

### User Story 2

- Ejecutar en paralelo T021, T022, T023, T024, T025
- Ejecutar T027 y T030 en paralelo; T028 depende de ambos

### User Story 3

- Ejecutar en paralelo T032, T033, T034, T035, T036, T037, T038
- Ejecutar T039 y T042 en paralelo; T043 depende de T042
- T044 depende de T043; T041 depende de T039/T040

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1 + Phase 2
2. Completar US1 (T013-T020)
3. Validar flujo create y errores de negocio
4. Publicar incremento MVP

### Incremental Delivery

1. US1: alta de departamentos
2. US2: listado y detalle con embebidos limitados
3. US3: update/delete + validaciones de relación en empleados
4. Polish final y validación quickstart

### Suggested Team Parallelization

1. Equipo completo en Setup/Foundational
2. Luego dividir por historia:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integrar con Phase 6 de hardening y validación final

---

## Validation Checklist

- Todas las tareas usan formato `- [X] T### [P] [US#] Descripción con archivo`
- IDs secuenciales sin huecos (`T001` ... `T053`)
- Fases separadas: Setup, Foundational, historias por prioridad, Polish
- Cada historia tiene criterio de prueba independiente
- Se incluyen oportunidades de paralelización y dependencias
