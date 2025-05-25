package backend.facade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import backend.commands.ModuleManager;
import backend.config.ConfigManager;
import backend.config.ConfigService;
import backend.core.Bot;
import backend.core.ServiceFactory;
import backend.db.DatabaseManager;
import backend.facade.dto.BotConfigDTO;
import backend.facade.dto.BotIntegracionesDTO;
import backend.facade.dto.BotPresenceDTO;
import backend.facade.dto.BotStatusDTO;
import backend.facade.dto.DatabaseStatsDTO;
import backend.log.LoggingManager;
import backend.models.Penalizacion;
import backend.models.Usuario;
import backend.modules.CommandManager;
import backend.services.ModerationService;
import backend.services.UsuarioService;

import backend.core.JPackageSSLHelper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Implementación de la interfaz BotFacade.
 * Coordina acciones delegando a los servicios subyacentes y gestiona el ciclo
 * de vida del bot.
 * 
 * @author PelayoPS
 */
@Service
public class BotFacadeImpl implements BotFacade {

    /**
     * Devuelve la presencia/actividad actual del bot.
     *
     * @return BotPresenceDTO con la presencia actual del bot
     */
    @Override
    public BotPresenceDTO getBotPresence() {
        return new BotPresenceDTO("online", "PLAYING", "DiscordBot", null);
    }

    private static final LoggingManager logger = new LoggingManager();
    private final UsuarioService usuarioService;
    private final ModerationService moderationService;
    private final CommandManager commandManager;
    private final DatabaseManager databaseManager;
    private Bot botInstance;
    private final ModuleManager moduleManager;
    private Instant botStartTime;
    @Value("${bot.version:1.0.0}")
    private String botVersion;

    // Estado de integraciones externas
    private String aiApiStatus = "Desconocido";
    private long aiApiLastCheck = 0L;
    private static final long AI_API_CACHE_MS = 30 * 60 * 1000; // 30 minutos

    private final ConfigService configService;

    /**
     * Constructor para inyección de dependencias (con ConfigService real de
     * Spring).
     * 
     * @param usuarioService    Servicio de usuarios
     * @param moderationService Servicio de moderación
     * @param commandManager    Gestor de comandos
     * @param databaseManager   Gestor de base de datos
     * @param configService     Servicio de configuración (inyectado por Spring)
     */
    public BotFacadeImpl(UsuarioService usuarioService, ModerationService moderationService,
            CommandManager commandManager, DatabaseManager databaseManager, ConfigService configService,
            ModuleManager moduleManager) {
        this.usuarioService = usuarioService;
        this.moderationService = moderationService;
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;
        this.configService = configService;
        this.moduleManager = moduleManager;
    }

    /**
     * Inicia el bot de Discord.
     */
    @Override
    public synchronized void startBot() {
        if (botInstance == null) {
            String token = configService.get("token");
            ServiceFactory serviceFactory = new ServiceFactory(configService, databaseManager);
            botInstance = new Bot(token, serviceFactory, databaseManager, moduleManager);
            botStartTime = Instant.now();
            logger.logInfo("Bot iniciado desde BotFacadeImpl (con dependencias reales)");
        }
    }

    /**
     * Detiene el bot de Discord de forma segura.
     */
    @Override
    public synchronized void stopBot() {
        if (botInstance != null) {
            botInstance.shutdown();
            botInstance = null;
            logger.logInfo("Bot detenido desde BotFacadeImpl");
        }
    }

    /**
     * Reinicia el bot y recarga la configuración.
     */
    @Override
    public synchronized void restartBot() {
        stopBot();
        ConfigManager config = new ConfigManager();
        config.loadConfig();
        startBot();
        logger.logInfo("Bot reiniciado y configuración recargada desde BotFacadeImpl");
    }

    /**
     * Recupera el estado actual del bot.
     *
     * @return String representando el estado del bot.
     */
    @Override
    public String getBotStatus() {
        if (botInstance == null)
            return "OFFLINE";
        return "ONLINE";
    }

