# 🎯 RESUMEN: Cómo Generar APK de Discord Bot Manager

## ⚡ OPCIÓN RÁPIDA (Recomendada)

```bash
# 1. Navegar al directorio
cd "C:/Users/pelay/Desktop/repos/DiscordBot_app/src/main/android"

# 2. Generar APK de debug (para pruebas)
npm run build-apk-debug

# 3. Generar APK de release (para distribución)
npm run build-apk-release
```

## 📋 TODAS LAS OPCIONES

### 1️⃣ Scripts NPM (Automatizado)
```bash
npm run build-apk-debug    # APK debug
npm run build-apk-release  # APK release
npm run apk                # Script interactivo
```

### 2️⃣ PowerShell Directo
```powershell
.\build-apk.ps1 -BuildType debug
.\build-apk.ps1 -BuildType release -Clean -OpenFolder
```

### 3️⃣ Gradle Manual
```bash
npm run build              # Preparar archivos
cd android                 # Ir al proyecto Android
.\gradlew.bat assembleDebug     # APK debug
.\gradlew.bat assembleRelease   # APK release
```

### 4️⃣ Android Studio
```bash
npm run open               # Abrir Android Studio
# Luego: Build > Build Bundle(s) / APK(s) > Build APK(s)
```

## 📍 UBICACIÓN DE LOS APKs

```
📁 APK Debug:
android/app/build/outputs/apk/debug/app-debug.apk

📁 APK Release:
android/app/build/outputs/apk/release/app-release.apk

📁 Copias con timestamp:
discord-bot-manager-debug-20250525_1630.apk
discord-bot-manager-release-20250525_1630.apk
```

## 🔧 SI HAY PROBLEMAS

### ❌ ANDROID_HOME no configurado
```bash
# Verificar:
echo $env:ANDROID_HOME

# Configurar (ejemplo):
$env:ANDROID_HOME = "C:\Users\%USERNAME%\AppData\Local\Android\Sdk"
```

### ❌ Gradle no encuentra
```bash
npx cap doctor             # Verificar configuración
npm run clean              # Limpiar archivos
npm run copy               # Sincronizar archivos
npm run sync               # Sincronizar Capacitor
```

### ❌ Build falla
```bash
cd android
.\gradlew.bat clean        # Limpiar build
.\gradlew.bat assembleDebug --info  # Ver logs detallados
```

## 📱 INSTALAR EN DISPOSITIVO

1. **Habilitar instalación**: Configuración > Seguridad > "Orígenes desconocidos"
2. **Transferir APK**: USB, email, cloud, etc.
3. **Instalar**: Abrir APK desde administrador de archivos

## ✅ VERIFICACIÓN RÁPIDA

```bash
npx cap doctor             # ¿Todo configurado?
npm run copy               # ¿Archivos sincronizados?
npm run build-apk-debug    # ¡Generar APK!
```

## 📊 INFORMACIÓN DEL PROCESO

- **Primera vez**: ~5-10 minutos (descarga Gradle)
- **Siguientes**: ~2-3 minutos  
- **Tamaño APK**: ~15-25 MB (debug), ~10-20 MB (release)
- **Compatibilidad**: Android 5.1+ (API 22+)

---
¡Con estos comandos ya puedes generar tu APK de Discord Bot Manager! 🚀
