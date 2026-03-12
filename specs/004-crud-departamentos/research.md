# Research Phase: CRUD Departamentos

**Date**: 12 de marzo de 2026  
**Status**: Complete - All clarifications resolved in specification phase

## Summary

No remaining unknowns. All critical design decisions were clarified during specification phase `/speckit.clarify`.

## Decisions Recorded

### D1: Department Name Uniqueness
**Decision**: Enforce UNIQUE constraint at database level  
**Rationale**: Prevents duplicate department names which could cause confusion in organizational hierarchy. Matches common enterprise practice.  
**Alternatives Considered**: Allow duplicates (rejected - causes ambiguity in org structure)

### D2: Authorization Model
**Decision**: Any authenticated user can perform CRUD operations (no role-based restrictions)  
**Rationale**: Departamentos are foundational data; no specific departmental ownership model required. Simplifies permission management for MVP.  
**Alternatives Considered**: Admin-only (rejected - business needs indicate broader access). Role-based RBAC (rejected - future enhancement candidate if needed)

### D3: Delete Behavior with Associated Employees
**Decision**: Prevent deletion (return HTTP 409) if department has associated employees; require reassignment first  
**Rationale**: Protects employee records from being orphaned or deleted indirectly. Maintains data integrity and audit trail.  
**Alternatives Considered**: Cascade delete (rejected - employee records are authoritative). Soft-delete + reassign (rejected - requires complex business logic for current scope)

### D4: Entity Lifecycle State
**Decision**: Add `estado` enum (ACTIVO/INACTIVO) for soft-delete semantics; mark as INACTIVO instead of hard-deleting  
**Rationale**: Maintains consistency with empleados entity which uses same pattern. Enables audit trail and recovery capability. Matches architectural pattern.  
**Alternatives Considered**: Optional state (rejected - empleados uses required state). Hard-delete (rejected - loses audit trail)

### D5: Long Name Validation
**Decision**: Reject with HTTP 400 validation error if name exceeds 255 characters; no silent truncation  
**Rationale**: Explicit validation prevents silent data loss and aligns with REST API best practices. Users get immediate feedback.  
**Alternatives Considered**: Silent truncation (rejected - hides data loss). No validation (rejected - allows unbounded string storage)

## Technology Stack (Confirmed from Constitution)

✅ **Language**: Java 17 (required by constitution)  
✅ **Framework**: Spring Boot 3.3.2 (required by constitution)  
✅ **Database**: PostgreSQL (required by constitution)  
✅ **Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test  
✅ **API Documentation**: springdoc-openapi / Swagger UI  
✅ **Authentication**: HTTP Basic Auth (inherited from SecurityConfig)  
✅ **Persistence Layer**: Spring Data JPA  
✅ **Validation**: Spring Validation (javax.validation)

## Design Patterns to Apply

### Pattern 1: Enum Reuse for State Management
**Apply**: Reuse existing `EstadoAcceso` enum from empleados instead of creating new enum  
**Benefit**: Maintains architectural consistency, simplifies codebase, facilitates future shared reference tables

### Pattern 2: Soft-Delete Implementation
**Apply**: Mark records as INACTIVO instead of deleting; filter out INACTIVO in default queries  
**Benefit**: Enables audit trail, supports recovery, maintains referential integrity for historical data

### Pattern 3: Unique Constraint at DB Level
**Apply**: Add `UNIQUE` constraint in Flyway migration + `@UniqueConstraint` annotation  
**Benefit**: Prevents duplicate inserts, enforces at database integrity layer, faster than application-level checking

### Pattern 4: Pagination Convention
**Apply**: Use `Page<T>` from Spring Data, fixed page size of 10 records per request  
**Benefit**: Matches existing empleados pattern, deterministic pagination for API consumers

### Pattern 5: DTO Layering
**Apply**: Create separate DTOs for CreateRequest, UpdateRequest, Response  
**Benefit**: Decouples API contract from entity, allows flexible request/response shapes, version-independent

## No Blockers

All questions clarified. Ready to proceed to Phase 1 design and Phase 2 task generation.
