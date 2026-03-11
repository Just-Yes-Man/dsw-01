# Tasks: CRUD de Empleados

**Input**: Design documents from `/specs/001-crud-empleados/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Se incluyen tareas de pruebas porque la especificación exige evidencia de pruebas automatizadas.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicialización del proyecto y base de ejecución local

- [X] T001 Inicializar proyecto Spring Boot 3 + Java 17 en `pom.xml`
- [X] T002 Crear clase de arranque en `src/main/java/com/dsw02/empleados/EmpleadosApplication.java`
- [X] T003 [P] Configurar servicio PostgreSQL en `docker-compose.yml`
- [X] T004 [P] Definir variables base de entorno en `.env.example`
- [X] T005 Configurar propiedades de aplicación para PostgreSQL, Flyway y Swagger en `src/main/resources/application.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura obligatoria antes de cualquier historia de usuario

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T006 Crear migración inicial de tabla `empleados` con PK compuesta (`prefijo`, `consecutivo`) y `clave` única en `src/main/resources/db/migration/V1__create_empleados.sql`
- [X] T007 [P] Implementar configuración de autenticación básica en `src/main/java/com/dsw02/empleados/config/SecurityConfig.java`
- [X] T008 [P] Configurar usuario/credenciales por variables de entorno en `src/main/java/com/dsw02/empleados/config/SecurityUsersConfig.java`
- [X] T009 [P] Configurar esquema OpenAPI y seguridad Basic Auth en `src/main/java/com/dsw02/empleados/config/OpenApiConfig.java`
- [X] T010 Crear entidad base `Empleado` en `src/main/java/com/dsw02/empleados/entity/Empleado.java`
- [X] T011 [P] Crear repositorio `EmpleadoRepository` en `src/main/java/com/dsw02/empleados/repository/EmpleadoRepository.java`
- [X] T012 Crear manejo global de errores HTTP en `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T013 Configurar logging estructurado para operaciones críticas en `src/main/resources/application.yml`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Registrar empleado (Priority: P1) 🎯 MVP

**Goal**: Permitir alta de empleado con `nombre`, `direccion`, `telefono` y `clave` autogenerada con formato `EMP-<autonumérico>`

**Independent Test**: Crear un empleado válido y verificar respuesta 201 con `clave` en formato `EMP-<autonumérico>`; validar rechazo para longitudes > 100

### Tests for User Story 1

- [X] T014 [P] [US1] Crear prueba de contrato POST `/empleados` en `src/test/java/com/dsw02/empleados/contract/EmpleadoCreateContractTest.java`
- [X] T015 [P] [US1] Crear prueba de integración de alta válida/inválida en `src/test/java/com/dsw02/empleados/integration/EmpleadoCreateIntegrationTest.java`
- [X] T016 [P] [US1] Crear prueba de rechazo cuando cliente envía `clave` en POST `/empleados` en `src/test/java/com/dsw02/empleados/integration/EmpleadoCreateIntegrationTest.java`

### Implementation for User Story 1

- [X] T017 [P] [US1] Crear DTO de alta en `src/main/java/com/dsw02/empleados/dto/EmpleadoCreateRequest.java`
- [X] T018 [P] [US1] Crear DTO de respuesta en `src/main/java/com/dsw02/empleados/dto/EmpleadoResponse.java`
- [X] T019 [US1] Implementar generación de `clave` con prefijo `EMP-` y consecutivo en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T020 [US1] Implementar endpoint POST `/empleados` en `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T021 [US1] Registrar eventos de creación en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T022 [US1] Alinear contrato de creación en `specs/001-crud-empleados/contracts/openapi.yaml`

**Checkpoint**: User Story 1 funcional, autenticada y validable de forma independiente

---

## Phase 4: User Story 2 - Consultar empleados (Priority: P2)

**Goal**: Permitir consulta de listado y detalle por `clave`

**Independent Test**: Obtener listado y detalle de un empleado existente; verificar 404 para `clave` inexistente

### Tests for User Story 2

- [X] T023 [P] [US2] Crear prueba de contrato GET `/empleados` y GET `/empleados/{clave}` en `src/test/java/com/dsw02/empleados/contract/EmpleadoReadContractTest.java`
- [X] T024 [P] [US2] Crear prueba de integración de consulta y no encontrado en `src/test/java/com/dsw02/empleados/integration/EmpleadoReadIntegrationTest.java`

### Implementation for User Story 2

- [X] T025 [US2] Implementar lógica de listado y búsqueda por `clave` tipo `EMP-<autonumérico>` en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T026 [US2] Implementar endpoints GET `/empleados` y GET `/empleados/{clave}` en `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T027 [US2] Mapear error de no encontrado para consultas en `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [X] T028 [US2] Alinear contrato de consultas en `specs/001-crud-empleados/contracts/openapi.yaml`

**Checkpoint**: User Story 1 y User Story 2 funcionan independientemente

---

## Phase 5: User Story 3 - Actualizar y eliminar empleado (Priority: P3)

**Goal**: Permitir actualización de datos y eliminación por `clave`

**Independent Test**: Actualizar un empleado existente y eliminarlo; verificar 404 al operar con `clave` inexistente

### Tests for User Story 3

