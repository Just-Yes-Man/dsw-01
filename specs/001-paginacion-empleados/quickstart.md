# Quickstart: Paginación de Consultas de Empleados

## Prerrequisitos
- Java 17
- Maven 3.9+
- Docker y Docker Compose

## 1) Levantar PostgreSQL
```bash
docker compose up -d postgres
```

## 2) Configurar variables de entorno
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=empleadosdb
export DB_USER=empleados
export DB_PASSWORD=empleados123
export BASIC_AUTH_USER=admin
export BASIC_AUTH_PASSWORD=admin123
```

## 3) Ejecutar la aplicación
```bash
mvn spring-boot:run
```

## 4) Validar listado paginado (size fijo 10)
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  "http://localhost:8080/api/v1/empleados?page=0"
```

### Resultado esperado
- Código `200`
- Cuerpo con forma:
  - `page` (>= 0)
  - `size` (= 10)
  - `totalElements` (>= 0)
  - `items` (0..10 elementos)
- `items` ordenados por `clave` ascendente

## 5) Validar página fuera de rango
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  "http://localhost:8080/api/v1/empleados?page=9999"
```

### Resultado esperado
- Código `200`
- `items: []`

## 6) Validar parámetro inválido
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  "http://localhost:8080/api/v1/empleados?page=-1"
```

### Resultado esperado
- Código `400`

## 7) Validar compatibilidad de consulta por clave (sin paginación)
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  "http://localhost:8080/api/v1/empleados/EMP-1"
```

### Resultado esperado
- Código `200` cuando existe
- Respuesta de recurso único `EmpleadoResponse` (sin `items` ni metadatos de página)

## 8) Ejecutar pruebas automáticas
```bash
mvn test
```

### Evidencia de ejecución
- Resultado observado: `BUILD SUCCESS`
- Suite ejecutada: `Tests run: 23, Failures: 0, Errors: 0, Skipped: 0`

## 9) Verificar criterio de performance (SC-003)
La verificación queda automatizada en la prueba:
- `EmpleadoReadIntegrationTest#shouldKeepPagedQueryP95UnderTwoSeconds`

El caso ejecuta 30 consultas paginadas (`GET /api/v1/empleados?page=0`) y valida que el p95 sea menor a 2000 ms.