# Feature Specification: CRUD de Empleados en Frontend

**Feature Branch**: `006-crud-empleados-frontend`  
**Created**: 2026-03-18  
**Status**: Draft  
**Input**: User description: "vamos a hacer un crud en el front end para los empleados, con las capacidades basicas del CRUD pero de manera de frontend."

## Clarifications

### Session 2026-03-18

- Q: ¿Quién puede ejecutar acciones de crear/editar/eliminar en el frontend? → A: Cualquier usuario autenticado puede crear, editar y eliminar empleados.
- Q: ¿Cómo debe ser la eliminación de empleados en el frontend? → A: La eliminación será lógica: el empleado se marca como inactivo y no se muestra en el listado por defecto.
- Q: ¿Cómo se deben visualizar los empleados inactivos en el listado? → A: Se muestran empleados activos por defecto y se habilita un control para alternar la visualización de inactivos.
- Q: ¿Cómo validar unicidad de correo al crear o editar empleados? → A: Se valida formato en frontend y la unicidad del correo en backend, mostrando mensaje específico de conflicto cuando aplique.
- Q: ¿Debe incluirse reactivación de empleados inactivos en este feature? → A: Sí, se incluye reactivación desde edición cambiando el estado a activo.

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Visualizar empleados (Priority: P1)

Como usuario autenticado, quiero ver el listado de empleados en el frontend para consultar quiénes existen, su información principal y navegar entre páginas.

**Why this priority**: Sin visualización no hay valor de negocio mínimo del CRUD, ni base para editar/desactivar.

**Independent Test**: Desde una sesión válida, entrar al módulo de empleados y verificar que se muestra una tabla con datos, estado de carga y paginación funcional.

**Acceptance Scenarios**:

1. **Given** que el usuario está autenticado y existen empleados, **When** entra a la pantalla de empleados, **Then** ve el listado paginado con al menos clave, nombre, correo y estado.
2. **Given** que el listado tiene más de una página, **When** el usuario cambia de página, **Then** el frontend muestra el nuevo bloque de resultados sin perder el contexto de navegación.
3. **Given** que no hay empleados o la consulta no devuelve registros, **When** se carga la vista, **Then** se muestra un estado vacío claro y accionable.
4. **Given** que el usuario necesita revisar empleados inactivos, **When** activa el control para mostrarlos, **Then** el listado incluye los registros inactivos según el estado seleccionado.

---

### User Story 2 - Crear y editar empleados (Priority: P2)

Como usuario autenticado, quiero crear y editar empleados desde el frontend para mantener actualizada la información operativa del personal.

**Why this priority**: Crear y actualizar registros aporta el núcleo operativo del mantenimiento diario, después de poder consultar.

**Independent Test**: Desde la vista de empleados, abrir formulario de alta, guardar un empleado válido y luego editarlo verificando que los cambios se reflejan en el listado.

**Acceptance Scenarios**:

1. **Given** que el usuario abre el formulario de alta, **When** completa campos obligatorios válidos y guarda, **Then** el sistema confirma creación y el nuevo empleado aparece en el listado.
2. **Given** que el usuario abre el formulario de edición de un empleado existente, **When** actualiza datos válidos y guarda, **Then** el sistema confirma actualización y el listado refleja los cambios.
3. **Given** que el formulario tiene datos inválidos, **When** intenta guardar, **Then** se muestran mensajes de validación por campo y no se aplica ningún cambio.
4. **Given** que el backend detecta conflicto de unicidad en correo, **When** el usuario intenta guardar, **Then** el frontend muestra un mensaje específico de conflicto y mantiene el formulario editable.
5. **Given** que un empleado está inactivo, **When** el usuario lo edita y cambia su estado a activo, **Then** el sistema confirma la actualización y el empleado vuelve a mostrarse en el listado por defecto.

---

### User Story 3 - Desactivar empleados (Priority: P3)

Como usuario autenticado, quiero desactivar empleados desde el frontend para retirar registros que ya no deben permanecer activos sin borrarlos físicamente.

**Why this priority**: Completa el ciclo CRUD, pero depende de tener primero consulta y mantenimiento para validar impacto.

**Independent Test**: Desde el listado, desactivar un empleado con confirmación y validar que ya no aparece en la vista actual ni en recargas posteriores por defecto.

**Acceptance Scenarios**:

1. **Given** que el usuario selecciona desactivar un empleado, **When** confirma la acción, **Then** el sistema marca el registro como inactivo y actualiza el listado.
2. **Given** que el usuario cancela la confirmación, **When** cierra el diálogo de desactivación, **Then** no se aplica ningún cambio en el empleado.

---

### Edge Cases

