# Implementation Plan: Login de Empleados en Frontend

**Branch**: `001-empleados-login-angular` | **Date**: 2026-03-17 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-empleados-login-angular/spec.md`

## Summary

Implementar autenticación de empleados en frontend Angular 22 LTS usando correo + contraseña con sesión basada en cookie segura (`HttpOnly`, `Secure`, `SameSite`), redirección a `/empleados`, manejo de error genérico y cobertura E2E con Cypress. El flujo de sesión se limita a endpoints públicos de auth (`/api/v1/auth/login`, `/api/v1/auth/session`, `/api/v1/auth/logout`) y se preserva Basic Authentication en endpoints protegidos de negocio/legados.

## Technical Context

**Language/Version**: Java 17 (backend), TypeScript (Angular 22 LTS frontend)  
**Primary Dependencies**: Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular 22 LTS, Cypress  
**Storage**: PostgreSQL (runtime), H2 (tests backend), almacenamiento de sesión en cookie segura (sin persistencia de credenciales en navegador)  
**Testing**: JUnit 5 + Spring Boot Test + MockMvc + Spring Security Test (backend), Cypress E2E (frontend), pruebas unitarias Angular (Jest/Karma según preset elegido)  
**Target Platform**: Linux + Docker Compose para backend/DB, navegador moderno para frontend web  
**Project Type**: Monolito backend + aplicación frontend web separada en el mismo repositorio  
**Performance Goals**: Login exitoso p95 < 500ms en backend bajo carga nominal; tiempo de acceso de usuario final < 30s (SC-001)  
**Constraints**: Error de autenticación genérico único, redirección fija `/empleados`, expiración de sesión configurable por entorno (default 8h inactividad con renovación), exclusión explícita de registro y recuperación de contraseña, distinción obligatoria entre endpoints públicos de sesión y endpoints protegidos con Basic Authentication  
**Scale/Scope**: Feature acotada a login/logout/rutas protegidas para empleados con cuentas existentes

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Pre-design gate result: **PASS**

- [x] Stack gate: Backend conserva Spring Boot 3 + Java 17; frontend web en Angular 22 LTS.
- [x] Security gate: Se define alcance híbrido sin conflicto constitucional: endpoints públicos de sesión para frontend (`/api/v1/auth/login`, `/api/v1/auth/session`, `/api/v1/auth/logout`) y Basic Auth obligatorio para endpoints protegidos de negocio/legacy.
- [x] API gate: Incluye actualización de contrato OpenAPI para endpoints de sesión (`/api/v1/auth/...`) y comportamiento de errores.
- [x] Pagination gate: No cambia convención de colecciones; se preserva size fijo 10 en endpoints existentes.
- [x] Data gate: Se mantiene PostgreSQL + Docker Compose para runtime y paridad local.
- [x] Quality gate: Se define cobertura backend + E2E Cypress para flujos críticos de autenticación.
- [x] Frontend gate (conditional): UI web definida con Angular 22 LTS.
- [x] E2E gate (conditional): Se exige suite Cypress para login, fallo de login, protección de rutas y logout.

Post-design re-check result: **PASS** (sin violaciones no justificadas tras research + design)

## Project Structure

### Documentation (this feature)

```text
specs/001-empleados-login-angular/
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
├── main/java/com/dsw02/empleados/
│   ├── config/
│   ├── controller/
│   ├── service/
│   └── ...
└── test/java/com/dsw02/empleados/
    ├── contract/
    ├── integration/
    └── unit/

frontend/
├── src/
│   ├── app/
│   │   ├── auth/
│   │   ├── empleados/
│   │   └── shared/
│   └── environments/
├── cypress/
│   ├── e2e/
│   └── support/
└── package.json
```

**Structure Decision**: Mantener backend actual en `src/` y agregar aplicación Angular 22 LTS en `frontend/` con Cypress co-localizado para E2E. Esta separación minimiza impacto en build Java existente y habilita pipeline independiente para UI.

## Phase 0 - Research Focus

1. Patrón de autenticación por cookie en Spring Security sin romper Basic Auth legado.
2. Manejo de sesión y renovación por inactividad configurable por entorno.
3. Estrategia CORS/CSRF para frontend Angular con cookies seguras.
4. Diseño de pruebas Cypress determinísticas para login/logout/rutas protegidas.

## Phase 1 - Design Focus

1. Definir modelo de dominio de autenticación frontend/backend (credenciales, sesión, estado de error).
2. Diseñar contrato OpenAPI para login/logout/session-check con códigos de respuesta y formato de error.
3. Definir arquitectura Angular (`AuthService`, guardias de ruta, interceptores y páginas mínimas).
4. Documentar quickstart de ejecución local (backend + frontend + Cypress).
5. Re-validar gates constitucionales tras decisiones técnicas.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
