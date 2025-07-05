package backend.api;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Interfaz base para todos los comandos del bot.
 * Define los métodos esenciales que debe implementar cualquier comando
 * para integrarse con el sistema de comandos del bot.
 * 
 * @author PelayoPS
 */
public interface Command {

    /**
     * Ejecuta la lógica del comando cuando es invocado por un usuario.
     * 
     * @param event El evento de interacción del comando
     */
    void execute(SlashCommandInteractionEvent event);

    /**
     * Obtiene los datos necesarios para registrar el comando slash en Discord.
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