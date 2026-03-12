# Tasks: CRUD Departamentos

**Input**: Design documents from `/specs/004-crud-departamentos/`
**Prerequisites**: plan.md ✓, spec.md ✓, research.md ✓, data-model.md ✓, contracts/openapi.yaml ✓
**Status**: Implementation-ready - phases are prioritized and can be parallelized within each story

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story (US1, US2, US3) - MVP scope in priority order
- **Exact file paths** included in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and database foundation

- [ ] T001 Create package structure `src/main/java/com/dsw02/departamentos/` with subdirectories: config/, controller/, dto/, entity/, exception/, repository/, service/
- [ ] T002 Create test package structure `src/test/java/com/dsw02/departamentos/` with subdirectories: contract/, integration/, unit/
- [ ] T003 [P] Create Flyway migration file `src/main/resources/db/migration/V3__departamentos_table.sql` with departamentos table, estado enum constraint, and indexes per data-model.md
- [ ] T003b [P] Document migration rollback procedure for V3: create file `specs/004-crud-departamentos/rollback-v3.md` with the rollback SQL (`DROP TABLE IF EXISTS departamentos;`) and steps to execute it, satisfying constitution Gate 6 (schema changes MUST include rollback plan)
- [ ] T004 [P] Verify EstadoAcceso enum exists in `src/main/java/com/dsw02/empleados/entity/EstadoAcceso.java` (reuse from empleados feature)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core shared infrastructure that MUST complete before user stories

**⚠️ CRITICAL**: No user story work begins until Phase 2 completes

- [ ] T005 Create Departamento JPA entity with `@Entity`, `@Table`, `@UniqueConstraint(nombre)`, and fields (id, nombre, estado, creadoEn, actualizadoEn) in `src/main/java/com/dsw02/departamentos/entity/Departamento.java`
- [ ] T006 [P] Create DepartamentoRepository interface extending JpaRepository with custom methods: `findByIdAndEstado()`, `existsByNombreAndEstado()`, `findByEstado()` in `src/main/java/com/dsw02/departamentos/repository/DepartamentoRepository.java`
- [ ] T007 [P] Create DTO classes in `src/main/java/com/dsw02/departamentos/dto/`:
  - DepartamentoCreateRequest (nombre field with @NotEmpty, @Size validation)
  - DepartamentoUpdateRequest (nombre field with @NotEmpty, @Size validation)
  - DepartamentoResponse (id, nombre, estado, timestamps)
- [ ] T008 Create DepartamentoService with dependency injection of DepartamentoRepository in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`
- [ ] T009 [P] Create DepartamentoController with Route annotation for `/api/v1/departamentos` (placeholder methods for all 5 CRUD endpoints) in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`
- [ ] T010 [P] Create custom exception class `DepartamentoConflictException` (NOT `ConstraintViolationException` — that name clashes with `jakarta.validation.ConstraintViolationException` already on the classpath) in `src/main/java/com/dsw02/departamentos/exception/DepartamentoConflictException.java`
- [ ] T011 Configure GlobalExceptionHandler (or extend existing one) to catch validation errors and constraint violations, returning HTTP 400/409 responses with standardized ErrorResponse format in `src/main/java/com/dsw02/empleados/config/GlobalExceptionHandler.java`
- [ ] T012 Verify SecurityConfig in `src/main/java/com/dsw02/empleados/config/SecurityConfig.java` is correctly configured to intercept `/api/v1/departamentos/**` with Basic Auth requirement

**Checkpoint**: Foundation ready - all user story implementation can now begin in parallel across the 3 stories

---

## Phase 3: User Story 1 - Crear Nuevo Departamento (Priority: P1) 🎯 MVP

**Goal**: Allow any authenticated user to create a new department with validation for unique name and length constraints (max 255 chars). Returns HTTP 201 with created departamento details.

**Independent Test**: Can fully test by sending POST request with valid department name, verifying HTTP 201 response with id and estado=ACTIVO. Can also test validation errors (400) and duplicates (409) independently.

### Contract Tests for User Story 1

