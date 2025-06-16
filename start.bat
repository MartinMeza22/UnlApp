@echo off

echo Paso 1: Maven
call mvn clean package
echo Maven exit code: %ERRORLEVEL%

echo Paso 2: Docker Build
docker build -f DockerfileJetty -t tallerwebi .
echo Docker build exit code: %ERRORLEVEL%

echo Paso 3: Docker Run
docker run -p 8080:8080 -e DB_HOST=host.docker.internal tallerwebi


