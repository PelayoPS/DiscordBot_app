const { app, BrowserWindow, ipcMain } = require('electron/main')
const path = require('path')
const http = require('http')
const fs = require('fs').promises
const { spawn } = require('child_process')
const os = require('os')

// Variable global para la ventana principal
let mainWindow
let connectionMonitorInterval = null

// Función para verificar si el backend está corriendo
const checkBackendConnection = () => {
  return new Promise((resolve) => {
    const req = http.get('http://localhost:8080/', (res) => {
      resolve(true)
    })
    
    req.on('error', () => {
      resolve(false)
    })
    
    req.setTimeout(3000, () => {
      req.destroy()
      resolve(false)
    })
  })
}

// Función para monitorear la conexión continuamente
const startConnectionMonitor = () => {
  if (connectionMonitorInterval) {
    clearInterval(connectionMonitorInterval)
  }  
  connectionMonitorInterval = setInterval(async () => {
    if (mainWindow && !mainWindow.isDestroyed()) {
      const isConnected = await checkBackendConnection()
      const currentURL = mainWindow.webContents.getURL()
      
      console.log(`Estado de conexión: ${isConnected}, URL actual: ${currentURL}`)
      
      // Si estamos en la página principal pero no hay conexión, mostrar error
      if (currentURL.includes('localhost:8080') && !isConnected) {
        console.log('❌ Conexión perdida detectada, mostrando página de error')
        mainWindow.loadFile(path.join(__dirname, 'error.html'))
      }
      // Si estamos en la página de error pero ahora hay conexión, recargar
      else if (currentURL.includes('error.html') && isConnected) {
        console.log('✅ Conexión restablecida detectada, cargando aplicación')
        mainWindow.loadURL('http://localhost:8080/')
      }
      // También verificar si el webContents falló en cargar
      else if (currentURL.includes('localhost:8080') && isConnected) {
        // Verificar si la página realmente se cargó o si hay un error de red
        mainWindow.webContents.executeJavaScript('document.readyState')
          .then((readyState) => {
            if (readyState !== 'complete') {
              console.log('⚠️ Página no completamente cargada, verificando conexión')
            }
          })
          .catch(() => {
            // Si hay error ejecutando JavaScript, la página probablemente falló
            console.log('❌ Error ejecutando JavaScript, posible fallo de página')
            mainWindow.loadFile(path.join(__dirname, 'error.html'))
          })
      }
    }
  }, 5000) // Verificar cada 5 segundos para detección más rápida
}

// Función para buscar un archivo recursivamente en un directorio
const searchFileRecursively = async (directory, fileName, maxDepth = 5, currentDepth = 0) => {
  if (currentDepth >= maxDepth) {
    return null
  }
  
  try {
    const items = await fs.readdir(directory, { withFileTypes: true })
    
    // Buscar el archivo en el directorio actual
    for (const item of items) {
      if (item.isFile() && item.name.toLowerCase() === fileName.toLowerCase()) {
        return path.join(directory, item.name)
      }
    }
    
    // Buscar recursivamente en subdirectorios
    for (const item of items) {
      if (item.isDirectory() && !item.name.startsWith('.') && !item.name.startsWith('$')) {        try {
          const fullPath = path.join(directory, item.name)
          const result = await searchFileRecursively(fullPath, fileName, maxDepth, currentDepth + 1)
          if (result) {
            return result
          }
        } catch (error) {
          // Ignorar errores de acceso a directorios (permisos, etc.)
          continue
        }
      }
    }
  } catch (error) {
    // Ignorar errores de acceso al directorio
  }
  
  return null
}

// Función para buscar discord-bot.exe en ubicaciones comunes
const findDiscordBotExecutable = async () => {
  const fileName = 'discord-bot.exe'
  const searchPaths = [
    // Directorio actual del proyecto
    process.cwd(),
    // Directorio del usuario
    os.homedir(),
    // Desktop
    path.join(os.homedir(), 'Desktop'),
    // Downloads
    path.join(os.homedir(), 'Downloads'),
    // Documents
    path.join(os.homedir(), 'Documents'),
    // Unidades del sistema (C:, D:, etc.)
    ...['C:', 'D:', 'E:', 'F:'].map(drive => `${drive}\\`)
  ]

  console.log('Buscando discord-bot.exe en ubicaciones comunes...')
  
  for (const searchPath of searchPaths) {
    try {
      console.log(`Buscando en: ${searchPath}`)
      const result = await searchFileRecursively(searchPath, fileName)
      if (result) {
        console.log(`discord-bot.exe encontrado en: ${result}`)
        return result
      }
    } catch (error) {
      // Ignorar errores y continuar con la siguiente ubicación
      continue
    }
  }
  
  console.log('discord-bot.exe no encontrado en ubicaciones comunes')
  return null
}