- [ ] T013 [P] [US1] Create contract test verifying POST `/api/v1/departamentos` accepts DepartamentoCreateRequest and returns DepartamentoResponse with estado=ACTIVO in `src/test/java/com/dsw02/departamentos/contract/DepartamentoCreateContractTest.java`
- [ ] T014 [P] [US1] Create contract test verifying HTTP 400 validation errors (empty name, name > 255 chars) in `src/test/java/com/dsw02/departamentos/contract/DepartamentoCreateContractTest.java`
- [ ] T015 [P] [US1] Create contract test verifying HTTP 409 conflict when duplicate name submitted in `src/test/java/com/dsw02/departamentos/contract/DepartamentoCreateContractTest.java`

### Integration Tests for User Story 1

- [ ] T016 [P] [US1] Create integration test for successful department creation with Spring Boot test harness, verifying database persistence and response in `src/test/java/com/dsw02/departamentos/integration/DepartamentoCreateIntegrationTest.java`
- [ ] T017 [P] [US1] Create integration test for duplicate name constraint violation (HTTP 409) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoCreateIntegrationTest.java`
- [ ] T018 [P] [US1] Create integration test for validation errors (empty/null/too long name → HTTP 400) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoCreateIntegrationTest.java`

### Unit Tests for User Story 1

- [ ] T019 [P] [US1] Create unit test for DepartamentoService.create() method validating inputs and calling repository in `src/test/java/com/dsw02/departamentos/unit/DepartamentoServiceTest.java`
- [ ] T020 [P] [US1] Create unit test for DTO validation annotations (OpenAPI @Schema, Jakarta @Size, @NotEmpty) in `src/test/java/com/dsw02/departamentos/unit/DepartamentoRequestValidationTest.java`

### Implementation for User Story 1

- [ ] T021 [US1] Implement DepartamentoService.create(DepartamentoCreateRequest) method:
  - Validate nombre (not null, not empty, max 255 chars)
  - Check for duplicate nombre via repository
  - If duplicate found, throw DepartamentoConflictException → HTTP 409
  - Create Departamento entity with estado = EstadoAcceso.ACTIVO
  - Persist to database (let Flyway handle timestamps)
  - Return DepartamentoResponse with created entity
  - Add logging for successful creation in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`
  
- [ ] T022 [US1] Implement DepartamentoController.create() POST endpoint:
  - Route: `POST /api/v1/departamentos`
  - Request: @RequestBody DepartamentoCreateRequest with validation
  - Call DepartamentoService.create()
  - Response: HTTP 201 + DepartamentoResponse + Location header with new resource URL
  - Error handling delegated to GlobalExceptionHandler (400, 409)
  - Add @PostMapping, @RequestBody, @Validated annotations in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`
  
- [ ] T023 [US1] Add logging for User Story 1 operations (successful creates, validation errors, constraint violations) using structured logging pattern from empleados in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T024 [US1] Update Swagger/OpenAPI contract for POST endpoint - ensure spec in `specs/004-crud-departamentos/contracts/openapi.yaml` matches implementation (request schema, response schema, error responses)

---

## Phase 4: User Story 2 - Listar y Obtener Departamentos (Priority: P2)

**Goal**: Allow any authenticated user to retrieve departments with pagination (10 per page) and to read a single department. Only return ACTIVO departments in default queries. Full test independently of create/update/delete.

**Independent Test**: Can fully test by querying empty list (returns empty page), creating departments via DB or API, then verifying paginated list returns exactly 10 per page. Can also test 404 for non-existent IDs independently.

### Contract Tests for User Story 2

