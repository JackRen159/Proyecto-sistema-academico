#!/bin/bash
set -e

echo "================================================================="
echo " Verificación de Infraestructura Docker y Configuración (Fase D) "
echo "================================================================="

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

echo "[1/5] Verificando existencia de artefactos de infraestructura..."
test -f "Dockerfile" || { echo "ERROR: Falta Dockerfile backend"; exit 1; }
test -f "frontend/Dockerfile" || { echo "ERROR: Falta frontend/Dockerfile"; exit 1; }
test -f "frontend/nginx.conf" || { echo "ERROR: Falta frontend/nginx.conf"; exit 1; }
test -f "docker-compose.yml" || { echo "ERROR: Falta docker-compose.yml"; exit 1; }
test -f "deploy.env.example" || { echo "ERROR: Falta deploy.env.example"; exit 1; }
test -f "src/main/resources/application-prod.properties" || { echo "ERROR: Falta application-prod.properties"; exit 1; }
echo "  ✓ Todos los archivos de infraestructura existen."

echo "[2/5] Validando sintaxis y estructura de docker-compose.yml..."
python3 - << 'EOF'
import yaml, sys

with open("docker-compose.yml", "r") as f:
    try:
        data = yaml.safe_load(f)
    except Exception as e:
        print(f"ERROR: Error de sintaxis en docker-compose.yml: {e}")
        sys.exit(1)

services = data.get("services", {})
required_services = ["postgres", "backend", "frontend"]
for s in required_services:
    if s not in services:
        print(f"ERROR: Servicio requerido '{s}' ausente en docker-compose.yml")
        sys.exit(1)
    if "healthcheck" not in services[s]:
        print(f"ERROR: Servicio '{s}' no tiene healthcheck configurado")
        sys.exit(1)

print("  ✓ docker-compose.yml es sintácticamente válido y contiene los 3 servicios con healthcheck.")
EOF

echo "[3/5] Verificando directivas de seguridad en Dockerfile y Nginx..."
grep -q "USER spring:spring" Dockerfile || { echo "ERROR: Dockerfile no ejecuta como usuario no privilegiado"; exit 1; }
grep -q "HEALTHCHECK" Dockerfile || { echo "ERROR: Dockerfile no tiene directiva HEALTHCHECK"; exit 1; }
grep -q "X-Frame-Options" frontend/nginx.conf || { echo "ERROR: frontend/nginx.conf no contiene encabezado X-Frame-Options"; exit 1; }
grep -q "Content-Security-Policy" frontend/nginx.conf || { echo "ERROR: frontend/nginx.conf no contiene directiva CSP"; exit 1; }
grep -q "proxy_pass http://backend:8080/api/;" frontend/nginx.conf || { echo "ERROR: frontend/nginx.conf no enruta /api/ hacia backend"; exit 1; }
echo "  ✓ Hardening y directivas de seguridad verificadas en Dockerfile y Nginx."

echo "[4/5] Verificando perfil de producción backend application-prod.properties..."
grep -q "spring.jpa.hibernate.ddl-auto=validate" src/main/resources/application-prod.properties || { echo "ERROR: ddl-auto no es validate en prod"; exit 1; }
grep -q "spring.flyway.enabled=true" src/main/resources/application-prod.properties || { echo "ERROR: Flyway no está habilitado en prod"; exit 1; }
echo "  ✓ application-prod.properties cumple con las reglas de inmutabilidad y Flyway."

echo "[5/5] Estado de herramientas Docker en el entorno local..."
if command -v docker &> /dev/null; then
    echo "  ✓ Docker CLI detectado: $(docker --version)"
    if docker compose version &> /dev/null; then
        echo "  ✓ Docker Compose detectado: $(docker compose version)"
    fi
else
    echo "  ℹ Docker CLI no está presente en este entorno Linux/WSL directo (los contenedores se ejecutan en host o CI)."
fi

echo "================================================================="
echo " ¡Verificación de infraestructura de la Fase D exitosa!          "
echo "================================================================="
