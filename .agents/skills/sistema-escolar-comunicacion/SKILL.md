---
name: sistema-escolar-comunicacion
description: Analiza o implementa avisos, comunicados, notificaciones, correo y mensajería del Sistema de Gestión Escolar.
---

# Módulo Comunicación

## Alcance documentado

El módulo puede incluir noticias, comunicados, notificaciones, avisos y mensajería. El roadmap contempla correo y mensajería según necesidad concreta y política institucional.

## Principios

- Es un módulo de evolución; no asumir que forma parte del alcance mínimo de V1.
- Definir casos de uso, actores, canales, preferencias, eventos que generan notificación y reglas de entrega antes de implementarlo.
- Respetar permisos y contexto: no enviar información académica o personal a destinatarios no autorizados.
- Evitar exponer datos personales en asuntos, vistas previas, URLs o logs.
- Auditar acciones sensibles cuando corresponda.

## No asumir

No inventes proveedores, plantillas, canales, horarios de envío, reintentos, prioridad, confirmaciones de lectura, retención, consentimiento ni política de notificaciones.

## Dependencias

Usuarios/seguridad, estudiantes/acudientes/profesores según destinatarios, configuración, auditoría e infraestructura. Añade pruebas para autorización, destinatarios, errores y duplicados.

## Referencia

Documento Maestro V2.0, secciones 4, 14, 15, 18 y 20.