- [ ] T025 [P] [US2] Create contract test verifying GET `/api/v1/departamentos` returns paginated list (Page object with content, totalElements, totalPages, size=10) in `src/test/java/com/dsw02/departamentos/contract/DepartamentoReadContractTest.java`
- [ ] T026 [P] [US2] Create contract test for empty list response (no departments exist) in `src/test/java/com/dsw02/departamentos/contract/DepartamentoReadContractTest.java`
- [ ] T027 [P] [US2] Create contract test for multiple pages (request page 2 when > 10 departments exist) in `src/test/java/com/dsw02/departamentos/contract/DepartamentoReadContractTest.java`
- [ ] T028 [P] [US2] Create contract test verifying GET `/api/v1/departamentos/{id}` returns single DepartamentoResponse in `src/test/java/com/dsw02/departamentos/contract/DepartamentoReadContractTest.java`
- [ ] T029 [P] [US2] Create contract test verifying HTTP 404 when department ID not found in `src/test/java/com/dsw02/departamentos/contract/DepartamentoReadContractTest.java`

### Integration Tests for User Story 2

- [ ] T030 [P] [US2] Create integration test verifying GET with 0 departments returns empty paginated response in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [ ] T031 [P] [US2] Create integration test for pagination: insert 15 departments, verify page 0 returns 10, page 1 returns 5 in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [ ] T032 [P] [US2] Create integration test verifying GET by ID returns matching department in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [ ] T033 [P] [US2] Create integration test verifying GET by ID returns HTTP 404 for non-existent ID in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [ ] T034 [P] [US2] Create integration test verifying INACTIVE departments are NOT returned in list queries (soft-delete filtering) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`
- [ ] T035 [P] [US2] Create integration test for performance: verify GET queries complete < 500ms p95 for 1000 departments in `src/test/java/com/dsw02/departamentos/integration/DepartamentoReadIntegrationTest.java`

### Unit Tests for User Story 2

- [ ] T036 [P] [US2] Create unit test for DepartamentoService.findAll(Pageable) with pagination logic in `src/test/java/com/dsw02/departamentos/unit/DepartamentoServiceTest.java`
- [ ] T037 [P] [US2] Create unit test for DepartamentoService.findById() returning Optional in `src/test/java/com/dsw02/departamentos/unit/DepartamentoServiceTest.java`
- [ ] T038 [P] [US2] Create unit test verifying estado=ACTIVO filter is applied in repository queries in `src/test/java/com/dsw02/departamentos/unit/DepartamentoRepositoryTest.java`

### Implementation for User Story 2

- [ ] T039 [US2] Implement DepartamentoService.findAll(Pageable) method:
  - Query repository for all ACTIVO departments (WHERE estado = 'ACTIVO')
  - Apply pagination (pageSize fixed at 10 as per constitution)
  - Return Page<DepartamentoResponse> mapped from entities
  - Add logging for list queries in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T040 [US2] Implement DepartamentoService.findById(id) method:
  - Query repository by ID and estado=ACTIVO
  - If not found, throw EntityNotFoundException → HTTP 404
  - Return DepartamentoResponse with found entity
  - Add logging for read operations in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T041 [US2] Implement DepartamentoController.listDepartamentos() GET endpoint:
  - Route: `GET /api/v1/departamentos?page=0` (size is NOT a client parameter — fixed at 10 per constitution)
  - Parameter: @RequestParam(defaultValue="0") int page only; page size MUST be hardcoded as 10
  - Call DepartamentoService.findAll(PageRequest.of(page, 10)) — size is hardcoded, NOT passed from client
  - Response: HTTP 200 + PagedDepartamentoResponse (Page<DepartamentoResponse>)
  - Error handling: delegated to GlobalExceptionHandler
  - Add @GetMapping, @RequestParam annotations in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`

