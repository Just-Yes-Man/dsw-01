# Phase 0 Research: CRUD de Empleados

## Decisión 1: Tipo de clave primaria
- Decision: Usar clave expuesta con formato `EMP-<autonumérico>` y PK compuesta interna (`prefijo`, `consecutivo`) generada por el sistema.
- Rationale: Mantiene un identificador legible para negocio (`EMP-...`) sin perder control técnico sobre unicidad y secuencia autogenerada.
- Alternatives considered:
  - `BIGINT` puro como PK expuesta: menos legible para usuarios operativos.
  - `UUID`: mayor complejidad sin beneficio claro para este alcance.
  - Clave alfanumérica manual: incrementa errores operativos y validaciones.

## Decisión 2: Estrategia de validación de campos de 100 caracteres
- Decision: Aplicar validaciones de longitud en capa API (Bean Validation) y restricciones de esquema en base de datos (`VARCHAR(100)`).
- Rationale: Defensa en profundidad; la API comunica errores claros y la base garantiza integridad.
- Alternatives considered:
  - Solo validación en API: riesgo de inserciones inválidas por rutas no previstas.
  - Solo restricción en DB: peor experiencia de error para consumidores del API.

## Decisión 3: Autenticación para endpoints CRUD
- Decision: Proteger endpoints de empleados con Basic Authentication vía Spring Security.
- Rationale: Cumple constitución del proyecto y es suficiente para este alcance inicial.
- Alternatives considered:
  - JWT/OAuth2: mayor complejidad fuera del alcance solicitado.
  - Endpoints públicos: incumple requisito y constitución.

## Decisión 4: Persistencia y entorno local/CI
- Decision: PostgreSQL como única base de datos y Docker Compose para levantar servicio local/CI.
- Rationale: Paridad de entorno, reproducibilidad y cumplimiento de lineamientos de constitución.
- Alternatives considered:
  - H2 en memoria para ejecución principal: diferencias de comportamiento frente a PostgreSQL.
  - PostgreSQL instalado localmente sin Docker: baja reproducibilidad entre equipos.

## Decisión 5: Documentación de contrato API
- Decision: Publicar contrato OpenAPI con springdoc y mantenerlo sincronizado con cada endpoint CRUD versionado en path (`/api/v1/...`).
- Rationale: Alinea desarrollo y consumo de API, y cumple requerimiento de Swagger como fuente de verdad con versionado explícito.
- Alternatives considered:
  - Documentación manual en Markdown: propensa a desalineación.
  - Sin contrato formal: incumple constitución.

## Decisión 7: Versionado de endpoints
- Decision: Aplicar versionado de API en las rutas con prefijo `/api/v1` para todas las operaciones CRUD de empleados.
- Rationale: Permite evolucionar el contrato sin romper consumidores existentes y cumple con la gobernanza vigente.
- Alternatives considered:
  - Versionado por cabecera: menos visible para consumidores y tooling.
  - Sin versionado: alto riesgo de cambios incompatibles.

## Decisión 8: Paginación de consultas
- Decision: Implementar paginación obligatoria en consultas de colección con tamaño fijo de 10 instancias por respuesta.
- Rationale: Estandariza carga de respuesta, mejora predictibilidad de performance y cumple lineamiento constitucional.
- Alternatives considered:
  - Sin paginación: riesgo de respuestas excesivas con crecimiento de datos.
  - Tamaño de página variable por cliente: menor consistencia operativa para esta iteración.

## Decisión 6: Estrategia de pruebas
- Decision: Combinar pruebas unitarias de servicio, integración de repositorio/controlador y pruebas de contrato HTTP.
- Rationale: Cubre reglas de negocio, persistencia y estabilidad de contrato en cambios futuros.
- Alternatives considered:
  - Solo unitarias: cobertura insuficiente de integración.
  - Solo integración end-to-end: mayor costo y diagnóstico más lento.
