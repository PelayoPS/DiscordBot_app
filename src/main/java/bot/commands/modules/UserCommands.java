package bot.commands.modules;

import java.util.ArrayList;
import java.util.List;

import bot.events.EventListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Clase que maneja los comandos de usuario del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class UserCommands extends ICommandManager {

    /**
     * Constructor de la clase UserCommands.
     * 
     * Agrega los comandos de usuario a la lista de comandos.
     */
    public UserCommands() {
        // TODO Agregar los comandos de usuario a la lista de comandos
    }

    /**
     * Maneja la interacción de comandos de barra.
     * 
     * @param event El evento de interacción del comando.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return;
        } else {
            switch (event.getName()) {
                // Agrega aquí los comandos de usuario
                default:
                    event.reply("Comando no reconocido").setEphemeral(true).queue();
                    break;
            }
        }
    }
}