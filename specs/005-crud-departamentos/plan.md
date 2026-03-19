# Implementation Plan: CRUD de Departamentos en Frontend

**Branch**: `005-crud-departamentos` | **Date**: 2026-03-19 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/005-crud-departamentos/spec.md`

## Summary

Implementar en frontend un módulo CRUD de departamentos para usuarios autenticados, con listado paginado, alta, edición y eliminación con confirmación. La solución reutiliza la API versionada existente `/api/v1/departamentos`, respeta tamaño de página fijo en 10 y maneja explícitamente errores de conflicto (nombre duplicado y eliminación con empleados asociados), expiración de sesión y bloqueo de doble envío en acciones mutables.

## Technical Context

**Language/Version**: Java 17 (backend existente), TypeScript en Angular (frontend web; objetivo constitucional Angular 22 LTS)  
**Primary Dependencies**: Spring Boot 3, Spring Security, springdoc-openapi, Angular, RxJS, Cypress  
**Storage**: PostgreSQL en backend; sesión HTTP por cookie para autenticación de frontend  
**Testing**: JUnit 5/Spring Boot Test (backend existente), pruebas unitarias Angular (Karma/Jasmine), Cypress E2E para flujos críticos  
**Target Platform**: Linux + Docker Compose para backend/DB y navegador moderno para frontend web
**Project Type**: Monorepo web app con backend Spring Boot + frontend Angular en `frontend/`  
**Performance Goals**: 95% de consultas de listado < 2s y creación/edición completables en < 2 minutos en condiciones nominales  
**Constraints**: paginación fija de 10, acciones CRUD permitidas a usuario autenticado, eliminación con confirmación y bloqueo por relación con empleados, redirección a login en 401, sin credenciales hardcodeadas en cliente  
**Scale/Scope**: módulo nuevo `/departamentos` en frontend (listado, formularios create/edit y confirmación de eliminación) con cobertura E2E dedicada

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Pre-design gate result: **PASS**

- [x] Stack gate: se mantiene backend en Spring Boot 3 + Java 17 y el frontend se alinea con el estándar Angular 22 LTS definido por constitución.
- [x] Security gate: consumo frontend de endpoints protegidos sin credenciales hardcodeadas; autenticación gestionada por backend y sesión/cookie; HTTPS exigido fuera de local.
- [x] API gate: se consumen endpoints versionados existentes `/api/v1/departamentos` y se documenta contrato de integración frontend sin romper versión.
- [x] Pagination gate: el listado usa `GET /api/v1/departamentos?page={n}` con tamaño fijo `10` en backend.
- [x] Data gate: PostgreSQL y Docker Compose permanecen como base de ejecución local/CI.
- [x] Quality gate: se define cobertura automatizada en unitarias frontend + Cypress E2E para jornadas críticas CRUD.
- [x] Frontend gate (conditional): alcance web en Angular conforme a baseline del proyecto.
- [x] E2E gate (conditional): cobertura Cypress definida para listar, crear, editar y eliminar (incluyendo conflictos).

Post-design re-check result: **PASS** (research, data model, contrato y quickstart mantienen cumplimiento constitucional sin excepciones)

## Project Structure

### Documentation (this feature)

```text
specs/005-crud-departamentos/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── frontend-departamentos-crud.md
└── tasks.md
```

### Source Code (repository root)
```text
src/
└── main/java/com/dsw02/empleados/
    ├── config/
    ├── departamentos/
    └── ...

src/test/java/com/dsw02/empleados/
├── departamentos/
└── ...

frontend/
├── src/app/
│   ├── auth/
│   ├── empleados/
│   └── departamentos/   # nuevo módulo del feature
├── src/environments/
├── cypress/e2e/
└── package.json
```

**Structure Decision**: Se conserva la estructura monorepo existente. Este feature introduce únicamente el módulo frontend de departamentos y pruebas asociadas (unitarias + Cypress), reutilizando backend y contratos ya versionados sin reestructuración.

## Phase 0 - Research Focus

1. Definir estrategia de consumo frontend del CRUD de departamentos usando endpoints existentes.
2. Confirmar reglas de conflicto y mensajes esperados para nombre duplicado y eliminación bloqueada por empleados.
3. Establecer patrón de manejo de sesión expirada (401) y redirección homogénea a login.
4. Definir cobertura mínima E2E y pruebas unitarias para evitar regresiones en flujos críticos.

## Phase 1 - Design Focus

1. Modelar estados de listado, formulario y confirmación de eliminación para departamentos.
2. Documentar contrato de integración frontend (endpoints, payloads, errores y rutas UI).
3. Definir quickstart reproducible para ejecución local y validación manual/automatizada del feature.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
