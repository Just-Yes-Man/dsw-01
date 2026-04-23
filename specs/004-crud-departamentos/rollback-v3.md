# Rollback Plan: V3 Migration (departamentos_table)

**Constitution Gate 6 compliance** — every schema change MUST include a migration and rollback plan.

**Migration file**: `src/main/resources/db/migration/V3__departamentos_table.sql`  
**Applies to**: `departamentos` table creation + `departamento_id` column added to `empleados`

## Rollback SQL

Run the following SQL statements in order against the target PostgreSQL database to fully revert V3:

```sql
-- Step 1: Remove FK constraint and column from empleados
ALTER TABLE empleados DROP CONSTRAINT IF EXISTS fk_empleados_departamento;
ALTER TABLE empleados DROP COLUMN IF EXISTS departamento_id;

-- Step 2: Drop departamentos table (cascades indexes and constraints)
DROP TABLE IF EXISTS departamentos;
```

## Flyway Repair

After reversing the SQL manually, repair the Flyway schema history to remove the V3 checksum entry:

```bash
mvn flyway:repair
```

## Pre-Conditions

- Run rollback ONLY if no `departamentos` rows and no `departamento_id` values are in use.
- Check first: `SELECT COUNT(*) FROM departamentos;` and `SELECT COUNT(*) FROM empleados WHERE departamento_id IS NOT NULL;`
- If data exists, reassign or archive before rollback.

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Data loss in `departamentos` | Low (new table) | High | Backup before rollback |
| Orphaned `departamento_id` in empleados | Low (nullable, no FK yet used) | Medium | Verify count before rollback |
| Flyway state mismatch | Medium | Low | `mvn flyway:repair` after SQL rollback |
