// Manejadores de eventos al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    // Botón de inicio de sesión
    const loginButton = document.getElementById('login-button');
    // Pantallas e interfaz
    const loginScreen = document.getElementById('login-screen');
    const mainInterface = document.querySelector('.main-interface');
    
    // Handler para el botón de login
    loginButton.addEventListener('click', () => {
        // Simulación de inicio de sesión
        loginScreen.style.display = 'none';
        mainInterface.classList.remove('hidden');
    });
    
    // Navegación entre pantallas en la barra lateral
    const menuItems = document.querySelectorAll('.menu-item');
    const screens = document.querySelectorAll('.content-area .screen');
    
    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            // Eliminar clase activa de todos los elementos de menú
            menuItems.forEach(i => i.classList.remove('active'));
            // Añadir clase activa al elemento seleccionado
            item.classList.add('active');
            
            // Obtener el id de la pantalla a mostrar
            const screenId = item.getAttribute('data-screen');
            
            // Ocultar todas las pantallas
            screens.forEach(screen => {
                screen.classList.remove('active');
            });
            
            // Mostrar la pantalla seleccionada
            document.getElementById(`${screenId}-screen`).classList.add('active');
        });
    });
    
    // Cambio de tema (modo oscuro/claro)
    const themeToggle = document.querySelector('.theme-toggle input');
    const body = document.body;
    
    themeToggle.addEventListener('change', () => {
        if (themeToggle.checked) {
            body.classList.remove('light-theme');
        } else {
            body.classList.add('light-theme');
        }
    });
    
    // Pestañas en la pantalla de base de datos
    const databaseTabs = document.querySelectorAll('.database-tabs .tab');
    const databaseTables = document.querySelectorAll('.database-table-container');
    
    databaseTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Eliminar clase activa de todas las pestañas
            databaseTabs.forEach(t => t.classList.remove('active'));
            // Añadir clase activa a la pestaña seleccionada
            tab.classList.add('active');
            
            // Obtener el id de la tabla a mostrar
            const tableId = tab.getAttribute('data-tab');
            
            // Ocultar todas las tablas
            databaseTables.forEach(table => {
                table.classList.remove('active');
            });
            
            // Mostrar la tabla seleccionada
            document.getElementById(tableId).classList.add('active');
        });
    });
    
    // Gestionar enlaces internos
    const internalLinks = document.querySelectorAll('.view-all-link');
    
    internalLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Obtener el id de la pantalla a mostrar
            const screenId = link.getAttribute('data-screen');
            
            // Actualizar la navegación
            menuItems.forEach(i => {
                if (i.getAttribute('data-screen') === screenId) {
                    // Simular click en el elemento de menú
                    i.click();
                }
            });
        });
    });
    
    // Manejo de modales
    const moduleConfigButtons = document.querySelectorAll('.module-config-button:not([disabled])');
    const moduleConfigModal = document.getElementById('module-config-modal');
    const closeModalButton = document.querySelector('.close-modal');
    const cancelButton = document.querySelector('.modal-footer .secondary-button');
    
    // Abrir modal
    moduleConfigButtons.forEach(button => {
        button.addEventListener('click', () => {
            moduleConfigModal.style.display = 'flex';
        });
    });
    
    // Cerrar modal
    [closeModalButton, cancelButton].forEach(button => {
        button.addEventListener('click', () => {
            moduleConfigModal.style.display = 'none';
        });
    });
    
    // Cerrar modal al hacer clic fuera del contenido
    moduleConfigModal.addEventListener('click', (e) => {
        if (e.target === moduleConfigModal) {
            moduleConfigModal.style.display = 'none';
        }
    });
    
    // Simular acciones para botones principales
    const actionButtons = document.querySelectorAll('.primary-button, .secondary-button, .danger-button');
    
    actionButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            // No hacer nada si está dentro del modal
            if (button.closest('.modal')) {
                return;
            }
            
            // Mostrar una notificación de simulación
            showNotification('Acción simulada: ' + (button.textContent.trim() || 'Acción ejecutada'), 'info');
        });
    });
    
    // Simulación de toggles de módulos
    const moduleToggles = document.querySelectorAll('.module-toggle .switch input');
    
    moduleToggles.forEach(toggle => {
        toggle.addEventListener('change', () => {
            const moduleCard = toggle.closest('.module-card');
            if (toggle.checked) {
                moduleCard.classList.add('enabled');
                moduleCard.classList.remove('disabled');
                showNotification('Módulo activado', 'success');
            } else {
                moduleCard.classList.remove('enabled');
                moduleCard.classList.add('disabled');
                showNotification('Módulo desactivado', 'warning');
            }
        });
    });
    
    // Simulación de toggles de comandos
    const commandToggles = document.querySelectorAll('.command-item .switch input');
    
    commandToggles.forEach(toggle => {
        toggle.addEventListener('change', () => {
            const commandItem = toggle.closest('.command-item');
            const commandName = commandItem.querySelector('.command-name').textContent;
            
            if (toggle.checked) {
                commandItem.classList.remove('disabled');
                showNotification(`Comando ${commandName} activado`, 'success');
            } else {
                commandItem.classList.add('disabled');
                showNotification(`Comando ${commandName} desactivado`, 'warning');
            }
        });
    });
    
    // Controles de ventana (simulados)
    const minimizeButton = document.querySelector('.control.minimize');
    const maximizeButton = document.querySelector('.control.maximize');
    const closeButton = document.querySelector('.control.close');
    
    minimizeButton.addEventListener('click', () => {
        showNotification('Acción simulada: Minimizar ventana', 'info');
    });
    
    maximizeButton.addEventListener('click', () => {
        showNotification('Acción simulada: Maximizar ventana', 'info');
    });
    
    closeButton.addEventListener('click', () => {
        if (confirm('¿Realmente quieres cerrar la aplicación?')) {
            showNotification('Cerrando aplicación...', 'warning');
            setTimeout(() => {
                window.location.reload();
            }, 1500);
        }
    });
    
    // Botones de visualización/ocultación de contraseñas
    const passwordToggleButtons = document.querySelectorAll('.input-with-button .icon-button');
    
    passwordToggleButtons.forEach(button => {
        button.addEventListener('click', () => {
            const input = button.previousElementSibling;
            const icon = button.querySelector('i');
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });
    
    // Botones de paginación
    const paginationButtons = document.querySelectorAll('.pagination-button:not([disabled])');
    
    paginationButtons.forEach(button => {
        button.addEventListener('click', () => {
            showNotification('Acción simulada: Cambio de página', 'info');
        });
    });
    
    // Filtrado de logs (simulado)
    const logFilterCheckboxes = document.querySelectorAll('.filter-checkbox input');
    const logRows = document.querySelectorAll('.log-row');
    
    logFilterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            // Obtener todos los filtros activos
            const activeFilters = [];
            logFilterCheckboxes.forEach(cb => {
                if (cb.checked) {
                    const filterType = cb.nextElementSibling.classList[1]; // info, warning, error, debug
                    activeFilters.push(filterType);
                }
            });
            
            // Aplicar filtros
            logRows.forEach(row => {
                if (activeFilters.length === 0) {
                    row.style.display = 'table-row';
                } else {
                    const rowClass = row.classList[1]; // info, warning, error, debug
                    row.style.display = activeFilters.includes(rowClass) ? 'table-row' : 'none';
                }
            });
        });
    });
});

