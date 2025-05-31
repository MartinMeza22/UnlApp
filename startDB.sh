#!/bin/bash

echo "🗄️  Construyendo imagen de MySQL..."
podman build -f DockerfileSQL -t mysql .

echo "🚀 Iniciando contenedor de MySQL..."
echo -e "\033[4;32m➡ Database -> localhost:3306\033[0m"
echo -e "\033[0;33m📋 Database: tallerwebi | User: user | Password: user\033[0m"

# Stop existing MySQL container if running
podman stop mysql-container 2>/dev/null || true
podman rm mysql-container 2>/dev/null || true

# Start MySQL container
podman run --name mysql-container -d -p 3306:3306 mysql

echo "✅ MySQL está ejecutándose en segundo plano"
echo "💡 Para ver los logs: podman logs mysql-container"
echo "💡 Para detener: podman stop mysql-container"