# DSW02-Practica01 Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-02-26

## Active Technologies
- PostgreSQL (producción/dev), H2 (solo pruebas) (001-paginacion-empleados)
- Java 17 + Spring Boot 3 (Web, Data JPA, Validation), Spring Security, springdoc-openapi (003-empleado-email-auth)
- PostgreSQL (runtime), H2 (solo pruebas) (003-empleado-email-auth)
- Java 17 + Spring Boot 3.3.x, Spring Security (Basic Auth), Spring Data JPA, Flyway, springdoc-openapi (004-crud-departamentos)
- PostgreSQL (runtime) + H2 (tests) (004-crud-departamentos)
- Java 17 (backend), TypeScript (Angular 22 LTS frontend) + Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular 22 LTS, Cypress (001-empleados-login-angular)
- PostgreSQL (runtime), H2 (tests backend), almacenamiento de sesión en cookie segura (sin persistencia de credenciales en navegador) (001-empleados-login-angular)
- Java 17 (backend), TypeScript (frontend Angular 22 LTS objetivo del feature) + Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular, RxJS, Cypress (006-crud-empleados-frontend)
- PostgreSQL (runtime), H2 (tests backend), sesión por cookie segura para frontend (006-crud-empleados-frontend)
- Java 17 (backend existente), TypeScript en Angular (frontend web; objetivo constitucional Angular 22 LTS) + Spring Boot 3, Spring Security, springdoc-openapi, Angular, RxJS, Cypress (005-crud-departamentos)
- PostgreSQL en backend; sesión HTTP por cookie para autenticación de frontend (005-crud-departamentos)

- Java 17 + Spring Boot 3 (Web, Data JPA, Validation), Spring Security (Basic Auth), springdoc-openapi (001-crud-empleados)

## Project Structure

```text
backend/
frontend/
tests/
```

## Commands

# Add commands for Java 17

## Code Style

Java 17: Follow standard conventions

## Recent Changes
- 005-crud-departamentos: Added Java 17 (backend existente), TypeScript en Angular (frontend web; objetivo constitucional Angular 22 LTS) + Spring Boot 3, Spring Security, springdoc-openapi, Angular, RxJS, Cypress
- 006-crud-empleados-frontend: Added Java 17 (backend), TypeScript (frontend Angular 22 LTS objetivo del feature) + Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular, RxJS, Cypress
- 001-empleados-login-angular: Added Java 17 (backend), TypeScript (Angular 22 LTS frontend) + Spring Boot 3.3.x, Spring Security, springdoc-openapi, Angular 22 LTS, Cypress


<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
