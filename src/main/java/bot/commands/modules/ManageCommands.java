package bot.commands.modules;

import bot.events.EventListener;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class ManageCommands extends EventListener {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return; 
        } else {
            switch (event.getName()) {
                // Agrega aquí los comandos de gestión
                default:
                    event.reply("Comando no reconocido").setEphemeral(true).queue();
                    break;
            }
        }
    }
}