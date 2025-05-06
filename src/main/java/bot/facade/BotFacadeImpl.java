package bot.facade;

import bot.services.UsuarioService;
import bot.services.ModerationService;
import bot.modules.CommandManager;
import bot.log.LoggingManager;
import bot.models.Usuario;
import bot.models.Penalizacion;
import bot.config.ConfigManager;
import bot.config.ConfigService;
import bot.config.FileConfigService;
import bot.core.Bot;
import bot.core.ServiceFactory;
import bot.db.DatabaseManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
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

    private static final LoggingManager logger = new LoggingManager();
    private final UsuarioService usuarioService;
    private final ModerationService moderationService;
    private final CommandManager commandManager;
    private final LoggingManager loggingManager;
    private final DatabaseManager databaseManager;
    private Bot botInstance;
    private Instant botStartTime;
    @Value("${bot.version:1.0.0}")
    private String botVersion;

    // Estado de integraciones externas
    private String aiApiStatus = "Desconocido";
    private long aiApiLastCheck = 0L;
    private static final long AI_API_CACHE_MS = 30 * 60 * 1000; // 30 minutos

    /**
     * Constructor para inyección de dependencias (sin Bot).
     * 
     * @param usuarioService    Servicio de usuarios
     * @param moderationService Servicio de moderación
     * @param commandManager    Gestor de comandos
     * @param loggingManager    Gestor de logs
     * @param databaseManager   Gestor de base de datos
     */
    public BotFacadeImpl(UsuarioService usuarioService, ModerationService moderationService,
            CommandManager commandManager, LoggingManager loggingManager, DatabaseManager databaseManager) {
        this.usuarioService = usuarioService;
        this.moderationService = moderationService;
        this.commandManager = commandManager;
        this.loggingManager = loggingManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Inicia el bot de Discord.
     */
    @Override
    public synchronized void startBot() {
        if (botInstance == null) {
            // Cargar configuración
            ConfigService configService = new FileConfigService("src/main/resources/config.properties");
            String token = configService.get("token");
            ServiceFactory serviceFactory = new ServiceFactory(configService, databaseManager);
            botInstance = new Bot(token, serviceFactory, databaseManager);
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
        // Recargar configuración si es necesario
        ConfigManager config = new ConfigManager();
        config.loadConfig();
        startBot();
        logger.logInfo("Bot reiniciado y configuración recargada desde BotFacadeImpl");
    }

    /**
     * Recupera el estado actual del bot (por ejemplo, RUNNING, STOPPED).
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
     * Devuelve información extendida del estado del bot (DTO).
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
            // RAM y CPU
            Runtime runtime = Runtime.getRuntime();
            long usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMem = runtime.maxMemory() / (1024 * 1024);
            ram = usedMem + " MB / " + maxMem + " MB";
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double cpuLoad = -1;
            try {
                if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                    com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                    cpuLoad = sunOsBean.getProcessCpuLoad();
                    if (cpuLoad >= 0) {
                        cpu = String.format("%.2f%%", cpuLoad * 100);
                    } else {
                        cpu = "0%";
                    }
                } else {
                    cpu = String.format("%.2f", osBean.getSystemLoadAverage());
                }
            } catch (Exception e) {
                cpu = "-";
            }
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
            return usuarioService.findById(Long.parseLong(discordUserId)).orElse(null);
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public void timeoutUser(String guildId, String discordUserId, String reason, Duration duration) {
        moderationService.timeoutUsuario(Long.parseLong(discordUserId), reason, duration, null);
    }

    /**
     * Desbanea a un usuario.
     * 
     * @param guildId       El ID del servidor.
     * @param discordUserId El ID de Discord del usuario a desbanear.
     */
    @Override
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
    @Override
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
    @Override
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
     * @param types Lista de tipos de log (INFO, WARN, ERROR, DEBUG, opcional). Si
     *              es null o vacía, se devuelven todos los tipos.
     * @param limit Máximo número de entradas. Si es 0 o negativo, devuelve todas
     *              las coincidentes.
     * @param from  Fecha inicial en formato yyyy-MM-dd (opcional).
     * @param to    Fecha final en formato yyyy-MM-dd (opcional).
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
                var rs2 = stmt.executeQuery("SELECT COUNT(*) FROM experiencias");
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
            ConfigService configService = new FileConfigService("src/main/resources/config.properties");
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
}