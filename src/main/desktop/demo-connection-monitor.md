# Demo del Sistema de Monitoreo de Conexión

## ✨ Características Implementadas

### 🔍 **Detección Automática de Pérdida de Conexión**
- **Monitoreo continuo**: Verifica la conexión cada 5 segundos
- **Detección inmediata**: Eventos de fallo de carga detectan problemas al instante
- **Logs detallados**: Mensajes claros en consola con emojis para fácil identificación

### 📱 **Interfaz de Error Moderna**
- **Diseño atractivo**: Gradiente animado con partículas de fondo
- **Barra de título personalizada**: Sin frame, con controles de ventana integrados
- **Mensajes informativos**: Instrucciones claras para el usuario
- **Botones de acción**: Reintentar conexión y abrir backend automáticamente

### 🚀 **Búsqueda Automática del Ejecutable**
- **Búsqueda inteligente**: Localiza `discord-bot.exe` en ubicaciones comunes
- **Ejecución automática**: Lanza el backend con un solo clic
- **Feedback visual**: Indicadores de estado durante la búsqueda y ejecución

### 🔄 **Reconexión Automática**
- **Auto-reintento**: Verifica conexión cada 15 segundos en página de error
- **Detección de recuperación**: Cambia automáticamente a la aplicación cuando se restaura la conexión
- **Reinicio de monitoreo**: Reanuda el monitoreo continuo tras reconectar

## 🧪 **Cómo Probar**

### 1. **Iniciar la Aplicación Electron**
```powershell
cd "c:\Users\pelay\Desktop\repos\DiscordBot_app\src\main\desktop"
npm start
```

### 2. **Simular Pérdida de Conexión**
- Si el backend está corriendo: Detenerlo
- Si el backend no está corriendo: La aplicación mostrará la página de error automáticamente

### 3. **Observar el Comportamiento**
- **Sin backend**: Muestra página de error con mensaje informativo
- **Con backend**: Carga la aplicación principal
- **Transición**: Cambio automático entre estados

### 4. **Probar Funcionalidades**
- **Botón "Reintentar"**: Verifica conexión manualmente
- **Botón "Abrir Backend"**: Busca y ejecuta discord-bot.exe automáticamente
- **Auto-reintento**: Esperar 15 segundos para ver la verificación automática

## 📊 **Logs del Sistema**

El sistema muestra logs detallados en la consola:

```
Estado de conexión: false, URL actual: file:///c:/Users/pelay/Desktop/repos/DiscordBot_app/src/main/desktop/error.html
❌ Conexión perdida detectada, mostrando página de error
✅ Conexión restablecida detectada, cargando aplicación
Buscando discord-bot.exe en ubicaciones comunes...
discord-bot.exe encontrado en: c:\Users\pelay\Desktop\repos\DiscordBot_app\build\jpackage\discord-bot\discord-bot.exe
```

## 🎯 **Casos de Uso Cubiertos**

1. **Inicio sin backend**: Muestra error inmediatamente
2. **Backend se detiene**: Detecta pérdida en máximo 5 segundos
3. **Fallo de red**: Eventos inmediatos de navegador
4. **Backend se reinicia**: Reconecta automáticamente en máximo 15 segundos
5. **Usuario manual**: Botones para control manual
6. **Búsqueda de ejecutable**: Localiza y ejecuta backend automáticamente

## 🔧 **Configuración del Monitoreo**

- **Intervalo de verificación**: 5 segundos (más rápido que antes)
- **Timeout de conexión**: 3 segundos por intento
- **Auto-reintento en error**: Cada 15 segundos
- **Profundidad de búsqueda**: Máximo 5 niveles de directorios
- **Ubicaciones de búsqueda**: Usuario, Desktop, Downloads, Documents, unidades del sistema

La aplicación ahora detecta automáticamente cualquier pérdida de conexión con el backend y muestra el mensaje de error de manera inmediata y elegante.
