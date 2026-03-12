# Feature Specification: Autenticación de Empleados por Correo

**Feature Branch**: `003-empleado-email-auth`  
**Created**: 2026-03-11  
**Status**: Draft  
**Input**: User description: "se hara un una nueva implementacion, el sistema tendra una autenticacion de los empleados con correo y contraseña"

## Clarifications

### Session 2026-03-11

- Q: ¿Qué mecanismo de autenticación se usará para empleados? → A: Se mantiene HTTP Basic Auth usando correo como usuario y contraseña del empleado.
- Q: ¿Qué regla define si un empleado puede autenticarse? → A: Solo empleados con estado de acceso `activo` pueden autenticarse; `inactivo` debe bloquear acceso.
- Q: ¿Cómo se almacenará la contraseña del empleado en esta iteración? → A: Se almacenará en texto plano para esta iteración.
- Q: ¿Qué política de intentos fallidos se aplicará? → A: Bloquear autenticación por 15 minutos tras 5 intentos fallidos consecutivos por correo.
- Q: ¿Cómo se define la contraseña inicial del empleado? → A: La contraseña inicial del empleado la define el administrador al crear o actualizar empleado.
- Q: ¿Cómo se resuelve el bootstrap inicial sin credenciales estáticas hardcodeadas? → A: Se usará una cuenta técnica de bootstrap definida por variables de entorno solo para provisión inicial y deshabilitable posteriormente.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Iniciar sesión con correo y contraseña (Priority: P1)

Como empleado, quiero autenticarme con mi correo y contraseña para acceder al sistema con mis propias credenciales.

**Why this priority**: Sin autenticación de empleados, no existe control de acceso basado en identidad real del usuario.

**Independent Test**: Puede validarse creando un empleado con correo habilitado para acceso, autenticándose con credenciales correctas y verificando acceso autorizado; con credenciales inválidas debe rechazarse.

**Acceptance Scenarios**:

1. **Given** un empleado registrado con correo y contraseña válidos, **When** intenta iniciar sesión con esas credenciales, **Then** el sistema lo autentica y le permite usar endpoints protegidos.
2. **Given** un empleado registrado, **When** intenta iniciar sesión con contraseña incorrecta, **Then** el sistema rechaza el acceso e informa credenciales inválidas.

---

### User Story 2 - Proteger operaciones según autenticación (Priority: P2)

Como responsable del sistema, quiero que las operaciones protegidas solo estén disponibles para empleados autenticados para evitar accesos no autorizados.

**Why this priority**: La autenticación debe tener efecto real en seguridad de la API y no ser solo un formulario de entrada.

**Independent Test**: Puede validarse invocando un endpoint protegido con y sin credenciales válidas de empleado y comprobando respuestas de autorización.

**Acceptance Scenarios**:

1. **Given** un endpoint protegido, **When** se invoca sin credenciales, **Then** el sistema devuelve respuesta de no autorizado.
2. **Given** un endpoint protegido, **When** se invoca con credenciales válidas de empleado, **Then** el sistema permite la operación.

---

### User Story 3 - Gestionar credenciales de forma consistente (Priority: P3)

Como administrador del catálogo, quiero que el sistema valide formato y unicidad del correo de autenticación para mantener calidad y confiabilidad en el acceso.

**Why this priority**: Mantener datos de autenticación consistentes evita bloqueos de acceso y conflictos entre empleados.

**Independent Test**: Puede validarse creando y actualizando empleados con correos válidos/duplicados y verificando aceptación o rechazo según reglas de negocio.

**Acceptance Scenarios**:

1. **Given** un alta o actualización de empleado, **When** se proporciona un correo con formato inválido, **Then** el sistema rechaza la solicitud con error de validación.
2. **Given** un correo ya registrado por otro empleado, **When** se intenta reutilizar ese correo, **Then** el sistema rechaza la solicitud por duplicidad.

### Edge Cases

