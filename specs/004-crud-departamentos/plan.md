# Implementation Plan: CRUD Departamentos + Relación 1:N con Empleados

**Branch**: `004-crud-departamentos` | **Date**: 12 de marzo de 2026 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/004-crud-departamentos/spec.md`

## Summary

Implementar CRUD completo de `departamentos` con soft-delete y validaciones de unicidad/longitud, incorporando explícitamente la relación uno-a-muchos con `empleados`: cada `empleado` debe tener un `departamentoId` válido (ACTIVO), `EmpleadoResponse` debe incluir objeto embebido `departamento`, y `GET /api/v1/departamentos/{id}` debe incluir lista embebida de empleados con límite fijo de 50 elementos.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3.3.x, Spring Security (Basic Auth), Spring Data JPA, Flyway, springdoc-openapi  
**Storage**: PostgreSQL (runtime) + H2 (tests)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Mockito, Spring Security Test  
**Target Platform**: Linux containerized backend (Docker/Docker Compose)  
**Project Type**: Monolithic REST web-service (backend only)  
**Performance Goals**: p95 < 500ms por operación CRUD y lectura de detalle con embebidos dentro de límites definidos  
**Constraints**: Basic Auth obligatorio, rutas versionadas `/api/v1/...`, paginación fija de 10 para colecciones, embebido de empleados en detalle de departamento limitado a 50  
**Schema Governance**: Todo cambio de esquema PostgreSQL debe incluir plan de rollback verificable en el mismo incremento  
**Scale/Scope**: Feature backend única para `departamentos` y extensión de contrato/respuesta de `empleados`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: Uses Spring Boot 3 + Java 17, or includes approved RFC exception.
- [x] Security gate: Defines Basic Authentication scope, HTTPS requirement, and secret handling.
- [x] API gate: Includes Swagger/OpenAPI update plan for all new/changed endpoints with versioned routes (`/api/v{major}/...`).
- [x] Pagination gate: Defines collection query pagination with fixed page size of 10 instances per response.
- [x] Data gate: Uses PostgreSQL and Docker/Docker Compose strategy for local/CI parity.
- [x] Quality gate: Defines automated tests and structured logging coverage for critical flows.

## Project Structure

### Documentation (this feature)

```text
specs/004-crud-departamentos/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/dsw02/empleados/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── config/
│   │   └── departamentos/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
└── test/
    ├── java/com/dsw02/empleados/
    │   ├── contract/
    │   ├── integration/
    │   └── unit/
    └── java/com/dsw02/empleados/departamentos/
        ├── contract/
        ├── integration/
        └── unit/
```

**Structure Decision**: Monolito Spring Boot existente con módulo funcional `departamentos` bajo `com.dsw02.empleados.departamentos`, extendiendo DTOs/servicios de `empleados` para representar y validar la relación 1:N.

## Phase 0 - Research Focus

1. Regla de cardinalidad y validación de asignación (`departamentoId` obligatorio, departamento ACTIVO).
2. Contrato de representación embebida (`EmpleadoResponse.departamento` y `DepartamentoResponse.empleados[]` limitado a 50).
3. Estrategia de consultas para evitar payload excesivo y mantener p95 < 500ms.
4. Reglas de integridad para soft-delete de departamento con empleados asociados (409).

## Phase 1 - Design Focus

1. Actualizar modelo de datos y DTOs para reflejar obligatoriedad de `departamentoId` y embebidos.
2. Definir contrato OpenAPI de endpoints afectados (`/api/v1/departamentos/*` y respuestas de `empleados`).
3. Documentar quickstart con pruebas E2E en Docker para CRUD y relación.
4. Mantener compatibilidad con paginación fija 10 en colecciones; no paginar embebido de detalle (cap fijo 50).
5. Definir y validar plan de rollback para migraciones de esquema y cobertura de pruebas 401 en endpoints impactados.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
