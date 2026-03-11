<!--
Sync Impact Report
- Version change: 1.0.0 -> 1.1.0
- Modified principles:
	- III. Contratos API y Swagger como Fuente de Verdad -> III. Contratos API Versionados y Swagger como Fuente de Verdad
- Added sections:
	- Ninguna
- Removed sections:
	- Ninguna
- Templates requiring updates:
	- ✅ updated: .specify/templates/plan-template.md
	- ✅ updated: .specify/templates/spec-template.md
	- ✅ updated: .specify/templates/tasks-template.md
	- ✅ updated: specs/001-crud-empleados/spec.md
	- ✅ updated: specs/001-crud-empleados/plan.md
	- ✅ updated: specs/001-crud-empleados/tasks.md
	- ✅ updated: specs/001-crud-empleados/contracts/openapi.yaml
	- ✅ updated: specs/001-crud-empleados/quickstart.md
	- ✅ updated: specs/001-crud-empleados/research.md
	- ⚠ pending: .specify/templates/commands/*.md (directorio no existe)
- Deferred TODOs:
	- Ninguno
-->

# DSW02-Practica01 Constitution

## Core Principles

### I. Stack Baseline Obligatorio
Todo desarrollo backend MUST implementarse en Spring Boot 3 y Java 17. Cualquier propuesta
de librería, framework o versión distinta MUST incluir un RFC aprobado antes de su adopción.
El proyecto MUST mantener compatibilidad con el ecosistema oficial de Spring Boot 3.
Rationale: Un baseline único reduce riesgo operativo, deriva técnica y costos de mantenimiento.

### II. Seguridad con Basic Authentication
Todos los endpoints protegidos MUST requerir Basic Authentication sobre HTTPS en cualquier
entorno no local. Las credenciales MUST almacenarse fuera del código fuente (variables de
entorno o gestor de secretos) y MUST rotarse según política operativa definida por el equipo.
Los endpoints públicos MUST declararse explícitamente y justificarse en el spec.
Rationale: La autenticación uniforme evita configuraciones ambiguas y fortalece la postura base.

### III. Contratos API Versionados y Swagger como Fuente de Verdad
Cada API REST MUST estar documentada con OpenAPI/Swagger y la documentación MUST reflejar
el comportamiento real desplegable. Todas las rutas de endpoints MUST incluir versionado en
path con formato `/api/v{major}/...` y la versión MUST declararse explícitamente en el
contrato. Las consultas de colección MUST ser paginadas y MUST devolver exactamente 10
instancias por consulta para mantener consistencia operativa del consumo.
Ninguna funcionalidad HTTP se considera completa sin esquemas de request/response, códigos de
error y requisitos de autenticación documentados. Los cambios de contrato MUST actualizar
Swagger en el mismo incremento.
Rationale: El versionado en ruta permite evolución controlada de compatibilidad y la paginación
determinística estabiliza rendimiento y experiencia de integración.

### IV. Persistencia PostgreSQL en Entorno Dockerizado
La persistencia relacional MUST usar PostgreSQL. Los entornos de desarrollo e integración
MUST proveer PostgreSQL mediante Docker o Docker Compose para asegurar paridad operativa.
La configuración de conexión MUST ser parametrizable por entorno y nunca hardcodeada.
Rationale: PostgreSQL en contenedores estandariza la ejecución y minimiza problemas de entorno.

### V. Calidad Verificable por Pruebas y Observabilidad
Cada feature MUST incluir pruebas automatizadas proporcionales al riesgo (unitarias,
integración y contrato cuando aplique). El backend MUST emitir logs estructurados para
autenticación, errores y operaciones críticas de base de datos, evitando exposición de secretos.
No se aprueba un cambio sin evidencia de validación reproducible.
Rationale: La calidad medible previene regresiones y acelera diagnóstico en producción.

## Restricciones Técnicas Obligatorias

- Runtime MUST ser Java 17.
- Framework backend MUST ser Spring Boot 3.
- Base de datos MUST ser PostgreSQL.
- Entorno local/CI para base de datos MUST usar Docker o Docker Compose.
- Documentación de endpoints MUST publicarse en Swagger/OpenAPI.
- Rutas de API MUST incluir versión en path con formato `/api/v{major}/...`.
- Consultas de colección MUST ser paginadas con tamaño fijo de 10 instancias por respuesta.
- Gestión de configuración sensible MUST usar variables de entorno o secretos externos.
- Todo endpoint protegido MUST declarar explícitamente su requisito de Basic Authentication.

## Flujo de Entrega y Quality Gates

1. Todo spec MUST definir impacto en seguridad, contrato API y persistencia.
2. Todo spec MUST declarar estrategia de versionado de endpoints en path y reglas de paginación.
3. Todo plan MUST pasar Constitution Check antes de implementación.
4. Toda tarea MUST mapearse a user stories y declarar archivos concretos.
5. Todo PR MUST incluir evidencia de pruebas y actualización de Swagger cuando aplique.
6. Todo cambio de esquema PostgreSQL MUST incluir migración y plan de rollback.

## Governance

Esta constitución prevalece sobre convenciones informales del repositorio. Las enmiendas
MUST registrarse en PR dedicado con justificación, impacto de adopción y actualización de
plantillas afectadas en `.specify/templates`. El versionado de la constitución sigue SemVer:
MAJOR para cambios incompatibles de principios o gobernanza, MINOR para nuevos principios o
expansión normativa material, PATCH para aclaraciones sin cambio de significado.

La revisión de cumplimiento MUST ejecutarse en cada `/speckit.plan` (gates de diseño), en
cada `/speckit.tasks` (trazabilidad a principios) y en revisión de PR (evidencia de pruebas,
seguridad y documentación Swagger). Incumplimientos MUST bloquear la aprobación hasta su
resolución o excepción formal documentada.

**Version**: 1.1.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-04
