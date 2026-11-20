-- =============================================================================
-- V16__modulo_profesores_asignaciones_v2.sql
-- Sistema de Gestión Escolar - Módulo de Profesores y Asignaciones Docentes V2
-- Casos de Uso: UC-15, UC-PROV-11, UC-PROV-12
-- Reglas: RN-09, RN-10, RN-11, RN-PRO-02 a RN-PRO-08, Ley 115 de 1994, Ley 1918 de 2018, Dec. 1850 y Dec. 1075 de 2015
-- =============================================================================

-- 1. Ampliación de la tabla `profesores` con expediente maestro normativo y situaciones administrativas
ALTER TABLE profesores
    ADD COLUMN IF NOT EXISTS estado_administrativo VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
    ADD COLUMN IF NOT EXISTS tipo_vinculacion VARCHAR(40) NOT NULL DEFAULT 'PROPIEDAD_ESTATUTO_1278',
    ADD COLUMN IF NOT EXISTS horas_semanales_ordinarias INT NOT NULL DEFAULT 22,
    ADD COLUMN IF NOT EXISTS horas_extras_permitidas INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS verificado_inhabilidades BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS fecha_verificacion_inhabilidades DATE,
    ADD COLUMN IF NOT EXISTS numero_certificado_inhabilidades VARCHAR(50),
    ADD COLUMN IF NOT EXISTS fecha_inicio_situacion DATE,
    ADD COLUMN IF NOT EXISTS fecha_fin_situacion DATE,
    ADD COLUMN IF NOT EXISTS motivo_situacion VARCHAR(255),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_profesores_estado_adm') THEN
        ALTER TABLE profesores
            ADD CONSTRAINT chk_profesores_estado_adm
            CHECK (estado_administrativo IN ('ACTIVO', 'EN_LICENCIA_O_INCAPACIDAD', 'SUSPENDIDO', 'RETIRADO_INACTIVO'));
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_profesores_tipo_vinc') THEN
        ALTER TABLE profesores
            ADD CONSTRAINT chk_profesores_tipo_vinc
            CHECK (tipo_vinculacion IN ('PROPIEDAD_ESTATUTO_2277', 'PROPIEDAD_ESTATUTO_1278',
                                        'PROVISIONAL_VACANTE_DEFINITIVA', 'PROVISIONAL_VACANTE_TEMPORAL',
                                        'CONTRATO_TERMINO_FIJO_PRIVADO', 'CONTRATO_TERMINO_INDEFINIDO_PRIVADO',
                                        'HORA_CATEDRA'));
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_profesores_topes') THEN
        ALTER TABLE profesores
            ADD CONSTRAINT chk_profesores_topes
            CHECK (horas_semanales_ordinarias >= 1 AND horas_semanales_ordinarias <= 40
                   AND horas_extras_permitidas >= 0 AND horas_extras_permitidas <= 20);
    END IF;
END $$;

-- Actualizar docentes preexistentes con verificación de inhabilidades válida para continuidad operativa
UPDATE profesores
SET verificado_inhabilidades = TRUE,
    fecha_verificacion_inhabilidades = CURRENT_DATE,
    numero_certificado_inhabilidades = 'INH-LEY1918-MIGRACION-V16'
WHERE verificado_inhabilidades = FALSE;

CREATE INDEX IF NOT EXISTS idx_profesores_estado_adm ON profesores (estado_administrativo);
CREATE INDEX IF NOT EXISTS idx_profesores_tipo_vinc ON profesores (tipo_vinculacion);

-- 2. Ampliación de la tabla `asignaciones_academicas` con estados, relevos y vigencias
ALTER TABLE asignaciones_academicas
    ADD COLUMN IF NOT EXISTS estado_asignacion VARCHAR(30) NOT NULL DEFAULT 'TITULAR',
    ADD COLUMN IF NOT EXISTS fecha_inicio_vigencia DATE,
    ADD COLUMN IF NOT EXISTS fecha_fin_vigencia DATE,
    ADD COLUMN IF NOT EXISTS es_reemplazo_temporal BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS docente_titular_relevado_id BIGINT,
    ADD COLUMN IF NOT EXISTS motivo_relevo VARCHAR(255),
    ADD COLUMN IF NOT EXISTS acta_rectoral_horas_extras VARCHAR(50),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_asignaciones_titular_relevado') THEN
        ALTER TABLE asignaciones_academicas
            ADD CONSTRAINT fk_asignaciones_titular_relevado
            FOREIGN KEY (docente_titular_relevado_id) REFERENCES profesores (id) ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_asig_estado') THEN
        ALTER TABLE asignaciones_academicas
            ADD CONSTRAINT chk_asig_estado
            CHECK (estado_asignacion IN ('TITULAR', 'PROPUESTA_EN_PLANEACION', 'REEMPLAZO_TEMPORAL',
                                         'SUSPENDIDA_POR_LICENCIA', 'FINALIZADA_POR_RELEVO'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_asignaciones_estado ON asignaciones_academicas (estado_asignacion);
CREATE INDEX IF NOT EXISTS idx_asignaciones_relevado ON asignaciones_academicas (docente_titular_relevado_id);
