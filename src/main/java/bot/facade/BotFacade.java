package bot.facade;

import bot.models.Usuario;
import bot.models.Penalizacion;
import bot.facade.dto.BotConfigDTO;
import bot.facade.dto.BotIntegracionesDTO;
import bot.facade.dto.BotPresenceDTO;
import bot.facade.dto.BotStatusDTO;
import bot.facade.dto.DatabaseStatsDTO;
import java.util.List;

/**
 * Facade interface defining high-level operations for the Discord bot
 * application.
 * Este facade actúa como punto de entrada único para interactuar con la lógica
 * principal del bot.
 * 
 * @author PelayoPS
 */
public interface BotFacade {

    // --- Métodos de moderación usados por comandos ---

    /**
     * Expulsa a un usuario de un servidor específico.
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     * @param reason        Razón de la expulsión
     */
    void kickUser(String guildId, String discordUserId, String reason);

    /**
     * Advierte a un usuario (warn).
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     * @param reason        Razón de la advertencia
     */
    void warnUser(String guildId, String discordUserId, String reason);

    /**
     * Silencia (mute) a un usuario.
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     * @param reason        Razón del mute
     * @param duration      Duración del mute
     */
    void muteUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Aplica timeout a un usuario.
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     * @param reason        Razón del timeout
     * @param duration      Duración del timeout
     */
    void timeoutUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Desbanea a un usuario.
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     */
    void unbanUser(String guildId, String discordUserId);

    /**
     * Purga mensajes de un canal.
     *
     * @param guildId     ID del servidor
     * @param channelId   ID del canal
     * @param moderatorId ID del moderador
     * @param amount      Cantidad de mensajes a purgar
     */
    void purgeMessages(String guildId, String channelId, String moderatorId, int amount);

    /**
     * Indica si el token del bot está configurado (sin exponer el valor).
     *
     * @return true si el token está configurado, false en caso contrario
     */
    boolean hasBotToken();

    /**
     * Devuelve la presencia/actividad actual del bot (status, tipo, nombre, url).
     *
     * @return BotPresenceDTO con la presencia actual del bot
     */
    BotPresenceDTO getBotPresence();

    /**
     * Inicia el bot de Discord.
     */
    void startBot();

    /**
     * Detiene el bot de Discord de forma segura.
     */
    void stopBot();

    /**
     * Reinicia el bot y recarga la configuración.
     */
    void restartBot();

    /**
     * Recupera el estado actual del bot (por ejemplo, RUNNING, STOPPED).
     *
     * @return String representando el estado del bot.
     */
    String getBotStatus();

    /**
     * Devuelve información extendida del estado del bot (DTO).
     *
     * @return BotStatusDTO con información extendida del estado.
     */
    BotStatusDTO getBotStatusExtended();

    // --- User Management ---

    /**
     * Recupera la información de un usuario por su ID de Discord (String).
     *
     * @param discordUserId ID del usuario de Discord
     * @return Usuario con la información correspondiente
     */
    Usuario getUserInfo(String discordUserId);

    /**
     * Recupera el historial de penalizaciones de un usuario por su ID de Discord.
     *
     * @param discordUserId ID del usuario de Discord
     * @return Lista de penalizaciones del usuario
     */
    List<Penalizacion> getUserHistory(String discordUserId);

    /**
     * Recupera los logs de la aplicación.
     *
     * @param limit Límite de líneas
     * @return Lista de logs
     */
    List<String> getLogs(int limit);

    /**
     * Banea a un usuario.
     *
     * @param guildId       ID del servidor
     * @param discordUserId ID del usuario de Discord
     * @param reason        Razón del baneo
     */
    void banUser(String guildId, String discordUserId, String reason);

    /**
     * Recupera el estado de las integraciones del bot.
     *
     * @return BotIntegracionesDTO con el estado de las integraciones
     */
    BotIntegracionesDTO getIntegracionesStatus();

    /**
     * Añade un nuevo usuario.
     *
     * @param usuario Usuario a añadir
     * @return Usuario añadido
     */
    Usuario addUsuario(Usuario usuario);

    /**
     * Añade o actualiza la experiencia de un usuario.
     *
     * @param experiencia El objeto Experiencia a guardar.
     * @return La Experiencia guardada, o null si ocurre un error.
     */
    // Método eliminado: Experiencia addExperiencia(Experiencia experiencia);

    /**
     * Añade una nueva penalización al sistema.
     *
     * @param penalizacion El objeto Penalizacion a añadir.
     * @return La Penalizacion guardada, o null si ocurre un error.
     */
    Penalizacion addPenalizacion(Penalizacion penalizacion);

    // --- Database Management ---

    /**
     * Devuelve los nombres de todas las tablas de la base de datos.
     *
     * @return Lista de nombres de tablas.
     */
    List<String> getTableNames();

    /**
     * Devuelve los nombres y etiquetas de las columnas de una tabla.
     *
     * @param tableName Nombre de la tabla
     * @return Lista de columnas (nombre y etiqueta)
     */
    List<bot.facade.dto.ColumnDTO> getTableColumns(String tableName);

    /**
     * Devuelve el contenido de una tabla (todas las filas).
     *
     * @param tableName Nombre de la tabla.
     * @return Lista de mapas (columna -> valor) por cada fila.
     */
    List<java.util.Map<String, Object>> getTableData(String tableName);

    // --- Database Management (CRUD specific) ---

    /**
     * Actualiza un registro de experiencia existente.
     *
     * @param id El ID del registro de experiencia a actualizar.
     * @param data Un mapa con los nombres de las columnas y sus nuevos valores.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    boolean updateExperiencia(Long id, java.util.Map<String, Object> data);

    /**
     * Elimina un registro de experiencia.
     *
     * @param id El ID del registro de experiencia a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    boolean deleteExperiencia(Long id);

    // --- Configuración del Bot ---

    /**
     * Obtiene la configuración general del bot (sin exponer el token).
     *
     * @return BotConfigDTO con la configuración general
     */
    BotConfigDTO getBotConfig();

    /**
     * Guarda el token del bot.
     *
     * @param token Token de Discord
     */
    void saveBotToken(String token);

    /**
     * Indica si la clave Gemini está configurada (sin exponer el valor).
     *
     * @return true si la clave está configurada, false en caso contrario
     */
    boolean hasGeminiKey();

    /**
     * Guarda la clave Gemini.
     *
     * @param key Clave Gemini
     */
    void saveGeminiKey(String key);

    /**
     * Recupera estadísticas resumidas de la base de datos.
     *
     * @return DatabaseStatsDTO con estadísticas de la base de datos
     */
    DatabaseStatsDTO getDatabaseStats();
}