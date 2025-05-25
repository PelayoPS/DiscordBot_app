const { app, BrowserWindow, ipcMain } = require('electron/main')
const path = require('path')
const http = require('http')

// Variable global para la ventana principal
let mainWindow

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
  })

  // Verificar conexión al backend antes de cargar
  checkBackendConnection().then((isConnected) => {
    if (isConnected) {
      mainWindow.loadURL('http://localhost:8080/')
    } else {
      // Cargar página de error local
      mainWindow.loadFile(path.join(__dirname, 'error.html'))
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
  const isConnected = await checkBackendConnection()
  if (isConnected) {
    mainWindow.loadURL('http://localhost:8080/')
    return true
  }
  return false
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
  if (process.platform !== 'darwin') {
    app.quit()
  }
})