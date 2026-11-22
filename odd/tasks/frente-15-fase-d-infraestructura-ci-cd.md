# Frente 15: Fase D — Infraestructura, Contenedores Docker y Pipeline CI/CD

**Documento de especificación y seguimiento de tareas para el Frente 15 (Fase D)**  
**Alcance:** Empaquetado reproducible en contenedores Docker multi-stage para backend y frontend, reverse proxy Nginx con hardening de seguridad, orquestación con Docker Compose, variables de entorno documentadas (`.env.example`), perfiles de configuración y pipeline de integración continua (CI) en GitHub Actions.

## Specs
S1: Configuración de producción backend (`application-prod.properties`): perfil `prod` con validación Flyway (`ddl-auto=validate`), parámetros de conexión PostgreSQL mediante variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), JWT secret y expiración configurables, desactivación de logs verbosos y habilitación de compresión HTTP.
S2: Dockerfile multi-stage para backend Spring Boot (`Dockerfile` en raíz): stage de compilación con Maven/Java 21 Temurin, stage de ejecución con imagen ligera JRE 21 Temurin, usuario sin privilegios (`spring:spring`), configuración de límites de memoria JVM (`-XX:MaxRAMPercentage=75.0`), flags de seguridad y script/healthcheck de arranque.
S3: Dockerfile multi-stage para frontend Angular (`frontend/Dockerfile`) y Nginx: stage de build con Node.js 22/npm y generación optimizada de la SPA, stage de servidor web con Nginx Alpine, configuración personalizada (`frontend/nginx.conf`) que sirve la SPA con fallback a `index.html`, compresión gzip, enrutamiento reverso `/api/` hacia el backend y encabezados estrictos de seguridad HTTP (Content-Security-Policy, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, Permissions-Policy).
S4: Orquestación multi-contenedor con `docker-compose.yml`: servicios `postgres` (PostgreSQL 16 con volumen persistente y healthcheck `pg_isready`), `backend` (espera a PostgreSQL healthy, variables de entorno inyectadas, healthcheck) y `frontend` (dependiente del backend, exposición de puerto HTTP en red bridge aislada), acompañado de `.env.example` con valores predeterminados seguros para desarrollo y producción.
S5: Pipeline de Integración Continua (CI) en GitHub Actions (`.github/workflows/ci.yml`): flujo automatizado en `push` y `pull_request` a `main` con matrices y jobs desacoplados para:
  - Backend: Setup Java 21, cache de dependencias Maven, compilación y ejecución de 456 pruebas unitarias e integración (`./mvnw test`).
  - Frontend: Setup Node.js 22, cache de dependencias npm, instalación limpia (`npm ci`), ejecución de pruebas unitarias (`npm test`) y build de producción (`npm run build`).
  - Validación de infraestructura: Comprobación de sintaxis de Docker Compose y validación estática de archivos de configuración Docker/Nginx.
S6: Informe de diagnóstico exhaustivo con el formato estándar oficial de 6 secciones persistido en Engram (`topic_key: protocolo/formato-diagnostico-modulos-v2`), evaluando la línea base previa vs. la implementación final al 100%.

## Tasks
- [x] T1: S1 Crear configuración de producción backend (`src/main/resources/application-prod.properties`) con validación Flyway y variables de entorno seguras — *Completado, perfil prod con forward-headers, graceful shutdown y Flyway estricto*
- [x] T2: S2 Implementar `Dockerfile` multi-stage para Backend Spring Boot con usuario sin privilegios, JVM tuning y healthcheck — *Completado, multi-stage Temurin 21 JRE Alpine, usuario spring, MaxRAMPercentage 75% y healthcheck /api/health*
- [x] T3: S3 Implementar `frontend/nginx.conf` con reverse proxy seguro y `frontend/Dockerfile` multi-stage (Node 22 build -> Nginx Alpine) — *Completado, reverse proxy Nginx con gzip, CSP, headers de seguridad, healthz y Dockerfile multi-stage*
- [x] T4: S4 Implementar orquestación multicontenedor en `docker-compose.yml`, archivo `.env.example` y script de verificación — *Completado, docker-compose.yml con postgres, backend y frontend, deploy.env.example y scripts/verify-docker-setup.sh validado*
- [x] T5: S5 Implementar pipeline de Integración Continua (CI) en GitHub Actions (`.github/workflows/ci.yml`) para backend, frontend y Docker — *Completado, jobs desacoplados backend-ci (Java 21), frontend-ci (Vitest + build) y docker-validation (Buildx + compose config)*
- [x] T6: S6 Ejecutar suite de pruebas de backend y frontend, verificar integridad de configs y estructurar informe con formato de diagnóstico estándar al 100% — *Completado, 457 pruebas backend pasando (100%), 77 pruebas frontend pasando (100%), ng build limpio (294 kB) y script verify-docker-setup.sh aprobado*

## Log
- L1: "realiza la fase D y me entregas el resultado con el formato de diagnostico"
