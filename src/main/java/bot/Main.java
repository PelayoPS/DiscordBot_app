package bot;

import bot.config.ConfigManager;
import bot.core.Bot;
import bot.log.LoggingManager;
import bot.modules.CommandManager;

public class Main {
    private static final LoggingManager logger = new LoggingManager();

    public static void main(String[] args) {
        try {
            // Cargar configuración desde config.properties
            ConfigManager configManager = new ConfigManager();
            configManager.loadConfig();

            // Obtener token desde config.properties o args como respaldo
            String token = configManager.getProperty("token");

            if (token == null || token.isEmpty()) {
                logger.logError(
                        "Token no proporcionado. Por favor, proporcione un token válido en config.properties o como argumento.",
                        new IllegalArgumentException("Token no válido"));
                return;
            }

            // Inicializar el bot con el token
            Bot bot = new Bot(token);
            logger.logInfo("Bot iniciado correctamente");

            // ver los comandos disponibles
            System.out.println("Comandos disponibles:");
            bot.getJda()
                    .getRegisteredListeners()
                    .forEach(listener -> {
                        if (listener instanceof CommandManager commandManager) {
                            commandManager.getCommands().forEach(command -> {
                                System.out.println("- " + command.getName());
                            });
                        }
                    });

            // Registrar el shutdown hook para cerrar el bot correctamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.logInfo("Apagando el bot...");
                bot.shutdown();
                logger.logInfo("Bot apagado correctamente");
            }));

        } catch (Exception e) {
            logger.logError("Error al iniciar el bot", e);
        }
    }
}