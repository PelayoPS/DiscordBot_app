# Script para construir el ejecutable del Discord Bot Manager usando jpackage
# Autor: Discord Bot Team
# Versión: 1.0.0

Write-Host "=== Construcción del Discord Bot Manager ===" -ForegroundColor Green
Write-Host ""

# Verificar que Java 17+ esté disponible
Write-Host "Verificando versión de Java..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.Line }
Write-Host "Java detectado: $javaVersion" -ForegroundColor White

if (-not $javaVersion) {
    Write-Host "Error: Java no está instalado o no está en el PATH" -ForegroundColor Red
    exit 1
}

# Limpiar construcciones anteriores
Write-Host "Limpiando construcciones anteriores..." -ForegroundColor Yellow
./gradlew clean

# Construir el proyecto
Write-Host "Construyendo el proyecto..." -ForegroundColor Yellow
./gradlew build

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Falló la construcción del proyecto" -ForegroundColor Red
    exit 1
}

# Crear la imagen runtime
Write-Host "Creando imagen runtime..." -ForegroundColor Yellow
./gradlew runtime

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Falló la creación de la imagen runtime" -ForegroundColor Red
    exit 1
}

# Crear el ejecutable con jpackage
Write-Host "Creando ejecutable con jpackage..." -ForegroundColor Yellow
./gradlew jpackage

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Falló la creación del ejecutable" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Construcción completada exitosamente ===" -ForegroundColor Green
Write-Host "El ejecutable se encuentra en: build/jpackage/" -ForegroundColor Cyan
Write-Host "Instalador MSI disponible para distribución." -ForegroundColor White
Write-Host ""
