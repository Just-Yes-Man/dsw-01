# Feature Specification: CRUD de Departamentos en Frontend

**Feature Branch**: `005-crud-departamentos`  
**Created**: 2026-03-18  
**Status**: Draft  
**Input**: User description: "vamos a hacer otro crud igual al de usuarios esta vez para los departamentos"

## Clarifications

### Session 2026-03-18

- Q: ¿Quién puede ejecutar las acciones CRUD de departamentos en frontend? → A: Cualquier usuario autenticado puede listar, crear, editar y eliminar departamentos.
- Q: ¿Se incluye reactivación de departamentos inactivos? → A: No aplica para esta versión; se prioriza eliminación física en frontend.
- Q: ¿Cómo se deben visualizar los departamentos inactivos en listado? → A: No aplica para esta versión; el listado trabaja sobre departamentos existentes tras altas/ediciones/eliminaciones.
- Q: ¿Con qué estado se debe crear un departamento nuevo? → A: Se mantiene comportamiento vigente de alta estándar y no se agrega flujo explícito de activación/reactivación en frontend.
- Q: ¿Debe permitirse eliminación física de departamentos desde frontend? → A: Sí; se permite eliminación física cuando el departamento no tenga empleados asociados.

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

### User Story 1 - Visualizar departamentos (Priority: P1)

Como usuario autenticado, quiero ver el listado de departamentos para conocer la estructura organizacional vigente y consultar rápidamente sus datos principales.

**Why this priority**: Sin el listado no existe valor mínimo del módulo y no se puede iniciar ningún flujo de mantenimiento.

**Independent Test**: Iniciar sesión, abrir la pantalla de departamentos y comprobar tabla paginada con estados de carga, vacío y error.

**Acceptance Scenarios**:

1. **Given** que el usuario está autenticado y existen departamentos, **When** ingresa al módulo de departamentos, **Then** visualiza el listado paginado con al menos identificador, nombre y estado.
2. **Given** que hay más de una página de resultados, **When** el usuario navega entre páginas, **Then** la vista actualiza el bloque de resultados manteniendo el contexto de navegación.
3. **Given** que no hay departamentos disponibles, **When** se carga la pantalla, **Then** se muestra un estado vacío claro.

---

### User Story 2 - Crear y editar departamentos (Priority: P2)

Como usuario autenticado, quiero crear y editar departamentos desde frontend para mantener actualizada la estructura organizacional sin depender de herramientas técnicas.

**Why this priority**: Permite mantenimiento operativo continuo del catálogo una vez disponible la consulta.

**Independent Test**: Desde el listado, crear un departamento válido y editar uno existente, verificando que los cambios se reflejan en la vista.

**Acceptance Scenarios**:

1. **Given** que el usuario abre el formulario de alta, **When** envía datos válidos, **Then** el sistema confirma la creación y el departamento aparece en el listado.
2. **Given** que el usuario abre el formulario de edición de un departamento existente, **When** guarda cambios válidos, **Then** la vista muestra los datos actualizados.
3. **Given** que el nombre del departamento entra en conflicto con uno existente, **When** intenta guardar, **Then** el sistema muestra un mensaje de conflicto y mantiene el formulario editable.

---

### User Story 3 - Eliminar departamentos (Priority: P3)

Como usuario autenticado, quiero eliminar departamentos desde frontend para retirar registros que ya no deben existir en el catálogo operativo.

**Why this priority**: Completa el ciclo CRUD funcional con eliminación explícita para mantenimiento del catálogo.

**Independent Test**: Desde el listado, eliminar un departamento sin empleados asociados y validar que deja de aparecer en consultas posteriores.

**Acceptance Scenarios**:

1. **Given** que el usuario selecciona eliminar un departamento sin empleados asociados, **When** confirma la acción, **Then** el sistema elimina el registro y deja de aparecer en el listado.
2. **Given** que el departamento tiene empleados asociados, **When** el usuario intenta eliminarlo, **Then** el sistema informa que la operación no es permitida y no aplica cambios.
3. **Given** que el usuario cancela la confirmación de eliminación, **When** cierra el diálogo, **Then** no se modifica el departamento.

---

### Edge Cases

