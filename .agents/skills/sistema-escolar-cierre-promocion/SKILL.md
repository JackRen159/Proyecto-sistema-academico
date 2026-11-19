---
name: sistema-escolar-cierre-promocion
description: Diseña, implementa o prueba comisiones de evaluación periódicas, cierre y reapertura de periodos, emisión versionada de boletines, promoción anual, habilitaciones finales, promoción anticipada, graduación de Educación Media y cierre definitivo del año lectivo.
---

# Módulo Cierre Académico, Promoción y Graduación

## Fuente funcional vinculante (lectura obligatoria)
- **Especificación principal:** `docs/especificacion/modulos/10_Cierre_Academico_y_Promocion_v2.md`
- **Casos de uso:** `UC-20`, `UC-21`, `UC-PROV-23`, `UC-PROV-24`
- **Reglas obligatorias:** `RN-01` a `RN-05`, `RN-10`, `RN-11` y `RN-CIE-01` a `RN-CIE-09`
- **Guía transversal:** `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`

## Invariantes críticos del dominio
- Ejecutar validación integral de pre-cierre (`100.00%` en `UC-16`, captura completa en `UC-17`, excusas/reclamos cerrados y firmas/DANE en `UC-PROV-13`) antes de pasar un periodo a `CERRADO`.
- Nunca reprobar grados de Educación Preescolar (`PROMOVIDO_PREESCOLAR`, Decreto 2247/1997 Art. 10).
- Ninguna promoción, habilitación final ni repitencia se decide automáticamente por código: formalizar siempre mediante Acta numerada de la Comisión de Evaluación y Promoción con integrantes nominativos (sin cuentas compartidas).
- Promoción Anticipada (Decreto 1290 Art. 7): solo en primer periodo, con concepto del Consejo Académico, consentimiento del representante legal, cupo disponible y Acta del Consejo Directivo.
- Reapertura de periodo solo por Rector con motivo obligatorio, auditoría reforzada y generación de nueva versión (`V+1`) del boletín.
- Graduación de Media (Grado 11.°): validar promoción en `UC-21`, Servicio Social Estudiantil (>= 80 h), ICFES Saber 11 y documento vigente; si falta requisito no académico usar `APROBADO_PENDIENTE_TITULACION`.

## Dependencias
`sistema-escolar-calificaciones`, `sistema-escolar-asistencia`, `sistema-escolar-estructura-academica`, `sistema-escolar-configuracion`, `sistema-escolar-documentos-reportes`, `sistema-escolar-seguridad`, `sistema-escolar-auditoria`, `sistema-escolar-base-datos` y `sistema-escolar-calidad-testing`.