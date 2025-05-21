// dashboard.js: Lógica JS específica para el dashboard
let dashboardPingInterval = null;
let recentLogsInterval = null; // Variable para el intervalo de logs recientes

// Encapsula toda la lógica en una función global para carga dinámica
window.initDashboardBotControls = function () {
    // Lógica del dashboard: estado del bot y logs recientes
    function getStatusValue(label) {
        const items = document.querySelectorAll('.estado-bot .integracion-item');
        for (const item of items) {
            const labelElem = item.querySelector('.integracion-label');
            if (labelElem && labelElem.textContent.trim().toLowerCase().includes(label)) {
                return item.querySelector('.status-value');
            }
        }
        return null;
    }
    const botStatusFields = {
        estado: getStatusValue('estado'),
        tiempo: getStatusValue('tiempo'),
        version: getStatusValue('versión'),
        ram: getStatusValue('ram'),
        cpu: getStatusValue('cpu'),
    };
    const botControls = document.querySelector('.bot-controls');
    if (!botControls) {
        return;
    }
    botControls.style.flexDirection = 'column';
    botControls.style.gap = '12px';
    const startBtn = botControls.querySelector('.start-button');
    const restartBtn = botControls.querySelector('.restart-button');
    const stopBtn = botControls.querySelector('.stop-button');
    if (!startBtn || !restartBtn || !stopBtn) {
        return;
    }
    async function updateBotStatus() {
        try {
            const res = await fetch('/api/bot/status');
            if (!res.ok) {
                throw new Error('No se pudo obtener el estado del bot');
            }
            const data = await res.json();
            if (botStatusFields.estado) {
                botStatusFields.estado.textContent = data.estado;
                botStatusFields.estado.className = 'status-value ' + (data.estado === 'ONLINE' ? 'online' : (data.estado === 'OFFLINE' ? 'offline' : 'error'));
            }
            if (botStatusFields.tiempo) {
                botStatusFields.tiempo.textContent = data.tiempoActivo || '-';
            }
            if (botStatusFields.version) {
                botStatusFields.version.textContent = data.version || '-';
            }
            if (botStatusFields.ram) {
                botStatusFields.ram.textContent = data.ram || '-';
            }
            if (botStatusFields.cpu) {
                botStatusFields.cpu.textContent = data.cpu || '-';
            }
            if (data.estado === 'ONLINE') {
                startBtn.style.display = 'none';
                restartBtn.style.display = '';
                stopBtn.style.display = '';
                restartBtn.disabled = false;
                stopBtn.disabled = false;
            } else {
                startBtn.style.display = '';
                restartBtn.style.display = 'none';
                stopBtn.style.display = 'none';
                startBtn.disabled = false;
            }
        } catch (e) {
            if (botStatusFields.estado) {
                botStatusFields.estado.textContent = 'Desconocido';
                botStatusFields.estado.className = 'status-value error';
            }
            if (botStatusFields.tiempo) {
                botStatusFields.tiempo.textContent = '-';
            }
            if (botStatusFields.version) {
                botStatusFields.version.textContent = '-';
            }
            if (botStatusFields.ram) {
                botStatusFields.ram.textContent = '-';
            }
            if (botStatusFields.cpu) {
                botStatusFields.cpu.textContent = '-';
            }
            startBtn.style.display = '';
            restartBtn.style.display = 'none';
            stopBtn.style.display = 'none';
            startBtn.disabled = false;
        }
    }
    startBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        startBtn.disabled = true;
        showNotification('Iniciando bot...', 'info');
        await fetch('/api/bot/start', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    restartBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        restartBtn.disabled = true;
        showNotification('Reiniciando bot...', 'info');
        await fetch('/api/bot/restart', { method: 'POST' });
        setTimeout(updateBotStatus, 2000);
    };
    stopBtn.onclick = async (e) => {
        if (e) {
            e.preventDefault();
        }
        stopBtn.disabled = true;
        showNotification('Deteniendo bot...', 'warning');
        await fetch('/api/bot/stop', { method: 'POST' });
        setTimeout(updateBotStatus, 1000);
    };
    if (!botControls._interval) {
        botControls._interval = setInterval(updateBotStatus, 1000);
    }
    updateBotStatus();

    // Integraciones (JDA, AI API, Base de datos)
    async function safeUpdateIntegraciones() {
        try {
            const res = await fetch('/api/bot/integraciones');
            if (!res.ok) {
                throw new Error('No se pudo obtener el estado de integraciones');
            }
            const data = await res.json();
            const jdaStatus = document.getElementById('jda-status');
            const jdaPing = document.getElementById('jda-ping');
            const aiStatus = document.getElementById('ai-status');
            const aiMsg = document.getElementById('ai-msg');
            const dbStatus = document.getElementById('db-status');
            const dbPing = document.getElementById('db-ping');
            if (jdaStatus) {
                jdaStatus.innerHTML = data.jda.conectado ? 'Conectado <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'Desconectado <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (jdaPing) {
                jdaPing.textContent = data.jda.ping >= 0 ? `| Ping: ${data.jda.ping} ms` : '';
            }
            if (aiStatus) {
                aiStatus.innerHTML = data.aiApi.disponible ? 'Disponible <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'No disponible <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (aiMsg) {
                aiMsg.textContent = data.aiApi.mensaje ? `| ${data.aiApi.mensaje}` : '';
            }
            let backendActivo = false;
            let backendPing = null;
            try {
                const start = performance.now();
                const backendRes = await fetch('/api/botfacade/ping');
                backendActivo = backendRes.ok;
                backendPing = Math.round(performance.now() - start);
            } catch (err) {
                backendActivo = false;
            }
            if (dbStatus) {
                dbStatus.innerHTML = backendActivo ? 'Backend activo <span style="color:#43b581;font-weight:bold;">&#10004;</span>' : 'Backend inactivo <span style="color:#ed4245;font-weight:bold;">&#10008;</span>';
            }
            if (dbPing) {
                dbPing.textContent = backendPing !== null ? `| Ping: ${backendPing} ms` : '';
            }
        } catch (e) {
            const jdaStatus = document.getElementById('jda-status');
            const jdaPing = document.getElementById('jda-ping');
            const aiStatus = document.getElementById('ai-status');
            const aiMsg = document.getElementById('ai-msg');
            const dbStatus = document.getElementById('db-status');
            const dbPing = document.getElementById('db-ping');
            if (jdaStatus) {
                jdaStatus.innerHTML = 'Desconocido';
            }
            if (jdaPing) {
                jdaPing.textContent = '';
            }
            if (aiStatus) {
                aiStatus.innerHTML = 'Desconocido';
            }
            if (aiMsg) {
                aiMsg.textContent = '';
            }
            if (dbStatus) {
                dbStatus.innerHTML = 'Desconocido';
            }
            if (dbPing) {
                dbPing.textContent = '';
            }
        }
    }
    clearInterval(window._integracionesInterval);
    window._integracionesInterval = setInterval(safeUpdateIntegraciones, 2000);
    safeUpdateIntegraciones();

    // Estadísticas de Base de Datos (DASHBOARD y tarjeta)
    function updateDatabaseStats() {
        fetch('/api/db/stats')
            .then(res => res.json())
            .then(data => {
                const dbItems = document.querySelectorAll('.dashboard-card.base-datos .integracion-item');
                if (dbItems.length >= 4) {
                    // Mantener valores como strings para evitar errores de redondeo con IDs grandes
                    dbItems[0].querySelector('.db-count').textContent = data.userCount?.toString() || '0';
                    dbItems[1].querySelector('.db-count').textContent = data.experienceCount?.toString() || '0';
                    dbItems[2].querySelector('.db-count').textContent = data.penaltyCount?.toString() || '0';
                    const estadoSpan = dbItems[3].querySelector('.db-count');
                    estadoSpan.textContent = data.available ? 'Disponible' : 'No disponible';
                    estadoSpan.classList.remove('disponible', 'no-disponible');
                    estadoSpan.classList.add(data.available ? 'disponible' : 'no-disponible');
                }
            })
            .catch(() => {
                const dbItems = document.querySelectorAll('.dashboard-card.base-datos .integracion-item');
                if (dbItems.length >= 4) {
                    dbItems[0].querySelector('.db-count').textContent = '-';
                    dbItems[1].querySelector('.db-count').textContent = '-';
                    dbItems[2].querySelector('.db-count').textContent = '-';
                    const estadoSpan = dbItems[3].querySelector('.db-count');
                    estadoSpan.textContent = 'No disponible';
                    estadoSpan.classList.remove('disponible');
                    estadoSpan.classList.add('no-disponible');
                }
            });
    }
    setInterval(updateDatabaseStats, 2000);
    updateDatabaseStats();

    // Logs Recientes
    const recentLogsListElement = document.getElementById('recent-logs-list');

    async function fetchAndRenderRecentLogs() {
        if (!recentLogsListElement) {
            return;
        }

        try {
            const params = new URLSearchParams();
            params.append('limit', '0'); 
            const res = await fetch(`/api/logs?${params.toString()}`);
            if (!res.ok) {
                if (recentLogsListElement) {
                    recentLogsListElement.innerHTML = '<p class="error-message">Error al cargar logs recientes.</p>';
                }
                return;
            }
            const logs = await res.json();

            if (!recentLogsListElement) { return; } // Defensa extra

            if (!logs || logs.length === 0) {
                recentLogsListElement.innerHTML = '<p>No hay logs recientes.</p>';
                return;
            }

            recentLogsListElement.innerHTML = ''; // Limpiar logs anteriores
            const parsedLogs = [];

            // Parsear todos los logs obtenidos y recolectar los parseables
            logs.forEach(logString => {
                const logObject = window.logUtils.parseLogEntry(logString);
                if (logObject.isParsed) {
                    parsedLogs.push(logObject); 
                }
            });

            if (parsedLogs.length === 0) {
                recentLogsListElement.innerHTML = '<p>No hay logs recientes con formato estándar.</p>';
                return;
            }

            // Mostrar solo los últimos X logs parseados (ej. 7)
            const logsToShow = parsedLogs.slice(0, 7); // Tomar los primeros 7 de la lista (que son los más recientes)

            logsToShow.forEach(logObject => {
                const logItem = document.createElement('div');
                logItem.classList.add('recent-log-item');
                
                // Aplicar borde izquierdo con el color del tipo de log
                logItem.style.borderLeft = '3px solid ' + window.logUtils.getLogTypeColor(logObject.tipo);
                
                // Hora con estilo
                const timeSpan = document.createElement('span');
                timeSpan.classList.add('log-time');
                timeSpan.textContent = logObject.hora;
                
                // Etiqueta de nivel
                const levelSpan = document.createElement('span');
                levelSpan.classList.add('log-level-tag');
                levelSpan.textContent = logObject.tipo;
                levelSpan.style.backgroundColor = window.logUtils.getLogTypeColor(logObject.tipo);
                
                // Origen
                const originSpan = document.createElement('span');
                originSpan.classList.add('log-origen');
                const maxOriginLength = 30;
                originSpan.textContent = logObject.origen.length > maxOriginLength 
                    ? logObject.origen.substring(0, maxOriginLength) + '...' 
                    : logObject.origen;
                originSpan.title = logObject.origen; // Mostrar origen completo en hover
                
                // Añadir elementos en una sola línea
                logItem.appendChild(timeSpan);
                logItem.appendChild(levelSpan);
                logItem.appendChild(originSpan);
                
                recentLogsListElement.appendChild(logItem);
            });

        } catch (e) {
            console.error('Error fetching recent logs:', e);
            if (recentLogsListElement) {
                recentLogsListElement.innerHTML = '<p class="error-message">No se pudieron cargar los logs recientes.</p>';
            }
        }
    }

    // Cargar logs recientes al iniciar y luego periódicamente
    fetchAndRenderRecentLogs();
    if (recentLogsInterval) {
        clearInterval(recentLogsInterval);
    }
    recentLogsInterval = setInterval(fetchAndRenderRecentLogs, 15000); // Actualizar cada 15 segundos

    // Navegación rápida desde dashboard (Ver base de datos / Ver todos los logs)
    document.querySelectorAll('.view-all-link[data-screen]').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const screen = this.getAttribute('data-screen');
            if (!screen) {
                return;
            }
            // Actualiza el menú lateral
            document.querySelectorAll('.menu-item').forEach(item => {
                if (item.getAttribute('data-screen') === screen) {
                    item.classList.add('active');
                } else {
                    item.classList.remove('active');
                }
            });
            // Carga la pantalla usando la función global
            if (window.loadScreen) {
                window.loadScreen(screen);
            }
        });
    });

    // Ejemplo: iniciar ping periódico
    if (dashboardPingInterval) {
        clearInterval(dashboardPingInterval);
    }
    dashboardPingInterval = setInterval(() => {
        // Aquí iría la lógica de ping a integraciones
        // Por ejemplo: actualizar el estado de integraciones
        // updateIntegracionesStatus();
    }, 10000); // cada 10 segundos
};

// Limpieza de recursos del dashboard
window.destroyDashboardScreen = function () {
    if (dashboardPingInterval) {
        clearInterval(dashboardPingInterval);
        dashboardPingInterval = null;
    }
    // No es necesario llamar a stopLogPolling aquí ya que se eliminó
    if (recentLogsInterval) {
        clearInterval(recentLogsInterval);
        recentLogsInterval = null;
    }
};
