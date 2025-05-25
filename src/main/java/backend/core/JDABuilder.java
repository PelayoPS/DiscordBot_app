package backend.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import backend.log.LoggingManager;

/**
 * Clase para construir instancias de JDA con todos los intents habilitados.
 * Facilita la creación de la instancia principal de JDA para el bot de Discord.
 * 
 * @author PelayoPS
 */
public class JDABuilder {

    private static final LoggingManager logger = new LoggingManager();

    /**
     * Crea una instancia de JDA configurada con todos los intents habilitados.
     * Incluye configuraciones SSL para compatibilidad con Java 21.
     *
     * @param token El token del bot de Discord.
     * @return Una instancia configurada de JDA.
     * @throws Exception Si ocurre un error durante la construcción de JDA.
     */
    public static JDA build(String token) throws Exception {
        // Configurar propiedades del sistema para SSL/TLS
        configureSSLProperties();

        logger.logInfo("Configurando JDA con propiedades SSL optimizadas para Java 21");

        return net.dv8tion.jda.api.JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .build();
    }

    /**
     * Configura las propiedades del sistema para SSL/TLS.
     * Estas configuraciones son necesarias para evitar problemas de handshake SSL
     * con Java 21 al conectarse a Discord.
     */
    private static void configureSSLProperties() {
        try {
            // Permitir protocolos TLS más antiguos si es necesario
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");

            // Configurar algoritmos de intercambio de claves
            System.setProperty("jdk.tls.disabledAlgorithms",
                    "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL");

            // Habilitar algoritmos de cifrado modernos
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");

            // Configurar el almacén de confianza para usar el predeterminado del sistema
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");

            // Permitir renegociación SSL (necesario para algunas conexiones)
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");

            // Configurar el cliente HTTP para usar protocolos TLS modernos
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "false");

            logger.logInfo("Propiedades SSL configuradas correctamente para Java 21");

        } catch (Exception e) {
            logger.logWarn("No se pudieron configurar todas las propiedades SSL: " + e.getMessage());
        }
    }
}
