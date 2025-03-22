package bot.commands.moderation;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Unban implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("No tienes permisos para desbanear usuarios").setEphemeral(true).queue();
            return;
        }
        var userId = event.getOption("usuario").getAsUser();

        event.getGuild().unban(userId).queue(
                success -> event.reply("Usuario desbaneado correctamente").setEphemeral(true).queue(),
                error -> event.reply("No se pudo desbanear al usuario: " + error.getMessage()).setEphemeral(true)
                        .queue());
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash(getName(), "Desbanea a un usuario usando su ID")
                .addOption(OptionType.USER, "usuario", "Usuario a desbanear", true).setGuildOnly(true);
    }

    @Override
    public String getName() {
        return "unban";
    }
}
