-- =============================================================================
-- V14__modulo_estructura_academica_v2.sql
-- Sistema de Gestión Escolar - Módulo de Estructura Académica V2
-- Casos de Uso: UC-14, UC-PROV-06, UC-PROV-07, UC-PROV-08
-- Reglas: RN-01 a RN-04, RN-EST-ACA-01 a RN-EST-ACA-09, Ley 115 de 1994, Dec. 1075 de 2015, Dec. 2247 de 1997
-- =============================================================================

-- 1. Ampliación de la tabla `grados` con secuencia de promoción y régimen de evaluación (RN-EST-ACA-01)
ALTER TABLE grados
    ADD COLUMN IF NOT EXISTS grado_siguiente_id BIGINT,
    ADD COLUMN IF NOT EXISTS es_grado_terminal BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS regimen_evaluacion VARCHAR(30) NOT NULL DEFAULT 'CUANTITATIVO_AREAS';

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grados_grado_siguiente') THEN
        ALTER TABLE grados
            ADD CONSTRAINT fk_grados_grado_siguiente
            FOREIGN KEY (grado_siguiente_id) REFERENCES grados (id) ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_grados_regimen_eval') THEN
        ALTER TABLE grados
            ADD CONSTRAINT chk_grados_regimen_eval
            CHECK (regimen_evaluacion IN ('CUALITATIVO_DIMENSIONES', 'CUANTITATIVO_AREAS'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_grados_grado_siguiente ON grados (grado_siguiente_id);

-- Configuración de secuencia de promoción y régimen estándar colombiano (Decreto 1075 de 2015)
UPDATE grados SET regimen_evaluacion = 'CUALITATIVO_DIMENSIONES' WHERE nivel = 'PREESCOLAR';

-- Actualizar secuencia de promoción: TRA -> 01 -> 02 -> 03 -> 04 -> 05 -> 06 -> 07 -> 08 -> 09 -> 10 -> 11
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '01') WHERE codigo = 'TRA';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '02') WHERE codigo = '01';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '03') WHERE codigo = '02';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '04') WHERE codigo = '03';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '05') WHERE codigo = '04';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '06') WHERE codigo = '05';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '07') WHERE codigo = '06';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '08') WHERE codigo = '07';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '09') WHERE codigo = '08';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '10') WHERE codigo = '09';
UPDATE grados SET grado_siguiente_id = (SELECT id FROM grados WHERE codigo = '11') WHERE codigo = '10';
UPDATE grados SET es_grado_terminal = TRUE, grado_siguiente_id = NULL WHERE codigo = '11';

-- 2. Ampliación de `anios_lectivos` (UC-14, RN-EST-ACA-02, RN-EST-ACA-07, RN-11)
ALTER TABLE anios_lectivos
    ADD COLUMN IF NOT EXISTS tipo_calendario VARCHAR(20) NOT NULL DEFAULT 'CALENDARIO_A',
    ADD COLUMN IF NOT EXISTS semanas_lectivas INT NOT NULL DEFAULT 40,
    ADD COLUMN IF NOT EXISTS numero_acta_aprobacion VARCHAR(50),
    ADD COLUMN IF NOT EXISTS fecha_acta_aprobacion DATE,
    ADD COLUMN IF NOT EXISTS organo_aprobador VARCHAR(40),
    ADD COLUMN IF NOT EXISTS motivo_ajuste_excepcional VARCHAR(500),
    ADD COLUMN IF NOT EXISTS resolucion_rectoral_excepcional VARCHAR(100),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    -- Ajustar la restricción de estados para admitir el ciclo de vida V2
    ALTER TABLE anios_lectivos DROP CONSTRAINT IF EXISTS chk_anios_lectivos_estado;
    ALTER TABLE anios_lectivos
        ADD CONSTRAINT chk_anios_lectivos_estado
        CHECK (estado IN ('PLANIFICACION', 'EN_PLANEACION', 'ACTIVO', 'EN_CIERRE_ANUAL', 'CERRADO', 'CERRADO_HISTORICO'));

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_anios_lectivos_tipo_cal') THEN
        ALTER TABLE anios_lectivos
            ADD CONSTRAINT chk_anios_lectivos_tipo_cal
            CHECK (tipo_calendario IN ('CALENDARIO_A', 'CALENDARIO_B'));
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_anios_lectivos_semanas') THEN
        ALTER TABLE anios_lectivos
            ADD CONSTRAINT chk_anios_lectivos_semanas
            CHECK (semanas_lectivas >= 30 AND semanas_lectivas <= 52);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_anios_lectivos_organo') THEN
        ALTER TABLE anios_lectivos
            ADD CONSTRAINT chk_anios_lectivos_organo
            CHECK (organo_aprobador IS NULL OR organo_aprobador IN ('CONSEJO_DIRECTIVO', 'CONSEJO_ACADEMICO', 'RECTORIA'));
    END IF;
