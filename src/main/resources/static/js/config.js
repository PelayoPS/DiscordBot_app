// Inicialización de la pantalla de configuración

window.initConfigScreen = function() {
    const botTokenInput = document.getElementById('bot-token');
    const toggleTokenVisibilityButton = document.getElementById('toggle-token-visibility');
    const activityTypeSelect = document.getElementById('activity-type');
    const streamUrlGroup = document.getElementById('stream-url-group');
    const saveButtons = document.querySelectorAll('.save-config-btn');
    const botStatusTextInput = document.getElementById('bot-status-text');
    const botActivityNameInput = document.getElementById('bot-activity-name');
    const botStreamUrlInput = document.getElementById('bot-stream-url');

    // Elementos de estado visual
    let tokenStatusDiv = document.getElementById('token-status');
    let presenceStatusDiv = document.getElementById('presence-status');
    if (!tokenStatusDiv) {
        tokenStatusDiv = document.createElement('div');
        tokenStatusDiv.id = 'token-status';
        botTokenInput.parentElement.parentElement.appendChild(tokenStatusDiv);
    }
    if (!presenceStatusDiv) {
        presenceStatusDiv = document.createElement('div');
        presenceStatusDiv.id = 'presence-status';
        activityTypeSelect.parentElement.parentElement.appendChild(presenceStatusDiv);
    }

    // Notificación visual
    function showNotification(msg, type = 'info') {
        let notif = document.createElement('div');
        notif.className = 'config-notification ' + type;
        notif.innerText = msg;
        document.body.appendChild(notif);
        setTimeout(() => notif.remove(), 2500);
    }

    // 1. Cargar configuración actual y actualizar estado visual
    async function loadConfiguration() {
        try {
            // Presencia
            const presenceResp = await fetch('/api/config/presence');
            if (!presenceResp.ok) {
              throw new Error('Error al obtener la configuración de presencia');
            }
            const presence = await presenceResp.json();
            botStatusTextInput.value = presence.statusText || '';
            activityTypeSelect.value = presence.activityType || 'PLAYING';
            botActivityNameInput.value = presence.activityName || '';
            botStreamUrlInput.value = presence.streamUrl || '';
            handleActivityTypeChange();
            // Estado visual presencia
            presenceStatusDiv.innerHTML = `<b>Presencia actual:</b> ${presence.activityType || '-'} | ${presence.activityName || '-'}${presence.streamUrl ? ' | ' + presence.streamUrl : ''}<br><span style='font-size:0.9em;color:#888;'>${presence.statusText || ''}</span>`;
        } catch (e) {
            presenceStatusDiv.innerText = 'No se pudo cargar la presencia.';
        }
        try {
            // Token
            const tokenResp = await fetch('/api/config/bot-token-info');
            if (!tokenResp.ok) {
              throw new Error('Error al obtener la información del token');
            }
            const tokenInfo = await tokenResp.json();
            if (tokenInfo && tokenInfo.isTokenSet) {
                botTokenInput.placeholder = "Token configurado (oculto)";
                tokenStatusDiv.innerHTML = '<span style="color:green;font-weight:bold;">Token configurado</span>';
            } else {
                botTokenInput.placeholder = "Introduce el token de tu bot";
                tokenStatusDiv.innerHTML = '<span style="color:red;font-weight:bold;">Token NO configurado</span>';
            }
        } catch (e) {
            tokenStatusDiv.innerText = 'No se pudo cargar el estado del token.';
        }
        handleActivityTypeChange();
    }

    // 2. Manejar visibilidad del token
    if (toggleTokenVisibilityButton && botTokenInput) {
        toggleTokenVisibilityButton.addEventListener('click', () => {
            const icon = toggleTokenVisibilityButton.querySelector('i');
            if (botTokenInput.type === 'password') {
                botTokenInput.type = 'text';
                if (icon) {
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                }
            } else {
                botTokenInput.type = 'password';
                if (icon) {
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                }
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


    // Guardar token y presencia de forma centralizada y segura
    saveButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const field = btn.getAttribute('data-field');
            if (field === 'bot-token') {
                const {value} = botTokenInput;
                if (!value || value.trim() === '') {
                    showNotification('Introduce un token válido.', 'error');
                    return;
                }
                try {
                    const resp = await fetch('/api/config/token', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ token: value })
                    });
                    if (!resp.ok) {
                      throw new Error(await resp.text());
                    }
                    botTokenInput.value = '';
                    await loadConfiguration();
                    showNotification('Token guardado correctamente.', 'success');
                } catch (err) {
                    showNotification('Error al guardar el token: ' + err.message, 'error');
                }
                return;
            }

            // Guardar presencia: siempre enviar el objeto completo
            // Validaciones
            if (field === 'bot-activity-name' && (!botActivityNameInput.value || botActivityNameInput.value.trim() === '')) {
                showNotification('El nombre de la actividad no puede estar vacío.', 'error');
                return;
            }
            if (field === 'bot-stream-url' && activityTypeSelect.value === 'STREAMING' && (!botStreamUrlInput.value || !isValidHttpUrl(botStreamUrlInput.value))) {
                showNotification('Para "Transmitiendo", introduce una URL válida de Twitch o YouTube.', 'error');
                return;
            }
            // Construir objeto completo
            const presenceObj = {
                statusText: botStatusTextInput.value,
                activityType: activityTypeSelect.value,
                activityName: botActivityNameInput.value,
                streamUrl: botStreamUrlInput.value
            };
            try {
                const resp = await fetch('/api/config/presence', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(presenceObj)
                });
                if (!resp.ok) {
                  throw new Error(await resp.text());
                }
                await loadConfiguration();
                showNotification('Presencia guardada correctamente.', 'success');
            } catch (err) {
                showNotification('Error al guardar la presencia: ' + err.message, 'error');
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
};

// Limpieza de recursos de la pantalla de configuración
window.destroyConfigScreen = function() {
    // Eliminar notificaciones visuales si quedan
    document.querySelectorAll('.config-notification').forEach(n => n.remove());
};
