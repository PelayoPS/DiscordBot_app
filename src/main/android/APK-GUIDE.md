# 📱 Guía Rápida: Generar APK de Discord Bot Manager

## 🎯 Opciones Disponibles

### Opción 1: Script Automático (Recomendado) ⚡
```bash
# APK de debug (para pruebas)
npm run build-apk-debug

# APK de release (para distribución)
npm run build-apk-release

# Script interactivo
npm run apk
```

### Opción 2: PowerShell Avanzado 🔧
```powershell
# Debug con limpieza
.\build-apk.ps1 -BuildType debug -Clean

# Release y abrir carpeta
.\build-apk.ps1 -BuildType release -OpenFolder
```

### Opción 3: Android Studio (Manual) 🖥️
```bash
# Abrir proyecto en Android Studio
npm run open

# En Android Studio:
# Build > Build Bundle(s) / APK(s) > Build APK(s)
```

### Opción 4: Línea de Comandos ⌨️
```bash
# Preparar archivos
npm run build

# Ir al directorio Android
cd android

# Generar APK debug
./gradlew assembleDebug

# Generar APK release
./gradlew assembleRelease
```

## 📋 Prerrequisitos

### ✅ Software Necesario
- **Android Studio** (con Android SDK)
- **Java JDK** 11 o superior
- **Node.js** 16+ con npm
- **ANDROID_HOME** configurado en variables de entorno

### 🔍 Verificar Configuración
```bash
# Verificar herramientas
npm run doctor

# Variables de entorno
echo $env:ANDROID_HOME    # PowerShell
echo %ANDROID_HOME%       # CMD
```

## 📍 Ubicación de APKs Generados

### 🐛 Debug APK
```
android/app/build/outputs/apk/debug/app-debug.apk
```

### 🔒 Release APK
```
android/app/build/outputs/apk/release/app-release.apk
```

### 📋 Copias con Timestamp
```
discord-bot-manager-debug-YYYYMMDD_HHMM.apk
discord-bot-manager-release-YYYYMMDD_HHMM.apk
```

## 🔧 Solución de Problemas

### ❌ Error: ANDROID_HOME no configurado
```bash
# Windows (PowerShell)
$env:ANDROID_HOME = "C:\Users\%USERNAME%\AppData\Local\Android\Sdk"

# Permanente: Sistema > Variables de entorno
```

### ❌ Error: gradlew no encontrado
```bash
# Regenerar proyecto Android
npx cap add android
npm run sync
```

### ❌ Error: Sincronización fallida
```bash
# Limpiar y sincronizar
npm run clean
npm run copy
npm run sync
```

### ❌ Error: Build fallido
```bash
# Verificar configuración
npx cap doctor

# Ver logs detallados
cd android
./gradlew assembleDebug --info
```

## 📱 Instalación en Dispositivo

### 🔓 Habilitar Instalación
1. **Configuración** > **Seguridad**
2. Activar **"Orígenes desconocidos"** o **"Instalar apps desconocidas"**

### 📤 Transferir APK
- **USB**: Copiar APK al dispositivo
- **Email**: Enviar APK como adjunto
- **Cloud**: Subir a Drive/OneDrive y descargar
- **ADB**: `adb install app-debug.apk`

### 🎯 Instalar
1. Abrir **Administrador de archivos**
2. Navegar al APK
3. Tocar el archivo APK
4. Confirmar instalación

## 📊 Información del APK

### 🐛 Debug APK
- **Tamaño**: ~15-25 MB
- **Firma**: Debug (auto-generada)
- **Optimización**: Mínima
- **Uso**: Solo para pruebas

### 🔒 Release APK
- **Tamaño**: ~10-20 MB (optimizado)
- **Firma**: Requiere keystore
- **Optimización**: Máxima
- **Uso**: Distribución

## 🚀 Scripts Útiles

```bash
# Desarrollo completo
npm run dev               # Servir web localmente

# Build completo
npm run build            # Preparar y sincronizar

# Testing
npm run run              # Ejecutar en dispositivo
npm run live-reload      # Con recarga automática

# Utilidades
npm run clean            # Limpiar archivos
npm run doctor           # Verificar configuración
npm run open             # Abrir Android Studio
```

## 🎨 Personalización del APK

### 📝 Información de la App
Editar `android/app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Discord Bot Manager</string>
<string name="title_activity_main">Discord Bot Manager</string>
```

### 🎨 Iconos
Reemplazar iconos en `android/app/src/main/res/`:
- `mipmap-hdpi/ic_launcher.png`
- `mipmap-mdpi/ic_launcher.png`
- `mipmap-xhdpi/ic_launcher.png`
- `mipmap-xxhdpi/ic_launcher.png`

### 🔒 Firma de Release
Crear keystore y configurar en `android/app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file('my-release-key.keystore')
            storePassword 'password'
            keyAlias 'my-key-alias'
            keyPassword 'password'
        }
    }
}
```

## ⚡ Automatización Completa

### 🔄 Script Todo-en-Uno
```bash
# Desde cero hasta APK
npm install && npm run copy && npm run sync && npm run build-apk-debug
```

### 📦 CI/CD Pipeline
```yaml
# GitHub Actions ejemplo
- name: Build APK
  run: |
    npm install
    npm run build
    npm run build-apk-release
```

¡Listo! Con estos scripts y comandos puedes generar APKs fácilmente. 🎉