- Intento de autenticación con correo inexistente.
- Intento de autenticación con contraseña vacía.
- Intentos repetidos fallidos de autenticación en corto periodo.
- Empleado eliminado o deshabilitado intentando autenticarse.
- Cambio de correo de empleado y uso de correo anterior para autenticación.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir autenticación de empleados usando HTTP Basic Authentication, con correo como identificador de usuario y contraseña del empleado.
- **FR-002**: El sistema MUST validar que el correo tenga formato válido antes de aceptar registro o actualización de credenciales.
- **FR-003**: El sistema MUST garantizar que cada correo de autenticación pertenezca a un único empleado.
- **FR-004**: El sistema MUST rechazar autenticación cuando el correo no exista, la contraseña no corresponda, o el empleado tenga estado de acceso `inactivo`.
- **FR-005**: El sistema MUST mantener protegidos los endpoints de negocio y permitir acceso solo a empleados autenticados.
- **FR-006**: El sistema MUST responder errores de autenticación con payload estructurado y mensajes claros para el consumidor.
- **FR-007**: El sistema MUST registrar eventos de seguridad relevantes de autenticación (éxitos y rechazos) con trazabilidad operativa.
- **FR-008**: El sistema MUST mantener compatibilidad de rutas versionadas en formato `/api/v{major}/...` para endpoints existentes y nuevos.
- **FR-009**: El sistema MUST actualizar la documentación de contrato API para reflejar el nuevo esquema de autenticación de empleados.
- **FR-010**: El sistema MUST conservar la paginación de consultas de colección con 10 instancias por respuesta en funcionalidades ya existentes.
- **FR-011**: El sistema MUST permitir gestionar estado de acceso de empleado (`activo`/`inactivo`) para control de autenticación.
- **FR-012**: El sistema MUST almacenar la contraseña del empleado en texto plano en esta iteración.
- **FR-013**: El sistema MUST bloquear intentos de autenticación por 15 minutos después de 5 intentos fallidos consecutivos para el mismo correo.
- **FR-014**: El sistema MUST permitir que el administrador defina la contraseña inicial del empleado al crear o actualizar su registro.
- **FR-015**: El sistema MUST operar endpoints protegidos sobre HTTPS en cualquier entorno no local.
- **FR-016**: El sistema MUST permitir provisión inicial mediante cuenta técnica de bootstrap definida por configuración externa, sin credenciales hardcodeadas en código o documentación fija.
- **FR-017**: El sistema MUST aplicar respuesta `423 LOCKED` en cualquier endpoint protegido cuando la credencial del empleado se encuentre bloqueada temporalmente.

### Constitution Alignment *(mandatory)*

- **CA-001**: La solución MUST ejecutarse en Spring Boot 3 con Java 17, o documentar excepción aprobada.
- **CA-002**: La solución MUST definir el comportamiento de autenticación Basic para endpoints protegidos, sustituyendo credenciales estáticas por credenciales de empleado (correo como usuario y contraseña).
- **CA-003**: La solución MUST mantener impacto de persistencia en PostgreSQL y paridad de ejecución en Docker/Docker Compose.
- **CA-004**: La solución MUST actualizar OpenAPI/Swagger para cualquier cambio de endpoint o seguridad bajo rutas versionadas `/api/v{major}/...`.
- **CA-005**: La solución MUST preservar paginación de colecciones con tamaño fijo de 10 en consultas de empleados.
- **CA-006**: La solución MUST incluir evidencia de pruebas automatizadas y logs estructurados de seguridad.
- **CA-007**: La solución MUST definir y documentar cumplimiento de HTTPS para entornos no locales.

### Key Entities *(include if feature involves data)*

- **EmpleadoAutenticable**: Empleado con identidad de acceso basada en correo único y contraseña.
- **CredencialEmpleado**: Conjunto de datos de autenticación asociado a un empleado (correo, contraseña en texto plano, estado de acceso `activo`/`inactivo`, metadatos operativos de autenticación).
- **EventoAutenticacion**: Registro de resultado de intentos de autenticación para trazabilidad operativa.

## Assumptions

- Solo empleados registrados en el catálogo pueden autenticarse.
- La autenticación por correo y contraseña aplica a todos los endpoints protegidos del sistema de empleados.
- El estado de acceso por defecto para empleados habilitados será `activo`.
- Se mantiene HTTP Basic Auth en esta iteración (sin introducción de esquema Bearer/JWT).
- El almacenamiento de contraseña en texto plano se acepta para esta iteración.
- La contraseña inicial del empleado será asignada por un administrador en el alta o actualización del registro.
- La cuenta técnica de bootstrap será configurable por entorno y deberá poder deshabilitarse tras la provisión inicial.
- No se incluye recuperación de contraseña en esta iteración.
- La administración de permisos avanzados por rol queda fuera de alcance en esta iteración.
- En entorno local se permite HTTP para pruebas; en entornos no locales se exige HTTPS.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de intentos con credenciales válidas de empleados activos logra autenticación exitosa.
- **SC-002**: El 100% de intentos con credenciales inválidas es rechazado sin acceso a endpoints protegidos.
- **SC-003**: Al menos 95% de autenticaciones válidas responde en menos de 2 segundos bajo carga normal.
- **SC-004**: El 100% de correos duplicados o con formato inválido es rechazado en altas/actualizaciones.
- **SC-005**: El 100% de endpoints protegidos mantiene respuesta de no autorizado cuando faltan credenciales o son incorrectas.
