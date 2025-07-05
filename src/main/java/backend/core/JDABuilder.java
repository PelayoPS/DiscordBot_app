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
     * Incluye configuraciones SSL para compatibilidad con Java 21 y jpackage.
     *
     * @param token El token del bot de Discord.
     * @return Una instancia configurada de JDA.
     * @throws Exception Si ocurre un error durante la construcción de JDA.
     */
    public static JDA build(String token) throws Exception {
        // Configurar propiedades del sistema para SSL/TLS
        configureSSLProperties();

        // Aplicar configuración SSL específica para jpackage si es necesario
        JPackageSSLHelper.configureIfJPackage();

        logger.logInfo("Configurando JDA con propiedades SSL optimizadas para Java 21 y jpackage");

        // Crear el builder de JDA con configuraciones específicas para jpackage
        net.dv8tion.jda.api.JDABuilder jdaBuilder = net.dv8tion.jda.api.JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));

        // Configuraciones adicionales para mejorar conectividad en jpackage
        jdaBuilder.setRequestTimeoutRetry(true);

        return jdaBuilder.build();
    }

    /**
     * Configura las propiedades del sistema para SSL/TLS.
     * Estas configuraciones son necesarias para evitar problemas de handshake SSL
     * con Java 21 al conectarse a Discord, especialmente con jpackage.
     */
    private static void configureSSLProperties() {
        try {
            logger.logInfo("Configurando SSL para compatibilidad con jpackage y Java 21...");

            // Configuraciones SSL más permisivas para jpackage
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");

            // Algoritmos deshabilitados menos restrictivos para jpackage
            System.setProperty("jdk.tls.disabledAlgorithms",
                    "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, anon, NULL");

            // Configuraciones de truststore para jpackage
            System.setProperty("javax.net.ssl.trustStoreType", "JKS");

            // Permitir renegociación para compatibilidad
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
            System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");

            // Configuraciones específicas para jpackage JRE
            System.setProperty("jdk.tls.useExtendedMasterSecret", "false");
            System.setProperty("com.sun.net.ssl.checkRevocation", "false");
            System.setProperty("sun.security.ssl.allowLegacyResumption", "true");

            // Configuraciones para el cliente HTTP interno
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "false");
            System.setProperty("jdk.httpclient.allowRestrictedHeaders",
                    "host,connection,content-length,expect,upgrade,via");

            // Configuraciones específicas para OkHttp (usado por JDA)
            System.setProperty("okhttp.protocols", "http/1.1");
            System.setProperty("http.keepAlive", "true");
            System.setProperty("http.maxConnections", "5");

            // Configuraciones de red para mejorar conectividad
            System.setProperty("java.net.useSystemProxies", "true");
            System.setProperty("networkaddress.cache.ttl", "60");
            System.setProperty("networkaddress.cache.negative.ttl", "10");

            logger.logInfo("Propiedades SSL configuradas correctamente para Java 21 y jpackage");

        } catch (Exception e) {
            logger.logWarn("No se pudieron configurar todas las propiedades SSL: " + e.getMessage());
        }
    }
}
