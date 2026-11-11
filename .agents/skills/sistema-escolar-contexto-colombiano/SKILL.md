---
name: sistema-escolar-contexto-colombiano
description: Valida reglas escolares frente al contexto colombiano, Decreto 1290 de 2009, Decreto 1075 de 2015, PEI y SIEE; úsala para identificar decisiones académicas pendientes.
---

# Contexto colombiano y reglas institucionales

## Alcance

Esta skill evita convertir supuestos sobre colegios colombianos en reglas de software. El Documento Maestro orienta el diseño, pero no sustituye validación jurídica ni la aprobación institucional.

## Marco expresamente citado por el Documento Maestro

- Para evaluación y promoción en preescolar, básica y media, revisar el Decreto 1290 de 2009, incorporado en el Decreto 1075 de 2015, sus modificaciones aplicables y el SIEE/PEI de la institución.
- La institución debe definir y documentar las reglas que dependan de su SIEE/PEI.
- La escala nacional contempla desempeños Superior, Alto, Básico y Bajo; no deduzcas equivalencias numéricas ni promoción universal a partir de esas categorías.
- La referencia oficial del documento es el Decreto 1075 de 2015 publicado por el Ministerio de Educación Nacional. Verifica vigencia, modificaciones y texto oficial antes de tomar decisiones legales actuales.

## Reglas de diseño

No codifiques como universales:

- nota mínima o umbral de aprobación;
- ponderaciones o fórmulas de evaluación;
- reglas de precisión y redondeo;
- número o duración de periodos;
- umbrales de asistencia;
- criterios de promoción/reprobación;
- estrategias, plazos o condiciones de recuperación/apoyo;
- procedimiento de reclamación o reapertura de periodos.

Cuando una regla dependa de la institución, registra: descripción de la decisión, responsable de confirmarla, fuente institucional (PEI/SIEE u otra), impacto en datos/API/UI y pruebas que se derivan. Hasta confirmación, mantenla como pendiente configurable o bloqueante; no inventes un valor por defecto.

## Datos personales y menores

El proyecto debe proteger especialmente los datos de niños, niñas y adolescentes y aplicar finalidad, necesidad, acceso restringido, seguridad y conservación. El Documento Maestro indica que el tratamiento debe ajustarse a la normativa colombiana aplicable, pero no desarrolla aquí un análisis jurídico completo. No atribuyas obligaciones legales específicas sin consultar fuente vigente y validar con responsables institucionales/jurídicos.

## Método ante una pregunta normativa

1. Identifica si se pregunta por norma nacional, decisión institucional o comportamiento del software.
2. Verifica la fuente oficial vigente cuando la respuesta dependa de legislación actual.
3. Distingue texto normativo de interpretación y decisión del colegio.
4. No conviertas la orientación técnica del Documento Maestro en asesoría jurídica.
5. Documenta las reglas aprobadas y crea pruebas basadas en ellas.

## Referencia

Documento Maestro V2.0, secciones 1, 8, 9, 20 y 22; referencia normativa en sección 8 y referencias finales.
