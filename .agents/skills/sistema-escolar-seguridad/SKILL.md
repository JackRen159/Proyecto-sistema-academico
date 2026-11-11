---
name: sistema-escolar-seguridad
description: Implementa o revisa usuarios, autenticación, Spring Security, RBAC, permisos contextuales, cookies/tokens, CSRF, CORS, XSS y controles de seguridad Angular del Sistema de Gestión Escolar.
---

# Usuarios y seguridad

## Autoridad de seguridad

- La seguridad efectiva está en Spring Boot/Spring Security. Angular no es una frontera de seguridad.
- Cada solicitud protegida debe validar autenticación y autorización; algunas operaciones también requieren comprobar acceso al recurso concreto.
- RBAC es el modelo base, complementado por permisos contextuales.
- Los roles previstos en el Documento Maestro son: ADMINISTRADOR, RECTOR, COORDINADOR, PROFESOR, ESTUDIANTE, ACUDIENTE y SECRETARIA. Los permisos se definen por caso de uso, no solo por nombre del rol.
- Ejemplos contextuales: el profesor actúa solo sobre grupos/asignaturas asignados; el acudiente consulta únicamente estudiantes vinculados y autorizados.

## Autenticación y credenciales

- Utiliza hashing de contraseñas diseñado para contraseñas, como BCrypt o Argon2 mediante bibliotecas mantenidas. Nunca texto plano ni SHA-256 simple.
- Implementa cierre de sesión, expiración, renovación/revocación/rotación según la estrategia elegida.
- Protege recuperación de acceso y evita revelar existencia de cuentas cuando ello represente riesgo.
- No registres contraseñas, JWT, refresh tokens, cookies de sesión ni secretos.
- No guardes credenciales/tokens en `localStorage` ni `sessionStorage`.

## Estrategia de sesión de la SPA

La preferencia documentada para el cliente web es una cookie de autenticación con:

- `HttpOnly` para que JavaScript no pueda leer directamente la cookie;
- `Secure` en HTTPS/producción;
- `SameSite=Lax` o `Strict` según flujos; `None` solo si hay necesidad cross-site y siempre con `Secure`;
- dominio y ruta limitados; evaluar `__Host-` cuando el despliegue lo permita;
- expiración, renovación y revocación explícitas.

Alternativa admisible: access token Bearer solo en memoria de la SPA, sin persistencia web. Documenta impacto en renovación, recarga, cierre de sesión y XSS. Nunca persistas refresh tokens en storage web.

`HttpOnly` reduce extracción directa del token por JavaScript, pero no elimina XSS ni evita que un script malicioso ejecute acciones desde la sesión.

## CSRF y XSS

Cuando el navegador envía automáticamente la cookie de autenticación:

- configura protección CSRF apropiada en Spring Security;
- evalúa token sincronizado o double-submit correctamente implementado;
- si Angular debe leer cookie CSRF para enviar un header, esa cookie CSRF puede ser legible; no hagas legible la cookie de autenticación;
- adjunta CSRF solo a métodos que modifican estado y a la API confiable;
- considera validar `Origin`/`Referer` como defensa adicional;
- `SameSite` es defensa en profundidad, no sustituto único;
- `GET`/`HEAD` no deben cambiar estado;
- prueba que la solicitud sin token CSRF válido sea rechazada cuando aplique.

Defensas XSS Angular: preferir bindings seguros; evitar HTML no confiable, `innerHTML` y APIs que omiten sanitización; no usar `bypassSecurityTrust...` rutinariamente; sanitizar HTML enriquecido si se admite; definir CSP; actualizar dependencias; evitar datos sensibles en URLs/logs.

Si se elige Bearer sin credenciales automáticas, documenta el análisis CSRF correspondiente. Evita configuraciones híbridas accidentales.

## CORS

- Configura orígenes explícitos por entorno. El origen de desarrollo previsto es `http://localhost:4200` cuando corresponda.
- Integra CORS con Spring Security y procesa preflight `OPTIONS` apropiadamente.
- Define métodos/headers permitidos y headers expuestos de forma explícita.
- Si hay cookies/credenciales, habilita credenciales solo para orígenes explícitos; nunca `allowCredentials=true` con `*`.
- No reflejes automáticamente `Origin` ni permitas subdominios indiscriminadamente.
- CORS no reemplaza autenticación ni autorización.

## Angular: guards e interceptores

- Los guards controlan navegación/UX, no autorizan el backend.
- Los interceptores pueden centralizar CSRF, errores y estado de petición; solo deben adjuntar credenciales a la API de confianza.
- Maneja `401` como falta/expiración de autenticación y `403` como permiso insuficiente; evita bucles de redirección.
- Centraliza el estado de sesión sin copias inconsistentes en múltiples componentes.

## Pruebas mínimas relevantes

Acceso anónimo, rol insuficiente, acceso fuera del recurso autorizado, sesión/token vencido e inválido, CSRF inválido, CORS permitido/rechazado/preflight y permisos por endpoint/caso de uso.

## Referencias del documento

Documento Maestro V2.0, secciones 5, 10, 11, 16 y referencias técnicas de Spring Security/OWASP/Angular en la sección final. Para cambios de seguridad, verifica documentación oficial vigente de las versiones realmente usadas.
