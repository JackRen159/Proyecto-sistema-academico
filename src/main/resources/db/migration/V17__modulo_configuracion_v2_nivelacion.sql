-- =============================================================================
-- V17__modulo_configuracion_v2_nivelacion.sql
-- Sistema de Gestión Escolar - Nivelación al 100% Módulo 06: Configuración Institucional V2
-- Casos de Uso: UC-26, UC-PROV-13, UC-PROV-14
-- Normativa: Ley 115 de 1994, Decreto 1290 de 2009 / Decreto 1075 de 2015,
--            Ley 1620 de 2013, Ley 1581 de 2012 / Decreto 1377 de 2013, Sentencia T-004 de 2026.
-- =============================================================================

-- 1. Ampliación de Identificación Legal Institucional y Firmas (UC-PROV-13, RN-CFG-08)
ALTER TABLE institucion_datos_legales
    ADD COLUMN IF NOT EXISTS escudo_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS membrete_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS firma_rector_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS firma_secretario_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- 2. Ampliación de Versionamiento del SIEE (UC-26, RN-CFG-01, RN-CFG-02)
ALTER TABLE configuracion_siee_versiones
    ADD COLUMN IF NOT EXISTS constancia_socializacion VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_socializacion DATE,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- 3. Ampliación de Versionamiento del Manual de Convivencia (UC-26, Ley 1620, RN-CFG-05)
ALTER TABLE configuracion_manual_convivencia_versiones
    ADD COLUMN IF NOT EXISTS anio_lectivo_vigencia_id BIGINT,
    ADD COLUMN IF NOT EXISTS constancia_socializacion VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_socializacion DATE,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_manual_anio') THEN
        ALTER TABLE configuracion_manual_convivencia_versiones
            ADD CONSTRAINT fk_manual_anio
            FOREIGN KEY (anio_lectivo_vigencia_id) REFERENCES anios_lectivos (id) ON DELETE RESTRICT;
    END IF;
END $$;

-- 4. Actualización de Seed V1 para vincular año lectivo activo y socialización institucional
UPDATE configuracion_siee_versiones
SET constancia_socializacion = 'Socialización en asamblea general de padres y jornada pedagógica docente 2026',
    fecha_socializacion = CURRENT_DATE
WHERE numero_version = 1 AND constancia_socializacion IS NULL;

UPDATE configuracion_manual_convivencia_versiones
SET constancia_socializacion = 'Socialización y entrega de extracto del Manual en proceso de matrícula 2026',
    fecha_socializacion = CURRENT_DATE
WHERE numero_version = 1 AND constancia_socializacion IS NULL;

-- 5. Índice de consulta contextual de SIEE y Manual por año lectivo (UC-PROV-14)
CREATE INDEX IF NOT EXISTS idx_siee_anio_estado ON configuracion_siee_versiones (anio_lectivo_vigencia_id, estado);
CREATE INDEX IF NOT EXISTS idx_manual_anio_estado ON configuracion_manual_convivencia_versiones (anio_lectivo_vigencia_id, estado);
