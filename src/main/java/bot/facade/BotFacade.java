package bot.facade;

import bot.models.Usuario;
import bot.models.Penalizacion;
import bot.facade.dto.BotConfigDTO;
import bot.facade.dto.BotIntegracionesDTO;
import bot.facade.dto.BotPresenceDTO;
import bot.facade.dto.BotStatusDTO;
import bot.facade.dto.DatabaseStatsDTO;
import bot.models.Experiencia;
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
     */
    void kickUser(String guildId, String discordUserId, String reason);

    /**
     * Advierte a un usuario (warn).
     */
    void warnUser(String guildId, String discordUserId, String reason);

    /**
     * Silencia (mute) a un usuario.
     */
    void muteUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Aplica timeout a un usuario.
     */
    void timeoutUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Desbanea a un usuario.
     */
    void unbanUser(String guildId, String discordUserId);

    /**
     * Purga mensajes de un canal.
     */
    void purgeMessages(String guildId, String channelId, String moderatorId, int amount);

    /**
     * Indica si el token del bot está configurado (sin exponer el valor).
     */
    boolean hasBotToken();

    /**
     * Devuelve la presencia/actividad actual del bot (status, tipo, nombre, url).
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
     */
    Usuario getUserInfo(String discordUserId);

    /**
     * Recupera el historial de penalizaciones de un usuario por su ID de Discord.
     */
    List<Penalizacion> getUserHistory(String discordUserId);

    /**
     * Recupera los logs de la aplicación.
     * @param limit Límite de líneas
     * @return Lista de logs
     */
    List<String> getLogs(int limit);

    /**
     * Banea a un usuario.
     */
    void banUser(String guildId, String discordUserId, String reason);

    /**
     * Recupera el estado de las integraciones del bot.
     */
    BotIntegracionesDTO getIntegracionesStatus();

    /**
     * Añade un nuevo usuario.
     */
    Usuario addUsuario(Usuario usuario);

    /**
     * Añade o actualiza la experiencia de un usuario.
     *
     * @param experiencia El objeto Experiencia a guardar.
     * @return La Experiencia guardada, o null si ocurre un error.
     */
    Experiencia addExperiencia(Experiencia experiencia);

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
     * @return Lista de nombres de tablas.
     */
    List<String> getTableNames();

    /**
     * Devuelve los nombres de las columnas de una tabla.
     * @param tableName Nombre de la tabla.
     * @return Lista de nombres de columnas.
     */
    List<String> getTableColumns(String tableName);

    /**
     * Devuelve el contenido de una tabla (todas las filas).
     * @param tableName Nombre de la tabla.
     * @return Lista de mapas (columna -> valor) por cada fila.
     */
    List<java.util.Map<String, Object>> getTableData(String tableName);

    // --- Configuración del Bot ---

    /**
     * Obtiene la configuración general del bot (sin exponer el token).
     */
    BotConfigDTO getBotConfig();

    /**
     * Guarda el token del bot.
     * @param token Token de Discord
     */
    void saveBotToken(String token);

    /**
     * Indica si la clave Gemini está configurada (sin exponer el valor).
     */
    boolean hasGeminiKey();    

    /**
     * Guarda la clave Gemini.
     * @param key Clave Gemini
     */
    void saveGeminiKey(String key);

    /**
     * Recupera estadísticas resumidas de la base de datos.
     */
    DatabaseStatsDTO getDatabaseStats();
}