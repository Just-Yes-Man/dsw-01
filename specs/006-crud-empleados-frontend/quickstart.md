# Quickstart: CRUD de Empleados en Frontend

## 1) Prerrequisitos

- Docker + Docker Compose
- Java 17 + Maven
- Node.js LTS compatible con Angular 22
- npm

## 2) Levantar backend + base de datos

```bash
docker compose up --build -d
curl -u bootstrap_admin:bootstrap123 http://localhost:8080/actuator/health
```

## 3) Levantar frontend

```bash
cd frontend
npm install
npm run start
```

Frontend esperado en `http://localhost:4200`.

## 4) Recorrido manual CRUD esperado

1. Iniciar sesión en `/login` con usuario empleado válido.
2. Entrar a `/empleados` y verificar listado paginado.
3. Crear empleado con datos válidos y confirmar aparición en lista.
4. Editar empleado y confirmar actualización.
5. Desactivar empleado (estado `INACTIVO`) con confirmación.
6. Activar toggle para mostrar inactivos y verificar empleado desactivado.
7. Reactivar empleado desde edición (`estadoAcceso=ACTIVO`) y confirmar que reaparece en vista por defecto.

## 5) Verificación rápida de API (opcional)

```bash
# Listado paginado (endpoint protegido)
curl -i http://localhost:8080/api/v1/empleados?page=0 --cookie "JSESSIONID=<session-id>"

# Crear empleado (endpoint protegido)
curl -i -X POST http://localhost:8080/api/v1/empleados \
  -H 'Content-Type: application/json' \
  --cookie "JSESSIONID=<session-id>" \
  -d '{
    "nombre":"Empleado Demo",
    "direccion":"Calle 1",
    "telefono":"555-1111",
    "email":"demo@example.com",
    "password":"demo123",
    "estadoAcceso":"ACTIVO",
    "departamentoId":1
  }'
```

## 6) Pruebas automáticas sugeridas

### Unitarias frontend

```bash
cd frontend
npm test
```

### E2E con Cypress

```bash
cd frontend
npm run cypress:run
```

Cobertura mínima del feature:
- listado con paginación
- alta/edición válidas
- conflicto de unicidad (email)
- desactivación lógica
- visualización de inactivos
- reactivación
- expiración de sesión -> redirección a login
