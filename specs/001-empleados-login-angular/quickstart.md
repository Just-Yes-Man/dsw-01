# Quickstart: Login de Empleados en Frontend

## 1) Prerrequisitos

- Docker + Docker Compose
- Java 17 + Maven
- Node.js LTS compatible con Angular 22
- npm (o pnpm/yarn equivalente)

## 2) Levantar backend y base de datos

```bash
docker compose up --build -d
curl -u bootstrap_admin:bootstrap123 http://localhost:8080/actuator/health
```

Opcional para correr también frontend en Docker Compose:

```bash
docker compose --profile frontend up --build -d
```

## 3) Frontend Angular (feature scope)

```bash
cd frontend
npm install
npm run start
```

Frontend esperado en `http://localhost:4200`.

## 4) Flujo manual de autenticación

1. Abrir `/login`.
2. Enviar correo y contraseña válidos de empleado existente.
3. Verificar redirección a `/empleados`.
4. Abrir una ruta protegida sin sesión y verificar redirección a `/login`.
5. Ejecutar logout y verificar pérdida de sesión.

## 5) Verificación backend de contrato auth

```bash
# Login (cookie de sesión en Set-Cookie)
curl -i -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ana@example.com","password":"ana123"}'

# Estado de sesión (usando cookie)
curl -i http://localhost:8080/api/v1/auth/session --cookie "JSESSIONID=<valor>"

# Logout
curl -i -X POST http://localhost:8080/api/v1/auth/logout --cookie "JSESSIONID=<valor>"
```

## 6) Ejecución de pruebas

### Backend

```bash
mvn -Dtest=AuthLoginContractTest,AuthLoginIntegrationTest,AuthSessionContractTest,AuthSessionIntegrationTest test
```

### Frontend unitarias

```bash
cd frontend
npm test
```

### Cypress E2E

```bash
cd frontend
npm run cypress:run
```

Cobertura mínima requerida:
- Login exitoso
- Login fallido (mensaje genérico)
- Ruta protegida sin sesión
- Logout
