package bot.api;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Interfaz base para todos los comandos del bot.
 */
public interface Command {
    /**
     * Ejecuta el comando.
     * 
     * @param event El evento de interacción del comando
     */
    void execute(SlashCommandInteractionEvent event);

    /**
     * Obtiene los datos para registrar el comando slash.
     * 
     * @return Los datos del comando slash
     */
    SlashCommandData getSlash();

    /**
     * Obtiene el nombre del comando.
     * 
     * @return El nombre del comando
     */
    String getName();
}