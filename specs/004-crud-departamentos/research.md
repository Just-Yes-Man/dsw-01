# Research Phase: CRUD Departamentos + Relación 1:N

**Date**: 12 de marzo de 2026  
**Status**: Complete

## Decision 1: Cardinalidad de dominio

**Decision**: Modelar relación `Departamento (1) -> (N) Empleado`; cada empleado pertenece a un único departamento.

**Rationale**: Refleja estructura organizacional estándar, evita ambigüedad de pertenencia y simplifica validaciones de negocio y reporting.

**Alternatives considered**:
- N:M (empleado en múltiples departamentos) → Rechazada por complejidad extra no requerida.
- Empleado sin relación de departamento → Rechazada por nuevo requerimiento explícito.

## Decision 2: Obligatoriedad de asignación

**Decision**: `departamentoId` es obligatorio en create/update de empleado; `null` retorna HTTP 400.

**Rationale**: Alinea integridad funcional con el objetivo de modelar organización completa y evita estados incompletos.

**Alternatives considered**:
- Permitir `null` siempre → Rechazada por reglas funcionales acordadas.
- Requerir sólo en create → Rechazada por inconsistencia operativa en update.

## Decision 3: Validez de referencia

**Decision**: Sólo se permite asignar departamento existente y en estado ACTIVO; si no existe o está INACTIVO, responder HTTP 404.

**Rationale**: Evita asociaciones con entidades no operativas y mantiene coherencia con semántica de soft-delete.

**Alternatives considered**:
- Permitir referencia a INACTIVO → Rechazada por incoherencia con lectura por defecto.
- Retornar 409 en referencia inválida → Rechazada; semánticamente corresponde NOT_FOUND.

## Decision 4: Representación en respuestas de empleado

**Decision**: `EmpleadoResponse` incluirá objeto embebido `departamento` con `id`, `nombre`, `estado`.

**Rationale**: Mejora experiencia de consumidor API reduciendo roundtrips para datos básicos de relación.

**Alternatives considered**:
- Exponer sólo `departamentoId` → Rechazada por requerimiento de representación embebida.
- Exponer ambos (`id` + objeto) → Rechazada por redundancia innecesaria.

## Decision 5: Representación en detalle de departamento

**Decision**: `GET /api/v1/departamentos/{id}` embebe lista `empleados` con tope fijo de 50 registros.

**Rationale**: Proporciona visibilidad útil del detalle sin abrir endpoint adicional ni payload no acotado.

**Alternatives considered**:
- Sin embebido de empleados → Rechazada por requerimiento explícito.
- Embebido sin límite → Rechazada por riesgo de payload excesivo.
- Embebido paginado en mismo endpoint → Rechazada por complejidad adicional fuera de alcance.

## Decision 6: Regla de borrado de departamento

**Decision**: Mantener soft-delete (INACTIVO) y bloquear eliminación con HTTP 409 si hay empleados asociados.

**Rationale**: Preserva integridad referencial y evita huérfanos de negocio.

**Alternatives considered**:
- Cascade delete de empleados → Rechazada por pérdida de datos.
- Reasignación automática → Rechazada por ausencia de reglas de negocio para destino.

## Decision 7: Consistencia con constitución

**Decision**: Mantener stack y gates constitucionales: Java 17, Spring Boot 3, PostgreSQL en Docker, Basic Auth, OpenAPI y paginación fija de 10 para consultas de colección.

**Rationale**: Minimiza deriva arquitectónica y mantiene compatibilidad con prácticas establecidas del repositorio.

**Alternatives considered**:
- Cambiar estrategia de paginación global o autenticación → Rechazada por incumplimiento constitucional.
