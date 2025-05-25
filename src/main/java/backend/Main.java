package backend;

import backend.config.ConfigService;
import backend.config.FileConfigService;
import backend.core.Bot;
import backend.log.LoggingManager;
import backend.modules.CommandManager;

/**
 * Clase principal para lanzar el bot en modo manual (sin API REST).
 * Permite pruebas y ejecución directa del bot desde consola.
 * 
 * @author PelayoPS
 */
public class Main {
    private static final LoggingManager logger = new LoggingManager();

    /**
     * Método principal para iniciar el bot en modo manual.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        System.out.println("[INFO] Lanzador manual: este modo es solo para pruebas o uso sin API REST.");

        // Configurar propiedades SSL antes de inicializar el bot
        configureSSLProperties();

        try {
            // Usar FileConfigService para la configuración centralizada
            ConfigService configService = new FileConfigService("src/main/resources/config.properties");

            // Obtener token desde configService
            String token = configService.get("token");
            if (token == null || token.isEmpty()) {
                logger.logError(
                        "Token no proporcionado. Por favor, proporcione un token válido en config.properties o como argumento.",
                        new IllegalArgumentException("Token no válido"));
                return;
            }

            // Instanciar el bot directamente (sin exponer API REST)
            backend.commands.ModuleManager moduleManager = new backend.commands.ModuleManager();
            Bot bot = new Bot(token, null, null, moduleManager);
            logger.logInfo("Bot instanciado correctamente (modo manual)");

            // Mostrar comandos disponibles
            logger.logInfo("Comandos disponibles:");
            bot.getJda()
                    .getRegisteredListeners()
                    .forEach(listener -> {
                        if (listener instanceof CommandManager commandManager) {
                            commandManager.getCommands().forEach(command -> {
                                logger.logInfo("- " + command.getName());
                            });
                        }
                    });

            // Registrar el shutdown hook para cerrar el bot correctamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.logInfo("Apagando el bot...");
                bot.shutdown();
                logger.logInfo("Bot apagado correctamente");
            }));

            // Esperar indefinidamente (simula ejecución manual)
            System.out.println("Bot ejecutándose en modo manual. Pulsa Ctrl+C para salir.");
            Thread.currentThread().join();

        } catch (InterruptedException e) {
            logger.logInfo("Ejecución interrumpida. Cerrando bot...");
        } catch (Exception e) {
            logger.logError("Error al iniciar el bot en modo manual", e);
        }
    }

    /**
     * Configura las propiedades del sistema para SSL/TLS.
     * Esta configuración es crítica para evitar errores de handshake SSL
     * con Java 21 al conectarse a servicios externos como Discord.
     */
    private static void configureSSLProperties() {
        System.out.println("[INFO] Configurando propiedades SSL para Java 21...");

        try {
            // Protocolos TLS habilitados
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");

            // Algoritmos deshabilitados (menos restrictivo para compatibilidad)
            System.setProperty("jdk.tls.disabledAlgorithms",
                    "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, anon, NULL");

            // Configuraciones adicionales para mejorar compatibilidad
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");

            // Configuraciones específicas para OkHttp (utilizado por JDA)
            System.setProperty("okhttp.protocols", "http/1.1");

            System.out.println("[INFO] Propiedades SSL configuradas correctamente");

        } catch (Exception e) {
            System.err.println("[ERROR] Error al configurar propiedades SSL: " + e.getMessage());
        }
    }
}