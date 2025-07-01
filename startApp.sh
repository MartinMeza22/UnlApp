#!/bin/bash

# --- PASO 1: COMPILAR Y EMPAQUETAR LA APLICACIÓN JAVA ---

echo "📦 Limpiando y empaquetando el proyecto Java con Maven..."
mvn clean package

# Verificar si Maven tuvo éxito
if [ $? -ne 0 ]; then
    echo "❌ Error: La compilación y empaquetado de Maven falló. Revisa la salida de Maven."
    exit 1
fi

echo "✅ Proyecto Java empaquetado con éxito."
echo ""

# --- PASO 2: CONSTRUIR LA IMAGEN DE LA APLICACIÓN WEB (JETTY) ---

echo "🛠️  Construyendo imagen de Docker para la aplicación web..."
docker build -f DockerfileJetty -t tallerwebi .

# Verificar si la construcción de la imagen Docker tuvo éxito
if [ $? -ne 0 ]; then
    echo "❌ Error: La construcción de la imagen Docker 'tallerwebi' falló."
    exit 1
fi

echo "✅ Imagen 'tallerwebi' construida con éxito."
echo ""

# --- PASO 3: LEVANTAR LA APLICACIÓN WEB EN UN CONTENEDOR ---

echo "🚀 Iniciando contenedor de la aplicación web..."

# Detener y eliminar contenedor existente de la aplicación (si está corriendo)
docker stop tallerwebi-app-container 2>/dev/null || true
docker rm tallerwebi-app-container 2>/dev/null || true

# Iniciar el contenedor de la aplicación, enlazándolo a la red del host para que pueda ver MySQL
# El flag -e DB_HOST=host.docker.internal permite que la app dentro del contenedor se conecte a MySQL
# que está corriendo en el host de Docker.
docker run --name tallerwebi-app-container -p 8080:8080 -e DB_HOST=host.docker.internal tallerwebi

# Verificar si el contenedor de la aplicación se inició
if [ $? -ne 0 ]; then
    echo "❌ Error: El contenedor de la aplicación no pudo iniciarse. Revisa los logs."
    docker logs tallerwebi-app-container
    exit 1
fi

echo ""
echo -e "\033[4;32m🎉 ¡APLICACIÓN LANZADA CON ÉXITO! 🎉\033[0m"
echo -e "\033[4;32m➡ Visita tu aplicación en el navegador: http://localhost:8080/tallerwebi-base-1.0-SNAPSHOT/\033[0m"
echo ""
echo "💡 Para ver los logs de la aplicación: docker logs tallerwebi-app-container"
echo "💡 Para detener la aplicación: docker stop tallerwebi-app-container"
echo ""

exit 0 # Salida exitosa
