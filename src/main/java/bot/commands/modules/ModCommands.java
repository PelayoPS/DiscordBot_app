package bot.commands.modules;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import bot.commands.modules.mod.Ban;
import bot.commands.modules.mod.Kick;
import bot.commands.modules.mod.Mute;
import bot.events.EventListener;

/**
 * Clase que maneja los comandos de moderación del bot.
 * Extiende de {@link EventListener} para manejar eventos de interacción de
 * comandos.
 */
public class ModCommands extends ICommandManager {

    /**
     * Constructor de la clase ModCommands.
     * 
     * Agrega los comandos de moderación a la lista de comandos.
     */
    public ModCommands() {
        commands.add(Ban.class);
        commands.add(Kick.class);
        commands.add(Mute.class);
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
                case "ban":
                    Ban.banUser(event);
                    break;
                case "kick":
                    Kick.kickUser(event);
                    break;
                case "mute":
                    Mute.muteUser(event);
                    break;
                default:
                    event.reply("Comando no reconocido").setEphemeral(true).queue();
                    break;
            }

        }
    }
}