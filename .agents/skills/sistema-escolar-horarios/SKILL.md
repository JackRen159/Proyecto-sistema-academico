---
name: sistema-escolar-horarios
description: Diseña, implementa o revisa horarios de cursos/grupos y profesores, aulas, franjas temporales y detección de conflictos del Sistema de Gestión Escolar.
---

# Módulo Horarios

## Fuente funcional vinculante (V2)

Antes de diseñar, implementar o probar este módulo, lee:
- Especificación del módulo: `docs/especificacion/modulos/09_Horarios_v2.md`
- Reglas transversales: `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- Casos de uso cubiertos: `UC-04`, `UC-10`, `UC-PROV-20` a `UC-PROV-22`
- Reglas de negocio aplicables: `RN-04`, `RN-09`, `RN-HOR-01` a `RN-HOR-07`
- Convierte cada fila `A#` y `E#` (y su código HTTP esperado) en criterios de aceptación y pruebas automatizadas.

## Alcance documentado

Gestionar horarios de cursos/grupos y profesores, aulas y prevención de cruces.

## Regla de conflicto

El sistema debe detectar conflictos de horario entre:

- profesor;
- curso/grupo;
- aula;
- espacio o periodo temporal definido.

La granularidad temporal debe definirse antes de implementar la validación; no supongas duración estándar, bloques fijos, días lectivos ni calendario universal.

## Diseño

- Modela las asignaciones con referencias verificables a grupo, profesor, asignatura y aula cuando aplique.
- Define la política de solapamiento y excepciones con la institución.
- Protege cambios con permisos y auditoría cuando sean sensibles.
- Usa constraints/índices o validación transaccional cuando ayuden a proteger invariantes; analiza concurrencia para evitar dos asignaciones incompatibles simultáneas.

## Dependencias

Estructura académica, profesores/asignaciones, aulas/recursos si se formalizan, configuración, seguridad, auditoría, base de datos y pruebas.

## Pendientes

No inventes reglas de disponibilidad docente, recreos, restricciones de jornada, tiempo de desplazamiento, aulas compartidas o prioridad de asignaciones.

## Referencia

Documento Maestro V2.0, secciones 4, 7–8, 12, 16 y 18.
