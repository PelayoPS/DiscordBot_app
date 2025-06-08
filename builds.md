# Comandos de Build

## Backend (Spring Boot + Gradle)
```bash
# Limpiar proyecto
./gradlew clean

# Build de desarrollo
./gradlew build

# Ejecutar en desarrollo
./gradlew run

# Build JAR de producción (FAT JAR con dependencias)
./gradlew bootJar

# Ejecutar JAR compilado
java -jar build/libs/discord-bot-manager-1.0.1-fat.jar

# Crear ejecutable nativo con jpackage (Windows MSI)
./gradlew jpackage

# Script automatizado para build completo (PowerShell)
powershell -ExecutionPolicy Bypass -File build-exe.ps1
```

## Electron (Desktop App)
```bash
# Navegar al directorio desktop
cd src/main/desktop

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm start

# Build para Windows
npm run build:win

# Build para macOS
npm run build:mac

# Build para Linux
npm run build:linux

# Build para todas las plataformas
npm run build

# Crear distribución
npm run dist
```

## Android (Mobile App)
```bash
# Navegar al directorio android
cd src/main/android

# Instalar dependencias
npm install

# Copiar archivos estáticos y sincronizar
npm run build

# Ejecutar en desarrollo
npm run run

# Servidor de desarrollo con live reload
npm run dev

# Build APK debug
npm run build-apk-debug

# Build APK release
npm run build-apk-release

# Abrir en Android Studio
npm run open

# Verificar configuración de Capacitor
npm run doctor

# Actualizar Capacitor
npm run update
```
