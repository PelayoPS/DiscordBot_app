package bot;

import java.util.Scanner;

import bot.core.Bot;
import bot.log.LoggingManager;
import bot.modules.CommandManager;

public class Main {
    private static final LoggingManager logger = new LoggingManager();

    public static void main(String[] args) {
        try {
            // Obtener token desde args o configuración
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introduce el token del bot: ");
            String token = scanner.nextLine();
            if (token == null || token.isEmpty()) {
                logger.logError("No se ha proporcionado el token del bot", null);
                scanner.close();
                return;
            }

            // Inicializar el bot con el token
            Bot bot = new Bot(token);
            logger.logInfo("Bot iniciado correctamente");

            scanner.close();

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

            // Mantener el bot en ejecución
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