- ¿Qué ocurre cuando el usuario intenta navegar a una página mayor al total disponible tras crear/desactivar registros?
- ¿Cómo se comporta la vista cuando expira la sesión durante una operación de creación/edición/desactivación?
- ¿Qué mensaje se muestra cuando hay error de red o backend no disponible durante operaciones CRUD?
- ¿Cómo se evita el doble envío de formularios por clic repetido en guardar/desactivar?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El frontend DEBE mostrar un listado paginado de empleados para usuarios autenticados, mostrando activos por defecto y permitiendo alternar visualización de inactivos.
- **FR-002**: El frontend DEBE permitir crear un empleado mediante formulario con validaciones de campos obligatorios antes de enviar.
- **FR-003**: El frontend DEBE permitir editar un empleado existente mediante formulario prellenado con sus datos actuales.
- **FR-004**: El frontend DEBE permitir desactivar (eliminación lógica) un empleado con confirmación explícita de la acción.
- **FR-005**: El frontend DEBE mostrar estados de carga, éxito y error en todas las operaciones CRUD.
- **FR-006**: El frontend DEBE mantener consistencia visual del listado tras crear, editar o desactivar sin requerir recarga manual de la página.
- **FR-007**: El frontend DEBE redirigir a login cuando una operación CRUD detecte sesión inválida o expirada.
- **FR-008**: El frontend DEBE evitar envíos duplicados mientras una operación de guardado o desactivación está en curso.
- **FR-009**: El frontend DEBE mantener las acciones CRUD de empleados (incluyendo desactivación como eliminación lógica) accesibles para cualquier usuario autenticado, sin restricción adicional por rol en esta fase.
- **FR-010**: El frontend DEBE mostrar mensajes de validación comprensibles para errores de entrada y mensajes genéricos para errores técnicos.
- **FR-011**: El frontend DEBE validar formato de campos en cliente y manejar conflictos de unicidad de correo reportados por backend con mensajes específicos y accionables.
- **FR-012**: El frontend DEBE permitir reactivar empleados inactivos desde el flujo de edición al cambiar su estado a activo.

### Constitution Alignment *(mandatory)*

- **CA-001**: La solución DEBE ser compatible con backend Spring Boot 3 y Java 17 existentes, sin romper contratos actuales.
- **CA-002**: El comportamiento de autenticación DEBE mantener endpoints de negocio protegidos con Basic Authentication y permitir su consumo desde frontend sin credenciales hardcodeadas ni exposición de secretos en cliente.
- **CA-003**: El feature DEBE considerar ejecución local en Docker/Docker Compose para pruebas integradas frontend-backend.
- **CA-004**: Si requiere cambios de API, DEBE mantener versionado `/api/v{major}/...` y reflejar contratos en OpenAPI.
- **CA-005**: La visualización de colecciones DEBE respetar paginación de 10 elementos por respuesta cuando aplique.
- **CA-006**: El feature DEBE definir pruebas automatizadas mínimas (unitarias y E2E) para flujos CRUD críticos y manejo de errores.
- **CA-007**: Al incluir frontend web, DEBE alinearse con Angular 22 LTS o documentar excepción aprobada.
- **CA-008**: Al incluir frontend web, DEBE incluir cobertura Cypress de flujos críticos CRUD.

### Key Entities *(include if feature involves data)*

- **EmpleadoView**: Representa la información visible y editable en la UI de empleados (clave, nombre, correo, teléfono, dirección, estado y referencia de departamento).
- **EmpleadoFormState**: Representa el estado del formulario de alta/edición (valores, validaciones, estado de envío y errores).
- **EmpleadoListState**: Representa el estado de la lista (página actual, total, resultados, carga, vacío y error).
- **DeleteConfirmationState**: Representa la confirmación de desactivación (eliminación lógica), con registro objetivo, estado de procesamiento y resultado.

## Assumptions

- El backend de empleados existente ya expone operaciones para crear, listar, editar y cambiar estado de empleados (soporte para desactivación lógica).
- El usuario que usa el módulo ya llega autenticado mediante el flujo de login implementado previamente.
- Para cumplir seguridad constitucional, el frontend consumirá endpoints de negocio protegidos (`/api/v1/empleados`) bajo el esquema de autenticación vigente del backend y sin credenciales hardcodeadas en cliente.
- No se requiere en esta fase agregar nuevos campos de negocio fuera de los ya manejados por empleados.
- El alcance es exclusivamente frontend (interfaz y comportamiento), salvo ajustes menores de contrato si fueran estrictamente necesarios.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un usuario autenticado puede completar alta de empleado en menos de 2 minutos en el 90% de intentos válidos.
- **SC-002**: El 95% de consultas del listado muestran resultados al usuario en menos de 2 segundos en condiciones nominales.
- **SC-003**: Al menos 90% de intentos de edición válidos finalizan con confirmación de éxito en primer intento.
- **SC-004**: El 100% de intentos de desactivación requieren confirmación explícita antes de aplicar cambios.
- **SC-005**: En pruebas E2E de flujos críticos CRUD, la tasa de éxito debe ser del 100% en el pipeline acordado del feature.
