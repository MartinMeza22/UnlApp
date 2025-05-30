#!/bin/bash

# Construye la imagen con Podman
echo "🔧 Construyendo imagen con Podman..."
podman build -f DockerfileJetty -t tallerwebi .

# Ejecuta Maven para limpiar y empaquetar el proyecto
echo "📦 Limpiando y empaquetando el proyecto..."
mvn clean package

# Ejecuta el contenedor
echo -e "🚀 Ejecutando contenedor con Podman..."
echo -e "\033[4;32m➡ Link -> http://0.0.0.0:8080/tallerwebi-base-1.0-SNAPSHOT/login\033[0m"

podman run -p 8080:8080 tallerwebi

