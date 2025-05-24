const { app, BrowserWindow, Menu, ipcMain } = require('electron');
const path = require('path');
const Store = require('electron-store');

// Configuración del almacenamiento local
const store = new Store();

// URL del backend (por defecto)
const DEFAULT_BACKEND_URL = 'https://localhost:8443/';

// Configurar para permitir certificados autofirmados en desarrollo
if (process.env.NODE_ENV === 'development') {
  app.commandLine.appendSwitch('ignore-certificate-errors');
}

let mainWindow;

function createWindow() {
  // Crear la ventana del navegador
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 800,  // Ancho mínimo
    minHeight: 800, // Alto mínimo aumentado
    frame: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      webSecurity: process.env.NODE_ENV !== 'development'
    }
  });

  // Cargar el archivo HTML local
  mainWindow.loadFile(path.join(__dirname, 'index.html'));

  // Cuando el contenido esté listo, cargar la URL del backend en el iframe
  mainWindow.webContents.on('did-finish-load', () => {
    const backendUrl = store.get('backendUrl') || DEFAULT_BACKEND_URL;
    mainWindow.webContents.executeJavaScript(`
      const content = document.getElementById('content');
      content.innerHTML = '<iframe src="${backendUrl}" style="width: 100%; height: 100%; border: none;"></iframe>';
    `);
  });

  // Manejar eventos de la ventana
  mainWindow.on('maximize', () => {
    mainWindow.webContents.send('window-maximized');
  });

  mainWindow.on('unmaximize', () => {
    mainWindow.webContents.send('window-unmaximized');
  });

  // Crear el menú de la aplicación
  const template = [
    {
      label: 'Archivo',
      submenu: [
        {
          label: 'Configuración',
          click: () => {
            // TODO: Implementar ventana de configuración
          }
        },
        { type: 'separator' },
        {
          label: 'Salir',
          click: () => {
            app.quit();
          }
        }
      ]
    },
    {
      label: 'Ver',
      submenu: [
        { role: 'reload' },
        { role: 'forceReload' },
        { role: 'toggleDevTools' },
        { type: 'separator' },
        { role: 'resetZoom' },
        { role: 'zoomIn' },
        { role: 'zoomOut' },
        { type: 'separator' },
        { role: 'togglefullscreen' }
      ]
    }
  ];

  const menu = Menu.buildFromTemplate(template);
  Menu.setApplicationMenu(menu);
}

// Manejar eventos IPC
ipcMain.on('window-minimize', () => {
  mainWindow.minimize();
});

ipcMain.on('window-maximize', () => {
  if (mainWindow.isMaximized()) {
    mainWindow.unmaximize();
  } else {
    mainWindow.maximize();
  }
});

ipcMain.on('window-close', () => {
  mainWindow.close();
});

// Cuando la aplicación esté lista
app.whenReady().then(() => {
  createWindow();

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

// Cerrar la aplicación cuando todas las ventanas estén cerradas
app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
}); 