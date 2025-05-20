package bot.core;

import bot.commands.ModuleManager;
import bot.db.DatabaseManager;
import bot.listeners.MessageExperienceListener;
import bot.listeners.ai.AIChatThreadListener;
import bot.log.LoggingManager;
import bot.modules.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase principal del bot que delega responsabilidades a CommandRegistry y
 * EventRegistry.
 * Gestiona la inicialización de módulos, comandos, base de datos y listeners.
 * 
 * @author PelayoPS
 */
public class Bot {
    private JDA jda; 
    private final ModuleManager moduleManager;
    private final CommandRegistry commandRegistry;
    private final EventRegistry eventRegistry;
    private final LoggingManager logger = new LoggingManager();
    private final ServiceFactory serviceFactory;
    private final DatabaseManager databaseManager;
    private ScheduledExecutorService dbRetryScheduler;
    private volatile boolean dbInitializationFailed = false;

    /**
     * Constructor de la clase Bot.
     *
     * @param token           Token de autenticación de Discord
     * @param serviceFactory  Fábrica de servicios
     * @param databaseManager Gestor de base de datos
     */
    public Bot(String token, ServiceFactory serviceFactory, DatabaseManager databaseManager, ModuleManager moduleManager) {
        this.serviceFactory = serviceFactory;
        this.databaseManager = databaseManager;
        this.commandRegistry = new CommandRegistry();
        this.eventRegistry = new EventRegistry();
        this.moduleManager = moduleManager;

        initializeDatabase();

        try {
            this.jda = JDABuilder.build(token);
            initializeModules();
            updateCommands();
        } catch (Exception e) {
            logger.logError("Error crítico al inicializar JDA. El bot no podrá conectarse a Discord.", e);
        }
        
        if (dbInitializationFailed) {
            this.dbRetryScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                t.setName("DB-Retry-Scheduler");
                return t;
            });
            this.dbRetryScheduler.scheduleAtFixedRate(this::attemptDbReinitialization, 5, 120, TimeUnit.SECONDS);
            logger.logInfo("Programador de reintentos de inicialización de BD iniciado. Primer intento en 5 segundos, luego cada 2 minutos.");
        }
    }

    /**
     * Inicializa la conexión a la base de datos y crea las tablas si no existen.
     */
    private void initializeDatabase() {
        try {
            try (Connection conn = databaseManager.getConnection()) {
                logger.logInfo("Conexión a base de datos establecida correctamente durante la inicialización.");
                executeSchemaScript(conn);
                this.dbInitializationFailed = false; 
            }
        } catch (SQLException e) {
            logger.logError("Error al inicializar la base de datos durante el arranque. El bot continuará sin conexión a BD activa. Se intentará reconectar periódicamente.", e);
            this.dbInitializationFailed = true; 
        } catch (Exception e) { 
            logger.logError("Error inesperado durante la inicialización de la base de datos. Se marcará para reintento.", e);
            this.dbInitializationFailed = true;
        }
    }

    /**
     * Ejecuta el script SQL para crear el esquema de la base de datos.
     *
     * @param conn Conexión a la base de datos
     * @throws SQLException Si ocurre un error al ejecutar el script
     */
    private void executeSchemaScript(Connection conn) throws SQLException {
        try {
            String schemaScript = loadSchemaScript();
            try (Statement stmt = conn.createStatement()) {
                String[] statements = schemaScript.split(";");
                for (String statement : statements) {
                    statement = statement.trim();
                    if (!statement.isEmpty()) {
                        try {
                            stmt.execute(statement);
                        } catch (SQLException e) {
                            if (e.getMessage().contains("Duplicate key name")) {
                                logger.logInfo("Ignorando error de índice duplicado: " + e.getMessage());
                            } else {
                                logger.logError("Error al ejecutar sentencia SQL: " + statement, e);
                            }
                        }
                    }
                }
                logger.logInfo("Esquema de base de datos inicializado correctamente");
            }
        } catch (IOException e) {
            throw new SQLException("Error al cargar el script de esquema", e);
        }
    }

    /**
     * Carga el script SQL desde los recursos.
     *
     * @return Contenido del script SQL
     * @throws IOException Si ocurre un error al leer el archivo
     */
    private String loadSchemaScript() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                throw new IOException("No se pudo encontrar el archivo schema.sql");
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Inicializa y registra los módulos y listeners en JDA.
     */
    private void initializeModules() {
        if (this.jda == null) {
            logger.logWarn("JDA no está disponible, no se pueden inicializar módulos.");
            return;
        }
        // Registrar módulos básicos usando la factoría
        moduleManager.registerModule("management",
                (net.dv8tion.jda.api.hooks.EventListener) serviceFactory.getAdminCommands());
        moduleManager.registerModule("moderation",
                (net.dv8tion.jda.api.hooks.EventListener) serviceFactory.getModCommands());
        moduleManager.registerModule("user",
                (net.dv8tion.jda.api.hooks.EventListener) serviceFactory.getUserCommands());

        // Registrar los módulos como event listeners en JDA
        for (var module : moduleManager.getModules().values()) {
            jda.addEventListener(module);
            eventRegistry.registerEventListener(module);
        }

        // Registrar el listener de mensajes
        MessageExperienceListener messageListener = new MessageExperienceListener(
            serviceFactory.getUsuarioService(),
            serviceFactory.getConfigService()
        );
        jda.addEventListener(messageListener);
        logger.logInfo("Registrado listener de experiencia de mensajes");

        // Registrar el listener para los hilos de chat de IA
        AIChatThreadListener aiChatListener = new AIChatThreadListener();
        jda.addEventListener(aiChatListener);
        logger.logInfo("Registrado listener para hilos de chat de IA");
    }

    /**
     * Actualiza y registra los comandos slash en Discord.
     */
    private void updateCommands() {
        if (this.jda == null) {
            logger.logWarn("JDA no está disponible, no se pueden actualizar los comandos.");
            return;
        }
        List<SlashCommandData> allCommands = new ArrayList<>();

        // Recolectar todos los comandos slash de todos los módulos
        moduleManager.getModules().values().stream()
                .filter(manager -> manager instanceof CommandManager)
                .map(manager -> (CommandManager) manager)
                .forEach(manager -> allCommands.addAll(manager.getSlash()));

        // Actualizar los comandos en Discord
        jda.updateCommands().addCommands(allCommands).queue(
                success -> logger.logInfo("Comandos registrados correctamente"),
                error -> logger.logError("Error al registrar los comandos: " + error.getMessage(), error));
    }

    /**
     * Obtiene la instancia de JDA.
     *
     * @return JDA
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Obtiene el gestor de módulos.
     *
     * @return ModuleManager
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Obtiene el registro de comandos.
     *
     * @return CommandRegistry
     */
    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    /**
     * Obtiene el registro de eventos.
     *
     * @return EventRegistry
     */
    public EventRegistry getEventRegistry() {
        return eventRegistry;
    }
    
    /**
     * Intenta reinicializar la conexión a la base de datos si falló previamente.
     */
    private synchronized void attemptDbReinitialization() {
        if (!this.dbInitializationFailed) { 
            if (this.dbRetryScheduler != null && !this.dbRetryScheduler.isShutdown()) {
                this.dbRetryScheduler.shutdown();
                this.dbRetryScheduler = null;
            }
            return;
        }

        logger.logInfo("Intentando reinicializar la conexión a la base de datos...");
        try (Connection conn = databaseManager.getConnection()) {
            logger.logInfo("Conexión a base de datos reestablecida correctamente.");
            executeSchemaScript(conn); 
            logger.logInfo("Esquema de base de datos reinicializado tras reconexión.");
            
            this.dbInitializationFailed = false; 
            
            if (this.dbRetryScheduler != null && !this.dbRetryScheduler.isShutdown()) {
                this.dbRetryScheduler.shutdown(); 
                this.dbRetryScheduler = null;
                logger.logInfo("Reintentos de conexión a base de datos detenidos tras éxito.");
            }
        } catch (SQLException e) {
            logger.logError("Fallo al reintentar la inicialización de la base de datos. Se reintentará.", e);
        } catch (Exception e) {
            logger.logError("Error inesperado durante el reintento de inicialización de la base de datos. Se reintentará.", e);
        }
    }

    /**
     * Apaga el bot y desconecta JDA.
     */
    public void shutdown() {
        logger.logInfo("Iniciando apagado del bot...");
        if (this.dbRetryScheduler != null && !this.dbRetryScheduler.isShutdown()) {
            this.dbRetryScheduler.shutdownNow(); 
            try {
                if (!this.dbRetryScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.logWarn("El programador de reintentos de BD no terminó en 5 segundos.");
                }
            } catch (InterruptedException e) {
                logger.logWarn("Interrupción mientras se esperaba el cierre del programador de reintentos de BD.");
                Thread.currentThread().interrupt();
            }
            logger.logInfo("Programador de reintentos de BD detenido.");
        }
        
        if (jda != null) { 
           jda.shutdown();
           logger.logInfo("JDA desconectado.");
        } else {
           logger.logInfo("JDA no fue inicializado o ya fue desconectado, no se requiere desconexión de JDA.");
        }
        logger.logInfo("Bot apagado.");
    }
}