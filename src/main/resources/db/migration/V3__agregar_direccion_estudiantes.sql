-- =============================================================================
-- V3__agregar_direccion_estudiantes.sql
-- Agregar columna direccion a la tabla estudiantes con backfill seguro
-- =============================================================================

ALTER TABLE estudiantes ADD COLUMN IF NOT EXISTS direccion VARCHAR(150);

-- Backfill para registros existentes previos a la obligatoriedad
UPDATE estudiantes SET direccion = 'Sin registrar' WHERE direccion IS NULL;

-- Asegurar restriccion NOT NULL
ALTER TABLE estudiantes ALTER COLUMN direccion SET NOT NULL;

