package bot.commands.moderation;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PurgeCommand implements Command {
    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Elimina múltiples mensajes")
                .addOption(OptionType.INTEGER, "cantidad", "Cantidad de mensajes a eliminar (2-100)", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("No tienes permisos para usar este comando").setEphemeral(true).queue();
            return;
        }

        int amount = event.getOption("cantidad").getAsInt();
        if (amount < 2 || amount > 100) {
            event.reply("La cantidad debe estar entre 2 y 100 mensajes").setEphemeral(true).queue();
            return;
        }

        event.getChannel().asTextChannel().getHistory().retrievePast(amount)
                .queue(messages -> {
                    event.getChannel().asTextChannel().purgeMessages(messages);
                    event.reply("Se han eliminado " + messages.size() + " mensajes").setEphemeral(true).queue();
                });
    }
}
