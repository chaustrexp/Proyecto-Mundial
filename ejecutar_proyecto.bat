@echo off
echo Compilando el proyecto...
mkdir bin 2>nul
javac -encoding utf-8 -d bin -cp "lib/*" mundial_app\Main.java mundial_app\dao\*.java mundial_app\modelos\*.java mundial_app\utils\*.java mundial_app\controladores\*.java mundial_app\vistas\*.java

if %errorlevel% neq 0 (
    echo.
    echo ----------------------------------------------------
    echo ¡Hubo un error al compilar el proyecto!
    echo ----------------------------------------------------
    pause
    exit /b %errorlevel%
)

echo.
echo Ejecutando la aplicacion...
java -cp "bin;lib/*" Main

pause
