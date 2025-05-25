#!/bin/bash

# Script para detectar cambios de versión en build.gradle
# Uso: ./check-version-change.sh

echo "🔍 Discord Bot Manager - Detector de Cambios de Versión"
echo "======================================================"

# Verificar que existe build.gradle
if [ ! -f "build.gradle" ]; then
    echo "❌ Error: No se encontró el archivo build.gradle"
    exit 1
fi

# Obtener la versión actual
CURRENT_VERSION=$(grep "^version = " build.gradle | sed "s/version = '\(.*\)'/\1/" | tr -d '"')
echo "📦 Versión actual: $CURRENT_VERSION"

# Verificar si estamos en un repositorio git
if [ ! -d ".git" ]; then
    echo "⚠️ No es un repositorio git, no se puede comparar versiones"
    exit 0
fi

# Obtener la versión del commit anterior (si existe)
if [ "$(git rev-list --count HEAD)" -gt 1 ]; then
    PREVIOUS_VERSION=$(git show HEAD~1:build.gradle 2>/dev/null | grep "^version = " | sed "s/version = '\(.*\)'/\1/" | tr -d '"')
    
    if [ -n "$PREVIOUS_VERSION" ]; then
        echo "📦 Versión anterior: $PREVIOUS_VERSION"
        
        # Comparar versiones
        if [ "$CURRENT_VERSION" != "$PREVIOUS_VERSION" ]; then
            echo ""
            echo "✅ ¡CAMBIO DE VERSIÓN DETECTADO!"
            echo "   Anterior: $PREVIOUS_VERSION"
            echo "   Nueva:    $CURRENT_VERSION"
            echo ""
            echo "🚀 Esto activaría los builds automáticos:"
            echo "   - Backend (Java/Spring Boot)"
            echo "   - Desktop (Electron)"
            echo "   - Android (Capacitor)"
            echo ""
            exit 0
        else
            echo ""
            echo "❌ No hay cambios en la versión"
            echo "🔄 No se ejecutarían builds automáticos"
            exit 0
        fi
    else
        echo "⚠️ No se pudo obtener la versión anterior"
    fi
else
    echo "ℹ️ Es el primer commit, no hay versión anterior para comparar"
fi

echo ""
echo "💡 Tip: Para activar los builds, modifica la línea 'version = ...' en build.gradle"
