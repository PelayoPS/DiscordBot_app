package bot.facade;

// Import necessary models or DTOs if needed later
import bot.models.Usuario;
import bot.models.Penalizacion;
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
     * Recupera la información de un usuario por su ID de Discord.
     *
     * @param discordUserId El ID de Discord del usuario.
     * @return Usuario o null si no existe.
     */
    Usuario getUserInfo(String discordUserId);

    // --- Moderation ---

    /**
     * Banea a un usuario de un servidor específico.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a banear.
     * @param reason        La razón del baneo.
     */
    void banUser(String guildId, String discordUserId, String reason);

    /**
     * Expulsa a un usuario de un servidor específico.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a expulsar.
     * @param reason        La razón de la expulsión.
     */
    void kickUser(String guildId, String discordUserId, String reason);

    /**
     * Advierte a un usuario (warn).
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a advertir.
     * @param reason        La razón de la advertencia.
     */
    void warnUser(String guildId, String discordUserId, String reason);

    /**
     * Silencia (mute) a un usuario.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a silenciar.
     * @param reason        La razón del silencio.
     * @param duration      Duración del silencio.
     */
    void muteUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Aplica timeout a un usuario.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario.
     * @param reason        La razón del timeout.
     * @param duration      Duración del timeout.
     */
    void timeoutUser(String guildId, String discordUserId, String reason, java.time.Duration duration);

    /**
     * Desbanea a un usuario.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a desbanear.
     */
    void unbanUser(String guildId, String discordUserId);

    /**
     * Obtiene el historial de penalizaciones de un usuario.
     * 
     * @param discordUserId El ID de Discord del usuario.
     * @return Lista de penalizaciones del usuario.
     */
    List<Penalizacion> getUserHistory(String discordUserId);

    /**
     * Registra una purga de mensajes.
     * 
     * @param guildId     El ID del servidor.
     * @param channelId   El ID del canal.
     * @param moderatorId El ID del moderador.
     * @param amount      Cantidad de mensajes eliminados.
     */
    void purgeMessages(String guildId, String channelId, String moderatorId, int amount);

    // --- Other Potential Operations ---

    /**
     * Ejecuta un comando del bot programáticamente.
     * 
     * @param commandName El nombre del comando.
     * @param args        Argumentos del comando.
     * @return Resultado de la ejecución del comando.
     */
    String executeCommand(String commandName, String... args);

    /**
     * Recupera los logs de la aplicación.
     * 
     * @param level Nivel de log (INFO, WARN, ERROR).
     * @param limit Máximo número de entradas.
     * @return Lista de logs como String.
     */
    List<String> getLogs(String level, int limit);

    // --- Database Stats ---

    /**
     * Recupera estadísticas resumidas de la base de datos.
     *
     * @return DTO con los contadores de entidades.
     */
    DatabaseStatsDTO getDatabaseStats();

    /**
     * Recupera el estado de las integraciones del bot.
     *
     * @return DTO con el estado de las integraciones.
     */
    BotIntegracionesDTO getIntegracionesStatus();
}