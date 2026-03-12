# Phase 0 Research: Autenticación de Empleados por Correo

## Decision 1: Esquema de autenticación
- Decision: Mantener HTTP Basic Authentication y reemplazar usuario estático por correo de empleado.
- Rationale: Respeta constitución vigente y reduce cambios de arquitectura respecto al sistema actual.
- Alternatives considered:
  - Bearer/JWT: agrega complejidad de emisión/expiración de tokens fuera de alcance.
  - Soporte mixto Basic+Bearer: incrementa superficie de seguridad y validación.

## Decision 2: Estado de acceso
- Decision: Incluir estado `activo`/`inactivo` para controlar autenticación.
- Rationale: Permite deshabilitar acceso sin eliminar registros ni afectar trazabilidad histórica.
- Alternatives considered:
  - Eliminar empleado para bloquear acceso: pérdida de continuidad operativa.
  - Sin estado de acceso: control insuficiente de habilitación.

## Decision 3: Política de intentos fallidos
- Decision: Bloquear autenticación por 15 minutos después de 5 intentos fallidos consecutivos por correo.
- Rationale: Mitiga abuso por fuerza bruta con una regla simple y verificable.
- Alternatives considered:
  - Sin bloqueo: mayor riesgo de ataques automatizados.
  - Solo logging sin bloqueo: no detiene abuso activo.

## Decision 4: Gestión de contraseña
- Decision: Contraseña inicial definida por administrador y almacenada en texto plano en esta iteración.
- Rationale: Alinea con decisión explícita de alcance para entrega inmediata.
- Alternatives considered:
  - Hash de contraseña: más seguro, pero rechazado para esta iteración por decisión de alcance.
  - Contraseña temporal auto-generada: agrega flujo adicional no requerido.

## Decision 5: Contrato y endpoints
- Decision: Extender payloads de empleado con campos de credencial (`email`, `password`, `estadoAcceso`) en endpoints versionados existentes.
- Rationale: Evita crear endpoints adicionales innecesarios y mantiene compatibilidad de rutas.
- Alternatives considered:
  - Endpoint separado de autenticación de empleados: mayor complejidad de contrato y pruebas.
  - Mantener credenciales fuera del modelo de empleado: rompe trazabilidad funcional solicitada.

## Decision 6: Cobertura de pruebas
- Decision: Incluir pruebas de contrato/integración para éxito/fallo/bloqueo de autenticación y pruebas unitarias de validaciones de credencial.
- Rationale: Cubre criterios de calidad, seguridad y observabilidad definidos en constitución.
- Alternatives considered:
  - Solo pruebas unitarias: insuficiente para validar comportamiento HTTP real.
  - Solo pruebas manuales: sin evidencia reproducible para CI.
