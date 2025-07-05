# Script para detectar cambios de versión en build.gradle
# Uso: .\check-version-change.ps1

Write-Host "🔍 Discord Bot Manager - Detector de Cambios de Versión" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan

# Verificar que existe build.gradle
if (-not (Test-Path "build.gradle")) {
    Write-Host "❌ Error: No se encontró el archivo build.gradle" -ForegroundColor Red
    exit 1
}

# Obtener la versión actual
$currentVersionLine = Get-Content "build.gradle" | Where-Object { $_ -match "^version = " }
if ($currentVersionLine -match "version = ['""](.+)['""]") {
    $currentVersion = $matches[1]
    Write-Host "📦 Versión actual: $currentVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Error: No se pudo extraer la versión de build.gradle" -ForegroundColor Red
    exit 1
}

# Verificar si estamos en un repositorio git
if (-not (Test-Path ".git")) {
    Write-Host "⚠️ No es un repositorio git, no se puede comparar versiones" -ForegroundColor Yellow
    exit 0
}

# Verificar si hay commits anteriores
try {
    $commitCount = git rev-list --count HEAD
    if ($commitCount -gt 1) {
        # Obtener la versión del commit anterior
        $previousBuildGradle = git show "HEAD~1:build.gradle" 2>$null
        if ($previousBuildGradle) {
            $previousVersionLine = $previousBuildGradle | Where-Object { $_ -match "^version = " }
            if ($previousVersionLine -match "version = ['""](.+)['""]") {
                $previousVersion = $matches[1]
                Write-Host "📦 Versión anterior: $previousVersion" -ForegroundColor Blue
                
                # Comparar versiones
                if ($currentVersion -ne $previousVersion) {
                    Write-Host ""
                    Write-Host "✅ ¡CAMBIO DE VERSIÓN DETECTADO!" -ForegroundColor Green
                    Write-Host "   Anterior: $previousVersion" -ForegroundColor Yellow
                    Write-Host "   Nueva:    $currentVersion" -ForegroundColor Green
                    Write-Host ""
                    Write-Host "🚀 Esto activaría los builds automáticos:" -ForegroundColor Cyan
                    Write-Host "   - Backend (Java/Spring Boot)" -ForegroundColor White
                    Write-Host "   - Desktop (Electron)" -ForegroundColor White
                    Write-Host "   - Android (Capacitor)" -ForegroundColor White
                    Write-Host ""
                } else {
                    Write-Host ""
                    Write-Host "❌ No hay cambios en la versión" -ForegroundColor Red
                    Write-Host "🔄 No se ejecutarían builds automáticos" -ForegroundColor Yellow
                }
            } else {
                Write-Host "⚠️ No se pudo extraer la versión anterior" -ForegroundColor Yellow
            }
        } else {
            Write-Host "⚠️ No se pudo obtener el build.gradle anterior" -ForegroundColor Yellow
        }
    } else {
        Write-Host "ℹ️ Es el primer commit, no hay versión anterior para comparar" -ForegroundColor Blue
    }
} catch {
    Write-Host "⚠️ Error al verificar el historial de git: $_" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "💡 Tip: Para activar los builds, modifica la línea 'version = ...' en build.gradle" -ForegroundColor Magenta
