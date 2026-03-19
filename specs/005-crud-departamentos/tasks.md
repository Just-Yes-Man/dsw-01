# Tasks: CRUD de Departamentos en Frontend

**Input**: Design documents from `/specs/005-crud-departamentos/`  
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/frontend-departamentos-crud.md, quickstart.md

**Tests**: Se incluyen tareas de pruebas porque el spec exige cobertura E2E de flujos críticos (CA-008, SC-005).

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar la estructura del módulo frontend de departamentos.

- [X] T001 Crear estructura base del módulo en frontend/src/app/departamentos/
- [X] T002 [P] Crear modelo de contrato API en frontend/src/app/departamentos/models/departamento.model.ts
- [X] T003 [P] Crear estado de UI en frontend/src/app/departamentos/models/departamento-form-state.model.ts
- [X] T004 Configurar path de API para departamentos en frontend/src/environments/environment.ts
- [X] T043 Verificar versión objetivo Angular 22 LTS en frontend/package.json y documentar brecha si existe en specs/005-crud-departamentos/quickstart.md
- [ ] T044 Ejecutar actualización de Angular a 22 LTS (si aplica) y validar build base en frontend/package.json y frontend/angular.json

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura compartida que bloquea todas las historias hasta completarse.

- [X] T005 Implementar cliente HTTP de departamentos en frontend/src/app/departamentos/services/departamentos-api.service.ts
- [X] T006 [P] Implementar mapeador de errores de departamentos en frontend/src/app/departamentos/services/departamentos-error-mapper.service.ts
- [X] T007 Implementar store reactivo base en frontend/src/app/departamentos/services/departamentos-store.service.ts
- [X] T008 [P] Crear prueba unitaria del cliente HTTP en frontend/src/app/departamentos/services/departamentos-api.service.spec.ts
- [X] T009 [P] Crear prueba unitaria del store base en frontend/src/app/departamentos/services/departamentos-store.service.spec.ts
- [X] T010 Registrar rutas protegidas del módulo en frontend/src/app/app.routes.ts
- [X] T045 Documentar trazabilidad CA-004 (sin cambios de endpoints/versionado `/api/v1/...`) en specs/005-crud-departamentos/contracts/frontend-departamentos-crud.md

**Checkpoint**: Base del módulo lista; se puede implementar cada historia de usuario de forma independiente.

---

## Phase 3: User Story 1 - Visualizar departamentos (Priority: P1) 🎯 MVP

**Goal**: Permitir al usuario autenticado ver listado paginado con estados de carga, vacío y error.

**Independent Test**: Login válido, abrir `/departamentos`, verificar tabla paginada y manejo de estados.

### Tests for User Story 1

- [X] T011 [P] [US1] Crear prueba unitaria de página de listado en frontend/src/app/departamentos/pages/departamentos-list-page.component.spec.ts
- [X] T012 [P] [US1] Crear E2E de listado paginado en frontend/cypress/e2e/departamentos/listado-departamentos.cy.ts
- [X] T013 [P] [US1] Crear E2E de estado vacío y error de listado en frontend/cypress/e2e/departamentos/listado-vacio-error.cy.ts

### Implementation for User Story 1

- [X] T014 [P] [US1] Implementar plantilla de listado en frontend/src/app/departamentos/pages/departamentos-list-page.component.html
- [X] T015 [P] [US1] Implementar estilos de listado en frontend/src/app/departamentos/pages/departamentos-list-page.component.css
- [X] T016 [US1] Implementar lógica de listado y paginación en frontend/src/app/departamentos/pages/departamentos-list-page.component.ts
- [X] T017 [US1] Implementar carga de páginas y estados (`loading/error`) en frontend/src/app/departamentos/services/departamentos-store.service.ts
- [X] T018 [US1] Ajustar contrato de navegación protegida para `/departamentos` en frontend/src/app/app.routes.ts

**Checkpoint**: US1 funcional y demostrable como MVP.

---

## Phase 4: User Story 2 - Crear y editar departamentos (Priority: P2)

**Goal**: Permitir alta y edición con validación y manejo de conflicto por nombre duplicado.

**Independent Test**: Crear y editar desde UI; verificar actualización en listado y mensaje de conflicto.

### Tests for User Story 2