- ¿Qué ocurre si el usuario intenta acceder a una página mayor al total disponible tras crear o eliminar registros?
- ¿Qué ocurre si la sesión expira durante una operación de creación, edición o eliminación?
- ¿Qué mensaje se muestra cuando el backend no está disponible temporalmente?
- ¿Cómo se evita doble envío por clic repetido en acciones de guardar o eliminar?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El frontend DEBE mostrar un listado paginado de departamentos para usuarios autenticados.
- **FR-002**: El frontend DEBE permitir crear departamentos mediante formulario con validaciones de campos obligatorios.
- **FR-003**: El frontend DEBE permitir editar departamentos existentes desde formulario prellenado.
- **FR-004**: El frontend DEBE permitir eliminación física de departamentos con confirmación explícita cuando no existan empleados asociados.
- **FR-005**: El frontend DEBE reflejar en la vista los cambios de crear, editar y eliminar sin recarga manual completa de la aplicación.
- **FR-006**: El frontend DEBE mostrar estados de carga, vacío y error para operaciones de listado y mantenimiento.
- **FR-007**: El frontend DEBE mostrar mensajes específicos de conflicto cuando el backend reporte nombre duplicado de departamento.
- **FR-008**: El frontend DEBE bloquear acciones duplicadas mientras una operación de guardado o eliminación esté en curso.
- **FR-009**: El frontend DEBE redirigir a login si detecta sesión inválida durante el uso del módulo de departamentos.
- **FR-010**: El frontend DEBE informar de manera clara cuando una eliminación sea rechazada por asociación de empleados.
- **FR-011**: El frontend DEBE mantener las acciones CRUD de departamentos (listar, crear, editar y eliminar) disponibles para cualquier usuario autenticado, sin restricción adicional por rol en esta fase.

### Constitution Alignment *(mandatory)*

- **CA-001**: The feature MUST run on Spring Boot 3 with Java 17, or document an approved exception.
- **CA-002**: The feature MUST define Basic Authentication behavior for protected endpoints.
- **CA-003**: The feature MUST define PostgreSQL persistence impact and Docker/Docker Compose needs.
- **CA-004**: The feature MUST define Swagger/OpenAPI contract updates for every endpoint change with routes versioned as `/api/v{major}/...`.
- **CA-005**: The feature MUST define pagination for collection queries with 10 instances per response.
- **CA-006**: The feature MUST define required automated tests and logging/security evidence.
- **CA-007**: If the feature includes web frontend scope, it MUST use Angular 22 LTS (or document approved exception).
- **CA-008**: If the feature includes web frontend scope, it MUST define Cypress E2E coverage for critical flows.

### Key Entities *(include if feature involves data)*

- **DepartamentoView**: Representa los datos visibles de un departamento en listado y edición (identificador, nombre, estado).
- **DepartamentoFormState**: Representa el estado del formulario de alta/edición (valores, validaciones, estado de envío y errores de backend).
- **DepartamentoListState**: Representa el estado de consulta paginada (página, tamaño, total, resultados, carga y error).
- **DeleteDepartamentoState**: Representa el estado de confirmación y resultado de la eliminación desde frontend.

## Assumptions

- El backend ya dispone de endpoints versionados para listar, crear, editar y eliminar departamentos.
- El listado de departamentos en esta fase trabaja sobre registros disponibles en backend y paginación de 10 elementos por respuesta.
- El acceso al módulo se realiza con una sesión de usuario autenticado ya iniciada en frontend.
- El comportamiento “igual al CRUD de usuarios” se interpreta como experiencia equivalente de mantenimiento (listar, crear, editar, eliminar con confirmación y mensajes de error claros) aplicada al dominio de departamentos.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un usuario autenticado puede crear un departamento válido en menos de 2 minutos en al menos el 90% de los intentos.
- **SC-002**: El 95% de consultas de listado de departamentos muestra resultados en menos de 2 segundos bajo condiciones nominales.
- **SC-003**: Al menos el 90% de ediciones válidas de departamentos se completa exitosamente al primer intento.
- **SC-004**: El 100% de intentos de eliminación de departamentos requiere confirmación explícita antes de aplicar cambios.
- **SC-005**: La cobertura E2E definida para flujos críticos de departamentos alcanza 100% de ejecución exitosa en la validación del feature.
