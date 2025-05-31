@echo off

echo 🗄️  Construyendo imagen de MySQL...
docker build -f DockerfileSQL -t mysql .

echo 🚀 Iniciando contenedor de MySQL...
echo.
echo ➡ Database -^> localhost:3306
echo 📋 Database: tallerwebi ^| User: user ^| Password: user
echo.

REM Stop existing MySQL container if running
docker stop mysql-container >nul 2>&1
docker rm mysql-container >nul 2>&1

REM Start MySQL container
docker run --name mysql-container -d -p 3306:3306 mysql

echo ✅ MySQL está ejecutándose en segundo plano
echo 💡 Para ver los logs: docker logs mysql-container
echo 💡 Para detener: docker stop mysql-container
echo.

pause