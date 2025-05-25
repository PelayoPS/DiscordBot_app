#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Rutas
const sourceDir = path.join(__dirname, '..', 'resources', 'static');
const targetDir = path.join(__dirname, 'www', 'static');

// Función para copiar directorio recursivamente
function copyDir(src, dest) {
    if (!fs.existsSync(src)) {
        console.error(`Directorio fuente no existe: ${src}`);
        return false;
    }

    // Crear directorio destino si no existe
    if (!fs.existsSync(dest)) {
        fs.mkdirSync(dest, { recursive: true });
    }

    const entries = fs.readdirSync(src, { withFileTypes: true });
    
    for (let entry of entries) {
        const srcPath = path.join(src, entry.name);
        const destPath = path.join(dest, entry.name);
        
        if (entry.isDirectory()) {
            copyDir(srcPath, destPath);
        } else {
            fs.copyFileSync(srcPath, destPath);
            console.log(`Copiado: ${entry.name}`);
        }
    }
    
    return true;
}

// Función principal
function syncStaticFiles() {
    console.log('Sincronizando archivos estáticos...');
    console.log(`Desde: ${sourceDir}`);
    console.log(`Hacia: ${targetDir}`);
    
    const success = copyDir(sourceDir, targetDir);
    
    if (success) {
        console.log('✅ Archivos estáticos sincronizados correctamente');
        
        // Crear un index.html de fallback local si no existe el servidor
        const fallbackHtml = `
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Discord Bot Manager</title>
    <base href="./static/">
    <link rel="stylesheet" href="styles/styles.css">
    <link rel="stylesheet" href="styles/content/dashboard.css">
    <link rel="stylesheet" href="styles/content/logs.css">
    <link rel="stylesheet" href="styles/content/modules.css">
    <link rel="stylesheet" href="styles/content/config.css">
    <link rel="stylesheet" href="styles/content/database.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="offline-notice" style="background: #f8d7da; color: #721c24; padding: 10px; text-align: center; border-bottom: 1px solid #f5c6cb;">
        <i class="fas fa-exclamation-triangle"></i>
        Modo offline - Funcionalidad limitada. Conecta al servidor para acceso completo.
    </div>
    
    <!-- Incluir el contenido del index.html estático aquí -->
    <script>
        // Cargar el index.html estático
        fetch('./static/index.html')
            .then(response => response.text())
            .then(html => {
                // Extraer el contenido del body
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;
                const bodyContent = tempDiv.querySelector('body').innerHTML;
                
                // Insertar en el body actual después del notice
                document.body.innerHTML += bodyContent;
                
                // Notificar que estamos en modo offline
                if (window.initializeApp) {
                    window.initializeApp();
                }
            })
            .catch(error => {
                console.error('Error cargando modo offline:', error);
                document.body.innerHTML += '<div style="text-align: center; padding: 50px; color: #666;">Error al cargar la aplicación offline</div>';
            });
    </script>
</body>
</html>`;
        
        fs.writeFileSync(path.join(__dirname, 'www', 'offline.html'), fallbackHtml);
        console.log('✅ Archivo offline.html creado');
        
    } else {
        console.error('❌ Error al sincronizar archivos estáticos');
        process.exit(1);
    }
}

// Ejecutar si se llama directamente
if (require.main === module) {
    syncStaticFiles();
}

module.exports = { syncStaticFiles, copyDir };
