# Quickstart: Autenticación de Empleados por Correo

## Prerrequisitos
- Java 17
- Maven 3.9+
- Docker y Docker Compose

## 1) Levantar servicios base
```bash
docker compose up -d postgres
```

## 2) Ejecutar aplicación
```bash
mvn spring-boot:run
```

## 2.1) Configurar cuenta técnica de bootstrap (solo provisión inicial)
```bash
export BOOTSTRAP_AUTH_USER=bootstrap_admin
export BOOTSTRAP_AUTH_PASSWORD=change_me_securely
```

## 3) Crear empleado con credenciales de acceso
```bash
curl -u "$BOOTSTRAP_AUTH_USER:$BOOTSTRAP_AUTH_PASSWORD" \
  -H "Content-Type: application/json" \
  -X POST "http://localhost:8080/api/v1/empleados" \
  -d '{
    "nombre":"Ana",
    "direccion":"Calle 1",
    "telefono":"5551234567",
    "email":"ana@example.com",
    "password":"ana123",
    "estadoAcceso":"ACTIVO"
  }'
```

## 4) Autenticar con credenciales del empleado
```bash
curl -u ana@example.com:ana123 \
  "http://localhost:8080/api/v1/empleados?page=0"
```

### Resultado esperado
- Código `200`
- Respuesta paginada con `size=10`

## 5) Validar rechazo por contraseña inválida
```bash
curl -u ana@example.com:wrongpass \
  "http://localhost:8080/api/v1/empleados?page=0"
```

### Resultado esperado
- Código `401`

## 6) Validar bloqueo temporal
1. Repetir 5 intentos fallidos consecutivos con el mismo correo.
2. Reintentar autenticación con credenciales correctas antes de 15 minutos.

### Resultado esperado
- Código `423` (bloqueo temporal)

## 7) Validar bloqueo por estado de acceso
1. Actualizar empleado con `estadoAcceso=INACTIVO`.
2. Reintentar autenticación con correo/contraseña válidos.

### Resultado esperado
- Código `401` (o `423` solo si hay bloqueo temporal activo)

## 8) Ejecutar pruebas
```bash
mvn test
```

## 9) Endurecimiento posterior a bootstrap
- Deshabilitar la cuenta técnica de bootstrap una vez provisionado el primer empleado administrador.
- Mantener credenciales técnicas solo en variables de entorno/secret manager.
- En entornos no locales, exponer endpoints protegidos únicamente por HTTPS.

### Cobertura mínima esperada
- Contrato: payloads de create/update/read con campos de credencial
- Integración: login válido, inválido, inactivo y bloqueo temporal
- Unitarias: validación de email, unicidad y contadores de intentos

## Evidencia de implementación (2026-03-11)

- `mvn test` ejecutado en la rama `003-empleado-email-auth` sin fallos.
- Se validó autenticación por correo con credenciales de empleado activo (`200`) y contraseña inválida (`401`).
- Se validó bloqueo temporal tras intentos fallidos consecutivos con respuesta `423 LOCKED`.
- Se validó expiración de bloqueo a los 15 minutos usando fuente de tiempo controlable en pruebas de integración.
- Se validó rechazo de cuentas `INACTIVO` con respuesta `401`.
- Se validó chequeo de rendimiento p95 `< 2s` mediante prueba automatizada `shouldKeepPagedQueryP95UnderTwoSeconds`.
