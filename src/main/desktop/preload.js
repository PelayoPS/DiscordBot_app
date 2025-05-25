const { contextBridge, ipcRenderer } = require('electron')

// Exponer APIs seguras al proceso renderer
contextBridge.exposeInMainWorld('electronAPI', {
  // Funciones para controlar la ventana
  minimizeWindow: () => ipcRenderer.invoke('window-minimize'),
  maximizeWindow: () => ipcRenderer.invoke('window-maximize'),
  closeWindow: () => ipcRenderer.invoke('window-close'),
  isWindowMaximized: () => ipcRenderer.invoke('window-is-maximized'),

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
