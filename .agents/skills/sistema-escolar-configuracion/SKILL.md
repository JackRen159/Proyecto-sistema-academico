---
name: sistema-escolar-configuracion
description: Diseña o implementa parámetros institucionales, años y periodos lectivos, escalas, reglas académicas y configuración general autorizada del Sistema de Gestión Escolar.
---

# Módulo Configuración

## Fuente funcional vinculante (V2)

Antes de diseñar, implementar o probar este módulo, lee:
- Especificación del módulo: `docs/especificacion/modulos/06_Configuracion_Institucional_v2.md`
- Reglas transversales: `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- Casos de uso cubiertos: `UC-26`, `UC-PROV-13`, `UC-PROV-14`
- Reglas de negocio aplicables: `RN-01`, `RN-02`, `RN-CFG-01` a `RN-CFG-08`
- Convierte cada fila `A#` y `E#` (y su código HTTP esperado) en criterios de aceptación y pruebas automatizadas.

## Alcance documentado

Administrar parámetros institucionales, años y periodos lectivos, escalas, reglas académicas y configuraciones generales autorizadas.

## Principio

No codifiques como constantes universales las decisiones que dependen del PEI/SIEE o de la política del colegio. Configura únicamente reglas aprobadas, identificables, con condiciones, excepciones y pruebas.

## Reglas y controles

- Restringe quién puede consultar o modificar parámetros.
- Audita modificaciones de escalas, periodos, reglas y permisos.
- Define vigencia/alcance de cada parámetro cuando la regla lo necesite; no inventes estrategia de versionado sin analizar el dominio.
- Evita que un cambio de configuración altere silenciosamente resultados académicos históricos.
- Protege cambios concurrentes y valida su consistencia con periodos y operaciones existentes.

## Decisiones pendientes

No inventes catálogos, valores por defecto, calendarios, escalas numéricas, fórmulas, notas mínimas, umbrales de asistencia ni promoción. Confirma responsables y fuente institucional.

## Dependencias

Estructura académica, calificaciones, asistencia, seguridad, auditoría, base de datos, API y pruebas. Consultar contexto colombiano al diseñar reglas escolares.

## Referencia

Documento Maestro V2.0, secciones 4, 8–10, 12, 18 y 20.