// Función para mostrar notificaciones
function showNotification(message, type = 'info') {
    // Crear elemento de notificación
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    
    // Icono según el tipo
    let icon = 'info-circle';
    if (type === 'success') icon = 'check-circle';
    if (type === 'warning') icon = 'exclamation-triangle';
    if (type === 'error') icon = 'times-circle';
    
    notification.innerHTML = `
        <i class="fas fa-${icon}"></i>
        <span>${message}</span>
    `;
    
    // Añadir al DOM
    document.body.appendChild(notification);
    
    // Animar entrada
    setTimeout(() => {
        notification.classList.add('show');
    }, 10);
    
    // Eliminar después de un tiempo
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Datos simulados para las tablas
const simulatedData = {
    // Añadir más datos si es necesario para futuras funcionalidades
};

// Utilidades
function formatDate(date) {
    return new Date(date).toLocaleDateString('es-ES');
}

function formatDateTime(date) {
    return new Date(date).toLocaleString('es-ES');
}

// Añadir estilos para las notificaciones
(function addNotificationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .notification {
            position: fixed;
            bottom: 20px;
            right: 20px;
            background-color: var(--color-surface);
            border-left: 4px solid;
            border-radius: var(--border-radius-md);
            padding: 12px 16px;
            display: flex;
            align-items: center;
            box-shadow: var(--shadow-medium);
            transform: translateX(120%);
            transition: transform 0.3s ease;
            z-index: 1000;
            max-width: 300px;
        }
        
        .notification.show {
            transform: translateX(0);
        }
        
        .notification i {
            margin-right: 12px;
            font-size: 18px;
        }
        
        .notification span {
            flex: 1;
        }
        
        .notification.info {
            border-color: var(--color-info);
        }
        
        .notification.info i {
            color: var(--color-info);
        }
        
        .notification.success {
            border-color: var(--color-success);
        }
        
        .notification.success i {
            color: var(--color-success);
        }
        
        .notification.warning {
            border-color: var(--color-warning);
        }
        
        .notification.warning i {
            color: var(--color-warning);
        }
        
        .notification.error {
            border-color: var(--color-error);
        }
        
        .notification.error i {
            color: var(--color-error);
        }
    `;
    document.head.appendChild(style);
})();