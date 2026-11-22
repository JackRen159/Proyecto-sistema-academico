# ── Stage 1: Build (Compilación de la SPA Angular) ──────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app

# Instalar dependencias con caché limpia
COPY package*.json ./
RUN npm ci

# Copiar código fuente y generar bundle de producción optimizado
COPY . .
RUN npm run build

# ── Stage 2: Runtime (Servidor Web Nginx Alpine de Alto Rendimiento) ──────────
FROM nginx:1.27-alpine AS runner

# Limpiar configuración por defecto y desplegar configuración personalizada
RUN rm -rf /etc/nginx/conf.d/* /usr/share/nginx/html/*
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar bundle compilado de la aplicación Angular
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

# Sonda de salud integrada para el reverse proxy
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/healthz || exit 1

CMD ["nginx", "-g", "daemon off;"]
