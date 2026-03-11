# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-crud-empleados`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "crea un crud de empleados con los campos clave, nombre, direccion y telefono. Donde clave sea el PK y nombre, direccion y telefono sea de 100 caracteres"

## Clarifications

### Session 2026-02-26

- Q: ¿Qué tipo/formato tendrá `clave` como PK? → A: `clave` con prefijo fijo `EMP-` seguido de autonumérico, tratada como PK compuesta.

### Session 2026-03-04

- Q: ¿Cómo se aplicará versionado y paginación en la API? → A: Rutas versionadas en path `/api/v1/...` y consultas de colección paginadas con 10 instancias por consulta.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar empleado (Priority: P1)

Como usuario autorizado, quiero registrar un empleado con nombre, dirección y teléfono para comenzar a administrar el catálogo de empleados.

**Why this priority**: Sin alta de empleados no existe información base para consultar, editar o eliminar.

**Independent Test**: Puede probarse creando un empleado con datos válidos y verificando que queda disponible para consulta posterior.

**Acceptance Scenarios**:

1. **Given** datos válidos de nombre, dirección y teléfono, **When** se registra un empleado, **Then** el sistema almacena el registro, asigna una `clave` con formato `EMP-<autonumérico>` y confirma la creación.
2. **Given** que se intenta registrar un empleado con nombre, dirección o teléfono mayor a 100 caracteres, **When** se envía la solicitud, **Then** el sistema rechaza la operación e informa el error de validación.

---

### User Story 2 - Consultar empleados (Priority: P2)

Como usuario autorizado, quiero consultar empleados individuales y listados para localizar información rápidamente.

**Why this priority**: La consulta es la operación más frecuente después del alta y habilita validaciones operativas.

**Independent Test**: Puede probarse listando empleados existentes y consultando por clave para validar que se devuelven los datos esperados.

**Acceptance Scenarios**:

1. **Given** que existen empleados registrados, **When** se solicita el listado general en la ruta versionada, **Then** el sistema devuelve una página de hasta 10 empleados con sus campos.
2. **Given** una clave existente, **When** se consulta el empleado por clave, **Then** el sistema devuelve exactamente ese registro.
3. **Given** una clave inexistente, **When** se consulta por clave, **Then** el sistema responde que el recurso no existe.

---

### User Story 3 - Actualizar y eliminar empleado (Priority: P3)

Como usuario autorizado, quiero actualizar o eliminar empleados para mantener la información correcta y vigente.

**Why this priority**: Completa el ciclo de mantenimiento del catálogo una vez que ya se puede crear y consultar.

**Independent Test**: Puede probarse actualizando un empleado existente y luego eliminándolo, verificando ambos resultados sin depender de otras historias adicionales.

**Acceptance Scenarios**:

1. **Given** un empleado existente, **When** se actualizan nombre, dirección o teléfono con valores válidos, **Then** el sistema guarda los cambios.
2. **Given** un empleado existente, **When** se solicita su eliminación, **Then** el sistema elimina el registro y deja de retornarlo en búsquedas posteriores.
3. **Given** un empleado inexistente, **When** se intenta actualizar o eliminar, **Then** el sistema responde que el recurso no existe.

### Edge Cases

- Intento de alta enviando manualmente el campo `clave` en la solicitud.
- Campos `nombre`, `direccion` o `telefono` con longitud exactamente de 100 caracteres (deben aceptarse).
- Campos `nombre`, `direccion` o `telefono` vacíos o nulos.
- Operaciones protegidas ejecutadas sin credenciales válidas.
- Solicitud de listado sin parámetros de paginación explícitos (debe devolver 10 instancias por consulta).
- Solicitud de listado con página fuera de rango (debe devolver colección vacía sin error de servidor).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir crear empleados con los campos `nombre`, `direccion` y `telefono`.
- **FR-002**: El sistema MUST definir `clave` como identificador único e inmutable con formato `EMP-<autonumérico>`, donde la PK compuesta se base en prefijo fijo `EMP-` + consecutivo numérico autogenerado.
- **FR-003**: El sistema MUST limitar `nombre`, `direccion` y `telefono` a un máximo de 100 caracteres cada uno.
- **FR-004**: El sistema MUST permitir consultar la lista de empleados registrados mediante rutas versionadas en formato `/api/v{major}/...`.
- **FR-005**: El sistema MUST permitir consultar un empleado específico por su `clave`.
- **FR-006**: El sistema MUST permitir actualizar los datos de `nombre`, `direccion` y `telefono` de un empleado existente.
- **FR-007**: El sistema MUST permitir eliminar un empleado existente por su `clave`.
- **FR-008**: El sistema MUST rechazar cualquier intento de asignar manualmente `clave`, `prefijo` o consecutivo en operaciones de creación o actualización.
- **FR-009**: El sistema MUST devolver errores claros cuando el empleado no exista o cuando los datos incumplan validaciones.
- **FR-010**: El sistema MUST requerir autenticación para todas las operaciones de mantenimiento de empleados.
- **FR-011**: El sistema MUST registrar eventos de creación, actualización y eliminación para auditoría operativa.
- **FR-012**: El sistema MUST paginar las consultas de colección de empleados devolviendo 10 instancias por consulta.
- **FR-013**: El sistema MUST incluir metadatos de paginación en respuestas de listado (al menos número de página, tamaño y total de elementos).

### Constitution Alignment *(mandatory)*

- **CA-001**: La solución MUST cumplir el stack y versiones obligatorias definidos por la constitución vigente.
- **CA-002**: La solución MUST aplicar el esquema de autenticación obligatorio definido por la constitución para endpoints protegidos.
- **CA-003**: La solución MUST alinear persistencia y entorno de base de datos con las reglas de contenedorización definidas por la constitución.
- **CA-004**: La solución MUST mantener rutas versionadas en path para todas las operaciones CRUD (por ejemplo, `/api/v1/empleados`).
- **CA-005**: La solución MUST mantener paginación obligatoria de 10 instancias por consulta en endpoints de colección.
- **CA-006**: La solución MUST mantener actualizada la documentación de contrato API para todas las operaciones CRUD de empleados.
- **CA-007**: La solución MUST incluir evidencia de pruebas automatizadas y trazabilidad operativa de logs según quality gates.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa a una persona registrada en el catálogo interno; atributos: `clave` (`EMP-<autonumérico>`, PK compuesta por prefijo + consecutivo, inmutable), `nombre`, `direccion`, `telefono`.

## Assumptions

- `clave` será generada por el sistema con prefijo `EMP-` y consecutivo numérico, y se usará como identificador único en consultas, actualización y eliminación.
- El CRUD estará disponible solo para usuarios autenticados con permisos de gestión.
- No se requiere versionado histórico de cambios del empleado en esta iteración.
- La primera versión pública del contrato se publicará bajo prefijo de ruta `/api/v1`.
- El tamaño de página para listados de empleados será fijo en 10 instancias por consulta.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de operaciones de alta rechaza entradas donde `nombre`, `direccion` o `telefono` exceden 100 caracteres.
- **SC-002**: Al menos 95% de consultas individuales por `clave` de empleados existentes retornan resultado correcto en menos de 2 segundos bajo carga normal del entorno.
- **SC-003**: El 100% de consultas de listado retorna máximo 10 instancias por respuesta.
- **SC-004**: Al menos 95% de actualizaciones y eliminaciones válidas se completan exitosamente en el primer intento.
- **SC-005**: El 100% de operaciones protegidas sin credenciales válidas son bloqueadas.
