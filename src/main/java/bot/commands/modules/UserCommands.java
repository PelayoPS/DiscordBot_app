package bot.commands.modules;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            // Agrega aquí los comandos de usuario
            default:
                event.reply("Comando no reconocido").setEphemeral(true).queue();
                break;
        }
    }
}