END $$;

-- 3. Tabla `dias_no_lectivos` para exclusión en cómputo de asistencia válida (UC-14 A4, RN-04, RN-EST-ACA-02)
CREATE TABLE IF NOT EXISTS dias_no_lectivos (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    anio_lectivo_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    motivo VARCHAR(255),
    sede VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_dias_no_lectivos_anio FOREIGN KEY (anio_lectivo_id) REFERENCES anios_lectivos (id) ON DELETE RESTRICT,
    CONSTRAINT chk_dias_no_lectivos_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT chk_dias_no_lectivos_tipo CHECK (tipo IN ('FESTIVO', 'RECESO_ESTUDIANTIL', 'JORNADA_PEDAGOGICA', 'SUSPENSION_EXTRAORDINARIA', 'OTRO'))
);

CREATE INDEX IF NOT EXISTS idx_dias_no_lectivos_anio ON dias_no_lectivos (anio_lectivo_id);
CREATE INDEX IF NOT EXISTS idx_dias_no_lectivos_fechas ON dias_no_lectivos (fecha_inicio, fecha_fin);

-- 4. Ampliación de `periodos_academicos` (UC-14, RN-EST-ACA-05, RN-11)
ALTER TABLE periodos_academicos
    ADD COLUMN IF NOT EXISTS fecha_inicio_digitacion DATE,
    ADD COLUMN IF NOT EXISTS fecha_fin_digitacion DATE,
    ADD COLUMN IF NOT EXISTS numero_acta_aprobacion VARCHAR(50),
    ADD COLUMN IF NOT EXISTS fecha_acta_aprobacion DATE,
    ADD COLUMN IF NOT EXISTS organo_aprobador VARCHAR(40),
    ADD COLUMN IF NOT EXISTS motivo_ajuste_excepcional VARCHAR(500),
    ADD COLUMN IF NOT EXISTS resolucion_rectoral_excepcional VARCHAR(100),
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_periodos_fechas_digitacion') THEN
        ALTER TABLE periodos_academicos
            ADD CONSTRAINT chk_periodos_fechas_digitacion
            CHECK (fecha_fin_digitacion IS NULL OR fecha_inicio_digitacion IS NULL OR fecha_fin_digitacion >= fecha_inicio_digitacion);
    END IF;
END $$;

