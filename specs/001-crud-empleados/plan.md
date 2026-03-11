# Implementation Plan: CRUD de Empleados

**Branch**: `001-crud-empleados` | **Date**: 2026-02-26 | **Spec**: `/specs/001-crud-empleados/spec.md`
**Input**: Feature specification from `/specs/001-crud-empleados/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar un backend REST para CRUD de empleados con `clave` en formato `EMP-<autonumérico>`
y PK compuesta interna por prefijo + consecutivo,
validaciones de longitud máxima de 100 caracteres para `nombre`, `direccion` y `telefono`,
autenticación básica para endpoints protegidos, persistencia en PostgreSQL con entorno Docker,
y contrato documentado en Swagger/OpenAPI con rutas versionadas en `/api/v1/...`
y paginación obligatoria de 10 instancias por consulta en endpoints de colección.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Data JPA, Validation), Spring Security (Basic Auth), springdoc-openapi  
**Storage**: PostgreSQL 16  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Testcontainers (PostgreSQL)  
**Target Platform**: Linux server (contenedor Docker para BD y despliegue local)
**Project Type**: web-service backend monolítico  
**Performance Goals**: p95 < 500 ms para operaciones CRUD simples con dataset de hasta 10k empleados  
**Constraints**: `clave` con formato `EMP-<autonumérico>` y PK compuesta interna; campos `nombre`, `direccion`, `telefono` <= 100 chars; Basic Auth obligatoria; rutas API versionadas en `/api/v1/...`; paginación fija de 10 instancias por consulta de colección; Swagger actualizado en cada cambio de endpoint  
**Scale/Scope**: Catálogo interno de empleados, hasta 10k registros, 1 servicio backend

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Post-design re-check result: PASS.

- [x] Stack gate: Spring Boot 3 + Java 17 confirmado, sin excepción requerida.
- [x] Security gate: Basic Authentication para CRUD completo, credenciales por variables de entorno, HTTPS en entornos no locales.
- [x] API gate: Contrato OpenAPI definido para alta/listado/detalle/actualización/eliminación con respuestas de error y rutas versionadas en `/api/v1/...`.
- [x] Pagination gate: Consultas de colección diseñadas con paginación de tamaño fijo de 10 instancias por respuesta.
- [x] Data gate: PostgreSQL como almacenamiento único y estrategia Docker Compose para entorno local/CI.
- [x] Quality gate: Plan de pruebas unitarias/integración/contrato y logging estructurado de operaciones críticas.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
src/
├── main/
│   ├── java/com/dsw02/empleados/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── resources/
│       ├── application.yml
│       └── db/migration/
└── test/
  └── java/com/dsw02/empleados/
    ├── contract/
    ├── integration/
    └── unit/

docker-compose.yml
Dockerfile
```

**Structure Decision**: Se adopta estructura de proyecto único Spring Boot con capas controller/service/repository/entity y migraciones versionadas.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