- [ ] T042 [US2] Implement DepartamentoController.getDepartamento() GET by ID endpoint:
  - Route: `GET /api/v1/departamentos/{id}`
  - Parameter: @PathVariable Long id
  - Call DepartamentoService.findById(id)
  - Response: HTTP 200 + DepartamentoResponse
  - Error handling: delegated to GlobalExceptionHandler (404)
  - Add @GetMapping("{id}"), @PathVariable annotations in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`

- [ ] T043 [US2] Add logging for User Story 2 operations (successful reads, 404 errors) in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T044 [US2] Update Swagger/OpenAPI contract for GET endpoints - verify spec in `specs/004-crud-departamentos/contracts/openapi.yaml` matches implementation

---

## Phase 5: User Story 3 - Actualizar y Eliminar Departamentos (Priority: P3)

**Goal**: Allow any authenticated user to update department names (with validation and uniqueness check) and soft-delete departments. Prevent deletion if employees are associated (HTTP 409). Only allow update/delete of ACTIVO departments.

**Independent Test**: Can fully test by creating department, updating name, verifying change, and deleting. Can test prevent-delete-with-employees by mocking employee repository separately.

### Contract Tests for User Story 3

- [ ] T045 [P] [US3] Create contract test verifying PATCH `/api/v1/departamentos/{id}` accepts DepartamentoUpdateRequest and returns updated DepartamentoResponse in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`
- [ ] T046 [P] [US3] Create contract test for PATCH validation errors (empty name, name > 255 chars) returning HTTP 400 in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`
- [ ] T047 [P] [US3] Create contract test for PATCH duplicate name conflict (HTTP 409) in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`
- [ ] T048 [P] [US3] Create contract test verifying DELETE `/api/v1/departamentos/{id}` with no employees returns HTTP 204 in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`
- [ ] T049 [P] [US3] Create contract test verifying DELETE with associated employees returns HTTP 409 Conflict in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`
- [ ] T050 [P] [US3] Create contract test verifying HTTP 404 when updating/deleting non-existent department in `src/test/java/com/dsw02/departamentos/contract/DepartamentoWriteContractTest.java`

### Integration Tests for User Story 3

- [ ] T051 [P] [US3] Create integration test for successful PATCH operation (update nombre and verify in DB) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`
- [ ] T052 [P] [US3] Create integration test for PATCH validation errors (empty/null/too-long name) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`
- [ ] T053 [P] [US3] Create integration test for PATCH duplicate name constraint violation (HTTP 409) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`
- [ ] T054 [P] [US3] Create integration test for successful soft-delete (DELETE marks as INACTIVO, not hard-deleted) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`
- [ ] T055 [P] [US3] Create integration test for DELETE prevention when employees assigned (mock EmpleadoRepository.existsByDepartamentoId() → true, verify HTTP 409) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`
- [ ] T056 [P] [US3] Create integration test verifying PATCH/DELETE return 404 for INACTIVE departments (soft-delete semantics) in `src/test/java/com/dsw02/departamentos/integration/DepartamentoUpdateIntegrationTest.java`

### Unit Tests for User Story 3

- [ ] T057 [P] [US3] Create unit test for DepartamentoService.update() method validating inputs, checking duplicates (excluding current), and persisting changes in `src/test/java/com/dsw02/departamentos/unit/DepartamentoServiceTest.java`
- [ ] T058 [P] [US3] Create unit test for DepartamentoService.delete() method checking employees association and marking as INACTIVO in `src/test/java/com/dsw02/departamentos/unit/DepartamentoServiceTest.java`

### Implementation for User Story 3

- [ ] T059 [US3] Implement DepartamentoService.update(Long id, DepartamentoUpdateRequest) method:
  - Find department by ID and estado=ACTIVO (if not found → throw EntityNotFoundException → 404)
  - Validate nombre (not null, not empty, max 255 chars)
  - Check for duplicate nombre excluding current department (query by nombre AND estado=ACTIVO AND id!=currentId)
  - If duplicate found, throw DepartamentoConflictException → HTTP 409
  - Update entity's nombre field
  - Persist changes (JPA will auto-update actualizadoEn timestamp)
  - Return DepartamentoResponse with updated entity
  - Add logging for update operations in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T060 [US3] Implement DepartamentoService.delete(Long id) method:
  - Find department by ID and estado=ACTIVO (if not found → throw EntityNotFoundException → 404)
  - Check if any employees are associated with this department via EmpleadoRepository.existsByDepartamentoId(id)
  - If true, throw DepartamentoConflictException with message indicating employees exist → HTTP 409
  - Mark department estado = EstadoAcceso.INACTIVO (soft-delete)
  - Persist changes
  - Return void (no body)
  - Add logging for delete operations in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T061 [US3] Implement DepartamentoController.updateDepartamento() PATCH endpoint:
  - Route: `PATCH /api/v1/departamentos/{id}`
  - Parameter: @PathVariable Long id
  - Request: @RequestBody DepartamentoUpdateRequest with validation
  - Call DepartamentoService.update(id, request)
  - Response: HTTP 200 + DepartamentoResponse
  - Error handling: delegated to GlobalExceptionHandler (400, 404, 409)
  - Add @PatchMapping("{id}"), @RequestBody, @Validated annotations in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`

