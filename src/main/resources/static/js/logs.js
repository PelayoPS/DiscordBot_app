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

    function renderLogs(logEntries) { // Renombrado 'logs' a 'logEntries' para claridad
        if (!logEntries || logEntries.length === 0) {
            logsTableBody.innerHTML = '<tr><td class="log-cell-main">No hay logs para los filtros seleccionados</td></tr>';
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
        
        // 'logEntries' ya es la lista de entradas de log completas y filtradas por el backend.
        // La paginación se hace localmente sobre estos resultados.
        let currentPage = 1;
        const pageSize = 20; // O el tamaño de página que prefieras
        let totalPages = Math.ceil(logEntries.length / pageSize);
        
        let paginationContainer = document.querySelector('.pagination');
        let paginationInfo = document.querySelector('.pagination-info');
        let prevBtn = paginationContainer.querySelectorAll('.pagination-button')[0];
        let nextBtn = paginationContainer.querySelectorAll('.pagination-button')[1];
        
        function renderPage(page) {
            currentPage = page;
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            const pageEntries = logEntries.slice(start, end); // Usar 'logEntries' directamente
            
            logsTableBody.innerHTML = pageEntries.map(entry => {
                // La lógica de parseo y formateo de cada 'entry' se mantiene aquí,
                // ya que el backend devuelve strings de log, no HTML.

                switch (tipo.toLowerCase()) {
                    case 'debug':
                        levelColor = '#43b581'; // Verde
                        break;
                    case 'warn':
                        levelColor = '#faa61a'; // Naranja/Amarillo
                        break;
                    case 'error':
                        levelColor = '#ed4245'; // Rojo
                        break;
                }

                return `
                    <tr class="log-row-detailed ${tipo.toLowerCase()}">
                        <td class="log-cell-main">
                            <div class="log-entry-header">
                                <span class="log-timestamp">${fecha} ${hora}</span>
                                <span class="log-level-tag" style="background-color: ${levelColor};">${levelText}</span>
                            </div>
                            <div class="log-entry-body">
                                <span class="log-logger">${origen}</span> - <span class="log-message">${mensajeHtml}</span>
                            </div>
                        </td>
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
