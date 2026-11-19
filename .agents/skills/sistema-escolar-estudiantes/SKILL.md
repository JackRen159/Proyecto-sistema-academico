---
name: sistema-escolar-estudiantes
description: Analiza, diseña, implementa o prueba el módulo Estudiantes del Sistema de Gestión Escolar: datos personales, estado, documentos, historial y vínculos con acudientes.
---

# Módulo Estudiantes

## Fuente funcional vinculante (V2)

Antes de diseñar, implementar o probar este módulo, lee:
- Especificación del módulo: `docs/especificacion/modulos/02_Estudiantes_y_Acudientes_v2.md`
- Reglas transversales: `docs/especificacion/00_Guia_General_y_Reglas_Transversales_v2.md`
- Casos de uso cubiertos: `UC-12`, `UC-07`, `UC-PROV-04`
- Reglas de negocio aplicables: `RN-09`, `RN-EST-01`, `RN-EST-03`, `RN-EST-04`, `RN-EST-08`
- Convierte cada fila `A#` y `E#` (y su código HTTP esperado) en criterios de aceptación y pruebas automatizadas.

## Alcance documentado

- Administrar estudiantes y sus datos personales.
- Gestionar estado, documentos e historial académico.
- Relacionar al estudiante con uno o varios acudientes mediante el módulo/vínculo correspondiente.
- Integrarse con matrícula, estructura académica, calificaciones, asistencia y documentos/reportes.

## Reglas transversales

- Aplicar DTO y mapeo explícito; no exponer entidades JPA.
- Proteger datos personales, especialmente información de menores.
- Validar entradas en backend y reforzar integridad en PostgreSQL.
- Mantener historial y evitar eliminación física que rompa relaciones académicas; la política concreta de conservación debe confirmarse.
- Respetar permisos por rol y contexto; un usuario solo accede a información autorizada.
- Auditar cambios sensibles de datos personales cuando corresponda.

## Dependencias

Acudientes, matrículas, estructura académica, seguridad, auditoría, base de datos, API/OpenAPI y pruebas. Añadir Angular cuando se implemente la interfaz.

## Decisiones no definidas por esta fuente

No inventes campos obligatorios, catálogo de tipos de documento, reglas de edad/documentación, unicidad de cada dato, validaciones de contacto, estados detallados, flujo completo de admisión ni política de conservación. Deriva esos detalles de casos de uso y reglas aprobadas; registra las dudas como pendientes.

## Criterios de trabajo

Antes de implementar, revisa el modelo actual, DTO, mapper, repositorio, servicio, controlador, migraciones y pruebas. Para cada operación define actor, autorización, validación, resultado, errores y auditoría requerida.

## Referencia

Documento Maestro V2.0, secciones 4, 7–8, 12, 18, 21.2 y 22.