- [ ] T062 [US3] Implement DepartamentoController.deleteDepartamento() DELETE endpoint:
  - Route: `DELETE /api/v1/departamentos/{id}`
  - Parameter: @PathVariable Long id
  - Call DepartamentoService.delete(id)
  - Response: HTTP 204 No Content (no body)
  - Error handling: delegated to GlobalExceptionHandler (404, 409)
  - Add @DeleteMapping("{id}") annotation in `src/main/java/com/dsw02/departamentos/controller/DepartamentoController.java`

- [ ] T063 [US3] Add logging for User Story 3 operations (successful updates/deletes, validation errors, constraint violations) in `src/main/java/com/dsw02/departamentos/service/DepartamentoService.java`

- [ ] T064 [US3] Update Swagger/OpenAPI contract for PATCH/DELETE endpoints - verify spec in `specs/004-crud-departamentos/contracts/openapi.yaml` matches implementation

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, documentation, and integration polish

- [ ] T065 Run full test suite: `mvn test` - verify all contract, integration, and unit tests pass (100% pass rate required per SC-004)

- [ ] T066 [P] Verify all endpoints return standardized error responses via GlobalExceptionHandler with errorCode + message format

- [ ] T067 [P] Add JavaDoc comments to all public methods in DepartamentoService and DepartamentoController explaining inputs, outputs, errors, and examples

- [ ] T068 [P] Ensure Swagger UI endpoint is accessible at http://localhost:8080/swagger-ui.html and displays all 5 CRUD operations with proper request/response schemas

- [ ] T069 [P] Verify department name validation rejects special edge cases: null, empty string, whitespace-only, very long (> 255), non-UTF8 characters if applicable

- [ ] T070 [P] Verify pagination parameter ?page=0&size=10 works correctly and size is always 10 (fixed per constitution)

- [ ] T071 [P] Verify Basic Auth inheritance from SecurityConfig - all endpoints require Authorization header with valid credentials

- [ ] T072 [P] Verify soft-delete semantics: INACTIVE departments are never returned in GET/LIST queries, PATCH/DELETE on INACTIVE department returns 404

- [ ] T073 [P] Verify performance goal met: run load test with 1000 CRUD operations, confirm p95 < 500ms per SC-001

- [ ] T074 [P] Verify database constraints: UNIQUE(nombre) prevents duplicate inserts, CHECK(estado IN ('ACTIVO', 'INACTIVO')) prevents invalid states

- [ ] T075 Commit all implementation code to feature branch `004-crud-departamentos` with meaningful commit messages

- [ ] T076 Create pull request from `004-crud-departamentos` → `develop` with test evidence and summary of all 5 CRUD operations

- [ ] T077 [P] Update project-level README.md with reference to Departamentos API and link to Swagger documentation

- [ ] T078 [P] Verify Flyway migration V3__departamentos_table.sql executes successfully on clean database (run with `mvn flyway:clean flyway:migrate`)

---

## Task Execution Guide

### Suggested Implementation Order (Maximizes Parallelization)

**Phase 1 (Setup)**: T001-T004 (sequential, all small)
↓
**Phase 2 (Foundation)**: T005-T012 (can parallelize T006, T007, T009, T010 after T005)
↓  
**Phases 3, 4, 5 (User Stories)**: Can execute IN PARALLEL across the 3 stories:
- T013-T024 (US1 Create) in parallel with T025-T044 (US2 List/Read) in parallel with T045-T064 (US3 Update/Delete)
- Each story's tests (contract/integration/unit) should build before implementation tasks
↓
**Phase 6 (Polish)**: After all stories complete, run T065-T078 for validation and finalization

