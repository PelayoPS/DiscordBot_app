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

      // Si estamos en la página wrapper, gestionar el contenedor interno
      if (currentURL.includes('wrapper.html')) {
        try {
          // Verificar si el contenedor está mostrando la web o error
          const containerState = await mainWindow.webContents.executeJavaScript(`
            document.getElementById('web-container').innerHTML.includes('iframe') ? 'web' : 'error'
          `)

          if (!isConnected && containerState === 'web') {
            console.log('❌ Conexión perdida detectada, mostrando error en contenedor')
            mainWindow.webContents.executeJavaScript(`
              window.webContainerAPI.showError()
            `)
          } else if (isConnected && containerState === 'error') {
            console.log('✅ Conexión restablecida detectada, cargando web en contenedor')
            mainWindow.webContents.executeJavaScript(`
              window.webContainerAPI.loadWeb('http://localhost:8080/')
            `)
          }
        } catch (error) {
          console.log('❌ Error gestionando contenedor:', error.message)
        }
      }
      // Fallback: Si no estamos en wrapper, usar el método anterior
      else if (currentURL.includes('localhost:8080') && !isConnected) {
        console.log('❌ Conexión perdida detectada, mostrando página de error')
        mainWindow.loadFile(path.join(__dirname, 'error.html'))
      } else if (currentURL.includes('error.html') && isConnected) {
        console.log('✅ Conexión restablecida detectada, cargando wrapper')
        mainWindow.loadFile(path.join(__dirname, 'wrapper.html'))
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
      if (item.isDirectory() && !item.name.startsWith('.') && !item.name.startsWith('$')) {
        try {
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
    frame: true, // Usar la barra de ventana nativa
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      enableRemoteModule: false,
      preload: require('path').join(__dirname, 'preload.js')
    }  })

  // Cargar la página wrapper que contiene la barra de título y el contenedor
  mainWindow.loadFile(path.join(__dirname, 'wrapper.html'))

  // Verificar conexión al backend después de cargar el wrapper
  mainWindow.webContents.once('did-finish-load', () => {
    checkBackendConnection().then((isConnected) => {
      if (isConnected) {
        // Cargar la web en el contenedor específico
        mainWindow.webContents.executeJavaScript(`
          window.webContainerAPI.loadWeb('http://localhost:8080/')
        `)
      } else {
        // Mostrar error en el contenedor
        mainWindow.webContents.executeJavaScript(`
          window.webContainerAPI.showError()
        `)
      }
      
      // Iniciar monitoreo inmediatamente sin importar el estado inicial
      setTimeout(() => startConnectionMonitor(), 2000)
    })
  })

  // Detectar fallos de carga inmediatamente
  mainWindow.webContents.on('did-fail-load', (event, errorCode, errorDescription, validatedURL) => {
    console.log(`❌ Fallo al cargar ${validatedURL}: ${errorDescription} (Código: ${errorCode})`)
    if (validatedURL.includes('localhost:8080')) {
      console.log('❌ Fallo de conexión al backend detectado, mostrando página de error')
      mainWindow.loadFile(path.join(__dirname, 'error.html'))
    }
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
    console.log('Conexión exitosa, cargando aplicación en contenedor')
    // Cargar la web en el contenedor específico
    mainWindow.webContents.executeJavaScript(`
      window.webContainerAPI.loadWeb('http://localhost:8080/')
    `)
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

// Handler para redimensionar el contenedor web
ipcMain.handle('resize-web-container', async (event, width = '100%', height = '100vh', top = '0', left = '0') => {
  if (mainWindow && !mainWindow.isDestroyed()) {
    try {
      await mainWindow.webContents.executeJavaScript(`
        if (window.webContainerAPI && window.webContainerAPI.resize) {
          window.webContainerAPI.resize('${width}', '${height}', '${top}', '${left}');
        }
      `)
      return { success: true }
    } catch (error) {
      console.error('Error redimensionando contenedor:', error.message)
      return { success: false, error: error.message }
    }
  }
  return { success: false, error: 'Ventana no disponible' }
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