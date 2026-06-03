@echo off
REM Script para compilar y ejecutar los tests de SonarQ_Python

echo ========================================
echo Compilando el proyecto...
echo ========================================

REM Crear directorio out si no existe
if not exist out mkdir out

REM Compilar
javac -encoding UTF-8 -cp "ANTLR/*;gen" src/*.java -d out

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilacion fallida
    exit /b 1
)

echo.
echo ========================================
echo Compilacion exitosa!
echo ========================================
echo.

REM Ejecutar tests
for %%f in (input\*.py) do (
    echo.
    echo ========================================
    echo Analizando: %%f
    echo ========================================
    java -cp "out;ANTLR/*;gen" Main "%%f"
    echo.
)

echo.
echo ========================================
echo Tests completados
echo ========================================

@REM Made with Bob
