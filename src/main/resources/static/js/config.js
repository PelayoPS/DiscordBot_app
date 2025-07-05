// Inicialización de la pantalla de configuración



window.initConfigScreen = function() {
    const botTokenInput = document.getElementById('bot-token');
    const toggleTokenVisibilityButton = document.getElementById('toggle-token-visibility');
    const geminiKeyInput = document.getElementById('gemini-key');
    const toggleGeminiKeyVisibilityButton = document.getElementById('toggle-gemini-key-visibility');
    const saveButtons = document.querySelectorAll('.save-config-btn');
    let tokenStatusDiv = document.getElementById('token-status');
    let geminiKeyStatusDiv = document.getElementById('gemini-key-status');
    if (!tokenStatusDiv) {
        tokenStatusDiv = document.createElement('div');
        tokenStatusDiv.id = 'token-status';
        botTokenInput.parentElement.parentElement.appendChild(tokenStatusDiv);
    }
    if (!geminiKeyStatusDiv) {
        geminiKeyStatusDiv = document.createElement('div');
        geminiKeyStatusDiv.id = 'gemini-key-status';
        geminiKeyInput.parentElement.parentElement.appendChild(geminiKeyStatusDiv);
    }

    function showNotification(msg, type = 'info') {
        let notif = document.createElement('div');
        notif.className = 'config-notification ' + type;
        notif.innerText = msg;
        document.body.appendChild(notif);
        setTimeout(() => notif.remove(), 2500);
    }

    async function loadConfiguration() {
        // Token
        try {
            const tokenResp = await fetch('/api/config/bot-token-info');
            if (!tokenResp.ok) {
              throw new Error('Error al obtener la información del token');
            }
            const tokenInfo = await tokenResp.json();
            if (tokenInfo && (tokenInfo.isTokenSet || tokenInfo.configured)) {
                botTokenInput.placeholder = "Token configurado (oculto)";
                tokenStatusDiv.innerHTML = '<span style="color:green;font-weight:bold;">Token configurado</span>';
            } else {
                botTokenInput.placeholder = "Introduce el token de tu bot";
                tokenStatusDiv.innerHTML = '<span style="color:red;font-weight:bold;">Token NO configurado</span>';
            }
        } catch (e) {
            tokenStatusDiv.innerText = 'No se pudo cargar el estado del token.';
        }
        // Gemini Key
        try {
            const geminiResp = await fetch('/api/config/gemini-key-info');
            if (!geminiResp.ok) {
                throw new Error('Error al obtener la información de la clave Gemini');
            }
            const geminiInfo = await geminiResp.json();
            if (geminiInfo && geminiInfo.key === 'SET') {
                geminiKeyInput.placeholder = "Clave configurada (oculta)";
                geminiKeyStatusDiv.innerHTML = '<span style="color:green;font-weight:bold;">Clave AI configurada</span>';
            } else {
                geminiKeyInput.placeholder = "Introduce la clave AI";
                geminiKeyStatusDiv.innerHTML = '<span style="color:red;font-weight:bold;">Clave AI NO configurada</span>';
            }
        } catch (e) {
            geminiKeyStatusDiv.innerText = 'No se pudo cargar el estado de la clave AI.';
        }
    }

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
            if (field === 'gemini-key') {
                const {value} = geminiKeyInput;
                if (!value || value.trim() === '') {
                    showNotification('Introduce una clave Gemini válida.', 'error');
                    return;
                }
                try {
                    const resp = await fetch('/api/config/gemini-key', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ key: value })
                    });
                    if (!resp.ok) {
                        throw new Error(await resp.text());
                    }
                    geminiKeyInput.value = '';
                    await loadConfiguration();
                    showNotification('Clave Gemini guardada correctamente.', 'success');
                } catch (err) {
                    showNotification('Error al guardar la clave Gemini: ' + err.message, 'error');
                }
            }
        });
    });

    // Visibilidad clave Gemini
    if (toggleGeminiKeyVisibilityButton && geminiKeyInput) {
        toggleGeminiKeyVisibilityButton.addEventListener('click', () => {
            const icon = toggleGeminiKeyVisibilityButton.querySelector('i');
            if (geminiKeyInput.type === 'password') {
                geminiKeyInput.type = 'text';
                if (icon) {
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                }
            } else {
                geminiKeyInput.type = 'password';
                if (icon) {
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                }
            }
        });
    }

    loadConfiguration();
};

// Limpieza de recursos de la pantalla de configuración
window.destroyConfigScreen = function() {
    // Eliminar notificaciones visuales si quedan
    document.querySelectorAll('.config-notification').forEach(n => n.remove());
};
