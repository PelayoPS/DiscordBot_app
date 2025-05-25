# Discord Bot Manager - Aplicación Móvil Android

Aplicación móvil Android construida con Capacitor que envuelve la aplicación web responsiva de Discord Bot Manager.

## 🏗️ Arquitectura

Esta aplicación móvil funciona igual que la versión desktop de Electron:
- **Wrapper nativo**: Capacitor proporciona el contenedor nativo Android
- **Aplicación web**: Reutiliza la misma aplicación web responsiva del backend
- **Funcionalidades nativas**: Acceso a características específicas del móvil

## 📱 Características Móviles

### Plugins de Capacitor Incluidos
- **App**: Información de la aplicación
- **Haptics**: Retroalimentación táctil
- **Keyboard**: Gestión del teclado virtual
- **Network**: Detección de estado de red
- **Preferences**: Almacenamiento de preferencias
- **Splash Screen**: Pantalla de carga
- **Status Bar**: Control de la barra de estado
- **Toast**: Notificaciones emergentes

### Funcionalidades Implementadas
- ✅ Interfaz responsiva optimizada para móvil
- ✅ Gestión automática de conexión/desconexión
- ✅ Modo offline con archivos estáticos locales
- ✅ Retroalimentación háptica para interacciones
- ✅ Toast notifications nativas
- ✅ Persistencia de preferencias
- ✅ Pantalla de splash personalizada
- ✅ Barra de estado personalizada

## 🚀 Comandos Disponibles

### Desarrollo
```bash
# Instalar dependencias
npm install

# Copiar archivos estáticos desde el backend
npm run copy

# Sincronizar con Capacitor
npm run sync

# Construir la aplicación
npm run build

# Servir localmente para pruebas
npm run serve

# Desarrollo con live reload
npm run dev
```

### Android
```bash
# Abrir en Android Studio
npm run open

# Ejecutar en dispositivo/emulador
npm run run

# Ejecutar con live reload
npm run live-reload

# Limpiar archivos temporales
npm run clean
```

## 🔧 Configuración

### Capacitor Config (`capacitor.config.js`)
```javascript
const config = {
  appId: 'com.pelayo.discordbotmanager',
  appName: 'Discord Bot Manager',
  webDir: 'www',
  server: {
    androidScheme: 'https'
  }
};
```

### Configuración de Plugins
- **SplashScreen**: Pantalla de carga de 2 segundos con tema Discord
- **StatusBar**: Estilo oscuro que coincide con la aplicación
- **Keyboard**: Redimensionamiento del body para inputs

## 📂 Estructura del Proyecto

```
android/
├── capacitor.config.js          # Configuración de Capacitor
├── package.json                 # Dependencias y scripts
├── sync-static.js              # Script para copiar archivos del backend
├── www/                        # Archivos web para la app móvil
│   ├── index.html              # Punto de entrada principal
│   ├── mobile-integration.js   # Integración con plugins móviles
│   └── static/                 # Archivos copiados del backend
└── android/                    # Proyecto Android nativo (generado)
    ├── app/
    ├── build.gradle
    └── ...
```

## 🔄 Sincronización con Backend

La aplicación móvil se sincroniza automáticamente con el backend:

1. **Archivos estáticos**: Se copian desde `../resources/static/`
2. **Modo online**: Carga la aplicación desde `http://localhost:8080`
3. **Modo offline**: Utiliza archivos estáticos locales como fallback

### Script de Sincronización
```bash
npm run copy  # Ejecuta sync-static.js
```

## 📱 Desarrollo Android

### Prerrequisitos
- **Android Studio**: Para desarrollo y debugging
- **Android SDK**: API Level 22+ (Android 5.1+)
- **Java JDK**: 11 o superior
- **Node.js**: 16+ con npm

### Primera Configuración
```bash
# 1. Instalar dependencias
npm install

# 2. Copiar archivos del backend
npm run copy

# 3. Añadir plataforma Android (ya hecho)
npx cap add android

# 4. Sincronizar
npm run sync

# 5. Abrir en Android Studio
npm run open
```

### Testing
```bash
# Ejecutar en emulador
npm run run

# Live reload durante desarrollo
npm run live-reload

# Servir localmente para pruebas web
npm run serve
```

## 🎨 Personalización

### Splash Screen
- **Color de fondo**: `#2c2f33` (tema Discord)
- **Duración**: 2 segundos
- **Auto-hide**: Activado

### Status Bar
- **Estilo**: Oscuro
- **Color de fondo**: `#2c2f33`

### Temas
La aplicación hereda todos los estilos de la web:
- 🌙 **Tema Oscuro Discord**: Por defecto
- 📱 **Responsive**: Adaptado automáticamente para móvil
- 🎯 **Touch-friendly**: Elementos optimizados para touch

## 🔧 Integración con Funcionalidades Nativas

### Uso en la Aplicación Web
```javascript
// Mostrar toast nativo
window.mobileIntegration.showToast('Bot conectado exitosamente');

// Retroalimentación háptica
window.mobileIntegration.hapticFeedback('medium');

// Guardar preferencias
await window.mobileIntegration.setPreference('theme', 'dark');

// Obtener preferencias
const theme = await window.mobileIntegration.getPreference('theme', 'dark');
```

## 📦 Build y Distribución

### Generar APK
```bash
# En Android Studio:
# Build > Build Bundle(s) / APK(s) > Build APK(s)
```

### Generar AAB (para Play Store)
```bash
# En Android Studio:
# Build > Build Bundle(s) / APK(s) > Build Bundle(s)
```

## 🐛 Debugging

### Logs de Capacitor
```bash
# Ver logs en tiempo real
npx cap run android --list
```

### Chrome DevTools
1. Abrir Chrome
2. Ir a `chrome://inspect`
3. Seleccionar el dispositivo Android
4. Inspeccionar la aplicación

## 🔄 Actualización

### Actualizar Capacitor
```bash
npx cap update
```

### Actualizar Plugins
```bash
npm update
npm run sync
```

## 📋 Checklist de Desarrollo

- [x] Configuración inicial de Capacitor
- [x] Integración con aplicación web existente
- [x] Plugins nativos configurados
- [x] Modo offline implementado
- [x] Scripts de sincronización
- [x] Documentación completa
- [ ] Testing en dispositivos reales
- [ ] Optimización de rendimiento
- [ ] Configuración de firma para release
- [ ] Preparación para Play Store

## 🤝 Contribución

Esta aplicación móvil está sincronizada con el backend principal. Para contribuir:

1. Modifica la aplicación web en el backend
2. Ejecuta `npm run copy` para sincronizar
3. Prueba en el emulador/dispositivo
4. Sincroniza con `npm run sync`

## 📄 Licencia

MIT - Ver archivo LICENSE en el directorio raíz del proyecto.
