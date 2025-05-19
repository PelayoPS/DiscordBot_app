// Inicialización de la pantalla de módulos
window.initModulesScreen = async function () {
    try {
        const res = await fetch('/api/bot/status');
        if (!res.ok) {
            throw new Error('No se pudo obtener el estado del bot');
        }
        const data = await res.json();


        const modulesPlaceholder = document.getElementById('modules-placeholder');
        if (data.estado !== 'ONLINE') {
            if (modulesPlaceholder) {
                modulesPlaceholder.style.display = 'block';
            }
            const modulesListContainer = document.getElementById('modules-list');
            if (modulesListContainer) {
                modulesListContainer.innerHTML = '';
            }
            return;
        }
        else if (modulesPlaceholder) {
            modulesPlaceholder.style.display = 'none';
        }

        const modulesListContainer = document.getElementById('modules-list');
        if (!modulesListContainer) {
            console.error('No se encontró el contenedor de módulos.');
            return;
        }

        try {
            const modulesRes = await fetch('/api/modules');
            if (!modulesRes.ok) {
                throw new Error('No se pudo obtener la lista de módulos');
            }
            const modulesData = await modulesRes.json();

            if (modulesData.length === 0) {
                modulesListContainer.innerHTML = '<p>No hay módulos disponibles.</p>';
                return;
            }

            // Filtro de módulos y render con listeners
            const renderModules = (filter = 'all') => {
                let filtered = modulesData;
                if (filter === 'enabled') {
                    filtered = modulesData.filter(m => m.activo);
                }
                if (filter === 'disabled') {
                    filtered = modulesData.filter(m => !m.activo);
                }
                modulesListContainer.innerHTML = filtered.length === 0
                    ? '<p>No hay módulos disponibles para este filtro.</p>'
                    : filtered.map(module => `
                        <div class="module-card ${module.activo ? 'enabled' : 'disabled'}" data-module="${module.nombre}">
                    <div class="module-header">
                        <div class="module-title">
                            <h3>${module.nombre}</h3>
                            <p>${module.descripcion}</p>
                        </div>
                        <div class="module-toggle" style="margin-left:auto;">
                            <label class="switch">
                                <input type="checkbox" ${module.activo ? 'checked' : ''} data-module-toggle="${module.nombre}">
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>
                            <div class="module-commands">
                                ${module.comandos.map(command => `
                                    <div class="command-item ${command.activo ? '' : 'disabled'}">
                                        <span class="command-name">${command.nombre}</span>
                                        <label class="switch small">
                                            <input type="checkbox" ${command.activo ? 'checked' : ''} disabled>
                                            <span class="slider round"></span>
                                        </label>
                                    </div>
                                `).join('')}
                            </div>
                        </div>
                    `).join('');

                // Añadir listeners a los switches de módulos
                document.querySelectorAll('input[data-module-toggle]').forEach(input => {
                    input.addEventListener('change', async function () {
                        const moduleName = this.getAttribute('data-module-toggle');
                        const { checked } = this;
                        this.disabled = true;
                        // Mostrar feedback visual (notificación simple)
                        const showNotification = (msg, type = 'info') => {
                            let notif = document.createElement('div');
                            notif.className = 'config-notification ' + type;
                            notif.textContent = msg;
                            notif.style.position = 'fixed';
                            notif.style.top = '24px';
                            notif.style.right = '24px';
                            notif.style.zIndex = 9999;
                            notif.style.background = type === 'success' ? '#4caf50' : (type === 'error' ? '#e53935' : '#333');
                            notif.style.color = '#fff';
                            notif.style.padding = '12px 20px';
                            notif.style.borderRadius = '6px';
                            notif.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
                            document.body.appendChild(notif);
                            setTimeout(() => notif.remove(), 2200);
                        };
                        try {
                            const endpoint = `/api/modules/${encodeURIComponent(moduleName)}/${checked ? 'enable' : 'disable'}`;
                            const res = await fetch(endpoint, { method: 'POST' });
                            if (!res.ok) {
                                throw new Error('Error al cambiar el estado del módulo');
                            }
                            const updated = await res.json();
                            // Actualizar el estado en modulesData
                            const idx = modulesData.findIndex(m => m.nombre === moduleName);
                            if (idx !== -1) {
                                modulesData[idx].activo = updated.activo;
                                // Actualizar comandos si el backend los devuelve (por ahora, todos igual que el módulo)
                                if (Array.isArray(updated.comandos)) {
                                    modulesData[idx].comandos = updated.comandos;
                                }
                            }
                            // Volver a renderizar para reflejar el cambio
                            renderModules(filter);
                            showNotification(`Módulo ${checked ? 'activado' : 'desactivado'} correctamente`, 'success');
                        } catch (err) {
                            showNotification('No se pudo cambiar el estado del módulo', 'error');
                            // Revertir el switch visualmente
                            this.checked = !checked;
                        } finally {
                            this.disabled = false;
                        }
                    });
                });
            };
            // Inicializar filtro
            renderModules('all');
            document.querySelectorAll('input[name="modules-filter"]').forEach(radio => {
                radio.addEventListener('change', e => {
                    renderModules(e.target.value);
                });
            });
        } catch (modulesError) {
            console.error('Error al cargar los módulos:', modulesError);
            modulesListContainer.innerHTML = '<p>Error al cargar los módulos. Inténtalo más tarde.</p>';
        }
    } catch (error) {
        console.error('Error al verificar el estado del bot:', error);
        const modulesContainer = document.getElementById('modules-container');
        modulesContainer.innerHTML = '<p>Error al cargar los módulos. Inténtalo más tarde.</p>';
    }
};

// Limpieza de recursos de la pantalla de módulos
window.destroyModulesScreen = function () {
};
