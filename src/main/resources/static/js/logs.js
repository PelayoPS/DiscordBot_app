// Inicialización de la pantalla de logs
window.initLogsScreen = function () {
    const logFilterCheckboxes = document.querySelectorAll('.filter-checkbox input');
    const dateInputs = document.querySelectorAll('.date-filter input[type="date"]');
    const logsTableBody = document.querySelector('.logs-table tbody');

    // Establecer la fecha de hoy por defecto si está vacío
    const today = new Date().toISOString().slice(0, 10);
    dateInputs.forEach(input => {
        if (!input.value) {
            input.value = today;
        }
    });

    async function fetchAndRenderLogs() {
        // Tipos seleccionados
        const types = Array.from(logFilterCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.nextElementSibling.classList[1]);
        // Fechas
        const fromDate = dateInputs[0].value;
        const toDate = dateInputs[1].value;
        // Construir query
        const params = new URLSearchParams();
        if (fromDate) {
            params.append('from', fromDate);
        }
        if (toDate) {
            params.append('to', toDate);
        }
        if (types.length > 0) {
            types.forEach(t => params.append('types', t.toUpperCase()));
        }
        // Indicar al backend que queremos todos los logs que coincidan con los filtros de tipo/fecha
        // para realizar la paginación en el frontend.
        params.append('limit', '0'); 

        try {
            const res = await fetch(`/api/logs?${params.toString()}`);
            const logs = await res.json();
            renderLogs(logs); // 'logs' ya es la lista de entradas de log completas
        } catch (e) {
            logsTableBody.innerHTML = '<tr><td class="log-cell-main">Error al cargar logs</td></tr>';
        }
    }

    // Hacer fetchAndRenderLogs accesible globalmente para el botón
    window.reloadLogs = fetchAndRenderLogs;

    // Asignar evento al botón de recargar
    const reloadBtn = document.getElementById('reload-logs-btn');
    if (reloadBtn) {
        reloadBtn.addEventListener('click', fetchAndRenderLogs);
    }

    // Función para parsear una línea de log en formato texto plano
    function parseLogEntry(entry) {
        // Ejemplo: 2025-05-06 17:47:45 INFO bot.log.LoggingManager - Base de datos ...
        const regex = /^(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2}:\d{2}) (\w+) ([^\-]+)- (.*)$/;
        const match = entry.match(regex);
        if (!match) {
            // Si no coincide, devolver todo como mensaje
            return {
                fecha: '',
                hora: '',
                tipo: 'info',
                origen: '',
                mensaje: entry,
            };
        }
        return {
            fecha: match[1],
            hora: match[2],
            tipo: match[3],
            origen: match[4].trim(),
            mensaje: match[5],
        };
    }

    function renderLogs(logEntries) {
        if (!logEntries || logEntries.length === 0) {
            logsTableBody.innerHTML = '<tr><td class="log-cell-main" colspan="5">No hay logs para los filtros seleccionados</td></tr>';
            // Limpiar paginación si no hay logs
            const paginationContainer = document.querySelector('.pagination');
            const paginationInfo = document.querySelector('.pagination-info');
            if (paginationContainer) {
                paginationContainer.style.display = 'none';
            }
            if (paginationInfo) {
                paginationInfo.textContent = '';
            }
            return;
        }

        // Obtener tipos seleccionados en los checkboxes
        const selectedTypes = Array.from(logFilterCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.nextElementSibling.classList[1].toUpperCase());
        // Filtrar logs por tipo si hay alguno seleccionado
        let filteredEntries = logEntries;
        if (selectedTypes.length > 0) {
            filteredEntries = logEntries.filter(entry => {
                const { tipo } = parseLogEntry(entry);
                return selectedTypes.includes(tipo.toUpperCase());
            });
        }

        // 'filteredEntries' ya es la lista de entradas de log completas y filtradas por el backend.
        // La paginación se hace localmente sobre estos resultados.
        let currentPage = 1;
        const pageSize = 20; // O el tamaño de página que prefieras
        let totalPages = Math.ceil(filteredEntries.length / pageSize);
        
        let paginationContainer = document.querySelector('.pagination');
        let paginationInfo = document.querySelector('.pagination-info');
        let prevBtn = paginationContainer.querySelectorAll('.pagination-button')[0];
        let nextBtn = paginationContainer.querySelectorAll('.pagination-button')[1];
        
        function renderPage(page) {
            currentPage = page;
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            const pageEntries = filteredEntries.slice(start, end);
            logsTableBody.innerHTML = pageEntries.map(entry => {
                const { fecha, hora, tipo, origen, mensaje } = parseLogEntry(entry);
                let levelColor = '#5865f2';
                let levelText = tipo.toUpperCase();
                switch (tipo.toLowerCase()) {
                    case 'debug':
                        levelColor = '#43b581';
                        break;
                    case 'warn':
                        levelColor = '#faa61a';
                        break;
                    case 'error':
                        levelColor = '#ed4245';
                        break;
                    case 'info':
                        levelColor = '#5865f2';
                        break;
                }
                return `
                    <tr class="log-row-detailed ${tipo.toLowerCase()}">
                        <td class="log-cell-fecha">${fecha}</td>
                        <td class="log-cell-hora">${hora}</td>
                        <td class="log-cell-nivel"><span class="log-level-tag" style="background-color: ${levelColor};">${levelText}</span></td>
                        <td class="log-cell-origen">${origen}</td>
                        <td class="log-cell-mensaje">${mensaje}</td>
                    </tr>
                `;
            }).join('');
            renderPagination();
        }
        
        function renderPagination() {
            const paginationContainer = document.querySelector('.pagination');
            const paginationInfo = document.querySelector('.pagination-info');

            if (totalPages > 0) {
                if (paginationContainer) {
                    paginationContainer.style.display = 'flex'; // Mostrar paginador
                }
                if (paginationInfo) {
                    paginationInfo.textContent = `Página ${currentPage} de ${totalPages}`;
                }
            } else {
                 if (paginationContainer) {
                    paginationContainer.style.display = 'none'; // Ocultar si no hay páginas
                 }
                 if (paginationInfo) {
                    paginationInfo.textContent = '';
                 }
                 return;
            }
            
            // Botón anterior
            prevBtn.disabled = currentPage === 1;
            prevBtn.onclick = () => {
                if (currentPage > 1) {
                    renderPage(currentPage - 1);
                }
            };
            // Botón siguiente
            nextBtn.disabled = currentPage === totalPages;
            nextBtn.onclick = () => {
                if (currentPage < totalPages) {
                    renderPage(currentPage + 1);
                }
            };
        }
        renderPage(1);
    }

    logFilterCheckboxes.forEach(cb => cb.addEventListener('change', fetchAndRenderLogs));
    dateInputs.forEach(input => input.addEventListener('change', fetchAndRenderLogs));
    fetchAndRenderLogs();
};

// Limpieza de recursos de la pantalla de logs
window.destroyLogsScreen = function () {
};
