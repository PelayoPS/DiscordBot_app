// Capacitor Mobile Integration
// Funciones para integrar la aplicación web con las capacidades móviles

class MobileIntegration {
    constructor() {
        this.isCapacitor = window.Capacitor?.isNativePlatform() || false;
        this.plugins = window.Capacitor?.Plugins || {};
        this.initialized = false;
        
        console.log('MobileIntegration:', this.isCapacitor ? 'Modo nativo' : 'Modo web');
    }
    
    // Inicializar integración móvil
    async init() {
        if (this.initialized) return;
        
        try {
            if (this.isCapacitor) {
                await this.initializeNativeFeatures();
            }
            this.initialized = true;
            console.log('✅ Integración móvil inicializada');
        } catch (error) {
            console.error('❌ Error inicializando integración móvil:', error);
        }
    }
    
    // Inicializar características nativas
    async initializeNativeFeatures() {
        // Configurar StatusBar
        if (this.plugins.StatusBar) {
            try {
                await this.plugins.StatusBar.setStyle({ style: 'DARK' });
                await this.plugins.StatusBar.setBackgroundColor({ color: '#2c2f33' });
                console.log('✅ StatusBar configurado');
            } catch (e) {
                console.log('⚠️ StatusBar no disponible:', e);
            }
        }
        
        // Configurar Keyboard
        if (this.plugins.Keyboard) {
            try {
                await this.plugins.Keyboard.setResizeMode({ mode: 'body' });
                console.log('✅ Keyboard configurado');
            } catch (e) {
                console.log('⚠️ Keyboard no disponible:', e);
            }
        }
        
        // Configurar eventos de red
        if (this.plugins.Network) {
            try {
                this.plugins.Network.addListener('networkStatusChange', status => {
                    console.log('📶 Estado de red:', status.connected ? 'Conectado' : 'Desconectado');
                    this.handleNetworkChange(status.connected);
                });
                console.log('✅ Network listener configurado');
            } catch (e) {
                console.log('⚠️ Network no disponible:', e);
            }
        }
        
        // Ocultar SplashScreen
        if (this.plugins.SplashScreen) {
            try {
                setTimeout(async () => {
                    await this.plugins.SplashScreen.hide();
                    console.log('✅ SplashScreen ocultado');
                }, 2000);
            } catch (e) {
                console.log('⚠️ SplashScreen no disponible:', e);
            }
        }
    }
    
    // Manejar cambios de red
    handleNetworkChange(isConnected) {
        const event = new CustomEvent('networkChange', { 
            detail: { connected: isConnected } 
        });
        window.dispatchEvent(event);
    }
    
    // Mostrar toast nativo
    async showToast(message, duration = 'short') {
        if (this.isCapacitor && this.plugins.Toast) {
            try {
                await this.plugins.Toast.show({
                    text: message,
                    duration: duration
                });
            } catch (e) {
                console.log('⚠️ Toast no disponible, usando fallback');
                this.showWebToast(message);
            }
        } else {
            this.showWebToast(message);
        }
    }
    
    // Toast web como fallback
    showWebToast(message) {
        // Crear toast web si no existe
        let toast = document.getElementById('web-toast');
        if (!toast) {
            toast = document.createElement('div');
            toast.id = 'web-toast';
            toast.style.cssText = `
                position: fixed;
                bottom: 20px;
                left: 50%;
                transform: translateX(-50%);
                background: rgba(0,0,0,0.8);
                color: white;
                padding: 12px 24px;
                border-radius: 6px;
                z-index: 10000;
                font-size: 14px;
                opacity: 0;
                transition: opacity 0.3s;
            `;
            document.body.appendChild(toast);
        }
        
        toast.textContent = message;
        toast.style.opacity = '1';
        
        setTimeout(() => {
            toast.style.opacity = '0';
        }, 3000);
    }
    
    // Vibración háptica
    async hapticFeedback(type = 'medium') {
        if (this.isCapacitor && this.plugins.Haptics) {
            try {
                if (type === 'light') {
                    await this.plugins.Haptics.impact({ style: 'LIGHT' });
                } else if (type === 'heavy') {
                    await this.plugins.Haptics.impact({ style: 'HEAVY' });
                } else {
                    await this.plugins.Haptics.impact({ style: 'MEDIUM' });
                }
            } catch (e) {
                console.log('⚠️ Haptics no disponible:', e);
            }
        }
    }
    
    // Obtener información de la app
    async getAppInfo() {
        if (this.isCapacitor && this.plugins.App) {
            try {
                return await this.plugins.App.getInfo();
            } catch (e) {
                console.log('⚠️ App info no disponible:', e);
            }
        }
        return {
            name: 'Discord Bot Manager',
            version: '1.0.0',
            build: 'web'
        };
    }
    
    // Guardar preferencias
    async setPreference(key, value) {
        if (this.isCapacitor && this.plugins.Preferences) {
            try {
                await this.plugins.Preferences.set({
                    key: key,
                    value: JSON.stringify(value)
                });
                return true;
            } catch (e) {
                console.log('⚠️ Preferences no disponible, usando localStorage:', e);
            }
        }
        
        // Fallback a localStorage
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (e) {
            console.error('Error guardando preferencia:', e);
            return false;
        }
    }
    
    // Obtener preferencias
    async getPreference(key, defaultValue = null) {
        if (this.isCapacitor && this.plugins.Preferences) {
            try {
                const result = await this.plugins.Preferences.get({ key: key });
                return result.value ? JSON.parse(result.value) : defaultValue;
            } catch (e) {
                console.log('⚠️ Preferences no disponible, usando localStorage:', e);
            }
        }
        
        // Fallback a localStorage
        try {
            const value = localStorage.getItem(key);
            return value ? JSON.parse(value) : defaultValue;
        } catch (e) {
            console.error('Error obteniendo preferencia:', e);
            return defaultValue;
        }
    }
}

// Crear instancia global
window.mobileIntegration = new MobileIntegration();

// Auto-inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    window.mobileIntegration.init();
});

// Exportar para uso en otros scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = MobileIntegration;
}
