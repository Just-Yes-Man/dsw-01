# Tasks: CRUD de Empleados en Frontend

**Input**: Design documents from `/specs/006-crud-empleados-frontend/`  
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/frontend-empleados-crud.md`, `quickstart.md`

**Tests**: Se incluyen tareas de pruebas porque el spec exige cobertura automatizada (CA-006, CA-008).  
**Organization**: Tareas agrupadas por historia de usuario para permitir implementación y validación independiente.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Ejecutable en paralelo (archivos distintos, sin dependencias directas incompletas)
- **[Story]**: Etiqueta de historia (`[US1]`, `[US2]`, `[US3]`)
- Todas las descripciones incluyen ruta de archivo específica

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar base técnica del frontend para implementar CRUD.

- [ ] T001 Actualizar dependencias Angular a 22 LTS en frontend/package.json y frontend/package-lock.json
- [ ] T002 Ajustar configuración de build/test tras migración Angular 22 en frontend/angular.json y frontend/tsconfig.json
- [ ] T003 [P] Configurar variables de entorno del módulo empleados en frontend/src/environments/environment.ts y frontend/src/environments/environment.prod.ts
- [ ] T004 [P] Crear carpeta base de feature en frontend/src/app/empleados/{models,services,components,pages}
- [ ] T005 [P] Registrar lineamientos de ejecución local del feature en specs/006-crud-empleados-frontend/quickstart.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura común bloqueante para todas las historias.

**⚠️ CRITICAL**: Ninguna historia inicia antes de completar esta fase.

- [ ] T006 Implementar modelos TypeScript base de empleado y paginación en frontend/src/app/empleados/models/empleado.model.ts
- [ ] T007 [P] Implementar modelos de formulario/estado y errores en frontend/src/app/empleados/models/empleado-form-state.model.ts
- [ ] T008 [P] Implementar cliente HTTP del dominio empleados en frontend/src/app/empleados/services/empleados-api.service.ts
- [ ] T009 Implementar fachada de estado y reglas CRUD (loading/error/showInactive) en frontend/src/app/empleados/services/empleados-store.service.ts
- [ ] T010 [P] Implementar normalización de errores (validación/conflicto/técnico) en frontend/src/app/empleados/services/empleados-error-mapper.service.ts
- [ ] T011 Implementar manejo transversal de sesión inválida (`401`) en frontend/src/app/auth/interceptors/session-expired.interceptor.ts
- [ ] T012 Registrar interceptor y providers HTTP en frontend/src/app/app.config.ts
- [ ] T013 Implementar pruebas unitarias de cimientos de servicios en frontend/src/app/empleados/services/empleados-store.service.spec.ts

**Checkpoint**: Base CRUD lista; historias pueden desarrollarse en paralelo.

---

## Phase 3: User Story 1 - Visualizar empleados (Priority: P1) 🎯 MVP

**Goal**: Mostrar listado paginado de empleados autenticados con filtro de inactivos.

**Independent Test**: Iniciar sesión, abrir `/empleados`, validar tabla paginada, estados de carga/vacío/error y toggle de inactivos.

### Tests for User Story 1

- [ ] T014 [P] [US1] Crear prueba unitaria de lista/paginación en frontend/src/app/empleados/pages/empleados-list-page.component.spec.ts
- [ ] T015 [P] [US1] Crear prueba E2E de listado paginado en frontend/cypress/e2e/empleados/listado-empleados.cy.ts
- [ ] T016 [P] [US1] Crear prueba E2E de visualización de inactivos con toggle en frontend/cypress/e2e/empleados/listado-inactivos-toggle.cy.ts

### Implementation for User Story 1

- [ ] T017 [US1] Implementar página de listado de empleados en frontend/src/app/empleados/pages/empleados-list-page.component.ts
- [ ] T018 [US1] Implementar plantilla de tabla, paginación y estados UI en frontend/src/app/empleados/pages/empleados-list-page.component.html
- [ ] T019 [US1] Implementar estilos de listado y estados visuales en frontend/src/app/empleados/pages/empleados-list-page.component.css
- [ ] T020 [US1] Integrar navegación protegida a nueva página en frontend/src/app/app.routes.ts
- [ ] T021 [US1] Actualizar pantalla home para usar listado como entrada principal en frontend/src/app/empleados/pages/empleados-home.component.ts

**Checkpoint**: US1 funcional e independiente (MVP visible).

---

## Phase 4: User Story 2 - Crear y editar empleados (Priority: P2)

**Goal**: Permitir alta/edición con validaciones de cliente, manejo de conflictos y reactivación desde edición.

**Independent Test**: Desde listado, crear empleado válido, editar empleado existente, validar errores de formulario y conflicto de unicidad.

### Tests for User Story 2

- [ ] T022 [P] [US2] Crear pruebas unitarias de formulario create/edit en frontend/src/app/empleados/components/empleado-form.component.spec.ts
- [ ] T023 [P] [US2] Crear prueba E2E de alta exitosa en frontend/cypress/e2e/empleados/crear-empleado.cy.ts
- [ ] T024 [P] [US2] Crear prueba E2E de edición exitosa en frontend/cypress/e2e/empleados/editar-empleado.cy.ts
- [ ] T025 [P] [US2] Crear prueba E2E de conflicto de unicidad email en frontend/cypress/e2e/empleados/conflicto-email.cy.ts
- [ ] T026 [P] [US2] Crear prueba E2E de reactivación desde edición en frontend/cypress/e2e/empleados/reactivar-empleado.cy.ts

### Implementation for User Story 2

- [ ] T027 [US2] Implementar componente de formulario reusable create/edit en frontend/src/app/empleados/components/empleado-form.component.ts
- [ ] T028 [US2] Implementar plantilla reactiva con validaciones y mensajes en frontend/src/app/empleados/components/empleado-form.component.html
- [ ] T029 [US2] Implementar estilos y estados de envío/bloqueo en frontend/src/app/empleados/components/empleado-form.component.css
- [ ] T030 [US2] Integrar flujo de creación en página dedicada en frontend/src/app/empleados/pages/empleado-create-page.component.ts
- [ ] T031 [US2] Integrar flujo de edición/reactivación en página dedicada en frontend/src/app/empleados/pages/empleado-edit-page.component.ts
- [ ] T032 [US2] Actualizar rutas para create/edit en frontend/src/app/app.routes.ts
- [ ] T033 [US2] Implementar mapeo de conflicto backend a mensaje accionable en frontend/src/app/empleados/services/empleados-error-mapper.service.ts

**Checkpoint**: US2 funcional e independiente sobre base de US1.

---

## Phase 5: User Story 3 - Desactivar empleados (Priority: P3)

**Goal**: Permitir desactivación lógica con confirmación explícita y actualización inmediata del listado.

**Independent Test**: Desde listado, desactivar empleado con confirmación, cancelar sin cambios y verificar ausencia en vista por defecto.

### Tests for User Story 3

- [ ] T034 [P] [US3] Crear pruebas unitarias de confirmación de desactivación en frontend/src/app/empleados/components/empleado-disable-confirm-dialog.component.spec.ts
- [ ] T035 [P] [US3] Crear prueba E2E de desactivación confirmada en frontend/cypress/e2e/empleados/desactivar-empleado.cy.ts
- [ ] T036 [P] [US3] Crear prueba E2E de cancelación de desactivación en frontend/cypress/e2e/empleados/cancelar-desactivacion.cy.ts

### Implementation for User Story 3

- [ ] T037 [US3] Implementar diálogo/componente de confirmación para desactivar en frontend/src/app/empleados/components/empleado-disable-confirm-dialog.component.ts
- [ ] T038 [US3] Implementar plantilla del diálogo con estado processing en frontend/src/app/empleados/components/empleado-disable-confirm-dialog.component.html
- [ ] T039 [US3] Integrar acción de desactivación lógica en listado usando `PUT estadoAcceso=INACTIVO` en frontend/src/app/empleados/pages/empleados-list-page.component.ts
- [ ] T040 [US3] Actualizar store para refresco consistente post-desactivación en frontend/src/app/empleados/services/empleados-store.service.ts

**Checkpoint**: US3 funcional e independiente, ciclo CRUD completo.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Endurecer calidad, documentación y verificación final.

- [ ] T041 [P] Documentar decisiones finales de UX/errores y flujos en specs/006-crud-empleados-frontend/contracts/frontend-empleados-crud.md
- [ ] T042 [P] Ejecutar y ajustar suite unitaria Angular del módulo en frontend/src/app/empleados/**/*.spec.ts
- [ ] T043 [P] Ejecutar y ajustar suite Cypress crítica CRUD en frontend/cypress/e2e/empleados/*.cy.ts
- [ ] T044 Validar quickstart end-to-end y actualizar pasos finales en specs/006-crud-empleados-frontend/quickstart.md
- [ ] T045 Verificar compatibilidad constitucional y registrar evidencia en specs/006-crud-empleados-frontend/plan.md
- [ ] T046 [P] Validar en E2E que cualquier usuario autenticado puede ejecutar flujos CRUD (sin restricción adicional de rol) en frontend/cypress/e2e/empleados/autorizacion-autenticado-crud.cy.ts
- [ ] T047 Medir y registrar evidencia de SC-001 (alta < 2 minutos en 90% de intentos válidos) en specs/006-crud-empleados-frontend/quickstart.md
- [ ] T048 Medir y registrar evidencia de SC-002 (listado < 2s en 95% de consultas nominales) en specs/006-crud-empleados-frontend/quickstart.md
- [ ] T049 Medir y registrar evidencia de SC-003 (edición exitosa en primer intento >= 90%) en specs/006-crud-empleados-frontend/quickstart.md
- [ ] T050 Consolidar evidencia reproducible de validación (unitarias + Cypress + métricas SC) en specs/006-crud-empleados-frontend/plan.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia inmediatamente.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea historias.
- **Phase 3+ (US1, US2, US3)**: dependen de Phase 2; luego pueden ejecutarse por prioridad o en paralelo.
- **Phase 6 (Polish)**: depende de historias completadas.

### User Story Dependencies

- **US1 (P1)**: inicia al cerrar Foundational; no depende de otras historias.
- **US2 (P2)**: inicia al cerrar Foundational; reutiliza listado/rutas de US1 pero mantiene prueba independiente.
- **US3 (P3)**: inicia al cerrar Foundational; se integra sobre listado y estado de US1.

### Suggested Story Completion Order

1. **US1** (MVP de visualización)
2. **US2** (mantenimiento operativo: crear/editar/reactivar)
3. **US3** (desactivación lógica con confirmación)

### Within Each User Story

- Pruebas primero (deben fallar inicialmente).
- Componentes/modelos antes de integración de páginas/rutas.
- Integración de servicios antes de cierre de historia.

## Parallel Opportunities

- **Setup**: T003, T004 y T005 en paralelo.
- **Foundational**: T007, T008 y T010 en paralelo tras T006.
- **US1**: T014, T015 y T016 en paralelo; T018 y T019 en paralelo tras T017.
- **US2**: T022, T023, T024, T025 y T026 en paralelo; T028 y T029 en paralelo tras T027.
- **US3**: T034, T035 y T036 en paralelo; T038 en paralelo parcial con T037.

## Parallel Example: User Story 1

```bash
Task: "T014 [US1] prueba unitaria de listado en frontend/src/app/empleados/pages/empleados-list-page.component.spec.ts"
Task: "T015 [US1] E2E listado paginado en frontend/cypress/e2e/empleados/listado-empleados.cy.ts"
Task: "T016 [US1] E2E toggle inactivos en frontend/cypress/e2e/empleados/listado-inactivos-toggle.cy.ts"
```

## Parallel Example: User Story 2

```bash
Task: "T023 [US2] E2E alta en frontend/cypress/e2e/empleados/crear-empleado.cy.ts"
Task: "T024 [US2] E2E edición en frontend/cypress/e2e/empleados/editar-empleado.cy.ts"
Task: "T025 [US2] E2E conflicto email en frontend/cypress/e2e/empleados/conflicto-email.cy.ts"
Task: "T026 [US2] E2E reactivación en frontend/cypress/e2e/empleados/reactivar-empleado.cy.ts"
```

## Parallel Example: User Story 3

```bash
Task: "T035 [US3] E2E desactivación en frontend/cypress/e2e/empleados/desactivar-empleado.cy.ts"
Task: "T036 [US3] E2E cancelación en frontend/cypress/e2e/empleados/cancelar-desactivacion.cy.ts"
Task: "T034 [US3] unit test diálogo en frontend/src/app/empleados/components/empleado-disable-confirm-dialog.component.spec.ts"
```

## Implementation Strategy

### MVP First (US1 only)

1. Completar Phase 1 + Phase 2.
2. Completar US1 (Phase 3).
3. Validar listado/paginación/toggle inactivos como incremento demoable.

### Incremental Delivery

1. Base técnica (Setup + Foundational).
2. US1 (visualización) → validar/desplegar.
3. US2 (crear/editar/reactivar) → validar/desplegar.
4. US3 (desactivar con confirmación) → validar/desplegar.
5. Polish final con suites completas.

### Parallel Team Strategy

1. Equipo completo en Setup + Foundational.
2. Luego distribución sugerida:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integrar en Phase 6 con validación global.
