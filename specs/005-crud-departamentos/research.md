# Research: CRUD de Departamentos en Frontend

**Date**: 2026-03-19  
**Feature**: `005-crud-departamentos`

## Decision 1: Reusar API versionada existente de departamentos

- **Decision**: Implementar el CRUD frontend consumiendo endpoints existentes `GET/POST/PATCH/DELETE /api/v1/departamentos`.
- **Rationale**: La API ya está disponible, versionada y protegida; evita cambios innecesarios en backend y acelera entrega del módulo frontend.
- **Alternatives considered**:
  - Crear endpoints nuevos para frontend (rechazada por duplicidad de contrato).
  - Ajustar versión de API para este alcance (rechazada por no existir cambio de contrato requerido).

## Decision 2: Modelo de eliminación en frontend

- **Decision**: Permitir eliminación desde frontend mediante `DELETE /api/v1/departamentos/{id}` con confirmación explícita.
- **Rationale**: Cumple la decisión de producto de habilitar eliminación desde UI y se alinea al contrato backend actual que rechaza eliminación cuando hay empleados asociados.
- **Alternatives considered**:
  - Ocultar la eliminación y dejar solo edición (rechazada por no cubrir alcance CRUD completo).
  - Implementar reactivación en esta fase (rechazada por decisión de clarificación: fuera de alcance).

## Decision 3: Manejo de conflictos de negocio

- **Decision**: Tratar respuestas `409 CONFLICT` con mensajes específicos para dos casos: nombre duplicado y eliminación bloqueada por empleados asociados.
- **Rationale**: El backend ya retorna `ErrorResponse` con mensaje accionable; reflejarlo en UI evita errores genéricos y mejora operatividad.
- **Alternatives considered**:
  - Mostrar un único mensaje genérico para todos los errores (rechazada por baja claridad).
  - Duplicar validaciones complejas en frontend (rechazada por no ser fuente de verdad).

## Decision 4: Paginación y rendimiento de listado

- **Decision**: Consumir el listado por `page` respetando tamaño fijo de 10 elementos por respuesta y navegación por página en la UI.
- **Rationale**: Cumple constitución (paginación determinística) y mantiene performance estable al no cargar toda la colección.
- **Alternatives considered**:
  - Paginación en cliente cargando todo de una vez (rechazada por escalabilidad).
  - Cambiar tamaño de página desde frontend (rechazada por contrato fijo existente).

## Decision 5: Sesión y seguridad en cliente

- **Decision**: Consumir endpoints con `withCredentials: true` y, ante `401`, limpiar estado de autenticación y redirigir a `/login`.
- **Rationale**: Mantiene coherencia con el flujo de seguridad existente del proyecto y evita estados de sesión inconsistentes.
- **Alternatives considered**:
  - Reintento silencioso sin redirección (rechazada por opacidad en sesión expirada).
  - Manejo aislado de `401` en cada componente (rechazada por duplicación).

## Decision 6: Cobertura de calidad para el feature

- **Decision**: Definir pruebas unitarias frontend (servicio/store/componentes) y Cypress E2E para listar, crear, editar, eliminar exitosamente y eliminación rechazada por asociación.
- **Rationale**: Cumple gate de calidad/observabilidad y valida recorridos críticos de negocio de extremo a extremo.
- **Alternatives considered**:
  - Solo unitarias (rechazada por no validar integración real UI/API).
  - Solo E2E (rechazada por menor capacidad de diagnóstico de fallos).

## Decision 7: Alcance funcional cerrado para esta iteración

- **Decision**: Excluir en esta versión filtros avanzados, reactivación y visualización de inactivos.
- **Rationale**: Las clarificaciones del spec limitan el feature al CRUD operativo base de departamentos con flujo simple.
- **Alternatives considered**:
  - Incluir reactivación y vistas mixtas activo/inactivo (rechazada por fuera de alcance actual).
  - Añadir roles/permisos por acción (rechazada por requerimiento explícito de acceso para todo autenticado).
