#!/bin/bash

echo "📦 Limpiando y empaquetando el proyecto..."
mvn clean package

echo "🔧 Construyendo imagen con Podman..."
podman build -f DockerfileJetty -t tallerwebi .

echo -e "🚀 Ejecutando contenedor con Podman..."
echo -e "\033[4;32m➡ Link -> http://localhost:8080/tallerwebi-base-1.0-SNAPSHOT/\033[0m"

# For macOS, use port mapping and host.containers.internal for database
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🍎 Detected macOS - using port mapping"
    podman run -p 8080:8080 -e DB_HOST=host.containers.internal tallerwebi
else
    echo "🐧 Detected Linux - using host networking"
    podman run --network host tallerwebi
fi