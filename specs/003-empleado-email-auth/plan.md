# Implementation Plan: Autenticación de Empleados por Correo

**Branch**: `003-empleado-email-auth` | **Date**: 2026-03-11 | **Spec**: `/specs/003-empleado-email-auth/spec.md`
**Input**: Feature specification from `/specs/003-empleado-email-auth/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Reemplazar credenciales estáticas de autenticación por credenciales de empleado
(correo y contraseña), manteniendo esquema HTTP Basic, rutas versionadas y
paginación existente. La solución incorporará estado de acceso (`activo`/`inactivo`),
política de bloqueo temporal por intentos fallidos y actualización de contrato OpenAPI
para flujos de alta/actualización/autenticación de empleados.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Data JPA, Validation), Spring Security, springdoc-openapi  
**Storage**: PostgreSQL (runtime), H2 (solo pruebas)  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Spring Security Test  
**Target Platform**: Linux con ejecución local y CI en contenedores Docker
**Project Type**: web-service backend monolítico  
**Performance Goals**: p95 < 2s para autenticaciones válidas (SC-003)  
**Constraints**: Basic Auth con correo como usuario, estado `activo`/`inactivo`, bloqueo 15 min tras 5 fallos consecutivos aplicable a endpoints protegidos, bootstrap inicial por credenciales técnicas configuradas por entorno (sin hardcode), HTTPS obligatorio en entornos no locales, paginación fija de 10 en consultas de colección, rutas `/api/v{major}/...`  
**Scale/Scope**: Catálogo de empleados existente + autenticación para endpoints protegidos de API de empleados

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Pre-design gate result: PASS.
Post-design re-check result: PASS.

- [x] Stack gate: Se mantiene Spring Boot 3 + Java 17 sin excepción.
- [x] Security gate: Se mantiene Basic Authentication, se define transición a credenciales de empleado con configuración sensible externa y se explicita HTTPS obligatorio fuera de entorno local.
- [x] API gate: Se planifica actualización OpenAPI para cambios de payloads y seguridad en rutas versionadas `/api/v1/...`.
- [x] Pagination gate: Se preserva paginación fija de 10 en consultas de colección existentes.
- [x] Data gate: Persistencia en PostgreSQL con ejecución/paridad en Docker Compose.
- [x] Quality gate: Incluye pruebas contract/integration/unit y logging estructurado de eventos de autenticación.

## Project Structure

### Documentation (this feature)

```text
specs/003-empleado-email-auth/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
└── tasks.md
```

### Source Code (repository root)

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
│       └── db/migration/
└── test/
  ├── java/com/dsw02/empleados/contract/
  ├── java/com/dsw02/empleados/integration/
  ├── java/com/dsw02/empleados/unit/
  └── resources/

docker-compose.yml
pom.xml
```

**Structure Decision**: Se mantiene estructura monolítica existente con cambios en entidad/DTO/seguridad y pruebas por capas para minimizar regresiones.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Ninguna | N/A | N/A |
