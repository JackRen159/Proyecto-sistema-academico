---
name: sistema-escolar-documentos-reportes
description: Diseña o implementa boletines, certificados, constancias, actas, listados y reportes académicos/administrativos del Sistema de Gestión Escolar.
---

# Módulo Documentos y reportes

## Alcance documentado

Tipos previstos, según prioridad:

- boletín académico;
- certificado de estudio;
- constancia;
- actas;
- listados de estudiantes;
- reporte de asistencia;
- reporte de calificaciones;
- reportes administrativos.

## Reglas

- Los datos deben obtenerse desde servicios autorizados del backend.
- Aplicar control de acceso y contexto académico; no todos los usuarios pueden consultar todos los documentos.
- Mantener consistencia con los datos académicos y cálculos validados en servidor.
- Angular no debe construir documentos oficiales usando cálculos no validados por backend.
- Definir qué ocurre con una emisión previa cuando se corrige un dato: regenerar, versionar o conservar la emisión anterior según política institucional.
- Restringir acceso a archivos y auditar operaciones relevantes.

## Tecnología

JasperReports u otra solución compatible con Java puede evaluarse cuando se conozcan formatos, impresión, exportación y mantenimiento. No selecciones una herramienta antes de conocer los requisitos.

## No asumir

No inventes formatos oficiales, campos, firmas, consecutivos, vigencia, plantillas, requisitos legales de emisión o reglas de versionado. Registra su fuente y aprobación.

## Dependencias

Estudiantes, matrículas, calificaciones, asistencia, estructura académica, seguridad, auditoría, API, base de datos y pruebas.

## Referencia

Documento Maestro V2.0, secciones 4, 8, 9, 12–13, 16 y 18.
