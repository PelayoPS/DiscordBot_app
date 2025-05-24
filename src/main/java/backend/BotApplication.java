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
        SpringApplication.run(BotApplication.class, args);
    }
}
