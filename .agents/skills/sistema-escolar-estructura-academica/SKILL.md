---
name: sistema-escolar-estructura-academica
description: Diseña o implementa año lectivo, grados, grupos, asignaturas, periodos académicos, planes de estudio y asignaciones académicas del Sistema de Gestión Escolar.
---

# Módulo Estructura académica

## Alcance documentado

Incluye los conceptos de año lectivo, grados, grupos, asignaturas, periodos académicos y planes de estudio. Se relaciona con profesores/asignaciones, matrículas, calificaciones, asistencia y horarios.

## Reglas documentadas

- Los periodos académicos pueden estar abiertos o cerrados.
- Las modificaciones posteriores al cierre requieren política explícita de autorización, corrección y auditoría.
- No debe registrarse información incompatible con un periodo cerrado.
- El número de periodos, fechas, calendarios, relaciones y reglas concretas deben responder a la institución y a su documentación.
- Las escalas y reglas de evaluación deben ser configurables cuando dependan del SIEE.

## Reglas de diseño

- Diseña relaciones y vigencias explícitas para evitar confundir año lectivo, grupo, asignatura y periodo.
- Evita codificar fechas, cantidades o estructuras académicas universales sin aprobación.
- Protege cambios de configuración con permisos y auditoría.
- Asegura integridad referencial y pruebas de periodos abiertos/cerrados.

## Dependencias

Profesores/asignaciones, matrículas, calificaciones, asistencia, horarios, configuración, seguridad, auditoría, base de datos y API.

## Referencia

Documento Maestro V2.0, secciones 4, 7–9, 12 y 18.
