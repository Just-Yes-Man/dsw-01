# Data Model: Paginación de Consultas de Empleados

## Entity: Empleado
- Description: Registro individual del catálogo de empleados.

### Fields
- `clave`:
  - Type: `string`
  - Constraints: formato `EMP-<autonumérico>`
  - Source: persistencia existente
- `nombre`:
  - Type: `string`
  - Constraints: longitud `1..100`
- `direccion`:
  - Type: `string`
  - Constraints: longitud `1..100`
- `telefono`:
  - Type: `string`
  - Constraints: longitud `1..100`

## Entity: PaginaDeEmpleados
- Description: Sobre de respuesta para listados paginados de empleados.

### Fields
- `page`:
  - Type: `integer`
  - Constraints: `>= 0`
  - Semantics: índice de página solicitado
- `size`:
  - Type: `integer`
  - Constraints: valor fijo `10`
  - Semantics: tamaño de página aplicado
- `totalElements`:
  - Type: `integer`
  - Constraints: `>= 0`
  - Semantics: total de empleados disponibles
- `items`:
  - Type: `array<Empleado>`
  - Constraints: longitud `0..10`
  - Semantics: elementos de la página actual en orden ascendente por `clave`

## Relationships
- `PaginaDeEmpleados.items` contiene cero o más `Empleado`.

## Validation Rules
- `page` MUST ser mayor o igual a 0.
- `size` MUST ser siempre 10 para todas las consultas de colección.
- `items.length` MUST ser menor o igual a 10.
- Si `page` está fuera de rango, la respuesta MUST contener `items: []` y `200 OK`.

## State Transitions
- No se agregan nuevos estados de dominio persistente.
- La feature afecta solo proyección/consulta del estado existente.