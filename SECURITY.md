# 🚨 CONFIGURACIÓN DE SEGURIDAD

## ⚠️ IMPORTANTE: Configuración de Credenciales

Este proyecto requiere configuración de credenciales sensibles que **NO DEBEN** estar en el repositorio de Git.

### 📋 Pasos para configurar el proyecto:

1. **Copia los archivos template:**
   ```powershell
   copy src\main\resources\config.properties.example src\main\resources\config.properties
   copy src\main\resources\application.properties.example src\main\resources\application.properties
   ```

2. **Edita los archivos copiados** y reemplaza los valores entre `< >`:
   - `<TU_TOKEN_DE_DISCORD>` - Token de tu bot de Discord
   - `<TU_API_KEY_DE_GEMINI>` - API Key de Google Gemini
   - `<TU_USUARIO_DE_DB>` y `<TU_PASSWORD_DE_DB>` - Credenciales de MySQL

### 🔐 Obtener Credenciales:

#### Token de Discord:
1. Ve a https://discord.com/developers/applications
2. Crea una nueva aplicación o selecciona una existente
3. Ve a "Bot" → "Token" → "Reset Token"
4. **⚠️ IMPORTANTE:** Si tu token actual está comprometido, regénéralo

#### API Key AI openAI:
1. Ve a https://platform.openai.com/signup
2. Crea una nueva cuenta o inicia sesión
3. Ve a "API Keys"
4. Crea una nueva API Key

### 🛡️ Buenas Prácticas:

- **NUNCA** subas archivos con credenciales reales a Git
- **REGENERA** tokens comprometidos inmediatamente
- Usa variables de entorno en producción
- Revisa regularmente los permisos de tu bot

### 🚫 Archivos que NO deben estar en Git:

- `config.properties`
- `application.properties`
- Cualquier archivo en `/bin/`
- Archivos `.env`
- Logs con información sensible
