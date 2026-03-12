# Feature Specification: CRUD Departamentos

**Feature Branch**: `004-crud-departamentos`  
**Created**: 12 de marzo de 2026  
**Status**: Draft  
**Input**: User description: "Se creará una nueva tabla departamentos con los atributos: id y nombre. Igualmente debe tener un CRUD completo."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Crear Nuevo Departamento (Priority: P1)

Un usuario autenticado necesita crear nuevos departamentos en el sistema para organizar la estructura de la empresa.

**Why this priority**: La capacidad de crear departamentos es fundamental para establecer la base de datos del sistema. Sin esto, no hay forma de gestionar departamentos en absoluto.

**Independent Test**: Puede ser probado completamente al enviar una solicitud POST con el nombre del departamento (con autenticación válida) y verificar que se retorna un código 201 con el departamento creado.

**Acceptance Scenarios**:

1. **Given** an authenticated user provides a valid department name, **When** the user sends a POST request to create a department, **Then** the system creates the department and returns HTTP 201 with the created department ID and name
2. **Given** an authenticated user tries to create a department without a name, **When** the user sends a POST request, **Then** the system returns HTTP 400 with a validation error
3. **Given** an authenticated user sends a duplicate department name (name already exists), **When** the user sends a POST request, **Then** the system returns HTTP 409 (Conflict) with error message indicating name already exists

---

### User Story 2 - Listar Departamentos (Priority: P2)

Un usuario autenticado necesita ver la lista de todos los departamentos existentes en el sistema, con paginación.

**Why this priority**: La capacidad de listar departamentos es esencial para permitir búsqueda y navegación. Se implementa después de creación ya que la creación establece los datos qué listar.

**Independent Test**: Puede ser probado completamente al enviar una solicitud GET al endpoint de listado (con autenticación válida) y verificar que retorna una lista paginada de departamentos.

**Acceptance Scenarios**:

1. **Given** departments exist in the system, **When** an authenticated user sends a GET request to list departments, **Then** the system returns HTTP 200 with a paginated list of departments (10 per page)
2. **Given** no departments exist, **When** an authenticated user sends a GET request, **Then** the system returns HTTP 200 with an empty paginated response
3. **Given** more than 10 departments exist, **When** an authenticated user requests page 2, **Then** the system returns the next 10 departments

---

### User Story 3 - Actualizar y Eliminar Departamentos (Priority: P3)

Un usuario autenticado necesita actualizar o eliminar departamentos cuando cambios estructurales ocurren en la empresa.

**Why this priority**: Actualizar y eliminar son operaciones menos frecuentes que crear y listar. Estas refactorizaciones son típicamente necesarias después de que la estructura base está establecida.

**Independent Test**: Puede ser probado completamente al verificar que una actualización PATCH modifica correctamente los datos y que una solicitud DELETE elimina el departamento (ambas con autenticación válida).

**Acceptance Scenarios**:

1. **Given** a department exists, **When** an authenticated user sends a PATCH request with a new name, **Then** the system updates the department and returns HTTP 200
2. **Given** a department exists with no associated employees, **When** an authenticated user sends a DELETE request, **Then** the system deletes the department and returns HTTP 204
3. **Given** a department exists with one or more associated employees, **When** an authenticated user sends a DELETE request, **Then** the system returns HTTP 409 (Conflict) with error message indicating employees are assigned to this department
4. **Given** a department ID does not exist, **When** an authenticated user tries to update or delete it, **Then** the system returns HTTP 404

---

### Edge Cases

