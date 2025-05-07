package bot.facade;

import bot.models.Usuario;
import bot.models.Penalizacion;
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
     * Recupera los logs de la aplicación filtrados por fecha y tipo.
     * 
     * @param types Lista de tipos de log (INFO, WARN, ERROR, DEBUG, opcional). Si
     *              es null o vacía, se devuelven todos los tipos.
     * @param limit Máximo número de entradas. Si es 0 o negativo, devuelve todas
     *              las coincidentes.
     * @param from  Fecha inicial en formato yyyy-MM-dd (opcional).
     * @param to    Fecha final en formato yyyy-MM-dd (opcional).
     * @return Lista de logs como String.
     */
    List<String> getLogs(int limit);

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

    // --- Entity Management ---

    /**
     * Añade un nuevo usuario al sistema.
     *
     * @param usuario El objeto Usuario a añadir.
     * @return El Usuario guardado, o null si ocurre un error.
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
}