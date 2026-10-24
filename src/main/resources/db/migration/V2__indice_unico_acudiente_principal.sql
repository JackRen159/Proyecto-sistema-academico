-- =============================================================================
-- V2__indice_unico_acudiente_principal.sql
-- Invariante de negocio: Un estudiante no puede tener más de un acudiente
-- principal activo simultáneamente a nivel de base de datos.
-- =============================================================================

CREATE UNIQUE INDEX IF NOT EXISTS uk_estudiante_acudiente_principal
ON estudiante_acudiente (estudiante_id)
WHERE es_principal = true;