- What happens when a department name contains special characters? (Should be accepted if under 255 chars)
- What happens when an authenticated user tries to delete a department that has associated employees? (Must return HTTP 409 and prevent deletion)
- What happens when the system receives a department name longer than 255 characters? (Reject with HTTP 400 validation error)
- What is the error message format when attempting to delete a department with employees?
- How does the system handle pagination for departments marked as INACTIVE? (Not returned in default list)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow authenticated users to create new departments with a unique name (names must not duplicate existing department names; enforced at database level with UNIQUE constraint)
- **FR-002**: System MUST validate that department name is not empty, not null, and not longer than 255 characters; return HTTP 400 if validation fails
- **FR-003**: System MUST provide a paginated REST API endpoint to retrieve all departments with 10 departments per page (requires authentication via Basic Auth; only returns ACTIVO departments by default)
- **FR-004**: System MUST allow authenticated users to update an existing department's name via PATCH endpoint (must also enforce uniqueness and length constraints on update)
- **FR-005**: System MUST allow authenticated users to delete a department via DELETE endpoint, BUT mark as INACTIVE (soft-delete) instead of hard-deleting; only if no employees are assigned to the department; otherwise return HTTP 409 (Conflict)
- **FR-006**: System MUST return HTTP 404 when attempting to access a non-existent or INACTIVE department
- **FR-007**: System MUST persist all department data in PostgreSQL and handle migrations automatically via Flyway
- **FR-008**: System MUST provide Swagger/OpenAPI documentation for all department endpoints
- **FR-009**: System MUST return HTTP 409 (Conflict) when attempting to create or update a department with a name that already exists
- **FR-010**: System MUST return HTTP 401 (Unauthorized) for all endpoints when user is not authenticated via Basic Auth
- **FR-011**: System MUST enforce referential integrity by preventing deletion of departments that have associated employees (checked before soft-delete is executed)
- **FR-012**: System MUST set department estado to ACTIVO by default when creating a new department
- **FR-013**: System MUST include estado field in all department API responses (read, list, create, update)
- **FR-014**: System MUST return HTTP 400 with specific validation error message if department name exceeds 255 characters

### Constitution Alignment *(mandatory)*

- **CA-001**: The feature MUST run on Spring Boot 3 with Java 17 ✓
- **CA-002**: The feature MUST define Basic Authentication behavior - all department endpoints require Basic Auth (any authenticated user can access; no role-based restriction)
- **CA-003**: The feature MUST define PostgreSQL persistence - new `departamentos` table with Flyway migration V3
- **CA-004**: The feature MUST define Swagger/OpenAPI contract updates - all CRUD endpoints versioned as `/api/v1/departamentos`
- **CA-005**: The feature MUST define pagination for collection queries - GET endpoint returns 10 departments per page (consistent with empleados)
- **CA-006**: The feature MUST define required automated tests - contract tests, integration tests, and unit validation tests required

### Key Entities *(include if feature involves data)*

- **Departamento**: Represents an organizational department with a unique identifier, display name, and lifecycle state. Attributes: `id` (generated UUID or auto-increment as per existing pattern), `nombre` (string, required, max 255 characters, unique), `estado` (enum: ACTIVO/INACTIVO, default ACTIVO - soft-delete by marking as INACTIVE instead of hard-delete)

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All CRUD operations (Create, Read, List, Update, Delete) can be completed in under 500ms per request
- **SC-002**: System successfully validates 100% of invalid inputs (empty names, null values) and returns appropriate HTTP error codes
- **SC-003**: Pagination works correctly with exactly 10 departments per page across all pages
- **SC-004**: All automated tests pass, including contract tests, integration tests, and unit validation tests (100% pass rate)

## Clarifications

### Session 2026-03-12

- Q: Should department names be required to be unique? → A: Yes, enforce uniqueness with UNIQUE database constraint
- Q: Which user roles should have permission to manage departments? → A: Any authenticated user can perform CRUD operations (no role-based restrictions)
- Q: What happens when deleting a department with associated employees? → A: Prevent deletion; return HTTP 409 (Conflict); employees must be reassigned/deleted first
- Q: Should departments support ACTIVE/INACTIVE states? → A: Yes, add estado (ACTIVO/INACTIVO enum); soft-delete by marking as INACTIVE; matches empleados pattern
- Q: How should the system handle department names > 255 characters? → A: Reject with HTTP 400 validation error; prevent silent truncation

## Assumptions

- The system will follow the same authentication/authorization pattern as existing features (Spring Security with bootstrap credentials)
- API versioning follows the established pattern (`/api/v1/...`)
- Department names may contain special characters but are validated for length (≤ 255 chars)
- INACTIVE departments are not returned by default in list/read operations (soft-delete semantics)
