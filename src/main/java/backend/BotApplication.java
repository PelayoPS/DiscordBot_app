package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para iniciar la aplicación Spring Boot del bot de Discord.
 * 
 * @author PelayoPS
 */
@SpringBootApplication
public class BotApplication {

    /**
     * Método principal que inicia la aplicación.
     * 
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Configurar propiedades SSL antes de iniciar la aplicación
        configureSSLProperties();

        SpringApplication.run(BotApplication.class, args);
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

            // Habilitar logging SSL para diagnóstico (opcional, comentar en producción)
            // System.setProperty("javax.net.debug", "ssl:handshake");

            System.out.println("[INFO] Propiedades SSL configuradas correctamente");

        } catch (Exception e) {
            System.err.println("[ERROR] Error al configurar propiedades SSL: " + e.getMessage());
        }
    }
}
