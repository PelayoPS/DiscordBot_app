# Discord Bot Manager - Aplicación de Escritorio

## Características implementadas ✅

### Barra de ventana personalizada
- ✅ **Barra de título personalizada** con estilo moderno
- ✅ **Botones funcionales**: Minimizar, Maximizar/Restaurar, Cerrar
- ✅ **Diseño responsivo** que se adapta al estado de la ventana
- ✅ **Región arrastrable** para mover la ventana
- ✅ **Tamaño mínimo** de 800x800 píxeles

### Funcionalidades de los botones
- **Minimizar**: Minimiza la ventana a la barra de tareas
- **Maximizar/Restaurar**: Alterna entre ventana maximizada y tamaño normal
- **Cerrar**: Cierra la aplicación completamente

### Configuración de ventana
- **Tamaño inicial**: 1200x900 píxeles
- **Tamaño mínimo**: 800x800 píxeles
- **Sin barra de título nativa**: `frame: false`
- **Comunicación segura**: IPC entre procesos main y renderer

## Archivos creados/modificados

### 📁 Archivos principales
- `main.js` - Proceso principal de Electron con barra personalizada
- `preload.js` - Script de preload para comunicación segura IPC
- `renderer.js` - Script del proceso renderer (opcional/alternativo)

### 🎨 Características visuales
- Barra de título oscura (#2c2c2c) con bordes sutiles
- Iconos SVG personalizados para los botones
- Efectos hover para mejor experiencia de usuario
- Botón cerrar con color rojo característico (#e81123)

### 🔧 Funcionalidades técnicas
- Inyección automática de CSS y JavaScript en la página web cargada
- Manejo de eventos de maximizado/desmaximizado
- Comunicación IPC segura entre procesos
- Compatibilidad con contenido web externo (http://localhost:8080/)

## Cómo usar

1. **Ejecutar la aplicación:**
   ```bash
   cd src/main/desktop
   npm start
   ```

2. **Controles de ventana:**
   - La barra superior permite arrastrar la ventana
   - Los botones de la esquina superior derecha controlan la ventana
   - El título muestra "Discord Bot Manager" con un icono de robot

3. **Compatibilidad:**
   - Funciona con cualquier contenido web cargado en la ventana
   - Se inyecta automáticamente en la página cuando carga
   - Compatible con aplicaciones web complejas

## Notas técnicas

- La aplicación carga contenido desde `http://localhost:8080/`
- La barra de título se inyecta dinámicamente para no interferir con el contenido web
- Los estilos CSS tienen alta prioridad (z-index: 10000) para aparecer sobre todo el contenido
- El padding-top del body se ajusta automáticamente para evitar solapamientos
