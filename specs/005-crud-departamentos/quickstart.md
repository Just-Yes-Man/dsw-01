# Quickstart: CRUD de Departamentos en Frontend

## 1) Prerrequisitos

- Docker + Docker Compose
- Java 17 + Maven
- Node.js LTS compatible con Angular del proyecto
- npm

## 2) Levantar backend + base de datos

```bash
docker compose up --build -d
curl -u bootstrap_admin:bootstrap123 http://localhost:8080/actuator/health
```

## 3) Levantar frontend

```bash
cd frontend
npm install
npm run start
```

Frontend esperado en `http://localhost:4200`.

## 4) Recorrido manual CRUD esperado

1. Iniciar sesión en `/login` con usuario autenticado válido.
2. Navegar a `/departamentos` y verificar listado paginado.
3. Crear departamento con nombre válido y confirmar aparición en lista.
4. Editar departamento existente y confirmar actualización en UI.
5. Eliminar departamento sin empleados asociados y validar que deja de mostrarse.
6. Intentar eliminar un departamento con empleados asociados y validar mensaje de conflicto.
7. Simular sesión expirada (o cookie inválida) y validar redirección a `/login` al operar.

## 5) Verificación rápida de API (opcional)

```bash
# Listado paginado
curl -i http://localhost:8080/api/v1/departamentos?page=0 --cookie "JSESSIONID=<session-id>"

# Crear departamento
curl -i -X POST http://localhost:8080/api/v1/departamentos \
  -H 'Content-Type: application/json' \
  --cookie "JSESSIONID=<session-id>" \
  -d '{"nombre":"Departamento Demo"}'

# Editar departamento
curl -i -X PATCH http://localhost:8080/api/v1/departamentos/<id> \
  -H 'Content-Type: application/json' \
  --cookie "JSESSIONID=<session-id>" \
  -d '{"nombre":"Departamento Demo Actualizado"}'

# Eliminar departamento
curl -i -X DELETE http://localhost:8080/api/v1/departamentos/<id> --cookie "JSESSIONID=<session-id>"
```

## 6) Pruebas automáticas sugeridas

### Unitarias frontend

```bash
cd frontend
npm test
```

### E2E con Cypress

```bash
cd frontend
npm run cypress:run
```

Cobertura mínima del feature:
- listado paginado
- alta válida
- edición válida
- conflicto por nombre duplicado
- eliminación exitosa
- eliminación rechazada por empleados asociados
- sesión inválida con redirección a login

## 7) Evidencia de cumplimiento (CA-004, CA-006, CA-007)

- Verificar versión Angular en `frontend/package.json` y registrar si está en Angular 22 LTS.
- Resultado actual de verificación (T043): `@angular/core`, `@angular/cli` y `@angular/compiler-cli` se encuentran en rama `21.2.x`; upgrade a 22 queda bloqueado por disponibilidad en registry para el entorno actual.
- Confirmar en `contracts/frontend-departamentos-crud.md` que se mantiene versionado `/api/v1/...` sin cambios de contrato en este feature.
- Adjuntar resultado reproducible de:
  - `npm test`
  - `npm run cypress:run -- --spec "cypress/e2e/departamentos/**/*.cy.ts"`
- Registrar evidencia de seguridad y errores controlados:
  - respuesta `401` y redirección a `/login`
  - respuesta `409` por nombre duplicado
  - respuesta `409` por eliminación rechazada con empleados asociados
- Incluir referencia a logs backend observados para esos escenarios (sin exponer secretos).

### Registro de ejecución (2026-03-19)

- `npm --prefix frontend run build` ✅ completado sin errores.
- `npm --prefix frontend run test -- --watch=false --include='src/app/departamentos/**/*.spec.ts'` ✅ 6 tests passing.
- `npm --prefix frontend run cypress:run -- --spec 'cypress/e2e/departamentos/**/*.cy.ts'` ✅ 10 tests passing (9 specs).
- Evidencia de seguridad en E2E:
  - `sesion-expirada.cy.ts` valida `401` y redirección a `/login`.
  - `conflicto-nombre.cy.ts` valida conflicto `409` al crear duplicado.
  - `eliminar-rechazado-asociacion.cy.ts` valida conflicto `409` al eliminar con empleados asociados.

### Bloqueo de stack frontend (CA-007)

- Intento ejecutado: `npx ng update @angular/core@22 @angular/cli@22 --allow-dirty --force`.
- Resultado: fallo por ausencia de versión `22` en registry para el entorno actual.
- Acción recomendada: registrar excepción RFC temporal o reevaluar la restricción de versión hasta disponibilidad oficial.
