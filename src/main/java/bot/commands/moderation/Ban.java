package bot.commands.moderation;

import java.util.concurrent.TimeUnit;

import bot.api.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Ban implements Command {

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public SlashCommandData getSlash() {
        return Commands.slash("ban", "Banea a un usuario del servidor")
                .addOption(OptionType.USER, "usuario", "El usuario a banear", true)
                .addOption(OptionType.STRING, "razon", "Razón del baneo", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verificar permisos
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("No tienes permisos para banear usuarios").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("usuario").getAsMember();
        String reason = event.getOption("razon") != null ? event.getOption("razon").getAsString()
                : "No se proporcionó razón";

        if (target != null) {
            target.ban(0, TimeUnit.DAYS).reason(reason).queue(
                    success -> event.reply("Usuario baneado correctamente").setEphemeral(true).queue(),
                    error -> event.reply("No se pudo banear al usuario: " + error.getMessage()).setEphemeral(true)
                            .queue());
        } else {
            event.reply("No se encontró al usuario").setEphemeral(true).queue();
        }
    }
}