- [X] T019 [P] [US2] Crear prueba unitaria del formulario de departamento en frontend/src/app/departamentos/components/departamento-form.component.spec.ts
- [X] T020 [P] [US2] Crear E2E de alta de departamento en frontend/cypress/e2e/departamentos/crear-departamento.cy.ts
- [X] T021 [P] [US2] Crear E2E de edición de departamento en frontend/cypress/e2e/departamentos/editar-departamento.cy.ts
- [X] T022 [P] [US2] Crear E2E de conflicto por nombre duplicado en frontend/cypress/e2e/departamentos/conflicto-nombre.cy.ts

### Implementation for User Story 2

- [X] T023 [P] [US2] Implementar componente de formulario en frontend/src/app/departamentos/components/departamento-form.component.ts
- [X] T024 [P] [US2] Implementar vista del formulario en frontend/src/app/departamentos/components/departamento-form.component.html
- [X] T025 [US2] Implementar página de creación en frontend/src/app/departamentos/pages/departamento-create-page.component.ts
- [X] T026 [US2] Implementar página de edición en frontend/src/app/departamentos/pages/departamento-edit-page.component.ts
- [X] T027 [US2] Implementar operaciones `create/update` y refresh de lista en frontend/src/app/departamentos/services/departamentos-store.service.ts
- [X] T028 [US2] Implementar mapeo de `409 CONFLICT` a mensaje de nombre duplicado en frontend/src/app/departamentos/services/departamentos-error-mapper.service.ts
- [X] T029 [US2] Registrar rutas protegidas `/departamentos/nuevo` y `/departamentos/:id/editar` en frontend/src/app/app.routes.ts

**Checkpoint**: US2 completa y testable sin depender de US3.

---

## Phase 5: User Story 3 - Eliminar departamentos (Priority: P3)

**Goal**: Permitir eliminación con confirmación y feedback cuando backend rechaza por empleados asociados.

**Independent Test**: Eliminar uno sin asociados (éxito) y otro con asociados (conflicto sin cambios).

### Tests for User Story 3

- [X] T030 [P] [US3] Crear prueba unitaria del diálogo de confirmación en frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.spec.ts
- [X] T031 [P] [US3] Crear E2E de eliminación exitosa en frontend/cypress/e2e/departamentos/eliminar-departamento.cy.ts
- [X] T032 [P] [US3] Crear E2E de eliminación rechazada por asociación en frontend/cypress/e2e/departamentos/eliminar-rechazado-asociacion.cy.ts
- [X] T033 [P] [US3] Crear E2E de cancelación de eliminación en frontend/cypress/e2e/departamentos/cancelar-eliminacion.cy.ts

### Implementation for User Story 3

- [X] T034 [P] [US3] Implementar componente de confirmación en frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.ts
- [X] T035 [P] [US3] Implementar plantilla de confirmación en frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.html
- [X] T036 [US3] Integrar flujo abrir/cancelar/confirmar eliminación en frontend/src/app/departamentos/pages/departamentos-list-page.component.ts
- [X] T037 [US3] Implementar operación `delete` y manejo de conflicto en frontend/src/app/departamentos/services/departamentos-store.service.ts
- [X] T038 [US3] Implementar mapeo de rechazo por empleados asociados (`409`) en frontend/src/app/departamentos/services/departamentos-error-mapper.service.ts

**Checkpoint**: US3 funcional y sin romper US1/US2.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Endurecer calidad y cerrar validación integral del feature.

- [X] T039 [P] Agregar E2E de sesión expirada en módulo departamentos en frontend/cypress/e2e/departamentos/sesion-expirada.cy.ts
- [X] T040 Integrar cierre de sesión y redirección a `/login` en listado de departamentos para reforzar FR-009 en frontend/src/app/departamentos/pages/departamentos-list-page.component.ts
- [X] T041 [P] Ajustar quickstart con matriz de ejecución de pruebas y evidencia de seguridad/logging (401, 409 y trazas backend) en specs/005-crud-departamentos/quickstart.md
- [X] T042 Ejecutar suite E2E de departamentos y registrar resultado en specs/005-crud-departamentos/quickstart.md
- [X] T046 Verificar evidencia CA-006 con resultados reproducibles (`npm test`, `npm run cypress:run`, capturas de 401/409) en specs/005-crud-departamentos/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia inmediatamente.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phase 3 (US1)**: depende de Phase 2; entrega MVP.
- **Phase 4 (US2)**: depende de Phase 2; puede avanzar en paralelo con US3 cuando haya capacidad.
- **Phase 5 (US3)**: depende de Phase 2; recomendable después de US1 para reutilizar listado.
- **Phase 6 (Polish)**: depende de US1+US2+US3 completos.

