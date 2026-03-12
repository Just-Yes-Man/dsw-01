# Quickstart: CRUD Departamentos

**Date**: 12 de marzo de 2026  
**Status**: Ready for Implementation

## Overview

This guide covers setting up, building, testing, and deploying the Departamentos CRUD feature (Feature 004).

## Prerequisites

- Java 17+ (`java -version`)
- Maven 3.8.1+ (`mvn -version`)
- PostgreSQL 13+ running locally or via Docker
- Docker & Docker Compose (optional, for container-based development)
- Git (`git --version`)

## Project Structure

```
DSW02-Practica01/
├── src/main/java/com/dsw02/
│   ├── departamentos/          # NEW: Feature 004 package
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   └── service/
│   └── empleados/
├── src/test/java/com/dsw02/
│   ├── departamentos/
│   │   ├── contract/
│   │   ├── integration/
│   │   └── unit/
│   └── empleados/
├── src/main/resources/
│   ├── db/migration/           # Flyway migrations
│   ├── application.yml
│   └── schema.sql
├── pom.xml
├── docker-compose.yml
└── README.md
```

## Setup Steps

### Step 1: Clone & Branch

```bash
git clone https://github.com/Just-Yes-Man/dsw-01.git
cd DSW02-Practica01
git checkout 004-crud-departamentos
```

### Step 2: Start PostgreSQL

#### Option A: Docker Compose (Recommended)

```bash
docker-compose up -d postgres
# Wait for container to start
sleep 5
docker-compose logs postgres
```

#### Option B: Local PostgreSQL

```bash
# Ensure PostgreSQL is running
# Create database if needed:
psql -U postgres -c "CREATE DATABASE dsw02-practica01;"
```

### Step 3: Configure Connection

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dsw02-practica01
    username: postgres
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway handles schema
  flyway:
    enabled: true
    locations: classpath:db/migration
```

For testing, `src/test/resources/application-test.yml` configures H2 in-memory DB.

### Step 4: Build Project

```bash
mvn clean install
# This will:
# - Compile all sources
# - Run all tests (unit, integration, contract)
# - Package JAR
```

### Step 5: Run Application

```bash
mvn spring-boot:run
# Application starts on http://localhost:8080
# Swagger UI available at http://localhost:8080/swagger-ui.html
```

### Step 6: Verify Deployment

```bash
# Check application is running
curl -v http://localhost:8080/swagger-ui.html

# Test authentication (should return 401 without credentials)
curl -v http://localhost:8080/api/v1/departamentos

# Test with Basic Auth
curl -v -u bootstrap:bootstrap-secret \
  http://localhost:8080/api/v1/departamentos
# Expected: HTTP 200 with empty page (no departments yet)
```

## Configuration

### Bootstrap Credentials (Local Development)

```yaml
# application.yml
app:
  bootstrap:
    user: bootstrap
    password: bootstrap-secret
```

Change in non-local environments via environment variables:

```bash
export BOOTSTRAP_AUTH_USER=admin
export BOOTSTRAP_AUTH_PASSWORD=secure-password
```

### Database Migration

Flyway automatically runs migrations on startup:

1. **V1__initial_schema.sql** - Creates empleados table (existing)
2. **V2__empleado_email_auth.sql** - Adds email/password/bloqueo (existing)
3. **V3__departamentos_table.sql** - Creates departamentos table (NEW)

**Manual Migration Check**:

```bash
# List applied migrations
psql -U postgres -d dsw02-practica01 \
  -c "SELECT * FROM flyway_schema_history;"
```

## Testing

### Run All Tests

```bash
mvn test
# Runs all unit, integration, and contract tests
# Output: 40+ tests spanning all layers
```

### Run Specific Test Class

```bash
mvn test -Dtest=DepartamentoCreateContractTest
mvn test -Dtest=DepartamentoCreateIntegrationTest
mvn test -Dtest=DepartamentoValidationTest
```

### Test Coverage

```bash
# Run with JaCoCo coverage report
mvn clean test jacoco:report
# Report location: target/site/jacoco/index.html
```

### Integration Tests with Testcontainers

Tests automatically start PostgreSQL container if needed:

```bash
mvn test -DargLine="-Dspring.profiles.active=test"
```

## API Usage Examples

### Authentication

All endpoints require Basic Authentication:

```bash
# Use bootstrap credentials
USERNAME=bootstrap
PASSWORD=bootstrap-secret
CREDENTIALS=$(echo -n "$USERNAME:$PASSWORD" | base64)

curl -H "Authorization: Basic $CREDENTIALS" \
  http://localhost:8080/api/v1/departamentos
```

### Create Department

```bash
curl -X POST http://localhost:8080/api/v1/departamentos \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sales"}'

