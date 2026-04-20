# Research: CRUD de Empleados en Frontend

**Date**: 2026-03-18  
**Feature**: `006-crud-empleados-frontend`

## Decision 1: Cumplimiento de stack frontend constitucional

- **Decision**: Planificar migración de Angular 21.2.x a Angular 22 LTS como prerequisito técnico dentro de este feature.
- **Rationale**: La constitución exige Angular 22 LTS para frontend web; mantener Angular 21 dejaría el gate en incumplimiento.
- **Alternatives considered**:
  - Mantener Angular 21 con excepción no documentada (rechazada por violar gobernanza).
  - Posponer migración a otro feature (rechazada por bloquear aprobación constitucional de este plan).

## Decision 2: Estrategia CRUD sin cambios de API obligatorios

- **Decision**: Implementar CRUD frontend consumiendo endpoints existentes `/api/v1/empleados` y reutilizar `PUT /api/v1/empleados/{clave}` para desactivación/reactivación mediante `estadoAcceso`.
- **Rationale**: El backend ya soporta edición de estado y versión de API vigente; se evita agregar endpoints innecesarios.
- **Alternatives considered**:
  - Crear endpoint nuevo para desactivación lógica (rechazada por sobrediseño en alcance frontend).
  - Ejecutar `DELETE` físico desde UI (rechazada por decisión de clarificación: eliminación lógica).

## Decision 3: Modelo de visualización de activos/inactivos

- **Decision**: Mostrar activos por defecto y añadir control de alternancia para incluir inactivos en el listado.
- **Rationale**: Mantiene flujo operativo limpio y permite auditoría/recuperación de registros inactivos.
- **Alternatives considered**:
  - Mostrar siempre activos + inactivos (rechazada por ruido visual).
  - Ocultar inactivos sin opción de consulta (rechazada por baja trazabilidad operativa).

## Decision 4: Validaciones y manejo de conflictos de unicidad

- **Decision**: Validar formato/campos requeridos en frontend y tratar unicidad (`email`) a partir de respuesta backend con mensaje específico de conflicto.
- **Rationale**: Evita duplicar reglas de negocio en cliente y respeta la fuente de verdad en backend.
- **Alternatives considered**:
  - Validar unicidad solo en frontend (rechazada por condiciones de carrera y falta de certeza).
  - Mostrar solo error genérico ante conflicto (rechazada por mala UX y baja accionabilidad).

## Decision 5: Patrones de integración y estado de sesión

- **Decision**: Reutilizar `AuthService` + `authGuard` existentes; en `401` o sesión expirada, forzar redirección a `/login` y limpiar estado de autenticación.
- **Rationale**: Mantiene consistencia con el flujo de login ya implementado y reduce regresiones.
- **Alternatives considered**:
  - Manejo ad hoc por componente sin servicio compartido (rechazada por acoplamiento y repetición).

## Decision 6: Cobertura de pruebas del feature

- **Decision**: Definir cobertura mínima con pruebas unitarias Angular (servicio + componente/formulario) y Cypress E2E para listar, crear, editar, desactivar y reactivar.
- **Rationale**: Cumple la constitución y valida recorridos críticos de negocio en frontend.
- **Alternatives considered**:
  - Solo pruebas unitarias (rechazada por no cubrir integración real UI/API).
  - Solo E2E (rechazada por baja granularidad de diagnóstico).

## Decision 7: Paginación y rendimiento de listado

- **Decision**: Consumir paginación backend por `page` respetando tamaño fijo `10` y mantener navegación por página en UI.
- **Rationale**: Alinea con regla constitucional y contrato existente (`EmpleadoPageResponse`).
- **Alternatives considered**:
  - Cargar todos los empleados para paginar en frontend (rechazada por escalabilidad y divergencia de contrato).
