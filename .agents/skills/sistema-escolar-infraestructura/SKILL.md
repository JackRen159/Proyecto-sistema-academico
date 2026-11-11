---
name: sistema-escolar-infraestructura
description: Configura o revisa entornos, perfiles, secretos, Docker/Compose, HTTPS, Nginx, backups, restauración, logs, CI/CD y despliegue del Sistema de Gestión Escolar.
---

# Infraestructura y despliegue

## Principios

- La infraestructura debe hacer reproducible el entorno y proteger los datos; no agregar tecnología por moda.
- Backend, frontend y PostgreSQL deben configurarse por entorno (desarrollo, pruebas y despliegue).
- Mantén credenciales fuera de Git y fuera del bundle Angular. Usa variables de entorno o gestor de secretos.
- Si una credencial se expuso, rótala primero; después evalúa historial y limpieza con un procedimiento explícito.

## Tecnologías contempladas

- Docker / Docker Compose para entornos reproducibles cuando corresponda.
- Nginx como reverse proxy y HTTPS cuando el despliegue lo requiera.
- HTTPS obligatorio en despliegues que atraviesen redes no confiables.
- Redis es opcional: solo para una necesidad medida de cache, colas o control de frecuencia.
- CI/CD, observabilidad y servicios auxiliares se incorporan según alcance y riesgo.

## Base de datos y continuidad

- Define copias de seguridad, retención y pruebas de restauración antes de manejar datos institucionales reales.
- Las migraciones Flyway o Liquibase deben ejecutarse de forma explícita y reproducible.
- No uses `ddl-auto=update` como mecanismo permanente de despliegue.

## Seguridad de despliegue

- Configura CORS por ambiente con orígenes exactos.
- Usa TLS, encabezados de seguridad y CSP apropiados.
- Restringe acceso a logs, archivos generados y reportes.
- No incluyas datos personales o secretos en logs innecesarios.

## Verificación

Comprueba configuración, variables requeridas, conectividad, migraciones, HTTPS, CORS, backups y restauración. No afirmes que se ejecutó una restauración o despliegue sin haberlo probado.

## Referencia

Documento Maestro V2.0, secciones 2, 7, 10, 14, 17, 18, 20 y 22.
