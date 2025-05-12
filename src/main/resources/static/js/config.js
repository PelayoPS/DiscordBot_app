// Inicialización de la pantalla de configuración
window.initConfigScreen = function() {
    document.addEventListener('DOMContentLoaded', () => {
        const botTokenInput = document.getElementById('bot-token');
        const toggleTokenVisibilityButton = document.getElementById('toggle-token-visibility');
        const activityTypeSelect = document.getElementById('activity-type');
        const streamUrlGroup = document.getElementById('stream-url-group');
        const saveButtons = document.querySelectorAll('.save-config-btn');

        const botStatusTextInput = document.getElementById('bot-status-text');
        const botActivityNameInput = document.getElementById('bot-activity-name');
        const botStreamUrlInput = document.getElementById('bot-stream-url');

        // 1. Cargar configuración actual
        function loadConfiguration() {
            console.log('Intentando cargar la configuración existente...');
            
            // Cargar estado y actividad
            fetch('/api/config/presence')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error al obtener la configuración de presencia: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data) {
                        botStatusTextInput.value = data.statusText || '';
                        activityTypeSelect.value = data.activityType || 'PLAYING';
                        botActivityNameInput.value = data.activityName || '';
                        botStreamUrlInput.value = data.streamUrl || '';
                        handleActivityTypeChange(); 
                    }
                })
                .catch(error => console.error('Error al cargar la configuración de presencia:', error));

            // Cargar información sobre el token (si está configurado o no)
            fetch('/api/config/bot-token-info') 
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error al obtener la información del token: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data && data.isTokenSet) {
                        botTokenInput.placeholder = "Token configurado (oculto)";
                    } else {
                        botTokenInput.placeholder = "Introduce el token de tu bot";
                    }
                })
                .catch(error => console.error('Error al cargar la información del token:', error));
            
            handleActivityTypeChange(); 
        }

        // 2. Manejar visibilidad del token
        if (toggleTokenVisibilityButton && botTokenInput) {
            toggleTokenVisibilityButton.addEventListener('click', () => {
                if (botTokenInput.type === 'password') {
                    botTokenInput.type = 'text';
                    toggleTokenVisibilityButton.textContent = 'Ocultar';
                } else {
                    botTokenInput.type = 'password';
                    toggleTokenVisibilityButton.textContent = 'Mostrar';
                }
            });
        }

        // 3. Manejar visibilidad de URL del Stream
        function handleActivityTypeChange() {
            if (activityTypeSelect.value === 'STREAMING') {
                streamUrlGroup.style.display = 'block';
            } else {
                streamUrlGroup.style.display = 'none';
                botStreamUrlInput.value = ''; 
            }
        }

        if (activityTypeSelect && streamUrlGroup) {
            activityTypeSelect.addEventListener('change', handleActivityTypeChange);
            handleActivityTypeChange();
        }

        // 4. Guardar cada campo de forma independiente
        saveButtons.forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const field = btn.getAttribute('data-field');
                let value;
                let body;
                let endpoint;
                let method = 'POST';
                let headers = { 'Content-Type': 'application/json' };
                let showAlert = true;
                let errors = [];

                if (field === 'bot-token') {
                    value = botTokenInput.value;
                    if (!value || value.trim() === '') {
                        alert('Introduce un token válido.');
                        return;
                    }
                    endpoint = '/api/config/bot-token';
                    body = JSON.stringify({ token: value });
                } else if (field === 'bot-status-text') {
                    value = botStatusTextInput.value;
                    endpoint = '/api/config/presence';
                    body = JSON.stringify({ statusText: value });
                } else if (field === 'activity-type') {
                    value = activityTypeSelect.value;
                    endpoint = '/api/config/presence';
                    body = JSON.stringify({ activityType: value });
                } else if (field === 'bot-activity-name') {
                    value = botActivityNameInput.value;
                    if (!value || value.trim() === '') {
                        alert('El nombre de la actividad no puede estar vacío.');
                        return;
                    }
                    endpoint = '/api/config/presence';
                    body = JSON.stringify({ activityName: value });
                } else if (field === 'bot-stream-url') {
                    value = botStreamUrlInput.value;
                    if (activityTypeSelect.value === 'STREAMING' && (!value || !isValidHttpUrl(value))) {
                        alert('Para "Transmitiendo", introduce una URL válida de Twitch o YouTube.');
                        return;
                    }
                    endpoint = '/api/config/presence';
                    body = JSON.stringify({ streamUrl: value });
                } else {
                    alert('Campo no soportado.');
                    return;
                }

                try {
                    const response = await fetch(endpoint, { method, headers, body });
                    if (!response.ok) {
                        const errorData = await response.text();
                        throw new Error(errorData);
                    }
                    const data = await response.json();
                    if (field === 'bot-token') {
                        botTokenInput.value = '';
                        botTokenInput.placeholder = "Token configurado (oculto)";
                    }
                    alert((data && data.message) || 'Guardado correctamente.');
                } catch (error) {
                    alert('Error al guardar: ' + error.message);
                }
            });
        });

        function isValidHttpUrl(string) {
            let url;
            try {
                url = new URL(string);
            } catch (_) {
                return false;
            }
            // Permitir solo Twitch y YouTube para streaming
            if (activityTypeSelect.value === 'STREAMING') {
                return (url.protocol === "http:" || url.protocol === "https:") && 
                       (url.hostname === "www.twitch.tv" || url.hostname === "twitch.tv" || 
                        url.hostname === "www.youtube.com" || url.hostname === "youtube.com" || url.hostname === "youtu.be");
            }
            return url.protocol === "http:" || url.protocol === "https:";
        }

        loadConfiguration();
    });
};

// Limpieza de recursos de la pantalla de configuración
window.destroyConfigScreen = function() {
};
