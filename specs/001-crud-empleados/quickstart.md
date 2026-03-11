# Quickstart: CRUD de Empleados

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

## 4) Verificar endpoints
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 5) Probar CRUD con autenticación básica
### Crear empleado
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","direccion":"Calle 1","telefono":"555-1234"}' \
  -X POST http://localhost:8080/api/v1/empleados
```

### Listar empleados
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  "http://localhost:8080/api/v1/empleados?page=0"
```

### Obtener por clave
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  http://localhost:8080/api/v1/empleados/EMP-1
```

### Actualizar empleado
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Maria","direccion":"Calle 2","telefono":"555-5678"}' \
  -X PUT http://localhost:8080/api/v1/empleados/EMP-1
```

### Eliminar empleado
```bash
curl -u "$BASIC_AUTH_USER:$BASIC_AUTH_PASSWORD" \
  -X DELETE http://localhost:8080/api/v1/empleados/EMP-1
```

## 6) Ejecutar pruebas
```bash
mvn test
```

## 7) Comando validado en esta implementación
En este entorno, Maven se ejecutó desde una instalación local en el workspace:

```bash
./.tools/apache-maven-3.9.9/bin/mvn -f ../pom.xml test
```

Resultado: **BUILD SUCCESS** con 15 pruebas ejecutadas (0 fallas, 0 errores).
