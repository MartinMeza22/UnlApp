#!/bin/bash

# --- PASO 1: LEVANTAR LA BASE DE DATOS (MySQL) ---

echo "🗄️  Construyendo y levantando contenedor de MySQL..."

# Detener y eliminar contenedor existente de MySQL (si está corriendo)
docker stop mysql-container 2>/dev/null || true
docker rm mysql-container 2>/dev/null || true

# Construir la imagen de MySQL
docker build -f DockerfileSQL -t mysql .

# Iniciar el contenedor de MySQL
docker run --name mysql-container -d -p 3306:3306 mysql

# Verificar que el contenedor de MySQL se haya iniciado
if [ $? -ne 0 ]; then
    echo "❌ Error: El contenedor de MySQL no pudo iniciarse. Revisa los logs."
    exit 1
fi

echo "⏳ Esperando que MySQL esté completamente disponible (esto puede tardar unos segundos)..."
# Bucle para esperar que MySQL esté listo (ejecuta un comando simple dentro de la DB)
max_attempts=60
attempt_num=1
while ! docker exec mysql-container mysqladmin ping -hlocalhost --silent; do
  if [ $attempt_num -eq $max_attempts ]; then
    echo "❌ Error: MySQL no está disponible después de $max_attempts intentos. Revisa los logs del contenedor."
    docker logs mysql-container
    exit 1
  fi
  sleep 1
  attempt_num=$((attempt_num+1))
done

echo "✅ MySQL está ejecutándose en segundo plano y listo para conexiones."
echo "💡 Para ver los logs: docker logs mysql-container"
echo "💡 Para detener: docker stop mysql-container"
echo ""

# --- PASO 2: COMPILAR Y EMPAQUETAR LA APLICACIÓN JAVA ---

echo "📦 Limpiando y empaquetando el proyecto Java..."
mvn clean package

# Verificar si Maven tuvo éxito
if [ $? -ne 0 ]; then
    echo "❌ Error: La compilación y empaquetado de Maven falló. Revisa la salida de Maven."
    exit 1
fi

echo "✅ Proyecto Java empaquetado con éxito."
echo ""

# --- PASO 3: CONSTRUIR LA IMAGEN DE LA APLICACIÓN WEB (JETTY) ---

echo "🛠️  Construyendo imagen de Docker para la aplicación web..."
docker build -f DockerfileJetty -t tallerwebi .

# Verificar si la construcción de la imagen Docker tuvo éxito
if [ $? -ne 0 ]; then
    echo "❌ Error: La construcción de la imagen Docker de la aplicación falló."
    exit 1
fi

echo "✅ Imagen 'tallerwebi' construida con éxito."
echo ""

# --- PASO 4: LEVANTAR LA APLICACIÓN WEB EN UN CONTENEDOR ---

echo "🚀 Iniciando contenedor de la aplicación web..."

# Detener y eliminar contenedor existente de la aplicación (si está corriendo)
docker stop tallerwebi-app-container 2>/dev/null || true
docker rm tallerwebi-app-container 2>/dev/null || true

# Iniciar el contenedor de la aplicación
docker run --name tallerwebi-app-container -p 8080:8080 -e DB_HOST=host.docker.internal tallerwebi

# Verificar si el contenedor de la aplicación se inició
if [ $? -ne 0 ]; then
    echo "❌ Error: El contenedor de la aplicación no pudo iniciarse. Revisa los logs."
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