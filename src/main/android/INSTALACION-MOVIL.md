# 📱 GUÍA DE INSTALACIÓN - Discord Bot Manager Mobile

## ✅ APK LISTA PARA INSTALAR

**Archivo:** `discord-bot-manager-debug-instalable.apk`
**Tamaño:** ~3.8 MB
**Estado:** Firmada para desarrollo (INSTALABLE)

## 📲 PASOS DE INSTALACIÓN

### 1. Preparar el dispositivo Android
```
⚙️ Configuración > Seguridad > Orígenes desconocidos ✅ ACTIVAR
   (En Android 8+: Configuración > Apps > Acceso especial > Instalar apps desconocidas)
```

### 2. Transferir APK al dispositivo
- **USB:** Copia el archivo APK a la memoria del teléfono
- **Email:** Envíatelo por correo y descárgalo
- **Drive/Dropbox:** Sube y descarga desde tu servicio de nube
- **Bluetooth:** Transfiere desde otro dispositivo

### 3. Instalar
1. Abre el **Administrador de archivos** en tu teléfono
2. Navega hasta donde guardaste la APK
3. Toca el archivo `discord-bot-manager-debug-instalable.apk`
4. Confirma la instalación

### 4. Configurar la app
1. **Primera apertura:** La app te pedirá la URL del servidor
2. **Ejemplo URL:** `https://tu-servidor.com:8080` o `http://192.168.1.100:8080`
3. **URL guardada:** Se recuerda para próximos usos
4. **Cambiar URL:** Botón "Cambiar URL" en pantalla de error

## ❌ SOLUCIONANDO PROBLEMAS

### Error "App not installed"
- ✅ **SOLUCIÓN:** Usa `discord-bot-manager-debug-instalable.apk` (no la de release)
- ❌ **EVITAR:** `discord-bot-manager-release-*.apk` (sin firmar)

### Error "Parse error"
- Verificar que el archivo no se corrompió en la transferencia
- Re-descargar la APK

### No puede conectar al servidor
- Verificar que el servidor Discord Bot esté ejecutándose
- Comprobar la URL (incluir http:// o https://)
- Verificar conectividad de red

## 🔧 FUNCIONES DE LA APP

### ✨ Características
- 📱 **UI nativa Android** con tema Discord
- 🔄 **Auto-reconexión** si se pierde la conexión
- 💾 **Persistencia** de configuración del servidor
- 🎯 **Navegación nativa** con botón atrás de Android
- 📊 **Notificaciones toast** de estado
- 🌐 **Detección de red** automática

### 🎮 Funcionalidades heredadas del backend web
- 📊 Dashboard completo del bot
- ⚙️ Configuración de módulos
- 📝 Visualización de logs
- 🗄️ Gestión de base de datos
- 🔧 Configuración del bot

## 🚀 PARA DESARROLLO

### Generar nuevas APKs
```bash
cd C:\Users\pelay\Desktop\repos\DiscordBot_app\src\main\android
.\build-apk-simple.ps1 -BuildType debug
```

### APK de producción (requiere firma)
```bash
.\build-apk-simple.ps1 -BuildType release
# Luego firmar con jarsigner o Android Studio
```

---
*Generado el 25/05/2025 - Discord Bot Manager v1.0*
