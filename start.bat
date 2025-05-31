@echo off

echo 📦 Limpiando y empaquetando el proyecto...
mvn clean package

echo 🔧 Construyendo imagen con Docker...
docker build -f DockerfileJetty -t tallerwebi .

echo 🚀 Ejecutando contenedor con Docker...
echo.
echo ➡ Link -^> http://localhost:8080/tallerwebi-base-1.0-SNAPSHOT/
echo.

REM Windows Docker Desktop needs port mapping and host.docker.internal for database access
docker run -p 8080:8080 -e DB_HOST=host.docker.internal tallerwebi

pause