# Response: HTTP 201
# {
#   "id": 1,
#   "nombre": "Sales",
#   "estado": "ACTIVO",
#   "creadoEn": "2026-03-12T10:30:00",
#   "actualizadoEn": "2026-03-12T10:30:00"
# }
```

### List Departments

```bash
curl http://localhost:8080/api/v1/departamentos?page=0&size=10 \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)"

# Response: HTTP 200 with paginated list
# {
#   "content": [
#     {"id": 1, "nombre": "Sales", "estado": "ACTIVO", ...},
#     {"id": 2, "nombre": "Engineering", "estado": "ACTIVO", ...}
#   ],
#   "totalElements": 2,
#   "totalPages": 1,
#   "currentPage": 0,
#   "pageSize": 10
# }
```

### Update Department

```bash
curl -X PATCH http://localhost:8080/api/v1/departamentos/1 \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sales & Marketing"}'

# Response: HTTP 200 with updated entity
```

### Delete Department (Soft-Delete)

```bash
curl -X DELETE http://localhost:8080/api/v1/departamentos/1 \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)"

# Response: HTTP 204 No Content
# (Department marked as INACTIVO internally)
```

### Error Examples

#### Duplicate Name (409 Conflict)

```bash
curl -X POST http://localhost:8080/api/v1/departamentos \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Sales"}'  # Already exists

# Response: HTTP 409
# {
#   "errorCode": "INTEGRITY_CONSTRAINT_VIOLATION",
#   "message": "Department name 'Sales' already exists"
# }
```

#### Name Too Long (400 Validation Error)

```bash
curl -X POST http://localhost:8080/api/v1/departamentos \
  -H "Authorization: Basic $(echo -n 'bootstrap:bootstrap-secret' | base64)" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"'$(printf 'a%.0s' {1..300})'"}' # 300 'a's

# Response: HTTP 400
# {
#   "errorCode": "VALIDATION_ERROR",
#   "message": "Nombre must be between 1 and 255 characters"
# }
```

## Development Workflow

### 1. Create Feature Branch

```bash
git checkout -b feature/departamentos-crud develop
```

### 2. Make Changes

Edit files in `src/main/java/com/dsw02/departamentos/`

### 3. Write Tests First (TDD)

```bash
# Create test in src/test/java/com/dsw02/departamentos/
mvn test
```

### 4. Run Locally

```bash
mvn spring-boot:run
# Test via curl or Postman
```

### 5. Commit & Push

```bash
git add .
git commit -m "feat: implement departamentos CRUD"
git push origin feature/departamentos-crud
```

### 6. Create Pull Request

```bash
# On GitHub: Create PR from feature/departamentos-crud → develop
# Verify:
# - All tests pass
# - Code coverage maintained
# - Swagger contract updated
# - No security issues
```

## Performance Validation

### Success Criteria

- **SC-001**: All CRUD operations < 500ms p95
- **SC-002**: 100% input validation (empty, null, > 255 chars)
- **SC-003**: Pagination exactly 10 per page
- **SC-004**: 100% test pass rate (contract, integration, unit)

### Load Testing (Optional)

```bash
# Install Apache Bench
brew install httpd  # macOS
# or apt-get install apache2-utils  # Linux

# Simple load test
ab -n 1000 -c 10 -H "Authorization: Basic ..." \
  http://localhost:8080/api/v1/departamentos

# Verify p95 latency from output
```

## Troubleshooting

### Issue: PostgreSQL connection refused

```
Error: org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Solution**:
```bash
# Check if PostgreSQL is running
docker ps | grep postgres
# Or
psql -U postgres -c "\l"

# Start if not running
docker-compose up -d postgres
```

### Issue: Flyway migration failed

```
Error: org.flywaydb.core.api.FlywayException: Migration V3__departamentos_table.sql failed
```

**Solution**:
```bash
# Check migration history
psql -U postgres -d dsw02-practica01 \
  -c "SELECT * FROM flyway_schema_history;"

# If corrupted, repair (careful!):
psql -U postgres -d dsw02-practica01 \
  -c "DELETE FROM flyway_schema_history WHERE version = '3';"

# Re-run application
mvn spring-boot:run
```

### Issue: Tests fail with "Table not found"

**Solution**:
```bash
# Ensure H2 in-memory DB is properly configured in application-test.yml
# Run with explicit test profile:
mvn test -Dspring.profiles.active=test
```

## Documentation

- **Swagger/OpenAPI**: http://localhost:8080/swagger-ui.html (when running)
- **API Specification**: [contracts/openapi.yaml](contracts/openapi.yaml)
- **Data Model**: [data-model.md](data-model.md)
- **Research**: [research.md](research.md)

## Next Steps

1. ✅ Specification complete (spec.md + clarifications)
2. ✅ Planning complete (plan.md)
3. ✅ Design complete (data-model.md + contracts)
4. ⏳ **Implementation** (create files listed in tasks.md)
5. ⏳ Testing (run test suite)
6. ⏳ PR review & merge to develop/release/master

See [tasks.md](tasks.md) for detailed implementation checklist.
