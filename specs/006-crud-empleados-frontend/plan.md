# Implementation Plan: CRUD de Empleados en Frontend

**Branch**: `006-crud-empleados-frontend` | **Date**: 2026-03-18 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/006-crud-empleados-frontend/spec.md`

## Summary

Implementar CRUD de empleados en frontend con listado paginado, alta, edición, desactivación lógica y reactivación consumiendo endpoints de negocio protegidos `/api/v1/empleados` bajo el esquema de autenticación vigente. El plan incluye migración del frontend a Angular 22 LTS para cumplir la constitución, validaciones de formulario en cliente, manejo de conflictos de unicidad reportados por backend y cobertura mínima unitaria + Cypress E2E de recorridos críticos.

## Technical Context

**Language/Version**: Java 17 (backend), TypeScript (frontend Angular 22 LTS objetivo del feature)  
**Primary Dependencies**: Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular, RxJS, Cypress  
**Storage**: PostgreSQL (runtime), H2 (tests backend), sesión por cookie segura para frontend  
**Testing**: JUnit 5 + Spring Boot Test (backend existente), pruebas unitarias Angular (Karma/Jasmine), Cypress E2E  
**Target Platform**: Linux + Docker Compose para backend/DB, navegador moderno para frontend web  
**Project Type**: Monorepo con backend Spring Boot + frontend Angular en carpeta `frontend/`  
**Performance Goals**: listado p95 < 2s en condiciones nominales; alta completa < 2 minutos en 90% de casos válidos  
**Constraints**: paginación fija de 10, desactivación lógica (no `DELETE` desde UI), acceso solo para usuario autenticado, redirección a `/login` en sesión inválida, frontend sin credenciales Basic hardcodeadas  
**Scale/Scope**: alcance acotado al módulo `/empleados` en frontend (tabla + formulario + confirmación + filtros de estado)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Pre-design gate result: **PASS**

- [x] Stack gate: backend mantiene Spring Boot 3 + Java 17; frontend planifica alineación a Angular 22 LTS dentro del feature.
- [x] Security gate: endpoints de negocio protegidos con Basic Authentication y consumo frontend sin credenciales hardcodeadas ni exposición de secretos; secretos vía entorno.
- [x] API gate: se mantiene consumo de endpoints versionados existentes (`/api/v1/empleados`) y trazabilidad contractual en el feature.
- [x] Pagination gate: el listado usa `GET /api/v1/empleados?page={n}` con tamaño fijo `10`.
- [x] Data gate: PostgreSQL y Docker Compose se mantienen para paridad local/CI.
- [x] Quality gate: se define cobertura de unitarias frontend + E2E Cypress en flujos CRUD críticos.
- [x] Frontend gate (conditional): objetivo explícito Angular 22 LTS en el plan.
- [x] E2E gate (conditional): cobertura Cypress definida para listar, crear, editar, desactivar y reactivar.

Post-design re-check result: **PASS** (research + data-model + contract + quickstart mantienen cumplimiento sin violaciones no justificadas)

## Project Structure

### Documentation (this feature)

```text
specs/006-crud-empleados-frontend/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── frontend-empleados-crud.md
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── main/java/com/dsw02/empleados/
│   ├── controller/
│   ├── service/
│   ├── dto/
│   └── ...
└── test/java/com/dsw02/empleados/

frontend/
├── src/
│   ├── app/
│   │   ├── auth/
│   │   ├── empleados/
│   │   └── app.routes.ts
│   ├── environments/
│   └── styles.css
├── cypress/
│   └── e2e/
└── package.json
```

**Structure Decision**: Se mantiene arquitectura backend/frontend existente; la implementación del feature se concentra en `frontend/src/app/empleados/` y pruebas en `frontend` (unitarias + Cypress), sin refactor estructural del backend.

## Phase 0 - Research Focus

1. Definir estrategia de desactivación/reactivación lógica sin uso de `DELETE` desde UI.
2. Definir política de visualización activos/inactivos con toggle.
3. Definir manejo de conflictos de unicidad con mensajes accionables.
4. Asegurar cumplimiento de Angular 22 LTS según constitución.

## Phase 1 - Design Focus

1. Modelar estados de listado, formulario y confirmación de desactivación.
2. Documentar contrato de integración frontend con API versionada existente.
3. Definir flujo UI para alta/edición/desactivación/reactivación y expiración de sesión.
4. Documentar quickstart reproducible para ejecución y pruebas.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
