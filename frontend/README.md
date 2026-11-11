# Sistema de Gestión Escolar - Frontend SPA (Angular)

Single Page Application construida con Angular 22, TypeScript y consumo de API tipado generado mediante OpenAPI Generator.

## Arquitectura

- **Seguridad en Memoria:** El token JWT reside exclusivamente en memoria volátil de la aplicación (`AuthService`), sin persistirse en `localStorage` ni `sessionStorage` para mitigar riesgos XSS.
- **Interceptors:**
  - `authInterceptor`: Adjunta la cabecera `Authorization: Bearer <token>` únicamente a peticiones dirigidas a la API autorizada (`environment.apiUrl`).
  - `errorInterceptor`: Captura errores semánticos (401 para expiración/cierre de sesión con redirección a login; 403 para permisos insuficientes sin desautenticar; 400/409/429/500 estructurados).
- **Guards:**
  - `authGuard`: Protege rutas privadas frente a navegación anónima.
  - `roleGuard`: Restringe acceso por rol institucional (`ADMINISTRADOR`, `RECTOR`, `COORDINADOR`, `PROFESOR`, `ESTUDIANTE`, `ACUDIENTE`, `SECRETARIA`).
- **Contrato y Cliente OpenAPI:**
  - Código generado en `src/app/core/api/generated/` a partir de `../openapi/openapi.yaml`.
  - No debe editarse manualmente; se regenera con el script correspondiente.

## Scripts Disponibles

```bash
# Instalar dependencias
npm install

# Generar cliente HTTP y modelos TypeScript desde el contrato OpenAPI
npm run generate:api

# Ejecutar pruebas unitarias (Vitest)
npm test

# Ejecutar pruebas en modo observador continuo
npm run test:watch

# Compilar para producción
npm run build

# Levantar servidor de desarrollo local (puerto 4200)
npm start
```
