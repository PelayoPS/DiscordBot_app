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
            .map(cb => {
                let t = cb.nextElementSibling.classList[1];
                if (t.toUpperCase() === "WARNING") {
                  return "WARN";
                }
                return t.toUpperCase();
            });
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
    // Se elimina la función parseLogEntry de aquí, ya que se usará window.logUtils.parseLogEntry

    function renderLogs(logEntriesFromServer) { // Renombrado para claridad
        if (!logEntriesFromServer || logEntriesFromServer.length === 0) {
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

        // Parsear todas las entradas de log una vez
        const parsedLogEntries = logEntriesFromServer.map(entryText => window.logUtils.parseLogEntry(entryText));

        // Obtener valores de los filtros de fecha
        const fromDateFilter = dateInputs[0].value; // YYYY-MM-DD
        const toDateFilter = dateInputs[1].value;   // YYYY-MM-DD

        // Filtrar por fecha
        let dateFilteredEntries = parsedLogEntries;
        if (fromDateFilter) {
            dateFilteredEntries = dateFilteredEntries.filter(log => {
                if (!log.isParsed || !log.fecha) { return false; }
                return log.fecha >= fromDateFilter;
            });
        }
        if (toDateFilter) {
            dateFilteredEntries = dateFilteredEntries.filter(log => {
                if (!log.isParsed || !log.fecha) { return false; }
                return log.fecha <= toDateFilter;
            });
        }
        
        // Obtener tipos seleccionados en los checkboxes
        const selectedTypes = Array.from(logFilterCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => {
                let t = cb.nextElementSibling.classList[1];
                if (t.toUpperCase() === "WARNING") {
                  return "WARN";
                }
                return t.toUpperCase();
            });

        // Filtrar por tipo (sobre los ya filtrados por fecha)
        let finalFilteredEntries = dateFilteredEntries;
        if (selectedTypes.length > 0) {
            finalFilteredEntries = dateFilteredEntries.filter(log => {
                // El tipo en log ya está en mayúsculas por parseLogEntry
                return selectedTypes.includes(log.tipo);
            });
        }

        if (finalFilteredEntries.length === 0) {
            logsTableBody.innerHTML = '<tr><td class="log-cell-main" colspan="5">No hay logs para los filtros seleccionados</td></tr>';
            const paginationContainer = document.querySelector('.pagination');
            const paginationInfo = document.querySelector('.pagination-info');
            if (paginationContainer) { paginationContainer.style.display = 'none'; }
            if (paginationInfo) { paginationInfo.textContent = ''; }
            return;
        }

        // La paginación se hace localmente sobre estos resultados (finalFilteredEntries).
        let currentPage = 1;
        const pageSize = 20; // O el tamaño de página que prefieras
        let totalPages = Math.ceil(finalFilteredEntries.length / pageSize);
        
        let paginationContainer = document.querySelector('.pagination');
        let paginationInfo = document.querySelector('.pagination-info');
        let prevBtn = paginationContainer ? paginationContainer.querySelectorAll('.pagination-button')[0] : null;
        let nextBtn = paginationContainer ? paginationContainer.querySelectorAll('.pagination-button')[1] : null;
        
        function renderPage(page) {
            currentPage = page;
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            const pageEntries = finalFilteredEntries.slice(start, end);
            
            logsTableBody.innerHTML = pageEntries.map(logObject => {
                const { fecha, hora, tipo, origen, mensaje, isParsed, raw } = logObject;
                
                if (!isParsed) {
                    // Si no se pudo parsear, mostrar la entrada raw completa en la celda de mensaje
                    return `
                        <tr class="log-row-detailed unparsed">
                            <td class="log-cell-main" colspan="5">${raw}</td>
                        </tr>
                    `;
                }

                let levelColor = window.logUtils.getLogTypeColor(tipo);
                let levelText = tipo;
                // El switch para levelText y levelColor se puede simplificar usando getLogTypeColor
                // y asegurando que 'tipo' ya está en el formato deseado (ej. mayúsculas)
                // Si se necesita un texto diferente al 'tipo' (ej. 'Warning' en lugar de 'WARN'),
                // se podría añadir otra función de utilidad o manejarlo aquí.
                // Por ahora, asumimos que 'tipo' es suficiente como 'levelText'.

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
            if (!paginationContainer || !paginationInfo || !prevBtn || !nextBtn) {
                if (paginationContainer) { paginationContainer.style.display = 'none'; }
                if (paginationInfo) { paginationInfo.textContent = ''; }
                return;
            }

            if (totalPages > 0) {
                if (paginationContainer) {
                    paginationContainer.style.display = 'flex'; 
                }
                if (paginationInfo) {
                    paginationInfo.textContent = `Página ${currentPage} de ${totalPages}`;
                }
            } else {
                 if (paginationContainer) {
                    paginationContainer.style.display = 'none'; 
                 }
                 if (paginationInfo) {
                    paginationInfo.textContent = '';
                 }
                 return;
            }
            
            prevBtn.disabled = currentPage === 1;
            prevBtn.onclick = () => {
                if (currentPage > 1) {
                    renderPage(currentPage - 1);
                }
            };
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
