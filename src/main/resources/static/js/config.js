// Inicialización de la pantalla de configuración
window.initConfigScreen = function() {
    document.addEventListener('DOMContentLoaded', () => {
        const botTokenInput = document.getElementById('bot-token');
        const toggleTokenVisibilityButton = document.getElementById('toggle-token-visibility');
        const activityTypeSelect = document.getElementById('activity-type');
        const streamUrlGroup = document.getElementById('stream-url-group');
        const saveButton = document.getElementById('save-button');

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

        // 4. Guardar cambios
        if (saveButton) {
            saveButton.addEventListener('click', async () => {
                const token = botTokenInput.value;
                const statusText = botStatusTextInput.value;
                const activityType = activityTypeSelect.value;
                const activityName = botActivityNameInput.value;
                const streamUrl = botStreamUrlInput.value;

                let errors = [];

                if (activityType === 'STREAMING' && (!streamUrl || !isValidHttpUrl(streamUrl))) {
                    errors.push('Para "Transmitiendo", por favor introduce una URL válida de Twitch o YouTube.');
                }
                if (!activityName) {
                    errors.push('El nombre de la actividad no puede estar vacío.');
                }

                if (errors.length > 0) {
                    alert("Por favor, corrige los siguientes errores:\n" + errors.join("\n"));
                    return;
                }
                
                try {
                    // Guardar Token (si se ha introducido uno nuevo y no es solo espacios en blanco)
                    if (token && token.trim() !== '') {
                        console.log('Enviando nuevo Token...');
                        const tokenResponse = await fetch('/api/config/bot-token', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ token: token })
                        });
                        if (!tokenResponse.ok) {
                            const errorData = await tokenResponse.text();
                            throw new Error('Error al guardar el token: ' + errorData);
                        }
                        const tokenData = await tokenResponse.json(); 
                        console.log('Respuesta del token:', tokenData);
                        alert(tokenData.message || 'Token guardado. Puede que necesites reiniciar el bot.');
                        botTokenInput.value = ''; 
                        botTokenInput.placeholder = "Token configurado (oculto)";
                    } else {
                        console.log('No se ha introducido un nuevo token o solo contenía espacios en blanco.');
                    }

                    // Guardar Presencia
                    const presenceConfig = {
                        statusText: statusText,
                        activityType: activityType,
                        activityName: activityName,
                        streamUrl: activityType === 'STREAMING' ? streamUrl : null
                    };
                    console.log('Enviando configuración de Presencia:', presenceConfig);
                    const presenceResponse = await fetch('/api/config/presence', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(presenceConfig)
                    });
                    if (!presenceResponse.ok) {
                        const errorData = await presenceResponse.text();
                        throw new Error('Error al guardar la presencia: ' + errorData);
                    }
                    const presenceData = await presenceResponse.json();
                    console.log('Respuesta de la presencia:', presenceData);
                    alert(presenceData.message || 'Configuración de presencia guardada.');

                } catch (error) {
                    console.error('Error al guardar la configuración:', error);
                    alert('Error al guardar la configuración: ' + error.message);
                }
            });
        }

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