### User Story Dependencies

- **US1 (P1)**: sin dependencia funcional de otras historias.
- **US2 (P2)**: usa listado/refresh de US1, pero puede desarrollarse de forma independiente con store base.
- **US3 (P3)**: reutiliza listado de US1 y manejo de errores de US2 para mensajes consistentes.

### Within Each User Story

- Primero pruebas (unitarias/E2E), luego implementación.
- Modelos/plantillas/componentes en paralelo; lógica de store/rutas después.
- Cerrar cada historia con su prueba independiente antes de pasar a la siguiente prioridad.

---

## Parallel Opportunities

- **Setup**: T002 y T003 en paralelo.
- **Foundational**: T006, T008 y T009 en paralelo tras T005.
- **US1**: T011, T012, T013 en paralelo; T014 y T015 en paralelo.
- **US2**: T019, T020, T021, T022 en paralelo; T023 y T024 en paralelo.
- **US3**: T030, T031, T032, T033 en paralelo; T034 y T035 en paralelo.
- **Polish**: T039, T041 y T046 en paralelo.

---

## Parallel Example: User Story 1

```bash
# Tests US1 en paralelo
Task: "T011 [US1] frontend/src/app/departamentos/pages/departamentos-list-page.component.spec.ts"
Task: "T012 [US1] frontend/cypress/e2e/departamentos/listado-departamentos.cy.ts"
Task: "T013 [US1] frontend/cypress/e2e/departamentos/listado-vacio-error.cy.ts"

# UI US1 en paralelo
Task: "T014 [US1] frontend/src/app/departamentos/pages/departamentos-list-page.component.html"
Task: "T015 [US1] frontend/src/app/departamentos/pages/departamentos-list-page.component.css"
```

## Parallel Example: User Story 2

```bash
# Tests US2 en paralelo
Task: "T019 [US2] frontend/src/app/departamentos/components/departamento-form.component.spec.ts"
Task: "T020 [US2] frontend/cypress/e2e/departamentos/crear-departamento.cy.ts"
Task: "T021 [US2] frontend/cypress/e2e/departamentos/editar-departamento.cy.ts"
Task: "T022 [US2] frontend/cypress/e2e/departamentos/conflicto-nombre.cy.ts"

# Formulario US2 en paralelo
Task: "T023 [US2] frontend/src/app/departamentos/components/departamento-form.component.ts"
Task: "T024 [US2] frontend/src/app/departamentos/components/departamento-form.component.html"
```

## Parallel Example: User Story 3

```bash
# Tests US3 en paralelo
Task: "T030 [US3] frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.spec.ts"
Task: "T031 [US3] frontend/cypress/e2e/departamentos/eliminar-departamento.cy.ts"
Task: "T032 [US3] frontend/cypress/e2e/departamentos/eliminar-rechazado-asociacion.cy.ts"
Task: "T033 [US3] frontend/cypress/e2e/departamentos/cancelar-eliminacion.cy.ts"

# Confirmación US3 en paralelo
Task: "T034 [US3] frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.ts"
Task: "T035 [US3] frontend/src/app/departamentos/components/departamento-delete-confirm-dialog.component.html"
```

---

## Implementation Strategy

### MVP First (US1)

1. Completar Phase 1 + Phase 2.
2. Completar US1 (Phase 3) y validar listado paginado con estados.
3. Demostrar MVP navegable en `/departamentos`.

### Incremental Delivery

1. Entregar US1 (consulta).
2. Entregar US2 (alta/edición con conflicto).
3. Entregar US3 (eliminación con confirmación y rechazo por asociación).
4. Cerrar con Polish y corrida E2E completa.

### Suggested MVP Scope

- MVP sugerido: **solo User Story 1 (P1)** con su cobertura de pruebas de listado.