    /**
     * Devuelve información extendida del estado del bot.
     *
     * @return BotStatusDTO con información extendida del estado.
     */
    @Override
    public BotStatusDTO getBotStatusExtended() {
        String estado = getBotStatus();
        String version = botVersion;
        // Si la versión es la de fallback, intenta leer solo bot.version manualmente
        if ("1.0.0".equals(version)) {
            try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
                Properties props = new Properties();
                props.load(fis);
                String v = props.getProperty("bot.version");
                if (v != null && !v.isBlank()) {
                    version = v;
                }
            } catch (Exception ignored) {
            }
        }
        String tiempoActivo = "-";
        String ram = "-";
        String cpu = "-";
        if (botInstance != null && botStartTime != null) {
            Duration uptime = Duration.between(botStartTime, Instant.now());
            long days = uptime.toDays();
            long hours = uptime.toHours() % 24;
            long minutes = uptime.toMinutes() % 60;
            long seconds = uptime.getSeconds() % 60;
            tiempoActivo = String.format("%d días, %d horas, %d min, %d seg", days, hours, minutes, seconds);

            // Usar el helper de jpackage para información del sistema
            String[] systemInfo = JPackageSSLHelper.getCompatibleSystemInfo();
            ram = systemInfo[0];
            cpu = systemInfo[1];
        }
        return new BotStatusDTO(estado, tiempoActivo, version, ram, cpu);
    }

    /**
     * Recupera la información de un usuario por su ID de Discord.
     *
     * @param discordUserId El ID de Discord del usuario.
     * @return Usuario o null si no existe.
     */
    @Override
    public Usuario getUserInfo(String discordUserId) {
        logger.logInfo("FACADE: Getting user info for " + discordUserId);
        try {
            if (discordUserId == null || discordUserId.isBlank())
                return null;
            return usuarioService.findById(Long.parseLong(discordUserId))
                    .orElse(null);
        } catch (Exception e) {
            logger.logError("Error al obtener información de usuario", e);
            return null;
        }
    }

    /**
     * Banea a un usuario de un servidor específico.
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a banear.
     * @param reason        La razón del baneo.
     */
    @Override
    public void banUser(String guildId, String discordUserId, String reason) {
        moderationService.banearUsuario(Long.parseLong(discordUserId), reason, null);
    }

    /**
     * Expulsa a un usuario de un servidor específico.
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a expulsar.
     * @param reason        La razón de la expulsión.
     */
    public void kickUser(String guildId, String discordUserId, String reason) {
        moderationService.expulsarUsuario(Long.parseLong(discordUserId), reason, null);
    }

    /**
     * Advierte a un usuario (warn).
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a advertir.
     * @param reason        La razón de la advertencia.
     */
    public void warnUser(String guildId, String discordUserId, String reason) {
        moderationService.advertirUsuario(Long.parseLong(discordUserId), reason, null);
    }

    /**
     * Silencia (mute) a un usuario.
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a silenciar.
     * @param reason        La razón del silencio.
     * @param duration      Duración del silencio.
     */
    public void muteUser(String guildId, String discordUserId, String reason, Duration duration) {
        moderationService.silenciarUsuario(Long.parseLong(discordUserId), reason, duration, null);
    }

    /**
     * Aplica timeout a un usuario.
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario.
     * @param reason        La razón del timeout.
     * @param duration      Duración del timeout.
     */
    public void timeoutUser(String guildId, String discordUserId, String reason, Duration duration) {
        moderationService.timeoutUsuario(Long.parseLong(discordUserId), reason, duration, null);
    }

    /**
     * Desbanea a un usuario.
     *
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a desbanear.
     */
    public void unbanUser(String guildId, String discordUserId) {
        moderationService.desbanearUsuario(Long.parseLong(discordUserId), null);
    }

    /**
     * Obtiene el historial de penalizaciones de un usuario.
     *
     * @param discordUserId El ID de Discord del usuario.
     * @return Lista de penalizaciones del usuario.
     */
    @Override
    public List<Penalizacion> getUserHistory(String discordUserId) {
        logger.logInfo("FACADE: Getting user history for " + discordUserId);
        try {
            if (discordUserId == null || discordUserId.isBlank())
                return Collections.emptyList();
            return moderationService.obtenerHistorialUsuario(Long.parseLong(discordUserId));
        } catch (Exception e) {
            logger.logError("Error al obtener historial de usuario", e);
            return Collections.emptyList();
        }
    }

    /**
     * Registra una purga de mensajes.
     *
     * @param guildId     El ID del servidor.
     * @param channelId   El ID del canal.
     * @param moderatorId El ID del moderador.
     * @param amount      Cantidad de mensajes eliminados.
     */
    public void purgeMessages(String guildId, String channelId, String moderatorId, int amount) {
        moderationService.registrarPurge(Long.parseLong(moderatorId), amount, Long.parseLong(channelId));
    }

    /**
     * Ejecuta un comando del bot programáticamente.
     *
     * @param commandName El nombre del comando.
     * @param args        Argumentos del comando.
     * @return Resultado de la ejecución del comando.
     */
    public String executeCommand(String commandName, String... args) {
        logger.logInfo("FACADE: Executing command '" + commandName + "' with args: " + String.join(", ", args));
        return commandManager.getCommands().stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(commandName))
                .findFirst()
                .map(cmd -> {
                    return "Comando encontrado: " + commandName + ". Ejecución programática no implementada.";
                })
                .orElse("Comando no encontrado: " + commandName);
    }

    /**
     * Recupera los logs de la aplicación filtrados por fecha y tipo.
     *
     * @param limit Máximo número de entradas.
     * @return Lista de logs como String.
     */
    @Override
    public List<String> getLogs(int limit) {
        logger.logDebug("FACADE: Retrieving all logs (no filtering)");
        List<String> result = new ArrayList<>();
        Path logPath = Paths.get("logs\\app.log");
        if (!Files.exists(logPath)) {
            logger.logWarn("El archivo de log no se encuentra en: " + logPath.toString());
            return result;
        }
        try {
            // Leer todas las líneas
            List<String> allLines = Files.readAllLines(logPath);
            // Invertir para que los más recientes estén primero
            Collections.reverse(allLines);
            // Aplicar el límite sobre la lista invertida
            if (limit > 0 && allLines.size() > limit) {
                result = allLines.subList(0, limit);
            } else {
                result = allLines;
            }
        } catch (Exception e) {
            logger.logError("Error al leer logs desde BotFacadeImpl", e);
        }
        return result;
    }

    /**
     * Recupera estadísticas resumidas de la base de datos.
     *
     * @return DTO con los contadores de entidades.
     */
    @Override
    public DatabaseStatsDTO getDatabaseStats() {
        DatabaseManager dbManager = this.databaseManager;
        boolean available = true;
        if (dbManager == null) {
            available = false;
            logger.logError("No se pudo obtener la instancia existente de DatabaseManager para estadísticas.", null);
            return new DatabaseStatsDTO(0, 0, 0, false);
        }
        long userCount = 0;
        long experienceCount = 0;
        long penaltyCount = 0;
        try (java.sql.Connection conn = dbManager.getConnection()) {
            try (java.sql.Statement stmt = conn.createStatement()) {
                var rs1 = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
                if (rs1.next())
                    userCount = rs1.getLong(1);
                // Ahora la experiencia se almacena en la tabla usuarios
                var rs2 = stmt.executeQuery("SELECT SUM(puntos_xp) FROM usuarios");
                if (rs2.next())
                    experienceCount = rs2.getLong(1);
                var rs3 = stmt.executeQuery("SELECT COUNT(*) FROM penalizaciones");
                if (rs3.next())
                    penaltyCount = rs3.getLong(1);
            }
        } catch (Exception e) {
            logger.logError("Error al obtener estadísticas de la base de datos", e);
            available = false;
        }
        return new DatabaseStatsDTO(userCount, experienceCount, penaltyCount, available);
    }

    /**
     * Recupera el estado de las integraciones del bot.
     *
     * @return DTO con el estado de las integraciones.
     */
    public BotIntegracionesDTO getIntegracionesStatus() {
        boolean jdaConectado = false;
        long jdaPing = -1;
        if (botInstance != null && botInstance.getJda() != null
                && botInstance.getJda().getStatus().toString().equals("CONNECTED")) {
            jdaConectado = true;
            jdaPing = botInstance.getJda().getGatewayPing();
        }
        BotIntegracionesDTO.JdaStatus jda = new BotIntegracionesDTO.JdaStatus(jdaConectado, jdaPing);

        // AI API (cache cada 30 min)
        long now = System.currentTimeMillis();
        boolean aiDisponible = false;
        String aiMensaje = "Desconocido";
        if (now - aiApiLastCheck > AI_API_CACHE_MS) {
            String[] aiResult = comprobarAiApiStruct();
            aiDisponible = Boolean.parseBoolean(aiResult[0]);
            aiMensaje = aiResult[1];
            aiApiStatus = aiMensaje;
            aiApiLastCheck = now;
        } else {
            aiDisponible = aiApiStatus.startsWith("Disponible") || aiApiStatus.equals("OK");
            aiMensaje = aiApiStatus;
        }
        BotIntegracionesDTO.AiApiStatus aiApi = new BotIntegracionesDTO.AiApiStatus(aiDisponible, aiMensaje);

        // Base de datos
        boolean dbConectada = false;
        long dbPing = -1;
        try {
            if (botInstance != null) {
                long start = System.currentTimeMillis();
                DatabaseManager dbManager = botInstance.getClass().getDeclaredField("databaseManager")
                        .trySetAccessible()
                                ? (DatabaseManager) botInstance.getClass().getDeclaredField("databaseManager")
                                        .get(botInstance)
                                : null;
                if (dbManager == null)
                    throw new Exception();
                try (java.sql.Connection conn = dbManager.getConnection();
                        java.sql.Statement stmt = conn.createStatement()) {
                    stmt.executeQuery("SELECT 1");
                }
                dbPing = System.currentTimeMillis() - start;
                dbConectada = true;
            }
        } catch (Exception e) {
            dbConectada = false;
        }
        BotIntegracionesDTO.DatabaseStatus database = new BotIntegracionesDTO.DatabaseStatus(dbConectada, dbPing);

        return new BotIntegracionesDTO(jda, aiApi, database);
    }

    /**
     * Comprueba el estado de la API de IA.
     *
     * @return Array con el estado y mensaje de la API de IA.
     */
    private String[] comprobarAiApiStruct() {
        try {
            String apiKey = configService.get("gemini.api.key");
            if (apiKey != null && !apiKey.isBlank()) {
                // Simulación de petición real: si tienes método, hazlo aquí
                return new String[] { "true", "OK" };
            } else {
                return new String[] { "false", "No configurada" };
            }
        } catch (Exception e) {
            return new String[] { "false", "Error" };
        }
    }

    /**
     * Obtiene la configuración general del bot (sin exponer el token).
     *
     * @return BotConfigDTO con la configuración general
     */
    @Override
    public BotConfigDTO getBotConfig() {
        BotConfigDTO dto = new BotConfigDTO();
        // Por seguridad, solo indicamos si el token está configurado
        String token = configService.get("token");
        dto.setTokenSet(token != null && !token.isEmpty());
        return dto;
    }

    /**
     * Guarda el token del bot.
     *
     * @param token Token de Discord
     */
    @Override
    public void saveBotToken(String token) {
        configService.set("token", token);
        configService.save();
    }

    /**
     * Añade un nuevo usuario.
     *
     * @param usuario Usuario a añadir
     * @return Usuario añadido
     */
    @Override
    public Usuario addUsuario(Usuario usuario) {
        if (usuario == null) {
            logger.logWarn("Intento de añadir un usuario nulo.");
            return null; // O lanzar IllegalArgumentException
        }
        try {
            return usuarioService.save(usuario);
        } catch (Exception e) {
            logger.logError("Error al añadir usuario: " + usuario.getIdUsuario(), e);
            // Considerar lanzar una excepción personalizada
            return null;
        }
    }

    /**
     * Añade una nueva penalización al sistema.
     *
     * @param penalizacion El objeto Penalizacion a añadir.
     * @return La Penalizacion guardada, o null si ocurre un error.
     */
    @Override
    public Penalizacion addPenalizacion(Penalizacion penalizacion) {
        if (penalizacion == null) {
            logger.logWarn("Intento de añadir una penalización nula.");
            return null; // O lanzar IllegalArgumentException
        }
        try {
            // Asegurarse de que la fecha se establece si es nula, ya que el constructor de
            // Penalizacion la toma.
            // Si la API puede enviar una Penalizacion sin fecha, se podría establecer aquí.
            if (penalizacion.getFecha() == null) {
                // penalizacion.setFecha(java.time.LocalDateTime.now());
                logger.logWarn("Penalización recibida sin fecha. Se guardará tal cual o fallará si la BD lo requiere.");
            }
            return usuarioService.savePenalizacion(penalizacion);
        } catch (Exception e) {
            logger.logError("Error al añadir penalización para el usuario: " + penalizacion.getIdUsuario(), e);
            // Considerar lanzar una excepción personalizada
            return null;
        }
    }

    /**
     * Devuelve los nombres de todas las tablas de la base de datos.
     *
     * @return Lista de nombres de tablas.
     */
    @Override
    public List<String> getTableNames() {
        // Solo mostrar tablas relevantes para la interfaz
        List<String> tablasPermitidas = List.of("usuarios", "penalizaciones");
        List<String> tableNames = new ArrayList<>();
        if (databaseManager == null) {
            logger.logError("DatabaseManager no está disponible.", null);
            return tableNames;
        }
        try (java.sql.Connection conn = databaseManager.getConnection()) {
            java.sql.DatabaseMetaData metaData = conn.getMetaData();
            try (java.sql.ResultSet rs = metaData.getTables(null, null, "%", new String[] { "TABLE" })) {
                while (rs.next()) {
                    String nombre = rs.getString("TABLE_NAME");
                    if (tablasPermitidas.contains(nombre)) {
                        tableNames.add(nombre);
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            logger.logError("Error al obtener los nombres de las tablas", e);
        }
        return tableNames;
    }

    /**
     * Devuelve los nombres y etiquetas de las columnas de una tabla.
     * 
     * @param tableName Nombre de la tabla
     * @return Lista de columnas (nombre y etiqueta)
     */
    public java.util.List<backend.facade.dto.ColumnDTO> getTableColumns(String tableName) {
        java.util.List<backend.facade.dto.ColumnDTO> columns = new java.util.ArrayList<>();
        try (java.sql.Connection conn = databaseManager.getConnection()) {
            java.sql.DatabaseMetaData metaData = conn.getMetaData();
            try (java.sql.ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    String label = rs.getString("REMARKS");
                    if (label == null || label.isEmpty())
                        label = name;
                    columns.add(new backend.facade.dto.ColumnDTO(name, label));
                }
            }
        } catch (Exception e) {
            logger.logError("Error al obtener las columnas de la tabla " + tableName, e);
        }
        return columns;
    }

    /**
     * Devuelve el contenido de una tabla (todas las filas).
     *
     * @param tableName Nombre de la tabla.
     * @return Lista de mapas (columna -> valor) por cada fila.
     */
    @Override
    public List<java.util.Map<String, Object>> getTableData(String tableName) {
        List<java.util.Map<String, Object>> results = new ArrayList<>();
        if (databaseManager == null) {
            logger.logError("DatabaseManager no está disponible.", null);
            return results;
        }
        if (tableName == null || tableName.isBlank() || !tableName.matches("[a-zA-Z0-9_]+")) {
            logger.logError("Nombre de tabla inválido: " + tableName, null);
            return results;
        }
        String sql = "SELECT * FROM " + tableName;
        try (java.sql.Connection conn = databaseManager.getConnection();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
                java.sql.ResultSet rs = pstmt.executeQuery()) {
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    // Forzar id_usuario a String si la tabla es usuarios
                    if ("usuarios".equalsIgnoreCase(tableName) && "id_usuario".equalsIgnoreCase(columnLabel)
                            && value != null) {
                        row.put(columnLabel, String.valueOf(value));
                    } else {
                        row.put(columnLabel, value);
                    }
                }
                results.add(row);
            }
        } catch (java.sql.SQLException e) {
            logger.logError("Error al obtener datos de la tabla " + tableName, e);
        }
        return results;
    }

    @Override
    public boolean updateExperiencia(Long id, java.util.Map<String, Object> data) {
        if (id == null || data == null || data.isEmpty()) {
            logger.logWarn("ID o datos nulos/vacíos para actualizar experiencia.");
            return false;
        }
        try {
            Usuario usuario = usuarioService.findById(id).orElse(null);
            if (usuario == null) {
                logger.logWarn("No se encontró usuario con ID: " + id + " para actualizar experiencia.");
                return false;
            }

            boolean updated = false;
            if (data.containsKey("puntos_xp")) {
                Object puntosXpObj = data.get("puntos_xp");
                if (puntosXpObj instanceof Number) {
                    usuario.setPuntosXp(((Number) puntosXpObj).intValue());
                    updated = true;
                } else if (puntosXpObj instanceof String) {
                    try {
                        usuario.setPuntosXp(Integer.parseInt((String) puntosXpObj));
                        updated = true;
                    } catch (NumberFormatException e) {
                        logger.logWarn("Valor de puntos_xp no válido: " + puntosXpObj);
                    }
                }
            }
            if (data.containsKey("nivel")) {
                Object nivelObj = data.get("nivel");
                if (nivelObj instanceof Number) {
                    usuario.setNivel(((Number) nivelObj).intValue());
                    updated = true;
                } else if (nivelObj instanceof String) {
                    try {
                        usuario.setNivel(Integer.parseInt((String) nivelObj));
                        updated = true;
                    } catch (NumberFormatException e) {
                        logger.logWarn("Valor de nivel no válido: " + nivelObj);
                    }
                }
            }

            if (updated) {
                usuarioService.save(usuario);
                logger.logInfo("Experiencia actualizada para usuario ID: " + id);
                return true;
            } else {
                logger.logInfo("No se realizaron cambios en la experiencia para el usuario ID: " + id
                        + ". Datos proporcionados: " + data);
                return false; // O true si se considera éxito no hacer nada. Por ahora false.
            }
        } catch (Exception e) {
            logger.logError("Error al actualizar experiencia para usuario ID: " + id, e);
            return false;
        }
    }

    @Override
    public boolean deleteExperiencia(Long id) {
        if (id == null) {
            logger.logWarn("ID nulo para eliminar experiencia.");
            return false;
        }
        try {
            Usuario usuario = usuarioService.findById(id).orElse(null);
            if (usuario == null) {
                logger.logWarn("No se encontró usuario con ID: " + id + " para eliminar/resetear experiencia.");
                return false;
            }
            // En lugar de eliminar el usuario, reseteamos su experiencia.
            usuario.setPuntosXp(0);
            usuario.setNivel(0); // Asumiendo que el nivel base es 0 o 1. Ajustar si es necesario.
            usuarioService.save(usuario);
            logger.logInfo("Experiencia reseteada para usuario ID: " + id);
            return true;
        } catch (Exception e) {
            logger.logError("Error al eliminar/resetear experiencia para usuario ID: " + id, e);
            return false;
        }
    }

    /**
     * Indica si el token del bot está configurado (sin exponer el valor).
     *
     * @return true si el token está configurado, false en caso contrario
     */
    @Override
    public boolean hasBotToken() {
        String token = configService.get("token");
        return token != null && !token.isBlank();
    }

    /**
     * Indica si la clave Gemini está configurada (sin exponer el valor).
     *
     * @return true si la clave está configurada, false en caso contrario
     */
    @Override
    public boolean hasGeminiKey() {
        String key = configService.get("gemini.api.key");
        return key != null && !key.isBlank();
    }

    /**
     * Guarda la clave Gemini.
     *
     * @param key Clave Gemini
     */
    @Override
    public void saveGeminiKey(String key) {
        configService.set("gemini.api.key", key);
        configService.save();
    }

}