-- 5. Ampliación de `grupos` con sede, aula base y control de concurrencia (UC-PROV-06, RN-EST-ACA-06, RN-11)
ALTER TABLE grupos
    ADD COLUMN IF NOT EXISTS sede VARCHAR(100) NOT NULL DEFAULT 'SEDE_PRINCIPAL',
    ADD COLUMN IF NOT EXISTS aula_base VARCHAR(50),
    ADD COLUMN IF NOT EXISTS estado VARCHAR(30) NOT NULL DEFAULT 'ACTIVO',
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_grupos_estado') THEN
        ALTER TABLE grupos
            ADD CONSTRAINT chk_grupos_estado
            CHECK (estado IN ('EN_PROYECCION', 'ACTIVO', 'CERRADO_SIN_CUPOS', 'CERRADO_HISTORICO'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_grupos_sede ON grupos (sede);

-- 6. Ampliación de `grados_asignaturas` (UC-PROV-07, RN-EST-ACA-03, RN-EST-ACA-04, RN-EST-ACA-08, RN-11)
ALTER TABLE grados_asignaturas
    ADD COLUMN IF NOT EXISTS anio_lectivo_id BIGINT,
    ADD COLUMN IF NOT EXISTS peso_porcentual_en_area NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    ADD COLUMN IF NOT EXISTS tipo_componente VARCHAR(30) NOT NULL DEFAULT 'ASIGNATURA',
    ADD COLUMN IF NOT EXISTS computa_promocion BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grados_asignaturas_anio') THEN
        ALTER TABLE grados_asignaturas
            ADD CONSTRAINT fk_grados_asignaturas_anio
            FOREIGN KEY (anio_lectivo_id) REFERENCES anios_lectivos (id) ON DELETE RESTRICT;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_grados_asig_peso') THEN
        ALTER TABLE grados_asignaturas
            ADD CONSTRAINT chk_grados_asig_peso
            CHECK (peso_porcentual_en_area > 0 AND peso_porcentual_en_area <= 100);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_grados_asig_tipo_comp') THEN
        ALTER TABLE grados_asignaturas
            ADD CONSTRAINT chk_grados_asig_tipo_comp
            CHECK (tipo_componente IN ('ASIGNATURA', 'DIMENSION_PREESCOLAR', 'PROYECTO_PEDAGOGICO'));
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_grados_asignaturas_anio ON grados_asignaturas (anio_lectivo_id);

-- 7. Semilla Curricular: Dimensiones del Desarrollo Infantil para Educación Preescolar (Decreto 2247 de 1997 / Decreto 1075 de 2015)
INSERT INTO areas (codigo, nombre, descripcion, activo)
VALUES ('DIM_PRE', 'Dimensiones del Desarrollo - Educación Preescolar', 'Marco curricular para el nivel preescolar bajo enfoque integral cualitativo sin reprobación', TRUE)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO asignaturas (area_id, codigo, nombre, descripcion, activo)
SELECT a.id, vals.cod, vals.nom, vals.descrip, TRUE
FROM areas a
CROSS JOIN (
    VALUES
        ('DIM_CORP',  'Dimensión Corporal',        'Desarrollo psicomotriz, esquema corporal y hábitos de salud'),
        ('DIM_COGN',  'Dimensión Cognitiva',       'Procesos de pensamiento, exploración y comprensión del entorno'),
        ('DIM_COMU',  'Dimensión Comunicativa',    'Lenguaje verbal, no verbal y expresión de ideas y sentimientos'),
        ('DIM_ESTE',  'Dimensión Estética',        'Sensibilidad, imaginación, creatividad y apreciación artística'),
        ('DIM_ESPI',  'Dimensión Espiritual',      'Valores de trascendencia, respeto y sentido de pertenencia'),
        ('DIM_ETIC',  'Dimensión Ética',           'Convivencia, autonomía, toma de decisiones y respeto por normas'),
        ('DIM_SOCIO', 'Dimensión Socio-Afectiva',  'Autoestima, expresión de emociones y socialización')
) AS vals(cod, nom, descrip)
WHERE a.codigo = 'DIM_PRE'
ON CONFLICT (codigo) DO NOTHING;

-- Asociar las dimensiones a Transición (TRA) en grados_asignaturas
INSERT INTO grados_asignaturas (grado_id, asignatura_id, intensidad_horaria, es_optativa, peso_porcentual_en_area, tipo_componente, computa_promocion, activo)
SELECT g.id, asig.id, 4, FALSE, 100.00, 'DIMENSION_PREESCOLAR', FALSE, TRUE
FROM grados g
CROSS JOIN asignaturas asig
JOIN areas a ON asig.area_id = a.id
WHERE g.codigo = 'TRA' AND a.codigo = 'DIM_PRE'
ON CONFLICT (grado_id, asignatura_id) DO UPDATE SET
    tipo_componente = 'DIMENSION_PREESCOLAR',
    computa_promocion = FALSE,
    peso_porcentual_en_area = 100.00;
