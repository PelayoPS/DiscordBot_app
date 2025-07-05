const { contextBridge, ipcRenderer } = require('electron')

// Exponer APIs seguras al proceso renderer
contextBridge.exposeInMainWorld('electronAPI', {
  // Funciones para controlar la ventana
  minimizeWindow: () => ipcRenderer.invoke('window-minimize'),
  maximizeWindow: () => ipcRenderer.invoke('window-maximize'),
  closeWindow: () => ipcRenderer.invoke('window-close'),
  isWindowMaximized: () => ipcRenderer.invoke('window-is-maximized'),  // Funciones para manejar la conexión con el backend
  retryBackendConnection: () => ipcRenderer.invoke('retry-backend-connection'),
  checkBackendStatus: () => ipcRenderer.invoke('check-backend-status'),
  launchDiscordBot: () => ipcRenderer.invoke('launch-discord-bot'),
  
  // Función para redimensionar el contenedor web
  resizeWebContainer: (width, height, top, left) => ipcRenderer.invoke('resize-web-container', width, height, top, left),

  // Escuchar eventos de la ventana
  onWindowMaximized: (callback) => {
    ipcRenderer.on('window-maximized', callback)
  },
  onWindowUnmaximized: (callback) => {
    ipcRenderer.on('window-unmaximized', callback)
  },

  // Limpiar listeners cuando sea necesario
  removeAllListeners: (channel) => {
    ipcRenderer.removeAllListeners(channel)
  }
})