### Estimated Effort

| Phase | Tasks | Est. Time | Dependencies |
|-------|-------|-----------|--------------|
| **Phase 1** | T001-T004 | 1-2 hours | None |
| **Phase 2** | T005-T012 | 2-3 hours | Phase 1 |
| **Phase 3** | T013-T024 | 4-6 hours | Phase 2 |
| **Phase 4** | T025-T044 | 5-7 hours | Phase 2 (can start with Phase 3) |
| **Phase 5** | T045-T064 | 5-7 hours | Phase 2 (can start with Phase 3) |
| **Phase 6** | T065-T078 | 2-3 hours | All phases |
| **TOTAL** | **78 tasks** | **20-30 hours** | Sequential phases; parallel stories |

### Test-First Workflow (Recommended)

For each user story, implement in this order:
1. Contract tests (T013-T015, T025-T029, T045-T050)
2. Integration tests (T016-T018, T030-T035, T051-T056)
3. Unit tests (T019-T020, T036-T038, T057-T058)
4. Implementation (T021-T024, T039-T044, T059-T064)
5. Full test run: `mvn test`
6. Manual testing via curl/Postman
7. Update Swagger contract (T024, T044, T064)

---

## Success Criteria Validation

| Criterion | Validation Task |
|-----------|-----------------|
| **SC-001** (< 500ms p95) | T035, T073 - Performance integration tests |
| **SC-002** (100% validation) | T014, T018, T020, T046, T052, T057 - Validation tests |
| **SC-003** (10/page pagination) | T025-T031 - Pagination tests |
| **SC-004** (100% test pass) | T065 - Full test suite pass |

---

## Dependencies & Blocked Tasks

**Blocker**: Phase 2 must complete before any Phase 3/4/5 tasks begin
- T021-T024 (US1) require T005-T012 ✓
- T039-T044 (US2) require T005-T012 ✓
- T059-T064 (US3) require T005-T012 ✓

**Optional Dependencies** (within phases):
- T022 can run in parallel with T021 (both US1 create)
- T041 can run in parallel with T040 (both US2 read)
- T061 can run in parallel with T060 (both US3 write)

---

## Testing Strategy

### Contract Tests (Swagger Compliance)
Verify endpoints match OpenAPI spec in `specs/004-crud-departamentos/contracts/openapi.yaml`
- Request schemas accepted
- Response schemas returned
- HTTP status codes correct
- Error responses formatted

### Integration Tests (Spring Boot Slice)
Test with real database (H2 in memory) + MockMvc
- Full request/response cycle
- Database persistence verified
- Pagination with real data
- Authentication security enforced

### Unit Tests (Service/Repository)
Test business logic in isolation
- Validation logic
- Duplicate detection
- Entity mapping to DTOs
- Repository queries
- Soft-delete filtering

---

## Definition of Done (per Task)

A task is COMPLETE when:

1. ✅ Code written per specification and design docs
2. ✅ Tests written (if task includes tests)
3. ✅ All tests passing: `mvn test`
4. ✅ Code follows project conventions (same style as empleados feature)
5. ✅ No compiler warnings or lint errors
6. ✅ Logging added for debugging
7. ✅ File paths match exactly as specified
8. ✅ Endpoints return spec-compliant responses
9. ✅ Error handling delegated to GlobalExceptionHandler  
10. ✅ Database migration included (if data layer task)
11. ✅ Swagger contract updated (if API endpoint task)
12. ✅ Committed to feature branch with meaningful message

---

## Feature Ready for Delivery When

- ✅ Phase 1 (Setup) complete
- ✅ Phase 2 (Foundation) complete with constitution check PASS
- ✅ All 3 user stories (Phases 3, 4, 5) complete
- ✅ All 78 tasks marked `[X]` complete
- ✅ All tests passing: `mvn test` → 0 failures
- ✅ Performance validated: p95 < 500ms
- ✅ Manual testing via API verified
- ✅ Swagger documentation accessible
- ✅ PR reviewed and approved
- ✅ Merged to develop/release/master per git workflow
