package bot.commands.modules;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import bot.events.EventListener;

public class ModCommands extends EventListener {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!isCommandEnabled()) {
            event.reply("Los comandos están deshabilitados").setEphemeral(true).queue();
            return;
        } else {
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
    }

    private void banUser(SlashCommandInteractionEvent event) {

    }

    private void kickUser(SlashCommandInteractionEvent event) {

    }

    private void muteUser(SlashCommandInteractionEvent event) {

    }
}