// Función para ejecutar discord-bot.exe
const launchDiscordBot = async () => {
  try {
    const executablePath = await findDiscordBotExecutable()
    
    if (!executablePath) {
      throw new Error('No se pudo encontrar discord-bot.exe en el dispositivo')
    }
    
    console.log(`Ejecutando discord-bot.exe desde: ${executablePath}`)
    
    // Ejecutar el archivo
    const process = spawn(executablePath, [], {
      detached: true,
      stdio: 'ignore'
    })
    
    // Desacoplar el proceso del proceso padre
    process.unref()
    
    console.log('discord-bot.exe ejecutado exitosamente')
    return { success: true, path: executablePath }
    
  } catch (error) {
    console.error('Error al ejecutar discord-bot.exe:', error.message)
    return { success: false, error: error.message }
  }
}

const createWindow = () => {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 900,
    minWidth: 800,
    minHeight: 800,
    frame: false, // Eliminar la barra de ventana por defecto
    titleBarStyle: 'hidden',
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      preload: require('path').join(__dirname, 'preload.js')
    }
  })  // Verificar conexión al backend antes de cargar
  checkBackendConnection().then((isConnected) => {
    if (isConnected) {
      mainWindow.loadURL('http://localhost:8080/')
    } else {
      // Cargar página de error local
      mainWindow.loadFile(path.join(__dirname, 'error.html'))
    }    // Iniciar monitoreo inmediatamente sin importar el estado inicial
    setTimeout(() => startConnectionMonitor(), 2000)
  })

  // Detectar fallos de carga inmediatamente
  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription, validatedURL) => {
    console.log(`❌ Fallo al cargar ${validatedURL}: ${errorDescription} (Código: ${errorCode})`)
    if (validatedURL.includes('localhost:8080')) {
      console.log('❌ Fallo de conexión al backend detectado, mostrando página de error')
      mainWindow.loadFile(path.join(__dirname, 'error.html'))
    }
  })

  // Detectar errores de respuesta HTTP
  mainWindow.webContents.on('did-finish-load', () => {
    const currentURL = mainWindow.webContents.getURL()
    if (currentURL.includes('localhost:8080')) {
      // Verificar si la página se cargó correctamente
      mainWindow.webContents.executeJavaScript('document.title')
        .then((title) => {
          if (title.includes('Error') || title.includes('404') || title.includes('500')) {
            console.log('❌ Página de error detectada, mostrando página de error local')
            mainWindow.loadFile(path.join(__dirname, 'error.html'))
          }
        })
        .catch(() => {
          console.log('❌ Error accediendo al contenido de la página')
          mainWindow.loadFile(path.join(__dirname, 'error.html'))
        })
    }
  })

  // Inyectar la barra de título personalizada cuando la página cargue
  mainWindow.webContents.on('did-finish-load', () => {
    // Inyectar CSS y JavaScript para la barra de título personalizada
    mainWindow.webContents.insertCSS(`
      .custom-title-bar {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        height: 40px;
        background: #2c2c2c;
        border-bottom: 1px solid #404040;
        z-index: 10000;
        user-select: none;
        display: flex;
        align-items: center;
      }

      .title-bar-content {
        display: flex;
        align-items: center;
        width: 100%;
        height: 100%;
      }

      .title-bar-drag-region {
        flex: 1;
        height: 100%;
        -webkit-app-region: drag;
        display: flex;
        align-items: center;
        padding-left: 15px;
      }

      .app-title {
        color: #ffffff;
        font-size: 14px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .app-title i {
        color: #7289da;
      }

      .window-controls {
        display: flex;
        height: 100%;
        -webkit-app-region: no-drag;
      }

      .window-control-btn {
        width: 46px;
        height: 100%;
        border: none;
        background: transparent;
        color: #ffffff;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background-color 0.2s ease;
        font-size: 12px;
      }

      .window-control-btn:hover {
        background-color: #404040;
      }

      .window-control-btn.close-btn:hover {
        background-color: #e81123;
      }

      body {
        padding-top: 40px !important;
      }
    `)

    // Inyectar JavaScript para crear la barra de título
    mainWindow.webContents.executeJavaScript(`
      (function() {
        // Verificar si ya existe la barra de título
        if (document.querySelector('.custom-title-bar')) {
          return;
        }

        // Crear la barra de título personalizada
        const titleBar = document.createElement('div');
        titleBar.className = 'custom-title-bar';
        titleBar.innerHTML = \`
          <div class="title-bar-content">
            <div class="title-bar-drag-region">
              <div class="app-title">
                <i class="fas fa-robot"></i>
                Discord Bot Manager
              </div>
            </div>
            <div class="window-controls">
              <button class="window-control-btn minimize-btn" id="minimize-btn" title="Minimizar">
                <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor">
                  <rect x="0" y="5" width="12" height="2"/>
                </svg>
              </button>
              <button class="window-control-btn maximize-btn" id="maximize-btn" title="Maximizar">
                <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor">
                  <rect x="1" y="1" width="10" height="10" stroke="currentColor" stroke-width="1" fill="none"/>
                </svg>
              </button>
              <button class="window-control-btn close-btn" id="close-btn" title="Cerrar">
                <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor">
                  <line x1="1" y1="1" x2="11" y2="11" stroke="currentColor" stroke-width="1"/>
                  <line x1="11" y1="1" x2="1" y2="11" stroke="currentColor" stroke-width="1"/>
                </svg>
              </button>
            </div>
          </div>
        \`;

        // Insertar al principio del body
        document.body.insertBefore(titleBar, document.body.firstChild);

        // Agregar event listeners
        document.getElementById('minimize-btn').addEventListener('click', () => {
          window.electronAPI?.minimizeWindow();
        });

        document.getElementById('maximize-btn').addEventListener('click', async () => {
          await window.electronAPI?.maximizeWindow();
          updateMaximizeButton();
        });

        document.getElementById('close-btn').addEventListener('click', () => {
          window.electronAPI?.closeWindow();
        });

        // Función para actualizar el botón de maximizar
        async function updateMaximizeButton() {
          if (!window.electronAPI) return;
          
          const isMaximized = await window.electronAPI.isWindowMaximized();
          const maximizeBtn = document.getElementById('maximize-btn');
          const svg = maximizeBtn.querySelector('svg');
          
          if (isMaximized) {
            svg.innerHTML = \`
              <rect x="1" y="3" width="8" height="8" stroke="currentColor" stroke-width="1" fill="none"/>
              <rect x="3" y="1" width="8" height="8" stroke="currentColor" stroke-width="1" fill="none"/>
            \`;
            maximizeBtn.title = 'Restaurar';
          } else {
            svg.innerHTML = \`
              <rect x="1" y="1" width="10" height="10" stroke="currentColor" stroke-width="1" fill="none"/>
            \`;
            maximizeBtn.title = 'Maximizar';
          }
        }

        // Escuchar eventos de maximizado
        window.electronAPI?.onWindowMaximized?.(() => updateMaximizeButton());
        window.electronAPI?.onWindowUnmaximized?.(() => updateMaximizeButton());

        // Inicializar el estado del botón
        updateMaximizeButton();
      })();
    `)
  })

  // Eventos para actualizar el estado de maximizado
  mainWindow.on('maximize', () => {
    mainWindow.webContents.send('window-maximized')
  })

  mainWindow.on('unmaximize', () => {
    mainWindow.webContents.send('window-unmaximized')
  })
}

