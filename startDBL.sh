#!/bin/bash

# --- PASO 1: LEVANTAR LA BASE DE DATOS (MySQL) ---

echo "🗄️  Construyendo y levantando contenedor de MySQL..."

# Detener y eliminar contenedor existente de MySQL (si está corriendo)
docker stop mysql-container 2>/dev/null || true
docker rm mysql-container 2>/dev/null || true

# Construir la imagen de MySQL a partir de DockerfileSQL
docker build -f DockerfileSQL -t mysql .

# Verificar que la construcción de la imagen fue exitosa
if [ $? -ne 0 ]; then
    echo "❌ Error: La construcción de la imagen 'mysql' falló. Revisa los logs."
    exit 1
fi

# Iniciar el contenedor de MySQL en segundo plano
docker run --name mysql-container -d -p 3306:3306 mysql

# Verificar que el contenedor de MySQL se haya iniciado
if [ $? -ne 0 ]; then
    echo "❌ Error: El contenedor de MySQL no pudo iniciarse. Revisa los logs."
    exit 1
fi

echo "⏳ Esperando que MySQL esté completamente disponible (esto puede tardar unos segundos)..."
# Bucle para esperar que MySQL esté listo (ejecuta un comando 'ping' dentro del contenedor)
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

echo ""
echo -e "\033[1;32m✅ Base de datos MySQL iniciada y lista para conexiones.\033[0m"
echo "   - Contenedor: mysql-container"
echo "   - Puerto: 3306"
echo ""
echo "💡 Para ver los logs: docker logs mysql-container"
echo "💡 Para detener la base de datos: docker stop mysql-container"
echo ""

exit 0 # Salida exitosa
