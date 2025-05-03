package bot.core;

import bot.commands.ModuleManager;
import bot.db.DatabaseManager;
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

/**
 * Clase principal del bot que delega responsabilidades a CommandRegistry y
 * EventRegistry.
 * Gestiona la inicialización de módulos, comandos, base de datos y listeners.
 * 
 * @author PelayoPS
 */
public class Bot {
    private final JDA jda;
    private final ModuleManager moduleManager;
    private final CommandRegistry commandRegistry;
    private final EventRegistry eventRegistry;
    private final LoggingManager logger = new LoggingManager();
    private final ServiceFactory serviceFactory;
    private final DatabaseManager databaseManager;

    /**
     * Constructor de la clase Bot.
     * 
     * @param token           Token de autenticación de Discord
     * @param serviceFactory  Fábrica de servicios
     * @param databaseManager Gestor de base de datos
     */
    public Bot(String token, ServiceFactory serviceFactory, DatabaseManager databaseManager) {
        this.serviceFactory = serviceFactory;
        this.databaseManager = databaseManager;
        // Inicializar registros y managers
        this.commandRegistry = new CommandRegistry();
        this.eventRegistry = new EventRegistry();
        this.moduleManager = new ModuleManager();

        // Inicializar la base de datos
        initializeDatabase();

        // Inicializar JDA
        try {
            this.jda = JDABuilder.build(token);
            // Inicializar y registrar módulos
            initializeModules();
            // Registrar comandos en Discord
            updateCommands();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing bot", e);
        }
    }

    /**
     * Inicializa la conexión a la base de datos y crea las tablas si no existen.
     */
    private void initializeDatabase() {
        try {
            // Usar la instancia inyectada de DatabaseManager
            try (Connection conn = databaseManager.getConnection()) {
                logger.logInfo("Conexión a base de datos establecida correctamente");

                // Ejecutar el script de esquema para crear las tablas si no existen
                executeSchemaScript(conn);
            }
        } catch (SQLException e) {
            logger.logError("Error al conectar con la base de datos", e);
            throw new RuntimeException("Error crítico: No se pudo inicializar la base de datos", e);
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
            // Cargar el script schema.sql desde los recursos
            String schemaScript = loadSchemaScript();

            // Mejorar manejo de errores para sentencias individuales
            try (Statement stmt = conn.createStatement()) {
                // Dividir el script en sentencias individuales
                String[] statements = schemaScript.split(";");
                for (String statement : statements) {
                    statement = statement.trim();
                    if (!statement.isEmpty()) {
                        try {
                            stmt.execute(statement);
                        } catch (SQLException e) {
                            // Verificar si el error es por un índice duplicado (ignoramos este error
                            // específico)
                            if (e.getMessage().contains("Duplicate key name")) {
                                logger.logInfo("Ignorando error de índice duplicado: " + e.getMessage());
                            } else {
                                // Para otros errores, los registramos pero continuamos con las siguientes
                                // sentencias
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

        // Registrar el listener para los hilos de chat de IA
        AIChatThreadListener aiChatListener = new AIChatThreadListener();
        jda.addEventListener(aiChatListener);
        logger.logInfo("Registrado listener para hilos de chat de IA");
    }

    /**
     * Actualiza y registra los comandos slash en Discord.
     */
    private void updateCommands() {
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
     * Apaga el bot y desconecta JDA.
     */
    public void shutdown() {
        // Desconectar JDA
        jda.shutdown();
    }
}