// Funcionalidad de los botones de la ventana (IPC handlers globales)
ipcMain.handle('window-minimize', () => {
  if (mainWindow) {
    mainWindow.minimize()
  }
})

ipcMain.handle('window-maximize', () => {
  if (mainWindow) {
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize()
    } else {
      mainWindow.maximize()
    }
  }
})

ipcMain.handle('window-close', () => {
  if (mainWindow) {
    mainWindow.close()
  }
})

ipcMain.handle('window-is-maximized', () => {
  return mainWindow ? mainWindow.isMaximized() : false
})

// Handler para reintentar conexión al backend
ipcMain.handle('retry-backend-connection', async () => {
  console.log('Reintentando conexión al backend...')
  const isConnected = await checkBackendConnection()
  console.log('Estado de conexión:', isConnected)
  
  if (isConnected && mainWindow && !mainWindow.isDestroyed()) {
    console.log('Conexión exitosa, cargando aplicación principal')
    mainWindow.loadURL('http://localhost:8080/')
    // Reiniciar monitoreo después de cargar
    setTimeout(() => startConnectionMonitor(), 5000)
    return true
  }
  return false
})

// Handler para verificar estado de conexión sin recargar
ipcMain.handle('check-backend-status', async () => {
  return await checkBackendConnection()
})

// Handler para buscar y ejecutar discord-bot.exe
ipcMain.handle('launch-discord-bot', async () => {
  console.log('Iniciando búsqueda y ejecución de discord-bot.exe...')
  return await launchDiscordBot()
})

app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow()
    }
  })
})

app.on('window-all-closed', () => {
  // Limpiar el intervalo de monitoreo
  if (connectionMonitorInterval) {
    clearInterval(connectionMonitorInterval)
  }
  
  if (process.platform !== 'darwin') {
    app.quit()
  }
})