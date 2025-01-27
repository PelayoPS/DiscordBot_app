package bot.commands.modules;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModCommands extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ban":
                banUser(event);
                break;
            case "kick":
                kickUser(event);
                break;
            case "mute":
                muteUser(event);
                break;
            default:
                event.reply("Comando no reconocido").setEphemeral(true).queue();
                break;
        }
    }

    private void banUser(SlashCommandInteractionEvent event) {
        
    }

    private void kickUser(SlashCommandInteractionEvent event) {
        
    }

    private void muteUser(SlashCommandInteractionEvent event) {
        
    }
}