package bot.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * Clase para construir instancias de JDA con todos los intents habilitados.
 * Facilita la creación de la instancia principal de JDA para el bot de Discord.
 * 
 * @author PelayoPS
 */
public class JDABuilder {

    /**
     * Crea una instancia de JDA configurada con todos los intents habilitados.
     * 
     * @param token El token del bot de Discord.
     * @return Una instancia configurada de JDA.
     * @throws Exception Si ocurre un error durante la construcción de JDA.
     */
    public static JDA build(String token) throws Exception {
        return net.dv8tion.jda.api.JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .build();
    }
}
