# Quickstart: Feature 004 (CRUD Departamentos + Relación 1:N)

## 1) Prerrequisitos

- Docker + Docker Compose
- Java 17 + Maven (para pruebas locales opcionales)

## 2) Levantar entorno

```bash
docker compose down -v
docker compose up --build -d
docker compose ps
```

Validar salud:

```bash
curl -u bootstrap_admin:bootstrap123 http://localhost:8080/actuator/health
```

## 3) Flujo rápido E2E

## 3.1 Crear departamento

```bash
curl -X POST http://localhost:8080/api/v1/departamentos \
  -u bootstrap_admin:bootstrap123 \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Finanzas"}'
```

Esperado: `201` con `id`, `nombre`, `estado`.

## 3.2 Crear empleado con departamentoId obligatorio

```bash
curl -X POST http://localhost:8080/api/v1/empleados \
  -u bootstrap_admin:bootstrap123 \
  -H 'Content-Type: application/json' \
  -d '{
    "nombre":"Ana",
    "direccion":"Calle 1",
    "telefono":"555-1234",
    "email":"ana@example.com",
    "password":"ana123",
    "estadoAcceso":"ACTIVO",
    "departamentoId":1
  }'
```

Esperado: `201` y `EmpleadoResponse` con objeto `departamento` embebido.

## 3.3 Validar detalle de departamento con empleados embebidos

```bash
curl http://localhost:8080/api/v1/departamentos/1 \
  -u bootstrap_admin:bootstrap123
```

Esperado: `200` con campos de departamento y arreglo `empleados` (máximo 50).

## 3.4 Validar regla de conflicto en delete

Intentar borrar departamento con empleados asociados:

```bash
curl -X DELETE http://localhost:8080/api/v1/departamentos/1 \
  -u bootstrap_admin:bootstrap123
```

Esperado: `409 CONFLICT`.

## 3.5 Liberar relación y borrar departamento

```bash
curl -X DELETE http://localhost:8080/api/v1/empleados/EMP-1 \
  -u bootstrap_admin:bootstrap123

curl -X DELETE http://localhost:8080/api/v1/departamentos/1 \
  -u bootstrap_admin:bootstrap123
```

Esperado: `204` en ambas operaciones.

## 4) Pruebas automatizadas

```bash
mvn test
```

Esperado: suite completa en verde.

## 5) Archivos principales impactados

- `src/main/java/com/dsw02/empleados/departamentos/**`
- `src/main/java/com/dsw02/empleados/dto/**`
- `src/main/java/com/dsw02/empleados/service/EmpleadoService.java`
- `specs/004-crud-departamentos/contracts/openapi.yaml`