- [X] T029 [P] [US3] Crear prueba de contrato PUT/DELETE en `src/test/java/com/dsw02/empleados/contract/EmpleadoWriteContractTest.java`
- [X] T030 [P] [US3] Crear prueba de integración de actualización/eliminación en `src/test/java/com/dsw02/empleados/integration/EmpleadoWriteIntegrationTest.java`
- [X] T031 [P] [US3] Crear prueba de rechazo cuando cliente envía `clave` en PUT `/empleados/{clave}` en `src/test/java/com/dsw02/empleados/integration/EmpleadoWriteIntegrationTest.java`

### Implementation for User Story 3

- [X] T032 [P] [US3] Crear DTO de actualización en `src/main/java/com/dsw02/empleados/dto/EmpleadoUpdateRequest.java`
- [X] T033 [US3] Implementar lógica de actualización y eliminación en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T034 [US3] Implementar endpoints PUT `/empleados/{clave}` y DELETE `/empleados/{clave}` en `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [X] T035 [US3] Registrar eventos de actualización y eliminación en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [X] T036 [US3] Alinear contrato de actualización/eliminación en `specs/001-crud-empleados/contracts/openapi.yaml`

**Checkpoint**: Todas las historias de usuario quedan funcionales y probables de forma independiente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Ajustes transversales de calidad y verificación final

- [X] T037 [P] Documentar setup real de ejecución y ejemplos finales en `specs/001-crud-empleados/quickstart.md`
- [X] T038 Validar credenciales y secretos por entorno en `src/main/resources/application.yml`
- [X] T039 [P] Añadir prueba unitaria de validaciones de longitud y formato de clave en `src/test/java/com/dsw02/empleados/unit/EmpleadoValidationTest.java`
- [X] T040 Ejecutar suite completa y registrar comandos usados en `specs/001-crud-empleados/quickstart.md`

---

## Phase 7: Enmienda Constitucional - Versionado y Paginación

**Purpose**: Alinear implementación con la enmienda de constitución v1.1.0

- [ ] T041 [US2] Versionar rutas CRUD a `/api/v1/empleados` en `src/main/java/com/dsw02/empleados/controller/EmpleadoController.java`
- [ ] T042 [US2] Implementar paginación en listado con 10 instancias por consulta en `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- [ ] T043 [P] [US2] Actualizar contrato OpenAPI con metadatos de página en `specs/001-crud-empleados/contracts/openapi.yaml`
- [ ] T044 [P] [US2] Ajustar pruebas de contrato e integración para rutas versionadas y paginación en `src/test/java/com/dsw02/empleados/contract/` y `src/test/java/com/dsw02/empleados/integration/`
- [ ] T045 [US2] Actualizar quickstart con ejemplos de paginación y rutas versionadas en `specs/001-crud-empleados/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sin dependencias, inicio inmediato
- **Foundational (Phase 2)**: Depende de Setup y bloquea historias de usuario
- **User Stories (Phase 3+)**: Dependen de Foundational; pueden ejecutarse en paralelo o por prioridad
- **Polish (Phase 6)**: Depende de historias objetivo completadas

### User Story Dependencies

- **US1 (P1)**: Inicia tras Foundational, define MVP
- **US2 (P2)**: Inicia tras Foundational; no depende funcionalmente de US3
- **US3 (P3)**: Inicia tras Foundational; reutiliza componentes de servicio/controlador

### Within Each User Story

- Pruebas de contrato/integración antes de implementación
- DTOs y reglas de validación antes de endpoints
- Servicio antes de controlador
- Actualización de OpenAPI al cerrar cada historia

### Parallel Opportunities

- **Setup**: T003 y T004 en paralelo
- **Foundational**: T007, T008 y T009 en paralelo; T010 y T011 en paralelo
- **US1**: T014, T015, T016 y T017 en paralelo
- **US2**: T023 y T024 en paralelo
- **US3**: T029, T030 y T032 en paralelo
- **Polish**: T037 y T039 en paralelo

---

## Parallel Example: User Story 1

```bash
# Pruebas iniciales US1
Task: "T014 [US1] Crear prueba de contrato POST /empleados en src/test/java/com/dsw02/empleados/contract/EmpleadoCreateContractTest.java"
Task: "T015 [US1] Crear prueba de integración de alta válida/inválida en src/test/java/com/dsw02/empleados/integration/EmpleadoCreateIntegrationTest.java"

# DTOs US1
Task: "T017 [US1] Crear DTO de alta en src/main/java/com/dsw02/empleados/dto/EmpleadoCreateRequest.java"
Task: "T018 [US1] Crear DTO de respuesta en src/main/java/com/dsw02/empleados/dto/EmpleadoResponse.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational
3. Completar Phase 3: User Story 1
4. Validar alta y restricciones de longitud
5. Demostrar MVP autenticado con Swagger

### Incremental Delivery

1. Setup + Foundational
2. Entregar US1 (alta)
3. Entregar US2 (consultas)
4. Entregar US3 (actualización/eliminación)
5. Aplicar polish y cierre de calidad

### Parallel Team Strategy

1. Equipo completo en Setup + Foundational
2. Luego:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integración y verificación cruzada al final

---

## Notes

- Todas las tareas siguen formato checklist con ID secuencial y ruta de archivo.
- Las tareas `[P]` fueron marcadas solo cuando no comparten archivos ni dependencia directa.
- Cada historia mantiene criterio de prueba independiente.
- Se preserva trazabilidad entre spec, contrato OpenAPI y pruebas automatizadas.
